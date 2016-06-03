package mobot.kaggle

import com.univocity.parsers.tsv.TsvParser
import com.univocity.parsers.tsv.TsvParserSettings

/**
 * Created by Tony on 5/24/16.
 */
class LoadUtils {
    public interface FeatureUpdater {
        void update(Feature[] features);
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

    public static Instances loadInstances(File file) {
        KaggleFeatures features = new KaggleFeatures(new File(file.getParent(), "header.txt"));
        List<String[]> allRows = parser.parseAll(file);
        Instances instances = new Instances(features.getFeatures());
        instances.addAll(allRows);
        return instances;
    }

    public static Instances loadInstances(File file, FeatureUpdater updater) {
        KaggleFeatures features = new KaggleFeatures(new File(file.getParent(), "header.txt"));
        updater.update(features.features);
        List<String[]> allRows = parser.parseAll(file);
        Instances instances = new Instances(features.getFeatures());
        instances.addAll(allRows);
        return instances;
    }

}
