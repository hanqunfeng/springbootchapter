# springboot mybatis-plus 多数据源事务管理--Atomikos
这里要注意，这里要使用MybatisSqlSessionFactoryBean
com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean
```java
    @Bean(name = "sqlSessionFactoryOne")
    @DependsOn("oneDataSource")
    public MybatisSqlSessionFactoryBean sqlSessionFactoryBeanOne() {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(oneDataSource);

        //实体别名查找路径 @Alias("oneUser")
        bean.setTypeAliasesPackage("com.example.model.one");

        ////分页插件设置
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        // paginationInterceptor.setLimit(500);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        //添加分页插件
        bean.setPlugins(new Interceptor[]{paginationInterceptor});

        try {
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
```