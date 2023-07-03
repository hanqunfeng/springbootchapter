package org.example.common.azure;


import com.theokanning.openai.DeleteResult;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.edit.EditRequest;
import com.theokanning.openai.edit.EditResult;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.engine.Engine;
import com.theokanning.openai.file.File;
import com.theokanning.openai.finetune.FineTuneEvent;
import com.theokanning.openai.finetune.FineTuneRequest;
import com.theokanning.openai.finetune.FineTuneResult;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.moderation.ModerationRequest;
import com.theokanning.openai.moderation.ModerationResult;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.example.common.azure.pojo.AzureCreateImageRequest;
import org.example.common.azure.pojo.AzureCreateImageResponseNotStarted;
import org.example.common.azure.pojo.AzureCreateImageResult;
import org.example.common.azure.pojo.AzureCreateImageResultNew;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/24 13:43.
 */


public interface AzureOpenAiApi {

    @GET("openai/models?api-version=2022-12-01")
    Single<OpenAiResponse<Model>> listModels();

    @GET("openai/models/{model_id}?api-version=2022-12-01")
    Single<Model> getModel(@Path("model_id") String modelId);

    @POST("openai/deployments/{model_id}/completions")
    Single<CompletionResult> createCompletion(@Body CompletionRequest request, @Path("model_id") String modelId, @Query("api-version") String apiVersion);

    @Streaming
    @POST("openai/deployments/{model_id}/completions")
    Call<ResponseBody> createCompletionStream(@Body CompletionRequest request, @Path("model_id") String modelId, @Query("api-version") String apiVersion);

    @POST("openai/deployments/{model_id}/chat/completions")
    Single<ChatCompletionResult> createChatCompletion(@Body ChatCompletionRequest request, @Path("model_id") String modelId, @Query("api-version") String apiVersion);

    @Streaming
    @POST("openai/deployments/{model_id}/chat/completions")
    Call<ResponseBody> createChatCompletionStream(@Body ChatCompletionRequest request, @Path("model_id") String modelId, @Query("api-version") String apiVersion);

    @POST("dalle/text-to-image")
    Single<AzureCreateImageResponseNotStarted> createAuzreImageNotStarted(@Body AzureCreateImageRequest request, @Query("api-version") String apiVersion);

    @POST("dalle/text-to-image")
    Response<AzureCreateImageResponseNotStarted> createAuzreImageNotStartedResponse(@Body AzureCreateImageRequest request, @Query("api-version") String apiVersion);

    @GET("dalle/text-to-image/operations/{id}")
    Single<AzureCreateImageResult> createAuzreImageByNotStartedId(@Path("id") String id, @Query("api-version") String apiVersion);


    @POST("openai/images/generations:submit")
    Single<AzureCreateImageResponseNotStarted> createAuzreImageNotStartedNew(@Body CreateImageRequest request, @Query("api-version") String apiVersion);

    @GET("openai/operations/images/{id}")
    Single<AzureCreateImageResultNew> createAuzreImageByNotStartedIdNew(@Path("id") String id, @Query("api-version") String apiVersion);


    @Deprecated
    @POST("/v1/engines/{engine_id}/completions")
    Single<CompletionResult> createCompletion(@Path("engine_id") String engineId, @Body CompletionRequest request);

    @POST("/v1/edits")
    Single<EditResult> createEdit(@Body EditRequest request);

    @Deprecated
    @POST("/v1/engines/{engine_id}/edits")
    Single<EditResult> createEdit(@Path("engine_id") String engineId, @Body EditRequest request);

    @POST("deployments/lexing-test/embeddings?api-version=2023-03-15-preview")
    Single<EmbeddingResult> createEmbeddings(@Body EmbeddingRequest request);

    @Deprecated
    @POST("/v1/engines/{engine_id}/embeddings")
    Single<EmbeddingResult> createEmbeddings(@Path("engine_id") String engineId, @Body EmbeddingRequest request);

    @GET("/v1/files")
    Single<OpenAiResponse<File>> listFiles();

    @Multipart
    @POST("/v1/files")
    Single<File> uploadFile(@Part("purpose") RequestBody purpose, @Part MultipartBody.Part file);

    @DELETE("/v1/files/{file_id}")
    Single<DeleteResult> deleteFile(@Path("file_id") String fileId);

    @GET("/v1/files/{file_id}")
    Single<File> retrieveFile(@Path("file_id") String fileId);

    @POST("/v1/fine-tunes")
    Single<FineTuneResult> createFineTune(@Body FineTuneRequest request);

    @POST("/v1/completions")
    Single<CompletionResult> createFineTuneCompletion(@Body CompletionRequest request);

    @GET("/v1/fine-tunes")
    Single<OpenAiResponse<FineTuneResult>> listFineTunes();

    @GET("/v1/fine-tunes/{fine_tune_id}")
    Single<FineTuneResult> retrieveFineTune(@Path("fine_tune_id") String fineTuneId);

    @POST("/v1/fine-tunes/{fine_tune_id}/cancel")
    Single<FineTuneResult> cancelFineTune(@Path("fine_tune_id") String fineTuneId);

    @GET("/v1/fine-tunes/{fine_tune_id}/events")
    Single<OpenAiResponse<FineTuneEvent>> listFineTuneEvents(@Path("fine_tune_id") String fineTuneId);

    @DELETE("/v1/models/{fine_tune_id}")
    Single<DeleteResult> deleteFineTune(@Path("fine_tune_id") String fineTuneId);

    @POST("/v1/images/generations")
    Single<ImageResult> createImage(@Body CreateImageRequest request);

    @POST("/v1/images/edits")
    Single<ImageResult> createImageEdit(@Body RequestBody requestBody);

    @POST("/v1/images/variations")
    Single<ImageResult> createImageVariation(@Body RequestBody requestBody);

    @POST("/v1/moderations")
    Single<ModerationResult> createModeration(@Body ModerationRequest request);

    @Deprecated
    @GET("v1/engines")
    Single<OpenAiResponse<Engine>> getEngines();

    @Deprecated
    @GET("/v1/engines/{engine_id}")
    Single<Engine> getEngine(@Path("engine_id") String engineId);
}
