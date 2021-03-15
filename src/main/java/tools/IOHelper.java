package tools;

import org.jsoup.nodes.Document;

import java.io.*;

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

    public static void writeToFileFromNewLine(String value, String path){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            writer.write(value);
            writer.newLine();
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
