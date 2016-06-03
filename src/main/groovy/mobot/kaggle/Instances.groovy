package mobot.kaggle

import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.stat.descriptive.moment.Mean
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation

/**
 * Created by tlin1 on 5/20/16.
 */
class Instances {
    private final Feature[] features;
    private final List<Double[]> instances;

    public Instances(Feature[] features) {
        this.features = features;
        instances = new ArrayList<>();
    }

    public Feature getFeature(int featureIndex) {
        return features[featureIndex];
    }

    public List<Double[]> getInstances() {
        return instances;
    }

    public void addAll(List<String[]> data) {
        for (String[] row : data) {
            add(row);
        }
    }

    public Double[] add(String[] data) {
//        if (features.length != data.length) {
//            throw new IllegalArgumentException("Illegal data, size doesn't match:" + features.length + ", "
//                    + data.length);
//        }
        Double[] row = new Double[data.length];
        for (int i = 0; i < data.length; i++) {
            if (features[i].type == Feature.Type.Numberic) {
                row[i] = Double.parseDouble(data[i]);
            } else {
                row[i] = features[i].convert(data[i]);
            }
        }
        instances.add(row);
        return row;
    }

    public int getCount() {
        return instances.size();
    }

    public Feature[] getFeatures() {
        return features;
    }

    public Feature[] getEnabledFeatures() {
        List<Feature> list = new ArrayList<>();
        for (Feature feature: features) {
            if (feature.enabled) {
                list.add(feature);
            }
        }
        return list.toArray(new Feature[0]);
    }

    public void normalize(int expectedFeatureIndex) {
        NormalDistribution normalDistribution;
        Mean meanUtil = new Mean();
        StandardDeviation stdUtil = new StandardDeviation();
        double[] values = new double[instances.size()];
        for (int j = 0; j < features.length; j++) {
            if (j == expectedFeatureIndex || features[j].type == Feature.Type.Enumeric) {
                continue;
            }
            for (int i = 0; i < values.length; i++) {
                values[i] = instances.get(i)[j];
            }
            double mean = meanUtil.evaluate(values, 0, values.length);
            double std = stdUtil.evaluate(values, mean, 0, values.length);
            normalDistribution = new NormalDistribution(mean, std);
            for (int i = 0; i < values.length; i++) {
                instances.get(i)[j] = normalDistribution.cumulativeProbability(values[i]);
            }
        }
    }
}
