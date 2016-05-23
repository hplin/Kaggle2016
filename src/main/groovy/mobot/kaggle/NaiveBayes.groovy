package mobot.kaggle

import com.univocity.parsers.tsv.TsvParser
import com.univocity.parsers.tsv.TsvParserSettings
import groovy.json.JsonBuilder
import groovy.json.JsonOutput

class NaiveBayes {
    public NaiveBayes() {
    }
    final static TsvParser parser;
    static {
        TsvParserSettings settings = new TsvParserSettings();
        //the file used in the example uses '\n' as the line separator sequence.
        //the line separator sequence is defined here to ensure systems such as MacOS and Windows
        //are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
        settings.getFormat().setLineSeparator("\n");
        // creates a TSV parser
        parser = new TsvParser(settings);
    }

    private static Instances loadInstances(File file) {
        KaggleFeatures features = new KaggleFeatures(new File(file.getParent(), "header.txt"));
        List<String[]> allRows = parser.parseAll(file);
        Instances instances = new Instances(features.getFeatures());
        instances.addAll(allRows);
        return instances;
    }

    public static void main(String[] args) {
        File data = new File(args[0]);

        //f92c4298-15ef-4419-a387-a2ac66dfa2ca
        // parses all rows in one go.
        println("-----Base-----");
        Instances instances = loadInstances(new File(data, "training.tsv"));
        instances.train(KaggleFeatures.FEATURE_INCOME);
        instances.test(instances);
//
//        println("-----log value for fnlwgt-----");
//        instances = loadInstances(new File(data, "training.tsv"));
//        for (Double[] values: instances.getInstances()) {
//            values[KaggleFeatures.FEATURE_FNLWGT] = Math.log(values[KaggleFeatures.FEATURE_FNLWGT]);
//        }
//        instances.train(KaggleFeatures.FEATURE_INCOME);
//        instances.test(instances);

        println("-----ignore-----");
        instances = loadInstances(new File(data, "training.tsv"));
        int[] ignores = [
            KaggleFeatures.FEATURE_FNLWGT,
            KaggleFeatures.FEATURE_EDUCATION_NUM,
            KaggleFeatures.FEATURE_CAPITAL_GAIN,
            KaggleFeatures.FEATURE_CAPITAL_LOSS
        ];
        for (int i = 0; i < ignores.length; i++) {
            instances.getFeature(ignores[i]).enabled = false;
        }
        instances.train(KaggleFeatures.FEATURE_INCOME);
        instances.test(instances);

//        for (int i = 0; i < ignores.length; i++) {
//            println("----ignore:" + instances.getFeature(ignores[i]).name);
//            instances = loadInstances(new File(data, "training.tsv"));
//            instances.getFeature(ignores[i]).enabled = false;
//            instances.train(KaggleFeatures.FEATURE_INCOME);
//            instances.test(instances);
//        }

        Instances testInstances = loadInstances(new File(data, "test.tsv"));
        String[] results = instances.guess(testInstances);
        File out = new File(data, "test_result.json");
        out.write(JsonOutput.toJson(["guesses": results]));
    }
}
