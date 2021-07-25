package com.example.support;

import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * <h1>文件操作返回结果</h1>
 * Created by hanqf on 2021/7/24 21:34.
 */

@AllArgsConstructor
public class OssResult implements Serializable {
    private static final long serialVersionUID = 7178724359437634014L;
    /**
     * 上传是否成功
     */
    private boolean success;

    /**
     * 上传的文件名（如果使用自定义文件路径，会返回完整的路径+文件名，例：cf/abc.png）
     */
    private String fileName;

    /**
     * 上传成功的返回url（带过期时间），或者是本地路径
     */
    private String url;

    /**
     * 提示信息
     */
    private String msg;
}
