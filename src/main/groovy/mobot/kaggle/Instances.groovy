package mobot.kaggle
/**
 * Created by tlin1 on 5/20/16.
 */
class Instances {
    private final Feature[] features;
    private final List<Double[]> instances;
    private int expectedFeatureIndex

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

    public void train(int expectedFeatureIndex) {
        if (instances.isEmpty()) {
            throw new IllegalStateException("No data found");
        }
        this.expectedFeatureIndex = expectedFeatureIndex;
        Feature feature = features[expectedFeatureIndex];
        for (int i = 0; i < features.length; i++) {
            double[][] instanceValues = new double[feature.values.length][instances.size()];
            int[] categoryCounts = new int[feature.values.length];
            for (int j = 0; j < instances.size(); j++) {
                Double[] row = instances.get(j);
                int category = (int) row[expectedFeatureIndex];
                instanceValues[category][categoryCounts[category]] = row[i];
                categoryCounts[category]++;
            }
            features[i].normalize(instanceValues, categoryCounts);
        }
        println("Total: " + instances.size() + ", actual:" + feature.frequenceTable + "," + feature.values);
    }

    public void test(Instances instances) {
        Feature feature = features[expectedFeatureIndex];
        int[][] results = new int[feature.values.length][feature.values.length];
        for (Double[] values : instances.instances) {
            int expectedCategory = test(values);
            int actualCategory = (int) values[expectedFeatureIndex];
            results[actualCategory][expectedCategory]++;
        }
        println(feature.values);
        println(results);
        int correct = results[0][0] + results[1][1];
        int wrong = results[0][1] + results[1][0];
        int total = correct + wrong;
        println("correct=" + (correct / (double) total) + ", wrong=" + (wrong / (double) total));
    }

    public int test(Double[] instanceValues) {
        Feature feature = features[expectedFeatureIndex];
        double[] expected = new double[feature.values.length];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = 1.0;
        }
        // P(c|x) = P(x|c) * P(c)/P(x)
        double px = 1.0;
        for (int i = 0; i < features.length; i++) {
            if (i == expectedFeatureIndex || !features[i].enabled) {
                continue;
            }
            double[] predict = features[i].predict(instanceValues[i]);
            //P(x|c)
            for (int j = 0; j < expected.length; j++) {
                expected[j] = expected[j] * predict[j];
            }
            px = px * features[i].getFrequence(instanceValues[i]);
        }
        // P(x|c) * P(c)/P(x)
        for (int i = 0; i < expected.length; i++) {
            expected[i] = expected[i] * feature.getFrequence(i) / px;
        }
        int expectedCategory = 0;
        double value = expected[expectedCategory];
        for (int i = 1; i < expected.length; i++) {
            if (value < expected[i]) {
                expectedCategory = i;
                value = expected[i];
            }
        }
        return expectedCategory;
    }

    public String[] guess(Instances instances) {
        Feature feature = features[expectedFeatureIndex];
        int[] resultCounts = new int[feature.values.length];
        String[] results = new String[instances.instances.size()];
        int idx = 0;
        for (Double[] values : instances.instances) {
            int expectedCategory = test(values);
            resultCounts[expectedCategory]++;
            results[idx++] = feature.values[expectedCategory].toUpperCase();
        }
        println("----- guess results=" + resultCounts);
        return results;
    }

    public Feature[] getFeatures() {
        return features;
    }
}
