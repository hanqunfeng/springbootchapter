package com.example.runner;

import java.io.Serializable;

/**
 * <h1></h1>
 * Created by hanqf on 2022/5/13 20:09.
 */


public class ImageInfo implements Serializable {
    private static final long serialVersionUID = -5761039599313150000L;
    private String width;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    private String height;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
