package org.example.learnjuc.designpattern.immutability;

/**
 * 短信服务商信息
 */
public final class SmsInfo {

    /**
     * 短信服务商请求url
     */
    private final String url;
    /**
     * 每次发送短信内容的最大字节数
     */
    private final Integer maxSizeInBytes;

    public SmsInfo(String url, Integer maxSizeInBytes) {
        this.url = url;
        this.maxSizeInBytes = maxSizeInBytes;

    }

    public String getUrl() {
        return url;
    }

//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public void setMaxSizeInBytes(Integer maxSizeInBytes) {
//        this.maxSizeInBytes = maxSizeInBytes;
//    }

    public SmsInfo update(String url,Integer maxSizeInBytes){
        return new SmsInfo(url,maxSizeInBytes);
    }


    public Integer getMaxSizeInBytes() {
        return maxSizeInBytes;
    }

    @Override
    public String toString() {
        return "SmsInfo{" +
                "url='" + url + '\'' +
                ", maxSizeInBytes=" + maxSizeInBytes +
                '}';
    }
}
