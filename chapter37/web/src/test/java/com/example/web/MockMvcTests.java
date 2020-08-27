package com.example.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * <p>MockMvc测试类</p>
 * Created by hanqf on 2020/8/27 09:33.
 *
 * @SpringBootTest 实例化整个spring上下文
 */

@SpringBootTest
@AutoConfigureMockMvc
public class MockMvcTests {

    /**
     * @AutoConfigureMockMvc 表示测试类可以通过@Autowired 的方式加载MockMvc
    */
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHello() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/index/zhangsan"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("zhangsan"));
    }
}
