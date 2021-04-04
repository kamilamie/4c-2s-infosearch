package ru.itis.search.services.search;

import ru.itis.search.tools.IOHelper;
import ru.itis.search.tools.StanfordLemmatizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BooleanSearch {

    private static final String INVERTED_INDEXES_FILE = "invertedIndexes.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter words separated by space tab: ");
        String request = sc.nextLine();
        StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
        List<String> requestLemmas = lemmatizer.lemmatizeOneSentence(request);

        System.out.println("The result of the request are pages with numbers: " + booleanSearch(requestLemmas));
    }

    /**
     * @param requestLemmas лемматизированные токены запроса из консоли
     * @return номера документов, где были найдены введенные слова
     * <p>
     * Пример ввода: unionism kingdom
     */
    private static String booleanSearch(List<String> requestLemmas) {
        List<String> indexes = IOHelper.readFromFileByStrings(INVERTED_INDEXES_FILE);
        List<String> suitableFiles = new ArrayList<>();
        for (String lemma : requestLemmas) {
            for (String str : indexes) {
                String lemmaCandidate = str.split(":")[0];
                if (lemmaCandidate.equalsIgnoreCase(lemma)) {
                    List<String> files = Arrays.asList(str.split(": ")[1].split(" "));
                    System.out.println("Word " + lemma + " was found in next files: " + files.toString());
                    if (suitableFiles.isEmpty()) {
                        suitableFiles = new ArrayList<>(files);
                    } else {
                        suitableFiles.removeIf(fileNumber -> !files.contains(fileNumber));
                    }
                }
            }
            System.out.println("Remaining files: " + suitableFiles.toString());
        }
        return suitableFiles.toString();
    }
} 
