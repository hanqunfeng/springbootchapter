package com.hanqf.demo.milvus;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Random;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/18 11:00.
 */

@Component
public class MilvusEmbeddingStoreTool {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private OpenAiEmbeddingModel embeddingModel;

    public void test() {
//        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        TextSegment segment1 = TextSegment.from("I like football.");
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("The weather is good today.");
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);

        Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
//        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
//        EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);


        final EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)
                .build();
        final EmbeddingSearchResult<TextSegment> search = embeddingStore.search(embeddingSearchRequest);
        EmbeddingMatch<TextSegment> embeddingMatch = search.matches().get(0);

        System.out.println(embeddingMatch.score()); // 0.8144287765026093
        System.out.println(embeddingMatch.embedded().text()); // I like football.
    }

    public void init() {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.pdf");
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("/Users/hanqf/Desktop", pathMatcher, new ApacheTikaDocumentParser());

        //timeout
//        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
//                .documentSplitter(DocumentSplitters.recursive(500, 100))
//                .embeddingModel(embeddingModel)
//                .embeddingStore(embeddingStore)
//                .build();
//        embeddingStoreIngestor.ingest(documents);

        final DocumentSplitter documentSplitter = DocumentSplitters.recursive(500, 100);
        final List<TextSegment> textSegments = documentSplitter.splitAll(documents);
        Random random = new Random();
        textSegments.forEach(textSegment -> {
            textSegment.metadata().put("key", "value" + random.nextInt(4));
            final Embedding embedding = embeddingModel.embed(textSegment).content();
            embeddingStore.add(embedding, textSegment);
        });


    }
}
