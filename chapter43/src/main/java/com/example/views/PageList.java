package com.example.views;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <h1>page查询结果</h1>
 * Created by hanqf on 2020/10/26 17:57.
 */

@Data
public class PageList<T> implements Serializable {

    private static final long serialVersionUID = -1842907051804836951L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 记录数据
     */
    private List<T> data;
}
