package com.lexing;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.apache.ibatis.annotations.Mapper;

import java.io.File;
import java.util.Collections;

/**
 * Hello world!
 */
public class Generator {

    private static final String DB_URL = "jdbc:mysql://mysql.test.db:3306/mybatis?useUnicode=true&characterEncoding=utf-8";
    private static final String DB_USERNAME = "testUser";
    private static final String DB_PASSWORD = "123456";
    private static final String AUTHOR = "hanqf";
    private static final String PROJECT_DIR = "/Users/hanqf/idea_workspaces/springbootchapter/springboot3-demo/mybatis-plus-auto-generator";
    private static final String JAVA_OUTPUT_DIR = PROJECT_DIR + File.separator + "out";
    private static final String RESOURCES_OUTPUT_DIR = PROJECT_DIR + File.separator + "out";

    /**
     * 父包路径
     */
    private static final String PACKAGE_PARENT = "com.example";
    private static final String PACKAGE_ENTITY = "model.one";
    private static final String PACKAGE_SERVICE = "service.one";
    private static final String PACKAGE_SERVICE_IMPL = "service.one.impl";
    private static final String PACKAGE_MAPPER = "dao.one";
    private static final String PACKAGE_MAPPER_XML = "mappers/one";



    //要生成代码的表名称列表
    private static final String[] TABLE_ARRAY = {"books", "address", "role"};

    public static void main(String[] args) {
        mode1();
    }

    private static void mode1() {
        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                //全局配置
                .globalConfig(builder -> builder
                        .disableOpenDir() // 禁止打开输出目录，默认创建完成后会自动打开文件夹
                        .author(AUTHOR) //作者名
                        .dateType(DateType.TIME_PACK) //时间策略
                        .commentDate("yyyy-MM-dd HH:mm:ss") //注释日期
                        .outputDir(JAVA_OUTPUT_DIR)// 指定输出目录
                )

                //数据库配置
                .dataSourceConfig(builder -> builder
                        .keyWordsHandler(new MySqlKeyWordsHandler()))

                //包配置
                .packageConfig(builder -> builder
                        .parent(PACKAGE_PARENT) //父包名
                        .entity(PACKAGE_ENTITY) //Entity 包名
                        .service(PACKAGE_SERVICE) //Service 包名
                        .serviceImpl(PACKAGE_SERVICE_IMPL) //Service Impl 包名
                        .mapper(PACKAGE_MAPPER) //Mapper 包名
                        .controller("controller") //Controller 包名
                        .pathInfo(Collections.singletonMap(OutputFile.xml, RESOURCES_OUTPUT_DIR + File.separator + PACKAGE_MAPPER_XML)))

                //策略配置
                .strategyConfig(builder -> builder
                        .enableCapitalMode() //开启大写命名
                        .enableSkipView() //开启跳过视图
                        .disableSqlFilter() //禁用 sql 过滤
                        .addInclude(TABLE_ARRAY) //要映射的表
                        .addTablePrefix("t_", "tbl_") //过滤表前缀

                        //Entity配置
                        .entityBuilder()
                        .enableRemoveIsPrefix() // 开启 Boolean 类型字段移除 is 前缀
                        .enableTableFieldAnnotation() // 开启生成实体时生成字段注解
                        .idType(IdType.AUTO) // 全局主键类型
                        .enableLombok()  // 开启 lombok 模型
                        .formatFileName("%sEntity") //文件名格式
                        .enableFileOverride() //覆盖原先的文件

                        //Mapper配置
                        .mapperBuilder()
                        .mapperAnnotation(Mapper.class)
                        .formatMapperFileName("%sMapper")
                        .formatXmlFileName("%sMapper")
                        .enableFileOverride()

                        //service配置
                        .serviceBuilder()
                        .formatServiceFileName("I%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .enableFileOverride()

                        //controller配置
                        .controllerBuilder()
                        .formatFileName("%sController")
                        .enableFileOverride())
//                .templateEngine(new VelocityTemplateEngine())
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板

                // vm:velocity    ftl:freemarker
                .templateConfig(builder -> builder
                        //指定Entity的模板文件位置
                        .entity("templates/entity.java")
                        //关闭controller模板，不创建controller文件
                        .disable(TemplateType.CONTROLLER))

                //执行生成操作
                .execute();
    }

    private static void mode2() {
        // 数据库配置
        // 使用元数据查询的方式生成代码,默认已经根据jdbcType来适配java类型,支持使用typeConvertHandler来转换需要映射的类型映射
        final DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(DB_URL, DB_USERNAME, DB_PASSWORD)
                .keyWordsHandler(new MySqlKeyWordsHandler())
                .build();

        // 创建代码生成器对象，执行生成代码操作
        AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfig);

        // 全局配置
        final GlobalConfig globalConfig = new GlobalConfig.Builder()
                .disableOpenDir() // 禁止打开输出目录，默认创建完成后会自动打开文件夹
                .outputDir(JAVA_OUTPUT_DIR) //指定输出目录
                .author(AUTHOR) //作者名
                .dateType(DateType.TIME_PACK) //时间策略
                .commentDate("yyyy-MM-dd HH:mm:ss") //注释日期
                .build();

        autoGenerator.global(globalConfig);

        // 包配置
        final PackageConfig packageConfig = new PackageConfig.Builder()
                .parent(PACKAGE_PARENT) //父包名
                .entity(PACKAGE_ENTITY) //Entity 包名
                .service(PACKAGE_SERVICE) //Service 包名
                .serviceImpl(PACKAGE_SERVICE_IMPL) //Service Impl 包名
                .mapper(PACKAGE_MAPPER) //Mapper 包名
                .controller("controller") //Controller 包名
                .pathInfo(Collections.singletonMap(OutputFile.xml, RESOURCES_OUTPUT_DIR + File.separator + PACKAGE_MAPPER_XML))
                .build();
        autoGenerator.packageInfo(packageConfig);

        //策略配置
        final StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .enableCapitalMode() //开启大写命名
                .enableSkipView() //开启跳过视图
                .disableSqlFilter() //禁用 sql 过滤
                .addInclude(TABLE_ARRAY) //要映射的表
                .addTablePrefix("t_", "tbl_") //过滤表前缀

                //Entity配置
                .entityBuilder()
                .enableRemoveIsPrefix() // 开启 Boolean 类型字段移除 is 前缀
                .enableTableFieldAnnotation() // 开启生成实体时生成字段注解
                .idType(IdType.AUTO) // 全局主键类型
                .enableLombok()  // 开启 lombok 模型
                .formatFileName("%sEntity") //文件名格式
                .enableFileOverride() //覆盖原先的文件

                //Mapper配置
                .mapperBuilder()
                .mapperAnnotation(Mapper.class)
                .formatMapperFileName("%sMapper")
                .formatXmlFileName("%sMapper")
                .enableFileOverride()

                //service配置
                .serviceBuilder()
                .formatServiceFileName("I%sService")
                .formatServiceImplFileName("%sServiceImpl")
                .enableFileOverride()

                //controller配置
                .controllerBuilder()
                .formatFileName("%sController")
                .enableFileOverride()
                .build();
        autoGenerator.strategy(strategyConfig);

        //模板配置
        // vm:velocity    ftl:freemarker
        TemplateConfig templateConfig = new TemplateConfig.Builder()
                //指定Entity的模板文件位置
                .entity("templates/entity.java")
                //关闭controller模板，不创建controller文件
                .disable(TemplateType.CONTROLLER)
                .build();
        autoGenerator.template(templateConfig);

        // 执行生成操作
        autoGenerator.execute();
//        autoGenerator.execute(new VelocityTemplateEngine());
//        autoGenerator.execute(new FreemarkerTemplateEngine());
    }
}
