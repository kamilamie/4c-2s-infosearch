package ru.itis.search.services.search;

import ru.itis.search.tools.IOHelper;
import ru.itis.search.tools.StanfordLemmatizer;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class VectorSearch {
    private static final String TF_IDF_PATH = "tf-idf.txt";
    private static final String IDF_PATH = "idf.txt";
    private static final String INDEXES_PATH = "invertedIndexes.txt";
    private static final int MAX_DOCUMENTS_COUNT = 100;
    private static final int MAX_RESULT_COUNT = 5;
    private static StanfordLemmatizer lemmatizer = new StanfordLemmatizer();

    public Set<String> search(String request) {
        HashMap<String, Double> euclidDistances = new HashMap<>();

        //обрабатываем поисковой запрос
        HashMap<String, Double> requestLemmasTfIdf = countTfIdfForSearchRequest(request);

        List<String> invertedIndexes = IOHelper.readFromFileByStrings(INDEXES_PATH);
        //проходимся по списку файлов, составляем векторы, если лемма встречается в файле, ставим ее tf-idf, если нет - 0.0
        for (int i = 1; i <= MAX_DOCUMENTS_COUNT; i++) {
            System.out.println("Processing of file: " + i);
            HashMap<String, Double> fileLemmasTfIdf = getTfIdfByFile(i + ".txt");
            double sum = 0.0;
            for (String lemmaStr : invertedIndexes) {
                String lemma = lemmaStr.split(": ")[0];
                List<String> indexes = Arrays.asList(lemmaStr.split(": ")[1].split(" "));
                //если лемма встречается в файле или в поисковом запросе, то считаем сумму для Евклидова расстояния
                if (indexes.contains(i) || requestLemmasTfIdf.containsKey(lemma)) {
                    sum += Math.pow(Optional.ofNullable(fileLemmasTfIdf.get(lemma)).orElse(0.0)
                            - Optional.ofNullable(requestLemmasTfIdf.get(lemma)).orElse(0.0), 2);
                }
            }
            //берем корень, сохраняем евклидово расстояние для текущего файла
            euclidDistances.put(i + "", Math.sqrt(sum));
        }


        //ищем наименьшее евклидово расстояние
        LinkedHashMap<String, Double> topFiveResults =
                euclidDistances.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .limit(MAX_RESULT_COUNT)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        System.out.println("Top five results: " + topFiveResults);
        return topFiveResults.keySet();
    }

    //метод для подсчета эквивалентных tf-idf для слов из поискового запроса
    public HashMap<String, Double> countTfIdfForSearchRequest(String request) {
        HashMap<String, Double> requestLemmasTF = lemmatizer.countLemmasTF(request);
        List<String> idfs = IOHelper.readFromFileByStrings(IDF_PATH);
        for (String idf : idfs) {
            String word = idf.split(" ")[0];
            if (requestLemmasTF.containsKey(word)) {
                double idfValue = Double.parseDouble(idf.split(" ")[1]);
                requestLemmasTF.put(word, requestLemmasTF.get(word) * idfValue);
            }
        }
        return requestLemmasTF;
    }

    //метод для получения tf-idf для каждой леммы определенного файла
    public HashMap<String, Double> getTfIdfByFile(String filename) {
        HashMap<String, Double> tfIdfMap = new HashMap<>();
        List<String> allTfIdfs = IOHelper.readFromFileByStrings(TF_IDF_PATH);
        for (String fileTfIdf : allTfIdfs) {
            String name = fileTfIdf.split(":")[0];
            if (name.equals(filename)) {
                String str = fileTfIdf.split(":")[1];
                String[] tfIdfs = str.split(";");
                for (String tfidf : tfIdfs) {
                    tfIdfMap.put(tfidf.split(" ")[0].toLowerCase(), Double.parseDouble(tfidf.split(" ")[1]));
                }
            }
        }
        return tfIdfMap;
    }
} 
