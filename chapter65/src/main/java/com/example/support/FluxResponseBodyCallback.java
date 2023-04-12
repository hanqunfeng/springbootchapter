package com.example.support;

import okhttp3.ResponseBody;
import reactor.core.publisher.FluxSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/12 10:19.
 */


public class FluxResponseBodyCallback implements Callback<ResponseBody> {

    private FluxSink<SSE> emitter;
    private boolean emitDone;

    public FluxResponseBodyCallback(FluxSink<SSE> emitter, boolean emitDone) {
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
                    throw new RuntimeException(e);
                }
            }

            InputStream in = response.body().byteStream();
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            SSE sse = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    sse = new SSE(data);
                } else if (line.equals("") && sse != null) {
                    if (sse.isDone()) {
                        if (emitDone) {
                            emitter.next(sse);
                        }
                        break;
                    }

                    emitter.next(sse);
                    sse = null;
                } else {
                    throw new RuntimeException("Invalid sse format! " + line);
                }
            }

            emitter.complete();

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
        emitter.error(t);
    }
}
