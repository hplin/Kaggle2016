package mobot.kaggle

import groovy.json.JsonOutput

class Perceptron {

    private double[][] weights;
    private double threshold;
    private Feature[] features;
    int classNumber
    int attributesNumber

    public Perceptron() {
    }

    public void reset(int classNumber, int attributesNumber) {
        this.classNumber = classNumber;
        this.attributesNumber = attributesNumber;
        weights = new double[classNumber][attributesNumber];
        for (int i = 0; i < classNumber; i++) {
            for (int j = 0; j < attributesNumber; j++) {
                weights[i][j] = 0.2 * Math.random() - 0.1;
            }
        }
    }

    public void train(Instances kaggleInstance, int expectedFeatureIndex, double learningRate) {

        features = kaggleInstance.getEnabledFeatures();
        int attributesNumber = features.length;
        int classNumber = 2; // binary classification
        reset(classNumber, attributesNumber);

        List<Double[]> allInstances = kaggleInstance.getInstances();
        double[][] instances = new double[allInstances.size()][attributesNumber - 1];
        int[] classValues = new double[allInstances.size()];
        int index = 0;
        for (Double[] row: allInstances) {
            classValues[index] = (int) row[expectedFeatureIndex];
            int col = 0;
            for (int i = 0; i < attributesNumber; i++) {
                if (features[i].index == expectedFeatureIndex) {
                    continue;
                }
                instances[index][col++] = row[features[i].index];
            }
            index++;
        }

        for (int i = 0; i < instances.length; i++) {
            train(instances[i], classValues[i], learningRate);
        }
    }

    public void train(double[] instance, int classValue, double learningRate) {
        double[] predict = new double[classNumber];
        for (int i = 0; i < predict.length; i++) {
            predict[i] = prediction(instance, i);
        }
        for (int i = 0; i < classNumber; i++) {
            double actual = i == classValue ? 1.0 : 0.0;
            double delta = (actual - predict[i]) * predict[i] * (1 - predict[i]);
            for (int j = 0; j < attributesNumber - 1; j++) {
                weights[i][j] += learningRate * delta * instance[j];
            }
            weights[i][attributesNumber - 1] += learningRate * delta;
        }
    }

    public double prediction(double[] instance, int classVallue) {
        double sum = 0.0;
        for (int i = 0; i < instance.length; i++) {
            sum += weights[classVallue][i] * instance[i];
        }
        sum += weights[classVallue][instance.length];
        return 1.0 / (1.0 + Math.exp(-sum));
    }

    public void test(Instances kaggleInstance, int expectedFeatureIndex) {
        features = kaggleInstance.getEnabledFeatures();
        int attributesNumber = features.length;
        int classNumber = 2; // binary classification

        List<Double[]> allInstances = kaggleInstance.getInstances();
        double[][] instances = new double[allInstances.size()][attributesNumber - 1];
        int[] classValues = new double[allInstances.size()];
        int index = 0;
        for (Double[] row: allInstances) {
            classValues[index] = (int) row[expectedFeatureIndex];
            int col = 0;
            for (int i = 0; i < attributesNumber; i++) {
                if (features[i].index == expectedFeatureIndex) {
                    continue;
                }
                instances[index][col++] = row[features[i].index];
            }
            index++;
        }

        int correct = 0;
        for (int i = 0; i < instances.length; i++) {
            int guess = guess(instances[i]);
            if (guess == classValues[i]) {
                correct++;
            }
        }
        println ("correct = "+ (correct/(double) instances.length));
    }

    public double[] vote(double[] instance) {
        double[] votes = new double[classNumber];
        for (int i = 0; i < classNumber; i++) {
            votes[i] = prediction(instance, i);
        }
        normalize(votes);
        return votes;
    }

    public int guess(double[] instance) {
        double[] votes = vote(instance);
        return votes[0] < votes[1] ? 0 : 1;
    }

    private void normalize(double[] data) {
        double sum = 0.0;
        for (double d: data) {
            sum += d;
        }
        if (sum != 0) {
            for (int i = 0; i < data.length; i++) {
                data[i] = data[i]/sum;
            }
        }
    }

    public static void main(String[] args) {
        File data = new File(args[0]);
        Perceptron perceptron = new Perceptron();
        println("-----Base-----");
        Instances instances;
        instances = LoadUtils.loadInstances(new File(data, "training.tsv"));
//        instances.normalize(KaggleFeatures.FEATURE_INCOME);
        double learningRate = 0.5;
        perceptron.train(instances, KaggleFeatures.FEATURE_INCOME, learningRate);
        perceptron.test(instances, KaggleFeatures.FEATURE_INCOME);
//        Instances testInstances = LoadUtils.loadInstances(new File(data, "test.tsv"));
//        String[] results = perceptron.guess(testInstances.getInstances(), instances.getFeatures(), KaggleFeatures.FEATURE_INCOME);
//        File out = new File(data, "perceptron_result.json");
//        out.write(JsonOutput.toJson(["guesses": results]));
    }
}
