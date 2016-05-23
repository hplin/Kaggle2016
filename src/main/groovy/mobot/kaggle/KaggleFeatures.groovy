package mobot.kaggle

/**
 * Created by tlin1 on 5/20/16.
 */
class KaggleFeatures {
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
                features.add(new Feature(name, Feature.Type.Numberic, null));
            } else {
                features.add(new Feature(name, Feature.Type.Enumeric, type));
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
