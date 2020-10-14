package com.example.swagger3demo;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) //表示测试环境基于web环境，可以认为当前服务器已经启动
//启动自动配置MockMVC
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "api/generated-snippets")
class Swagger3DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private String snippetDir = "api/generated-snippets";

    @Test
    void makeFile() throws Exception {
        // 获取设置的静态文件生成路径
        String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
        // 根据api获取接口内容
        MvcResult mvcResult = this.mockMvc
                .perform(get("/v2/api-docs")//目前只支持v2格式
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding("utf-8");
        String swaggerJson = response.getContentAsString();
        Files.createDirectories(Paths.get(outputDir));

        // 输出为 json 文件，其实这里就是需要这个swagger-api的json文件
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir,
                "swagger.json"), StandardCharsets.UTF_8)) {
            writer.write(swaggerJson);
        }



        Swagger2MarkupConfig config;

        //ASCIIDOC
        config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();
        // 输出为 asciidoc 文件
        Swagger2MarkupConverter.from(swaggerJson)
                .withConfig(config)
                .build()
                .toFile(Paths.get(outputDir + "/index"));

        //MARKDOWN
        config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();
        // 输出为 md 文件，参数为json文件，这里也可以使用
        //Swagger2MarkupConverter.from(new URL("http://localhost:8080/v2/api-docs"))
        Swagger2MarkupConverter.from(swaggerJson)
                .withConfig(config)
                .build()
                .toFile(Paths.get(outputDir + "/index"));



    }

    //这个Swagger2Markup的版本不能加载测试用例生成的文档
    @Test
    public void TestApi() throws Exception{
        mockMvc.perform(get("/demo/demoMap").param("name", "zhangsan")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("demoMap", preprocessResponse(prettyPrint())));

    }

}
