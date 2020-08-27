package com.example.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * <p>WebMvcTest 注解</p>
 * Created by hanqf on 2020/8/27 09:40.
 *
 * @WebMvcTest 注释告诉 Spring Boot 仅实例化 Controller 层，而不去实例化整体上下文，从而提高测试效率
 *
 * @WebMvcTest(controllers = {DemoController.class}) 指定只初始化哪些controller，
 * @WebMvcTest 还可以指定初始化哪些Filter，以及去除哪些自动配置，等等
 *
 * @WebMvcTest同时引入了@AutoConfigureMockMvc
 */

@WebMvcTest
//@WebMvcTest(controllers = {DemoController.class})
public class WebMvcTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 因为只实例化了 Controller 层，所以依赖的 Service 层实例需要通过 @MockBean 创建，并通过 Mockito 的方法指定 Mock 出来的 Service 层实例在特定情况下方法调用时的返回结果。
    */
    @MockBean
    private DemoService demoService;

    @Test
    public void testHello() throws Exception {
        //指定Service方法的返回值，这里表示getName传入任意参数都返回固定的值，zhangsan
        Mockito.when(demoService.getName(Mockito.anyString())).thenReturn("zhangsan");

        this.mockMvc.perform(MockMvcRequestBuilders.get("/index/zhangsan"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("zhangsan"));
    }
}
