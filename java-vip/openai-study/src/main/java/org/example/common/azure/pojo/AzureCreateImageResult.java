package org.example.common.azure.pojo;

import lombok.Data;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/25 18:12.
 */

@Data
public class AzureCreateImageResult {
    /**
     * 返回状态,Succeeded
     */
    private String status;
    /**
     * id
     */
    private String id;

    /**
     * 图片信息
     */
    private ImageResult result;


}
