package org.civicconnect.portal.util;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import java.util.*;

public class SentimentAnalyzer {

    private static final StanfordCoreNLP pipeline;

    // Initialize NLP pipeline
    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    // Analyze sentiment with civic context adjustment
    public static String analyze(String text) {
        if (text == null || text.trim().isEmpty()) return "Neutral";

        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);

        int score = 0;
        for (CoreSentence sentence : document.sentences()) {
            String sentiment = sentence.sentiment();
            switch (sentiment) {
                case "Positive" -> score++;
                case "Negative" -> score--;
                default -> {}
            }
        }

        // ✅ Post-processing: handle civic feedback phrases
        String lower = text.toLowerCase();

        // Positive reinforcement words
        String[] positiveKeywords = {"satisfied", "good", "great", "happy", "excellent", "amazing", "improved", "helpful", "thank"};
        for (String word : positiveKeywords) {
            if (lower.contains(word)) score++;
        }

        // Negative triggers
        String[] negativeKeywords = {"bad", "poor", "not working", "complaint", "issue", "problem", "dirty", "delay", "worse"};
        for (String word : negativeKeywords) {
            if (lower.contains(word)) score--;
        }

        // Sentiment decision
        if (score > 0) return "Positive";
        if (score < 0) return "Negative";
        return "Neutral";
    }
}
