package com.example.model.one;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName address
 */
@Data
public class Address implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 
     */
    private String province;

    /**
     * 
     */
    private String city;

    /**
     * 
     */
    private Long userid;

    private static final long serialVersionUID = 1L;
}