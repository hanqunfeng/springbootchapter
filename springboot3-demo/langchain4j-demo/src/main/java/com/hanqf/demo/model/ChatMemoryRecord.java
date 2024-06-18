package com.hanqf.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.context.annotation.Lazy;

import java.io.Serializable;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/17 16:37.
 */

@Data
@Entity
@Table(name = "tbl_chat_memory")
@Lazy(value=true)
public class ChatMemoryRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * id主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private java.lang.Long id;



    /*
     * memory_json
     */
    @Column(name="memory_json")
    private java.lang.String memoryJson;

    /*
     * 本轮对话id
     */
    @Column(name="chat_memory_id")
    private java.lang.String chatMemoryId;
}
