package org.example.common.azure.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/25 18:41.
 */


@Data
public class ImageResult {
    /**
     * url过期时间
     */
    @JsonProperty("contentUrlExpiresAt")
    private Date contentUrlExpiresAt;
    /**
     * url地址
     */
    @JsonProperty("contentUrl")
    private String contentUrl;
    /**
     * 创建时间
     */
    @JsonProperty("createdDateTime")
    private Date createdDateTime;
    /**
     * 本次请求的提示语
     */
    private String caption;

}
