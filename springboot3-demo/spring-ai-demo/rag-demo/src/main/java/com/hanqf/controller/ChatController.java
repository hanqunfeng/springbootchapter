package com.hanqf.controller;

import com.hanqf.service.MockWeatherService;
import com.hanqf.utils.OpenAiTokenUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.*;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 * Created by hanqf on 2024/5/17 16:24.
 */

@Slf4j
@RestController
public class ChatController {
    @Autowired
    private OpenAiChatClient chatClient;
    @Autowired
    private EmbeddingClient embeddingClient;
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private OpenAiImageClient imageClient;
    @Autowired
    private OpenAiAudioTranscriptionClient audioTranscriptionClient;

    @Value("classpath:speech.mp3")
    private Resource audioFile;

    @Value("classpath:高并发秒杀系统的设计与实现.pdf") // This is the pdf document to load
    private Resource resourcePdf;

    @GetMapping("/ai/audioToText")
    public AudioTranscriptionResponse audioToText() {
        System.out.println(audioFile.exists());
        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .withModel("whisper-1") //默认值 whisper-1
                .withLanguage("zh") //音频文件的语言，不设置会自动识别
                .withTemperature(0f) // 默认值 0,0~1
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.JSON) //默认值 json
                .build();
        AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
        return audioTranscriptionClient.call(transcriptionRequest);

    }

    @GetMapping("/ai/image")
    public ImageResponse generateImage(@RequestParam(value = "message", defaultValue = "A light cream colored mini golden doodle") String message) {
        return imageClient.call(
                new ImagePrompt("A light cream colored mini golden doodle",
                        OpenAiImageOptions.builder()
                                .withModel(OpenAiImageApi.ImageModel.DALL_E_3.getValue())
                                .withQuality("hd")
                                .withN(1)
                                .withHeight(1024)
                                .withWidth(1024)
                                .withResponseFormat("url")
                                .build())
        );
    }

    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatClient.call(message);
    }

    @GetMapping("/ai/generateTemplate")
    public String generateTemplate() {
        String adjective = "funny";
        String topic = "chickens";
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

    @GetMapping("/ai/generateTemplate2")
    public String generateTemplate2() {
        String userText = """
                Tell me about three famous pirates from the Golden Age of Piracy and why they did.
                Write at least a sentence for each pirate.
                """;

        Message userMessage = new UserMessage(userText);

        String systemText = """
                You are a helpful AI assistant that helps people find information.
                Your name is {name}
                You should reply to the user's request with your name and also in the style of a {voice}.
                """;

        String name = "HanQF";
        String voice = "female";
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));

        List<Message> messageList = List.of(userMessage, systemMessage);
        System.out.println(OpenAiTokenUtil.countMessageTokens("gpt-3.5-turbo", messageList));
        Prompt prompt = new Prompt(messageList);

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt).map(chatResponse -> {
            if (chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null && chatResponse.getResult().getOutput().getContent() != null) {
//                System.out.print(chatResponse.getResult().getOutput().getContent());
                return chatResponse.getResult().getOutput().getContent();
            } else {
                return "";
            }
        });
    }

    @GetMapping(value = "/ai/generateStream2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream2(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt);
    }

    @GetMapping(value = "/ai/generateStream3")
    public String generateStream3(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt).collectList().block()
                .stream()
                .map(ChatResponse::getResults)
                .flatMap(List::stream)
                .map(Generation::getOutput)
                .map(AssistantMessage::getContent)
                .collect(Collectors.joining());
    }

    @GetMapping("/ai/generateFunction")
    public ChatResponse generateFunction(@RequestParam(value = "message", defaultValue = "北京、石家庄的天气怎么样，适合穿什么衣服？温度保留1位小数。") String message) {

        UserMessage userMessage = new UserMessage(message);

        var promptOptions = OpenAiChatOptions.builder()
                .withFunctionCallbacks(List.of(
                        FunctionCallbackWrapper.builder(new MockWeatherService())
                                .withName("CurrentWeather")
                                .withDescription("Get the weather in location").build()))
                .build();

        return chatClient.call(new Prompt(List.of(userMessage), promptOptions));

    }

    @GetMapping("/ai/generateFunction2")
    public ChatResponse generateFunction2(@RequestParam(value = "message", defaultValue = "北京、石家庄的天气怎么样，适合穿什么衣服？温度保留1位小数。") String message) {

        UserMessage userMessage = new UserMessage(message);

        var promptOptions = OpenAiChatOptions.builder()
                .withFunctions(Set.of("currentWeather2")) //注册的bean的名称
                .build();

        return chatClient.call(new Prompt(List.of(userMessage), promptOptions));

    }

    @GetMapping("/ai/generateFunction3")
    public ChatResponse generateFunction3(@RequestParam(value = "message", defaultValue = "北京、石家庄的天气怎么样，适合穿什么衣服？温度保留1位小数。") String message) {

        UserMessage userMessage = new UserMessage(message);

        var promptOptions = OpenAiChatOptions.builder()
                .withFunctions(Set.of("currentWeather3")) //注册的bean的名称
                .build();

        return chatClient.call(new Prompt(List.of(userMessage), promptOptions));

    }


    @GetMapping("/ai/embedding")
    public List<Double> embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        EmbeddingResponse embeddingResponse = this.embeddingClient.embedForResponse(List.of(message));
        return embeddingResponse.getResult().getOutput();
    }

    @GetMapping("/ai/rag_add")
    public List<Document> rag_add() {
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("country", "CHN")),
                new Document("The World is Big and Salvation Lurks Around the Corner", Map.of("country", "CHN")),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("country", "USA")));

        // Add the documents to PGVector
        vectorStore.add(documents);

        return documents;
    }

    @GetMapping("/ai/rag_add_pdf")
    public List<Document> rag_add_pdf() {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(this.resourcePdf,
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfBottomTextLinesToDelete(3)
                                .withNumberOfTopPagesToSkipBeforeDelete(1)
                                .build())
                        .withPagesPerDocument(1)
                        .build());

        final List<Document> documents = pdfReader.get();

        var tokenTextSplitter = new TokenTextSplitter();

        log.info(
                "Parsing document, splitting, creating embeddings and storing in vector store...  this will take a while.");
        this.vectorStore.accept(tokenTextSplitter.apply(documents));
        log.info("Done parsing document, splitting, creating embeddings and storing in vector store");

        return documents.subList(0, 5);

    }

    @GetMapping("/ai/rag_search_pdf")
    public List<Document> rag_search_pdf(@RequestParam(value = "message", defaultValue = "秒杀系统架构") String message) {
        // Retrieve documents similar to a query
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.query("message")
                        .withTopK(5)
        );

        return results;
    }

    @GetMapping("/ai/rag_search")
    public List<Document> rag_search() {
        // Retrieve documents similar to a query
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.query("Spring")
                        // 这里过滤的key实际上是metadata里面的key，封装表达式时会为其自动加上metadata['country']
                        .withFilterExpression("country == 'CHN'")
                        .withTopK(5)
        );


//        List<Document> results = vectorStore.similaritySearch("Spring");


        return results;
    }


    @Data
    public static class ActorsFilms {

        private String actor;

        private List<String> movies;

    }

    @GetMapping("/ai/outputParser")
    public ActorsFilms outputParser(@RequestParam(value = "actor", defaultValue = "Jeff Bridges") String actor) {
        var outputParser = new BeanOutputParser<>(ActorsFilms.class);

        String userMessage =
                """
                        Generate the filmography for the actor {actor}.
                        {format}
                        """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("actor", actor, "format", outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();

        return outputParser.parse(generation.getOutput().getContent());
    }

}
