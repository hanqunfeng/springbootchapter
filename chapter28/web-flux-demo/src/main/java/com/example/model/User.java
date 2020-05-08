package com.example.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p></p>
 * Created by hanqf on 2020/5/5 18:40.
 */

@Data
public class User implements Serializable {
    private static final long serialVersionUID = -7229906944062898852L;
    private Long id;
    private String name;
}
