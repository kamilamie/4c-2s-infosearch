package tools;

import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
    Класс, инкапсулирующий работу с файлами (запись/чтение)
 */
public class IOHelper {

    public static String readFromFile(File file){
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStringBuilder.toString();
    }

    public static List<String> readFromFileByStrings(String filepath){
        File file = new File(filepath);
        List<String> strings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                strings.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public static void writeToFileFromNewLine(String value, String path){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            writer.newLine();
            writer.write(value);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String value, String path){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            writer.write(value);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile(Document document, String path) {
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.println(document.text());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
} 
