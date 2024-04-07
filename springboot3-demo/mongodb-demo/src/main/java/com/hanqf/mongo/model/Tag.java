package com.hanqf.mongo.model;

import lombok.Builder;
import lombok.Data;

/**
 * <h1></h1>
 * Created by hanqf on 2024/3/4 18:28.
 */

@Data
@Builder
public class Tag {

    private String tagKey;
    private Object tagValue;

}

