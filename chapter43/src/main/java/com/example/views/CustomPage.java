package com.example.views;

import lombok.Data;

/**
 * <h1></h1>
 * Created by hanqf on 2020/10/26 13:58.
 */

@Data
public class CustomPage {

    /**
     * 当前页码，从0开始
     */
    private int index = 0;

    /**
     * 每页记录数
     */
    private int size = 10;

    /**
     * 总记录数
     */
    private long total;


    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getPageCount() {
        return (int) Math.ceil((double) total / (double) size);
    }

    /**
     * 是否有下一页
     *
     * @return true,如果有下一页，否则为false
     */
    public boolean isHasNext() {
        return index >= 0 && index < getPageCount() - 1;
    }

    /**
     * 是否有上一页
     *
     * @return true,如果有上一页，否则为false
     */
    public boolean isHasPrev() {
        return index > 0 && index < getPageCount();
    }
}
