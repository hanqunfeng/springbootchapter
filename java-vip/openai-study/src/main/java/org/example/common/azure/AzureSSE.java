package org.example.common.azure;

/**
 * <h1>流式数据封装对象</h1>
 * Created by hanqf on 2023/4/14 16:55.
 */


public class AzureSSE {
    private static final String DONE_DATA = "[DONE]";

    private final String data;

    public AzureSSE(String data){
        this.data = data;
    }

    public String getData(){
        return this.data;
    }

    public byte[] toBytes(){
        return String.format("data: %s\n\n", this.data).getBytes();
    }

    public boolean isDone(){
        return DONE_DATA.equalsIgnoreCase(this.data);
    }
}
