package mobot.kaggle

import com.univocity.parsers.tsv.TsvParser
import com.univocity.parsers.tsv.TsvParserSettings
import groovy.json.JsonBuilder
import groovy.json.JsonOutput

class NaiveBayes {
    public NaiveBayes() {
    }


    public void train(Instances kaggleInstances, int expectedFeatureIndex) {
        List<Double[]> instances = kaggleInstances.getInstances();
        Feature[] features = kaggleInstances.getFeatures();
        if (instances.isEmpty()) {
            throw new IllegalStateException("No data found");
        }
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

    public void test(Instances kaggleInstances, int expectedFeatureIndex) {
        List<Double[]> instances = kaggleInstances.getInstances();
        Feature[] features = kaggleInstances.getFeatures();
        Feature feature = features[expectedFeatureIndex];
        int[][] results = new int[feature.values.length][feature.values.length];
        for (Double[] values : instances) {
            int expectedCategory = test(values, features, expectedFeatureIndex);
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

    public int test(Double[] instanceValues, Feature[] features, int expectedFeatureIndex) {
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

    public String[] guess(List<Double[]> instances, Feature[] features, int expectedFeatureIndex) {
        Feature feature = features[expectedFeatureIndex];
        int[] resultCounts = new int[feature.values.length];
        String[] results = new String[instances.size()];
        int idx = 0;
        for (Double[] values : instances) {
            int expectedCategory = test(values, features, expectedFeatureIndex);
            resultCounts[expectedCategory]++;
            results[idx++] = feature.values[expectedCategory].toUpperCase();
        }
        println("----- guess results=" + resultCounts);
        return results;
    }


    public static void main(String[] args) {
        File data = new File(args[0]);

        //f92c4298-15ef-4419-a387-a2ac66dfa2ca
        // parses all rows in one go.
        NaiveBayes naiveBayes = new NaiveBayes();
        println("-----Base-----");
        Instances instances;
        instances = LoadUtils.loadInstances(new File(data, "training.tsv"));
        naiveBayes.train(instances, KaggleFeatures.FEATURE_INCOME);
        naiveBayes.test(instances, KaggleFeatures.FEATURE_INCOME);

        println("-----Grouping-----");
        instances = LoadUtils.loadInstances(new File(data, "training.tsv"), new LoadUtils.FeatureUpdater() {
            @Override
            void update(Feature[] features) {
                features[KaggleFeatures.FEATURE_EDUCATION].setConvertable(new Feature.EducationConvertable());
            }
        });
        naiveBayes.train(instances, KaggleFeatures.FEATURE_INCOME);
        naiveBayes.test(instances, KaggleFeatures.FEATURE_INCOME);
//        println("-----log value for fnlwgt-----");
//        instances = loadInstances(new File(data, "training.tsv"));
//        for (Double[] values: instances.getInstances()) {
//            values[KaggleFeatures.FEATURE_FNLWGT] = Math.log(values[KaggleFeatures.FEATURE_FNLWGT]);
//        }
//        instances.train(KaggleFeatures.FEATURE_INCOME);
//        instances.test(instances);

        println("-----ignore-----");
        instances = LoadUtils.loadInstances(new File(data, "training.tsv"));
        int[] ignores = [
                KaggleFeatures.FEATURE_FNLWGT,
                KaggleFeatures.FEATURE_EDUCATION_NUM,
                KaggleFeatures.FEATURE_CAPITAL_GAIN,
                KaggleFeatures.FEATURE_CAPITAL_LOSS
        ];
        for (int i = 0; i < ignores.length; i++) {
            instances.getFeature(ignores[i]).enabled = false;
        }
        naiveBayes.train(instances, KaggleFeatures.FEATURE_INCOME);
        naiveBayes.test(instances, KaggleFeatures.FEATURE_INCOME);

        Instances testInstances = LoadUtils.loadInstances(new File(data, "test.tsv"));
        String[] results = naiveBayes.guess(testInstances.getInstances(), instances.getFeatures(),
                KaggleFeatures.FEATURE_INCOME);
        File out = new File(data, "naive_bayes_result.json");
        out.write(JsonOutput.toJson(["guesses": results]));
    }
}
