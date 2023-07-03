package org.example.common.azure.pojo;

import lombok.Data;

/**
 * <h1>创建图像返回结果</h1>
 * Created by hanqf on 2023/6/19 10:41.
 */


@Data
public class AzureCreateImageResultNew {

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
    private com.theokanning.openai.image.ImageResult result;

    /**
     * 创建时间
    */
    private Long created;


    /**
     * 过期时间,默认24小时后自动到期
     */
    private Long expires;
}
