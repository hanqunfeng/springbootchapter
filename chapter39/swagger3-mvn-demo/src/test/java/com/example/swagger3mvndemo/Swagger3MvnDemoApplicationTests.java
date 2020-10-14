package com.example.swagger3mvndemo;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URL;
import java.nio.file.Paths;

//@SpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) //表示测试环境基于web环境，可以认为当前服务器已经启动
class Swagger3MvnDemoApplicationTests {

    @Value("${server.port}")
    private String port;


    @Value("${server.servlet.context-path}")
    private String context_path;


    @Test
    void makeFile() throws Exception {
        // 获取设置的静态文件生成路径
        String outputDir = "api/swagger";
        String swagger_api_url = "http://localhost:"+port+context_path+"/v2/api-docs";


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
        Swagger2MarkupConverter.from(new URL(swagger_api_url))
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
        Swagger2MarkupConverter.from(new URL(swagger_api_url))
                .withConfig(config)
                .build()
                .toFile(Paths.get(outputDir + "/index"));


    }
}
