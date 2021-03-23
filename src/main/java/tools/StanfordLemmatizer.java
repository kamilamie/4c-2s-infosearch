package tools;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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
        File stopWordsFile = new File("files/stopwords.txt");
        this.stopWordsList = IOHelper.readFromFileByStrings(stopWordsFile);

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
                if (StringUtils.isAlpha(lemma)&& !stopWordsList.contains(lemma.toLowerCase())) {
                    // Retrieve and add the lemma for each word into the list of lemmas
                    lemmas.put(token.originalText(), lemma);
                }
            }
        }
        return lemmas;
    }
}