# chapter36----Springboot SpringSecurity，CAS 
* cas-serve-5.x.x基于jdk1.8，cas-serve-6.x.x基于jdk11
* 两个cas版本部分cas属性名称发生变化，注意查看CasConfigurationProperties中的配置
* 两个cas版本自动生成的表名称和字段名称发生变化，具体查看数据库中生成的表结构吧

## cas单点退出

* 如果是json配置方式：services目录下需要为每个客户端配置一个json文件，如client1-10000005.json

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|http|imaps)://localhost:8081.*", #对应客户端的地址匹配路径
  "name" : "client1",  #与文件名称对应
  "id" : 10000005,     #与文件名称对应
  "description" : "This service definition authorizes all application urls that support HTTPS and IMAPS protocols.",
  "evaluationOrder" : 10, #顺序id不能重复
  "logoutType" : "BACK_CHANNEL", #固定写法
  "logoutUrl" : "http://localhost:8081/",  #客户端地址
  "attributeReleasePolicy": {
    "@class": "org.apereo.cas.services.ReturnAllAttributeReleasePolicy"
  },
  "accessStrategy" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy",
    "enabled" : true,
    "ssoEnabled" : true
  }
}

```

## cas多属性返回
* services的json文件中要增加如下配置
```json
# 返回全部配置属性
"attributeReleasePolicy": {
    "@class": "org.apereo.cas.services.ReturnAllAttributeReleasePolicy"
  }

# 返回指定配置属性，这里指定只返回username和email两个属性
  "attributeReleasePolicy" : {
  	"@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
 	"allowedAttributes" : [ "java.util.ArrayList", [ "username", "email" ] ]
  }
```

## 动态service
```
如果使用代码的方式维护service,则也要根据情况来创建返回策略，
参加代码chapter36/cas-overlay-template-5.3.14/src/main/java/com/cas/controller/ServiceController.java
```

* 配置属性
```properties
# 动态services配置
################################################
# 开启识别Json文件，默认false
# 这一段表示从json文件里面初始化服务，如果我们配置了这个，就会将这写json里面的数据，都会自动导入到数据库中
cas.serviceRegistry.initFromJson=true
cas.serviceRegistry.watcherEnabled=true
#设置配置的服务，一直都有，不会给清除掉 ， 第一次使用，需要配置为 create-drop
#create-drop 重启cas服务的时候，就会给干掉
#create  没有表就创建，有就不创建
#none 什么也不做
#update 更新
#Unrecognized legacy `hibernate.hbm2ddl.auto` value : create-drops
cas.serviceRegistry.jpa.ddlAuto=update
#配置将service配置到数据库中
cas.serviceRegistry.jpa.isolateInternalQueries=false
cas.serviceRegistry.jpa.url=${jdbc.url}
cas.serviceRegistry.jpa.user=${jdbc.username}
cas.serviceRegistry.jpa.password=${jdbc.password}
#这个必须是org.hibernate.dialect.MySQL5Dialect ,我就是这个问题导致表创建失败
cas.serviceRegistry.jpa.dialect=org.hibernate.dialect.MySQL5Dialect
cas.serviceRegistry.jpa.driverClass=${jdbc.driver}
cas.serviceRegistry.jpa.leakThreshold=10
cas.serviceRegistry.jpa.batchSize=1
cas.serviceRegistry.jpa.autocommit=true
cas.serviceRegistry.jpa.idleTimeout=5000
#配置结束
################################################
```

* 这里说一下delete异常的问题，可以引入jdbcTemplate直接操作数据表
```java
List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from regexregisteredservice where serviceId = '" + serviceId + "'");
if (list != null && list.size() > 0) {
    has_data = true;
    jdbcTemplate.update("delete from regexregisteredservice where serviceId = '" + serviceId + "'");
}

 //执行load生效
 servicesManager.load();
```

## 基于配置文件返回属性
```properties
#####################################################
## 属性返回
cas.authn.attributeRepository.jdbc[0].sql=select username,password,email from cas_user where username=?

# 格式cas.authn.attributeRepository.jdbc[0].attributes.key=value
# 返回上面查询结果的username，属性key名称也为username ,以下雷同
cas.authn.attributeRepository.jdbc[0].attributes.username=username
cas.authn.attributeRepository.jdbc[0].attributes.password=password
cas.authn.attributeRepository.jdbc[0].attributes.email=email

cas.authn.attributeRepository.jdbc[0].singleRow=true
cas.authn.attributeRepository.jdbc[0].order=0
cas.authn.attributeRepository.jdbc[0].requireAllAttributes=true
# cas.authn.attributeRepository.jdbc[0].caseCanonicalization=NONE|LOWER|UPPER
# cas.authn.attributeRepository.jdbc[0].queryType=OR|AND

cas.authn.attributeRepository.jdbc[0].username=username
#数据库连接
cas.authn.attributeRepository.jdbc[0].url=${jdbc.url}
#数据库dialect配置
cas.authn.attributeRepository.jdbc[0].dialect=${jdbc.dialect}
#数据库用户名
cas.authn.attributeRepository.jdbc[0].user=${jdbc.username}
#数据库用户密码
cas.authn.attributeRepository.jdbc[0].password=${jdbc.password}
#数据库事务自动提交
cas.authn.attributeRepository.jdbc[0].autocommit=true
#数据库驱动
cas.authn.attributeRepository.jdbc[0].driverClass=${jdbc.driver}
#超时配置
cas.authn.attributeRepository.jdbc[0].idleTimeout=5000
cas.authn.attributeRepository.jdbc[0].ddlAuto=none
cas.authn.attributeRepository.jdbc[0].leakThreshold=10
cas.authn.attributeRepository.jdbc[0].batchSize=1
cas.authn.attributeRepository.jdbc[0].dataSourceProxy=false

#####################################################
```

## 代码实现返回属性内容
* 这个需要结合自定义登录策略来实现，代码参考：chapter36/cas-overlay-template-5.3.14/src/main/java/com/cas/security/CustomerHandlerAuthentication.java，注意要关闭配置文件的登录策略，否则会失效
* 注册自定义登录策略，代码参考：chapter36/cas-overlay-template-5.3.14/src/main/java/com/cas/config/CustomAuthenticationConfig.java
* 然后将加入自动配置中,chapter36/cas-overlay-template-5.3.14/src/main/resources/META-INF/spring.factories
```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  org.apereo.cas.config.CasEmbeddedContainerTomcatConfiguration,\
  org.apereo.cas.config.CasEmbeddedContainerTomcatFiltersConfiguration,\
  com.cas.config.MyConfiguration,\ #自定义的其它需要加入spring上下文的bean
  com.cas.config.DataSourceConfig,\ #数据源配置，为了引入JdbcTemplate
  com.cas.config.CustomAuthenticationConfig,\  #自定义登录策略配置
  com.cas.config.CustomerAuthWebflowConfiguration #自定义登录流程配置
```


## cas-client-springsecurity
* 这里说一下，如果不使用springsecurity，而是直接依赖cas-client，其获取cas返回属性的方式
```java
Principal principal = request.getUserPrincipal();
if(principal instanceof AttributePrincipal){
    //cas传递过来的数据
    Map<String,Object> result =( (AttributePrincipal)principal).getAttributes();
    for(Map.Entry<String, Object> entry :result.entrySet()) {
        String key = entry.getKey();
        Object val = entry.getValue();
        System.out.printf("%s:%s\r\n",key,val);
    }
}
```

* 使用springsecurity的配置类代码参考：chapter36/springsecurity-client-demo/src/main/java/com/example/springsecuritydemo/config/WebSecurityConfigByCASByAttrs.java
```java
/**
     * cas 认证 Provider
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        //创建CAS授权认证器
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();

        //设置Cas授权认证器相关配置
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        //设置票据校验器
        casAuthenticationProvider.setTicketValidator(cas30ServiceTicketValidator());
        casAuthenticationProvider.setKey("casAuthenticationProviderKey");


        //setUserDetailsService和setAuthenticationUserDetailsService只能设置一个，其目的都是为了初始化属性authenticationUserDetailsService
        //setUserDetailsService的类型为UserDetailsService
        //setAuthenticationUserDetailsService的类型为AuthenticationUserDetailsService<CasAssertionAuthenticationToken>
        // 如果使用setUserDetailsService，则其会对UserDetailsService进行封装，new UserDetailsByNameServiceWrapper(userDetailsService)，
        // 将其转换为AuthenticationUserDetailsService<CasAssertionAuthenticationToken>类型
        //这里使用setAuthenticationUserDetailsService是为了接收cas服务端返回的属性，因为CasAssertionAuthenticationToken会接收到返回的属性
        casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsServiceImplByAttrs());

        return casAuthenticationProvider;
    }

    @Bean
    public UserDetailsServiceImplByAttrs userDetailsServiceImplByAttrs(){
        UserDetailsServiceImplByAttrs userDetailsServiceImplByAttrs = new UserDetailsServiceImplByAttrs();
        return userDetailsServiceImplByAttrs;
    }

```

* UserDetailsServiceImplByAttrs的重点就是要继承AbstractCasAssertionUserDetailsService
```java
//实际可以接收的属性
Map<String, Object> objectMap = assertion.getPrincipal().getAttributes();
```

* 这里说一下AbstractCasAssertionUserDetailsService，它有一个实现类GrantedAuthorityFromAssertionAttributesUserDetailsService
不过其将返回数据都做为用户的权限了，所以其只是用来从服务器端获取用户权限使用
```java
CasAuthenticationToken principal = (CasAuthenticationToken) request.getUserPrincipal();
UserDetails userDetails = principal.getUserDetails();
Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) userDetails.getAuthorities();
authorities.stream().forEach(System.out::println);
```

* 如果希望灵活使用cas返回属性，则需要自定义实现类，笔者这里将获取到的属性map存储到自定义的CustomerUser属性中了

* 代码中使用属性
```java
#获取登录的用户名称
String username = request.getRemoteUser(); 
#获取cas返回属性
CasAuthenticationToken principal = (CasAuthenticationToken) request.getUserPrincipal();
UserDetails userDetails = principal.getUserDetails();
if(userDetails instanceof CustomerUser){
    Map<String, Object> map = ((CustomerUser) userDetails).getMap();
    for (String key : map.keySet()) {
        Object value = map.get(key);
        if (value != null) {
            if (value instanceof List) {
                List list = (List) value;
                Iterator iterator = list.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    Object object = iterator.next();
                    System.out.println("key:" + key + ",values[" + i + "]:" + object.toString());
                    i++;
                }
            } else {
                System.out.println("key:" + key + ",value:" + value.toString());
            }
        }
    }
}
```



## locale，解决locale=en?locale=en?locale=en，语言参数无限连接的问题
* 重写org/springframework/web/servlet/i18n/LocaleChangeInterceptor.java
```java
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        String newLocale = request.getParameter(this.getParamName());
        if (newLocale != null && this.checkHttpMethod(request.getMethod())) {
            //解决locale=en?locale=en?locale=en，语言参数无限连接的问题
            if(newLocale.contains("?")){
                int index = newLocale.indexOf("?");
                newLocale = newLocale.substring(0,index);
                logger.info("newLocale==" + newLocale);
            }

            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }

            try {
                localeResolver.setLocale(request, response, this.parseLocaleValue(newLocale));
            } catch (IllegalArgumentException var7) {
                if (!this.isIgnoreInvalidLocale()) {
                    throw var7;
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Ignoring invalid locale value [" + newLocale + "]: " + var7.getMessage());
                }
            }
        }

        if(newLocale != null){ //重定向后去掉locale参数
            response.sendRedirect(request.getContextPath());
        }

        return true;
    }

```




