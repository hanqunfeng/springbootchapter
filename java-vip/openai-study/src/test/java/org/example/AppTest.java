package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.image.CreateImageRequest;
import io.reactivex.Flowable;
import org.example.common.azure.AzureOpenAiService;
import org.example.common.azure.pojo.AzureCreateImageRequest;
import org.example.common.azure.pojo.AzureCreateImageResult;
import org.example.common.azure.pojo.AzureCreateImageResultNew;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private static final AzureOpenAiService azureOpenAiService = new AzureOpenAiService();

    @Test
    void chat() throws JsonProcessingException {
        String json = "{\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"system\",\n" +
                "            \"content\": \"You are a helpful assistant.\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "            \"content\": \"Does Azure OpenAI support customer managed keys?\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": \"assistant\",\n" +
                "            \"content\": \"Yes, customer managed keys are supported by Azure OpenAI.\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "            \"content\": \"Do other Azure Cognitive Services support this too?\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ChatCompletionRequest chatCompletionRequest = AzureOpenAiService.defaultObjectMapper().readValue(json, ChatCompletionRequest.class);
        final ChatCompletionResult chatCompletionResult = azureOpenAiService.createChatCompletion(chatCompletionRequest);
        System.out.println(chatCompletionResult.toString());
    }

    @Test
    void chatStream() throws JsonProcessingException {
        String json = "{\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"system\",\n" +
                "            \"content\": \"You are a helpful assistant.\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "            \"content\": \"Does Azure OpenAI support customer managed keys?\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": \"assistant\",\n" +
                "            \"content\": \"Yes, customer managed keys are supported by Azure OpenAI.\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "            \"content\": \"Do other Azure Cognitive Services support this too?\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"stream\":true\n" +
                "}";
        ChatCompletionRequest chatCompletionRequest = AzureOpenAiService.defaultObjectMapper().readValue(json, ChatCompletionRequest.class);
        final Flowable<ChatCompletionChunk> chunkFlowable = azureOpenAiService.streamChatCompletion(chatCompletionRequest);
        chunkFlowable.blockingForEach(System.out::println);
    }

    /**
     * 估计很快就不再提供支持了
    */
    @Test
    void createImage() {
        AzureCreateImageRequest request = AzureCreateImageRequest.builder()
                .caption("a small dog,transparent background")
                .resolution("256x256").build();
        final AzureCreateImageResult azureCreateImageResult = azureOpenAiService.azureCreateImageResult(request);
        System.out.println(azureCreateImageResult);
    }

    /**
     * 最新版本2023-06-01
    */
    @Test
    void createImageNew() {
        CreateImageRequest request = CreateImageRequest.builder()
                .prompt("a small dog,transparent background")
                .size("256x256").build();
        final AzureCreateImageResultNew azureCreateImageResult = azureOpenAiService.azureCreateImageResultNew(request);
        System.out.println(azureCreateImageResult);
    }
}
