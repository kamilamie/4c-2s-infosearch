package ru.itis.search.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itis.search.services.search.VectorSearch;
import ru.itis.search.tools.IOHelper;

import java.util.*;

@Controller
public class SearchController {

    private static final String INDEXES_PATH = "index.txt";

    @GetMapping("/search")
    public String getAssignmentsPage(){
        return "search";
    }

    @PostMapping("/search")
    public String search(@RequestParam String request, ModelMap modelMap){
        Set<String> topFiveResults = new VectorSearch().search(request);
        Map<String, String> linksIndexes = getLinksIndexesFromFile(INDEXES_PATH);
        List<String> links = new ArrayList<>();

        for (String result : topFiveResults) {
            links.add(linksIndexes.get(result));
        }
        modelMap.addAttribute("results", links);
        return "search";
    }

    private Map<String, String> getLinksIndexesFromFile(String filename) {
        Map<String, String> links = new HashMap<>();
        List<String> indexes = IOHelper.readFromFileByStrings(filename);
        for (String str : indexes) {
            links.put(str.split(" ")[0], str.split(" ")[1]);
        }
        return links;
    }

}
