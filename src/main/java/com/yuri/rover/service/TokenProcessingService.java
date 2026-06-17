package com.yuri.rover.service;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

import java.io.*;
import java.util.*;

@Service
public class TokenProcessingService {
    private final RedisService redisService;
    private final Set<String> stopWords = new HashSet<>();
    private final StanfordCoreNLP pipeline;

    public TokenProcessingService(RedisService redisService) throws IOException, FileNotFoundException {
        this.redisService = redisService;

        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = new ClassPathResource("/data/stopwords.json").getInputStream();

        JsonNode node = mapper.readTree(inputStream);

        for (JsonNode item : node) {
            stopWords.add(item.asString());
        }


        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,pos,lemma");

        this.pipeline = new StanfordCoreNLP(props);
    }

    private String getPageContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.text();
    }

    private String lemmatizeWord(String word) {
        CoreDocument document = pipeline.processToCoreDocument(word);

        return document
                .tokens()
                .getFirst()
                .lemma();
    }

    public void processTokens(String url) throws IOException {
        String docContent = getPageContent(url);

        for (String word : docContent.split("\\W+")) {
            String normalizedWord = word.toLowerCase().trim();
            if (stopWords.contains(normalizedWord) || normalizedWord.isBlank() || normalizedWord.matches("[0-9]]")) {
                continue;
            }

            redisService.addToSet(lemmatizeWord(normalizedWord), url);
        }
    }

    public Set<String> queryTokens(String query) {
        Set<String> results = new HashSet<>();

        for (String word : query.strip().split(" ")) {
            redisService.getMembers(lemmatizeWord(word)).forEach((url) -> {
                results.add(url);
            });
        }

        return results;
    }
}
