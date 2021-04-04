package ru.itis.search.services.crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.itis.search.tools.IOHelper;

import java.io.*;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Краулер для сайта Википедии https://en.wikipedia.org/wiki/
 * Ищет ссылки на статьи, извлекает текст со страниц
 * и сохраняет его в файл с соответствующим порядковым номером в папку /downloads/.
 *
 * MAX_LINKS_COUNT - задает количество нужных нам страниц
 * index.txt - хранит соответствия ссылки и порядкового номера файла
 */

public class Crawler {
    private static final int MAX_LINKS_COUNT = 100;
    private static final String indexesPath = "index.txt";
    private HashSet<String> links;

    private static int CURRENT_LINKS_COUNT = 0;

    public Crawler() {
        links = new HashSet<String>();
    }

    public static void main(String[] args) {
        new Crawler().getPageLinks("https://en.wikipedia.org/wiki/Main_Page");
    }

    public void getPageLinks(String URL) {
        String baseUrl = "https://en.wikipedia.org/wiki/";
        if (!URL.contains(baseUrl)) {
            return;
        }
        String articleName = URL.split(baseUrl)[1];
        if (!links.contains(URL) && CURRENT_LINKS_COUNT < MAX_LINKS_COUNT && Pattern.matches("\\w+", articleName)) {
            try {
                links.add(URL);

                Document document = Jsoup.connect(URL).get();
                CURRENT_LINKS_COUNT++;
                IOHelper.saveFile(document, System.getProperty("user.dir") + "/downloads/" + CURRENT_LINKS_COUNT + ".txt");
                IOHelper.writeToFileFromNewLine(CURRENT_LINKS_COUNT + " " + URL, indexesPath);
                System.out.println(">> #" + CURRENT_LINKS_COUNT + " [" + URL + "]");

                Elements linksOnPage = document.select("a[href]");
                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }
}