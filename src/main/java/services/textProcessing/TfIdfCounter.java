package services.textProcessing;

import tools.IOHelper;
import tools.StanfordLemmatizer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TfIdfCounter {
    private static final String TF_IDF_PATH = "files/tf-idf.txt";
    private static final String INDEXES_PATH = "files/invertedIndexes.txt";
    private static final String DOWNLOADS_PATH = System.getProperty("user.dir") + "/downloads";

    public static void main(String[] args) {
        new TfIdfCounter().countTfIdf();
    }

    public void countTfIdf() {
        StanfordLemmatizer lemmatizer = new StanfordLemmatizer();

        File folder = new File(DOWNLOADS_PATH);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            System.out.println("Ensure /downloads folder is not empty");
            return;
        }
        List<String> invertedIndexes = IOHelper.readFromFileByStrings(INDEXES_PATH);
        HashMap<String, Double> wordsIdf = new HashMap<>();
        int documentsCount = listOfFiles.length;

        for (String wordIndexes : invertedIndexes) {
            String word = wordIndexes.split(": ")[0];
            int matchDocuments = wordIndexes.split(": ")[1].split(" ").length;
            wordsIdf.put(word, (double) documentsCount / matchDocuments);
        }

        for (File file : listOfFiles) {
            System.out.println(file.getName());
            if (file.isFile()) {
                IOHelper.writeToFileFromNewLine("===============================File "
                        + file.getName() + "===============================", TF_IDF_PATH);
                HashMap<String, Double> fileLemmasTF = lemmatizer.countLemmasTF(IOHelper.readFromFile(file));
                for (Map.Entry<String, Double> entry : fileLemmasTF.entrySet()) {
                    double tf = entry.getValue();
                    if (wordsIdf.get(entry.getKey().toLowerCase()) == null) {
                        continue;
                    }
                    double idf = wordsIdf.get(entry.getKey().toLowerCase());
                    IOHelper.writeToFileFromNewLine(entry.getKey() + " " + idf + " " + tf * idf, TF_IDF_PATH);
                }
            }
        }
    }
} 
