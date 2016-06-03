package mobot.kaggle

/**
 * Created by tlin1 on 5/20/16.
 */
class KaggleFeatures {
    public static final int FEATURE_AGE = 0;
    public static final int FEATURE_WORKCLASS = 1;
    public static final int FEATURE_FNLWGT = 2;
    public static final int FEATURE_EDUCATION = 3;
    public static final int FEATURE_EDUCATION_NUM = 4;
    public static final int FEATURE_MARITAL_STATUS = 5;
    public static final int FEATURE_OCCUPATION = 6;
    public static final int FEATURE_RELATIONSHIP = 7;
    public static final int FEATURE_RACE = 8;
    public static final int FEATURE_SEX = 9;
    public static final int FEATURE_CAPITAL_GAIN = 10;
    public static final int FEATURE_CAPITAL_LOSS = 11;
    public static final int FEATURE_HOUR_PER_WEEK = 12;
    public static final int FEATURE_NATIVE_COUNTRY = 13;
    public static final int FEATURE_INCOME = 14;

    private List<Feature> features;
    public KaggleFeatures(File file) {
        features = new ArrayList<>();
        List<String> lines = file.readLines();
        for (String s: lines) {
            String[] ss = s.split(":");
            if (ss.length != 2) {
                println ("ignore line: " + s);
                continue;
            }
            String name = ss[0].trim();
            String type = ss[1].trim();
            if ("NUMERIC".equals(type)) {
                features.add(new Feature(name, Feature.Type.Numberic, features.size(), null));
            } else {
                features.add(new Feature(name, Feature.Type.Enumeric, features.size(), type));
            }
        }
    }

    public Feature[] getFeatures() {
        return features.toArray(new Feature[0]);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Feature f: features) {
            sb.append(f.toString()).append("\n");
        }
        return sb.toString();
    }
}
