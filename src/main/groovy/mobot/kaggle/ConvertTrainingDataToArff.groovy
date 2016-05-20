package mobot.kaggle

import com.univocity.parsers.tsv.TsvParser
import com.univocity.parsers.tsv.TsvParserSettings

class ConvertTrainingDataToArff {
    public ConvertTrainingDataToArff() {
    }

    public static void main(String[] args) {
        File file = new File(args[0], 'training.tsv');
        String header = new File(args[0], 'Kaggle.arff.header').text;
        File out = new File(args[0], 'training.arff');
        out.write(header+"\n\n");
        out.append("@data\n");

        TsvParserSettings settings = new TsvParserSettings();
        //the file used in the example uses '\n' as the line separator sequence.
        //the line separator sequence is defined here to ensure systems such as MacOS and Windows
        //are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
        settings.getFormat().setLineSeparator("\n");
        // creates a TSV parser
        TsvParser parser = new TsvParser(settings);
        // parses all rows in one go.
        List<String[]> allRows = parser.parseAll(file);
        for(String[] row: allRows) {
            out.append(row.join(",")+"\n");
        }
    }
}
