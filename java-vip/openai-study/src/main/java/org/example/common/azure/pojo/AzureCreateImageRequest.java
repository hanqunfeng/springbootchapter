package org.example.common.azure.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>创建图片请求类</h1>
 * Created by hanqf on 2023/4/25 18:06.
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AzureCreateImageRequest {
    /**
     * 提示语
    */
    private String resolution;
    /**
     * The size of the generated images. Must be one of "256x256", "512x512", or "1024x1024". Defaults to "1024x1024".
    */
    private String caption;

}
