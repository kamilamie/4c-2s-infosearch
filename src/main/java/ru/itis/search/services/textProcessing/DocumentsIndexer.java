package ru.itis.search.services.textProcessing;

import ru.itis.search.tools.IOHelper;
import ru.itis.search.tools.StanfordLemmatizer;

import java.io.File;
import java.util.*;

/**
 * Обработка текстовых документов: токенизация, лемматизация, индексация
 */
public class DocumentsIndexer {
    private static final String UNIQUE_TOKENS_PATH = "tokens.txt";
    private static final String UNIQUE_LEMMAS_PATH = "lemmas.txt";
    private static final String INDEXES_PATH = "invertedIndexes.txt";
    private static final String DOWNLOADS_PATH = System.getProperty("user.dir") + "/downloads";
    private static Map<String, List<String>> lemmasIndexes = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
    private static Map<String, List<String>> uniqueLemmas = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

    public static void main(String[] args) {
        new DocumentsIndexer().processDocuments();
    }

    public void processDocuments() {
        StanfordLemmatizer lemmatizer = new StanfordLemmatizer();

        File folder = new File(DOWNLOADS_PATH);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            System.out.println("Ensure /downloads folder is not empty");
            return;
        }
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println("File " + file.getName() + " is processing");
                String fileNumber = file.getName().split(".txt")[0];
                HashMap<String, String> lemmas = lemmatizer.lemmatize(IOHelper.readFromFile(file));
                for (Map.Entry<String, String> entry : lemmas.entrySet()) {
                    filterLemmasUniqueIndexes(fileNumber, entry);
                    filterLemmasUniqueTokens(entry);
                }
                System.out.println("File " + file.getName() + " processing ended");
            }
        }

        writeInfoForEachLemmaToFile(uniqueLemmas, UNIQUE_LEMMAS_PATH);
        writeInfoForEachLemmaToFile(lemmasIndexes, INDEXES_PATH);
    }

    private void filterLemmasUniqueTokens(Map.Entry<String, String> entry) {
        //если такая лемма еще не встречалась, завести для нее пустой список токенов
        if (!uniqueLemmas.containsKey(entry.getValue())) {
            uniqueLemmas.put(entry.getValue(), new ArrayList<String>());
        }
        //если такая лемма уже есть, но токен еще не встречался, добавить токен в ее список
        if (!uniqueLemmas.get(entry.getValue()).contains(entry.getKey())) {
            IOHelper.writeToFileFromNewLine(entry.getKey(), UNIQUE_TOKENS_PATH);
            uniqueLemmas.get(entry.getValue()).add(entry.getKey());
        }
    }

    private void filterLemmasUniqueIndexes(String fileNumber, Map.Entry<String, String> entry) {
        //если такая лемма еще не встречалась, завести для нее пустой список индексов
        if (!lemmasIndexes.containsKey(entry.getValue())) {
            lemmasIndexes.put(entry.getValue(), new ArrayList<String>());
        }
        //если такая лемма уже есть, но индекс файла еще не встречался, добавить индекс в ее список
        if (!lemmasIndexes.get(entry.getValue()).contains(fileNumber)) {
            lemmasIndexes.get(entry.getValue()).add(fileNumber);
        }
    }

    private void writeInfoForEachLemmaToFile (Map<String, List<String>> lemmasMap, String filename){
        //запись леммы и соответствующей ей информации в файл
        for (Map.Entry<String, List<String>> entry : lemmasMap.entrySet()){
            StringBuilder result = new StringBuilder(entry.getKey().toLowerCase() + ":");
            for (String s : entry.getValue()) {
                result.append(" ").append(s);
            }
            IOHelper.writeToFileFromNewLine(result.toString(), filename);
        }
    }
} 
