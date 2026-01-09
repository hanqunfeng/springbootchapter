package com.example.demo.redissug;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Suggestion对象
 * Created by hanqf on 2026/1/7 11:03.
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suggestion {
    private String value;
    private Double score;
    private String payload;
}

