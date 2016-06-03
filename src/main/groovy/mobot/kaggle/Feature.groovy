package mobot.kaggle

import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.stat.descriptive.moment.Mean
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation

/**
 * Created by tlin1 on 5/20/16.
 */
class Feature {
    public enum Type {
        Numberic,
        Enumeric
    }
    public enum EducationGroup {
        AdvanceEdu,
        CollegeEdu,
        HighEdu,
        MiddleEdu,
        EleEdu
    }
    public interface Convertable {
        public double convert(String key);

        public int getCount();
    }
    public static class EducationConvertable implements Convertable {

        private Map<String, Integer> groups = new HashMap<String, Integer>() {
            {
                put("Bachelors", Feature.EducationGroup.CollegeEdu.ordinal());
                put("Some-college", Feature.EducationGroup.CollegeEdu.ordinal());
                put("11th", Feature.EducationGroup.HighEdu.ordinal());
                put("HS-grad", Feature.EducationGroup.HighEdu.ordinal());
                put("Prof-school", Feature.EducationGroup.CollegeEdu.ordinal());
                put("Assoc-acdm", Feature.EducationGroup.CollegeEdu.ordinal());
                put("Assoc-voc", Feature.EducationGroup.CollegeEdu.ordinal());
                put("9th", Feature.EducationGroup.HighEdu.ordinal());
                put("7th-8th", Feature.EducationGroup.MiddleEdu.ordinal());
                put("12th", Feature.EducationGroup.HighEdu.ordinal());
                put("Masters", Feature.EducationGroup.AdvanceEdu.ordinal());
                put("1st-4th", Feature.EducationGroup.EleEdu.ordinal());
                put("10th", Feature.EducationGroup.MiddleEdu.ordinal());
                put("Doctorate", Feature.EducationGroup.AdvanceEdu.ordinal());
                put("5th-6th", Feature.EducationGroup.EleEdu.ordinal());
                put("Preschool", Feature.EducationGroup.EleEdu.ordinal());
            }
        }

        @Override
        double convert(String key) {
            return groups.get(key);
        }

        @Override
        int getCount() {
            return Feature.EducationGroup.values().size();
        }
    }

    public final Convertable INDEX_CONVERTABLE = new Convertable() {
        @Override
        public double convert(String key) {
            String pkey = key.toLowerCase();
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(pkey)) {
                    return i;
                }
            }
            if ("?".equals(key)) {
                return -1;
            }
            throw new IllegalArgumentException("cannot find key: " + key);
        }

        @Override
        int getCount() {
            return values.length;
        }
    }
    public final String name;
    public final Type type;
    public final String[] values;
    public final int index;
    double[] frequenceTable;
    private double[][] conditionalFrequenceTable;
    private Convertable convertable = INDEX_CONVERTABLE;
    private NormalDistribution[] normalDistribution;
    private NormalDistribution frequenceDistribution
    public boolean enabled = true;

    public Feature(String name, Type type, int index, String value) {
        this.name = name;
        this.type = type;
        this.index = index;
        if (type == Type.Enumeric) {
            this.values = value.split(",");
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim().toLowerCase();
            }
        }
    }

    public void setConvertable(Convertable convertable) {
        this.convertable = convertable == null ? INDEX_CONVERTABLE : convertable;
    }

    public Double convert(String key) {
        return convertable.convert(key);
    }

    public String toString() {
        return "Feature:[" + name + (type == Type.Numberic ? "" : "(" + values.join(", ") + ")") + "]";
    }

    public void normalize(double[][] instanceValues, int[] categoryCounts) {
        if (type == Type.Numberic) {
            normalDistribution = new NormalDistribution[categoryCounts.length];
            Mean meanUtil = new Mean();
            StandardDeviation stdUtil = new StandardDeviation();
            for (int i = 0; i < categoryCounts.length; i++) {
                double mean = meanUtil.evaluate(instanceValues[i], 0, categoryCounts[i]);
                double std = stdUtil.evaluate(instanceValues[i], mean, 0, categoryCounts[i]);
                normalDistribution[i] = new NormalDistribution(mean, std);
            }
            int total = 0;
            for (int cnt : categoryCounts) {
                total += cnt;
            }
            double[] all = new double[total];
            int idx = 0;
            for (int i = 0; i < categoryCounts.length; i++) {
                for (int j = 0; j < categoryCounts[i]; j++) {
                    all[idx++] = instanceValues[i][j];
                }
            }
            double mean = meanUtil.evaluate(all, 0, all.length);
            double std = stdUtil.evaluate(all, mean, 0, all.length);
            frequenceDistribution = new NormalDistribution(mean, std);
        } else {
            int valueCount = convertable.getCount();
            conditionalFrequenceTable = new double[categoryCounts.length][valueCount];
            frequenceTable = new double[valueCount];
            int totalCount = 0;
            for (int i = 0; i < categoryCounts.length; i++) {
                int count = 0;
                int[] counts = new double[valueCount];
                for (int j = 0; j < categoryCounts[i]; j++) {
                    double v = instanceValues[i][j];
                    if (v < 0) {
                        continue;
                    }
                    counts[(int) v]++;
                    count++;
                    frequenceTable[(int) v]++;
                }
                for (int j = 0; j < valueCount; j++) {
                    conditionalFrequenceTable[i][j] = counts[j] / (double) count;
                }
                totalCount += count;
            }
            for (int i = 0; i < valueCount; i++) {
                frequenceTable[i] = frequenceTable[i] / (double) totalCount;
            }
        }
    }

    public double[] predict(double value) {
        if ((type == Type.Numberic && normalDistribution == null) ||
                (type == Type.Enumeric && conditionalFrequenceTable == null)) {
            throw new IllegalStateException("Need to run normalize() first");
        }
        if (type == Type.Numberic) {
            double[] results = new double[normalDistribution.length];
            for (int i = 0; i < results.length; i++) {
                results[i] = normalDistribution[i].cumulativeProbability(value);
            }
            return results;
        } else {
            double[] results = new double[conditionalFrequenceTable.length];
            for (int i = 0; i < conditionalFrequenceTable.length; i++) {
                results[i] = conditionalFrequenceTable[i][(int) value];
            }
            return results;
        }
    }

    public double getFrequence(double value) {
        if ((type == Type.Numberic && normalDistribution == null) ||
                (type == Type.Enumeric && conditionalFrequenceTable == null)) {
            throw new IllegalStateException("Need to run normalize() first");
        }
        if (type == Type.Numberic) {
            return frequenceDistribution.cumulativeProbability(value);
        }
        return frequenceTable[(int) value];
    }
}
