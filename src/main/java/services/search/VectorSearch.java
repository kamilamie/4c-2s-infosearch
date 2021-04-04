package services.search;

import tools.IOHelper;
import tools.StanfordLemmatizer;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VectorSearch {
    private static final String TF_IDF_PATH = "files/tf-idf.txt";
    private static final String IDF_PATH = "files/idf.txt";
    private static final String INDEXES_PATH = "files/invertedIndexes.txt";
    private static final String DOWNLOADS_PATH = System.getProperty("user.dir") + "/downloads";
    private static final int MAX_RESULT_COUNT = 5;
    private static StanfordLemmatizer lemmatizer = new StanfordLemmatizer();

    public static void main(String[] args) {
        search("unionism kingdom");
    }

    public static void search(String request) {
        HashMap<String, Double> euclidDistances = new HashMap<>();

        //обрабатываем поисковой запрос
        HashMap<String, Double> requestLemmasTfIdf = countTfIdfForSearchRequest(request);

        List<String> invertedIndexes = IOHelper.readFromFileByStrings(INDEXES_PATH);
        File folder = new File(DOWNLOADS_PATH);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            System.out.println("Ensure /downloads folder is not empty");
            return;
        }
        //проходимся по списку файлов, составляем векторы, если лемма встречается в файле, ставим ее tf-idf, если нет - 0.0
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println("Processing of file: " + file.getName());
                HashMap<String, Double> fileLemmasTfIdf = getTfIdfByFile(file.getName());
                double sum = 0.0;
                String fileNumber = file.getName().split(".txt")[0];
                for (String lemmaStr : invertedIndexes) {
                    String lemma = lemmaStr.split(": ")[0];
                    List<String> indexes = Arrays.asList(lemmaStr.split(": ")[1].split(" "));
                    //если лемма встречается в файле или в поисковом запросе, то считаем сумму для Евклидова расстояния
                    if (indexes.contains(fileNumber) || requestLemmasTfIdf.containsKey(lemma)) {
                        sum += Math.pow(Optional.ofNullable(fileLemmasTfIdf.get(lemma)).orElse(0.0)
                                - Optional.ofNullable(requestLemmasTfIdf.get(lemma)).orElse(0.0), 2);
                    }
                }
                //берем корень, сохраняем евклидово расстояние для текущего файла
                euclidDistances.put(fileNumber, Math.sqrt(sum));
            }
        }
        //ищем наименьшее евклидово расстояние
        Map<String, Double> topFiveResults =
                euclidDistances.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .limit(MAX_RESULT_COUNT)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        System.out.println(topFiveResults);
    }

    //метод для подсчета эквивалентных tf-idf для слов из поискового запроса
    public static HashMap<String, Double> countTfIdfForSearchRequest(String request) {
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
    public static HashMap<String, Double> getTfIdfByFile(String filename) {
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
