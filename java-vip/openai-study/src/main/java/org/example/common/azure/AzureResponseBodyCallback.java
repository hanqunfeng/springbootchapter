package org.example.common.azure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiError;
import com.theokanning.openai.OpenAiHttpException;
import io.reactivex.FlowableEmitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <h1>流式响应回调处理逻辑</h1>
 * Created by hanqf on 2023/4/14 16:54.
 */


public class AzureResponseBodyCallback implements Callback<ResponseBody> {
    private FlowableEmitter<AzureSSE> emitter;
    private boolean emitDone;

    public AzureResponseBodyCallback(FlowableEmitter<AzureSSE> emitter, boolean emitDone) {
        this.emitter = emitter;
        this.emitDone = emitDone;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        BufferedReader reader = null;

        try {
            if (!response.isSuccessful()) {
                HttpException e = new HttpException(response);
                ResponseBody errorBody = response.errorBody();

                if (errorBody == null) {
                    throw e;
                } else {
                    //throw new RuntimeException(e);
                    try {
                        if (e.response() == null || e.response().errorBody() == null) {
                            throw e;
                        }
                        String errorBodyStr = errorBody.string();
                        OpenAiError error = new ObjectMapper().readValue(errorBodyStr, OpenAiError.class);
                        throw new OpenAiHttpException(error, e, e.code());
                    } catch (IOException ex) {
                        // couldn't parse OpenAI error
                        throw e;
                    }
                }
            }

            InputStream in = response.body().byteStream();
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            AzureSSE azureSse = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    azureSse = new AzureSSE(data);
                } else if (line.equals("") && azureSse != null) {
                    if (azureSse.isDone()) {
                        if (emitDone) {
                            emitter.onNext(azureSse);
                        }
                        break;
                    }

                    emitter.onNext(azureSse);
                    azureSse = null;
                } else {
                    throw new RuntimeException("Invalid azureSse format! " + line);
                }
            }

            emitter.onComplete();

        } catch (Throwable t) {
            onFailure(call, t);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        emitter.onError(t);
    }
}
