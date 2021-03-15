import tools.IOHelper;
import tools.StanfordLemmatizer;

import java.io.*;
import java.util.*;

/*
    Обработка и выделение токенов (включая лемматизацию) для выкаченных страниц из папки /downloads
 */
public class Main {
    private static final String lemmasPath = "lemmas.txt";


    public static void main(String[] args) {
        tokenize();
    }

    public static void tokenize() {
        File folder = new File(System.getProperty("user.dir") + "/downloads");
        File[] listOfFiles = folder.listFiles();

        StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
        Map<String, List<String>> uniqueLemmas = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

        if (listOfFiles == null) {
            System.out.println("Ensure /downloads folder is not empty");
            return;
        }
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println("File " + file.getName() + " is processing");
                HashMap<String, String> lemmas = lemmatizer.lemmatize(IOHelper.readFromFile(file));
                for (Map.Entry<String, String> entry : lemmas.entrySet()) {
                    if (!uniqueLemmas.containsKey(entry.getValue())) {
                        uniqueLemmas.put(entry.getValue(), new ArrayList<String>());
                    }
                    if (!uniqueLemmas.get(entry.getValue()).contains(entry.getKey())) {
                        uniqueLemmas.get(entry.getValue()).add(entry.getKey());
                    }
                }
                System.out.println("File " + file.getName() + " processing ended");
            }
        }

        uniqueLemmas = new TreeMap<String, List<String>>(uniqueLemmas);
        for (Map.Entry<String, List<String>> entry : uniqueLemmas.entrySet()) {
            StringBuilder result = new StringBuilder(entry.getKey());
            for (String s : entry.getValue()) {
                result.append(" ").append(s);
            }
            IOHelper.writeToFileFromNewLine(result.toString(), lemmasPath);
        }
    }
} 
