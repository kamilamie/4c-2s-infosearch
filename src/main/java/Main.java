import tools.StanfordLemmatizer;

import java.io.*;
import java.util.*;

public class Main {
    private static final String outputPath = "output.txt";

    public static void main(String[] args) {
        tokenize();
    }

    public static void tokenize() {
        ArrayList<String> tokens = new ArrayList<>();
        File folder = new File(System.getProperty("user.dir") + "/downloads");
        File[] listOfFiles = folder.listFiles();

        StanfordLemmatizer lemmatizer = new StanfordLemmatizer();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println("==================================File number " + file.getName()+ "=======================================");
                List<String> lemmas = lemmatizer.lemmatize(readFromFile(file));
                for (String l: lemmas){
                    if(!tokens.contains(l))
                        tokens.add(l);
                }
            }
        }
        Collections.sort(tokens);
        for (String l: tokens){
            writeToken(l);
        }
    }

    private static void writeToken(String token) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true));
            writer.write(token);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFromFile(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStringBuilder.toString();
    }
} 
