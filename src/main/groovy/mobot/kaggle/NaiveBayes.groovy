package mobot.kaggle

import com.univocity.parsers.tsv.TsvParser
import com.univocity.parsers.tsv.TsvParserSettings

class NaiveBayes {
    public NaiveBayes() {
    }

    public static void main(String[] args) {
        File data = new File(args[0]);
        KaggleFeatures features = new KaggleFeatures(new File(data, "header.txt"));

        TsvParserSettings settings = new TsvParserSettings();
        //the file used in the example uses '\n' as the line separator sequence.
        //the line separator sequence is defined here to ensure systems such as MacOS and Windows
        //are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
        settings.getFormat().setLineSeparator("\n");
        // creates a TSV parser
        TsvParser parser = new TsvParser(settings);
        // parses all rows in one go.
        List<String[]> allRows = parser.parseAll(new File(data, "training.tsv"));
        Instances instances = new Instances(features.getFeatures());
        instances.addAll(allRows);
        instances.train(features.features.length - 1);

        instances.test(instances);
    }
}
