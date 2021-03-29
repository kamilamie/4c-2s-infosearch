package tools;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

import java.util.*;

/*
    Токенизатор и лемматизатор, использующие библиотеку от Stanford NLP Group
    Из результата удаляются все найденные леммы, являющиеся не словами
 */
public class StanfordLemmatizer {

    protected StanfordCoreNLP pipeline;
    protected List<String> stopWordsList;

    public StanfordLemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        this.stopWordsList = IOHelper.readFromFileByStrings("files/stopwords.txt");

        // StanfordCoreNLP loads a lot of models, so you probably
        // only want to do this once per execution
        this.pipeline = new StanfordCoreNLP(props);
    }

    public HashMap<String, String> lemmatize(String documentText) {
        HashMap<String, String> lemmas = new HashMap<>();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);

        // run all Annotators on this text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                if (StringUtils.isAlpha(lemma) && !stopWordsList.contains(lemma.toLowerCase())) {
                    // Retrieve and add the lemma for each word into the list of lemmas
                    lemmas.put(token.originalText(), lemma);
                }
            }
        }
        return lemmas;
    }

    public List<String> getUniqueLemmas(String requestSentence) {
        List<String> result = new ArrayList<>();
        Annotation document = new Annotation(requestSentence);
        this.pipeline.annotate(document);
        List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
        for (CoreLabel token : tokens) {
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            if (StringUtils.isAlpha(lemma) && !stopWordsList.contains(lemma.toLowerCase()) && !result.contains(lemma)) {
                result.add(lemma);
            }
        }
        return result;
    }

    public String getLemmaOfWord(String word) {
        Annotation document = new Annotation(word);
        this.pipeline.annotate(document);
        List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
        String lemma = tokens.get(0).get(CoreAnnotations.LemmaAnnotation.class);
        if (StringUtils.isAlpha(lemma) && !stopWordsList.contains(lemma.toLowerCase())) {
            System.out.println("Main lemma: " + lemma);
            return lemma;
        }
        return null;
    }

    //вынести отсюда логику, которая не относится к лемматайзеру
    public double countTF(String mainWord, String documentText) {
        String mainLemma = getLemmaOfWord(mainWord);
        int matchWords = 0;
        int allWords = 0;

        Annotation document = new Annotation(documentText);
        this.pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.originalText();
                if (StringUtils.isAlpha(word) && !stopWordsList.contains(word.toLowerCase())) {
                    allWords++;
                }
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                if (lemma.equalsIgnoreCase(mainLemma)) {
                    matchWords++;
                }
            }
        }
        System.out.println("All words in document: " + allWords);
        System.out.println("Matching words in document for lemma " + mainLemma + ": " + matchWords);
        return (double) matchWords / allWords;
    }

    public HashMap<String, Double> countLemmasTF(String documentText) {
        int allWords = 0;
        HashMap<String, Double> lemmasMatches = new HashMap<>();
        Annotation document = new Annotation(documentText);
        this.pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.originalText();
                if (StringUtils.isAlpha(word) && !stopWordsList.contains(word.toLowerCase())) {
                    allWords++;
                }
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                if (StringUtils.isAlpha(lemma) && !stopWordsList.contains(lemma.toLowerCase())) {
                    double currCount = lemmasMatches.getOrDefault(lemma, 0.0);
                    lemmasMatches.put(lemma, currCount + 1);
                }
            }
        }
        for (Map.Entry<String, Double> entry : lemmasMatches.entrySet()) {
            entry.setValue(entry.getValue() / allWords);
        }
        return lemmasMatches;
    }
}