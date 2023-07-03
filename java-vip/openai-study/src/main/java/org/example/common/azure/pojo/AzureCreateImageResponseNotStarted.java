package org.example.common.azure.pojo;

import lombok.Data;

/**
 * <h1>创建图片时会先返回id数据，然后再基于返回的id拼接请求图片的url</h1>
 * Created by hanqf on 2023/4/25 18:11.
 */

@Data
public class AzureCreateImageResponseNotStarted {
    /**
     * 状态 NotStarted
    */
    private String status;
    /**
     * id
    */
    private String id;
}
