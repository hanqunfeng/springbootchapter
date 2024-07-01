package com.hanqf.controller;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 * Created by hanqf on 2024/7/1 11:01.
 */

@RestController
public class IndexController {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @GetMapping("/")
    public String index(){
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        TextSegment segment1 = TextSegment.from("I like football.");
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        final String add1 = embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("The weather is good today.");
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        final String add2 = embeddingStore.add(embedding2, segment2);
        Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();

        final EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)
                .build();
        final EmbeddingSearchResult<TextSegment> search = embeddingStore.search(embeddingSearchRequest);
        EmbeddingMatch<TextSegment> embeddingMatch = search.matches().get(0);

        System.out.println(embeddingMatch.score()); // 0.8144288063049316
        System.out.println(embeddingMatch.embedded().text()); // I like football.
        return "ok";
    }

}
