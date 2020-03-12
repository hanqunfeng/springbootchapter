# springboot mybatis xml+注解 + PageHelper分页

xml和注解方式实现多对一，一对多的配置

## xml
```xml
  <resultMap id="BaseResultMap" type="com.example.model.User">
    <id column="id" jdbcType="BIGINT" property="id" />
    <result column="name" jdbcType="VARCHAR" property="name" />
    <result column="age" jdbcType="INTEGER" property="age" />
    <result column="email" jdbcType="VARCHAR" property="email" />
    <result column="del" jdbcType="VARCHAR" property="del" />
  </resultMap>
  
    <!--   一对一、一对多-->
  <resultMap id="UserResultMap" type="com.example.model.User" extends="BaseResultMap">
    <association column="id" property="userAddress" select="com.example.dao.AnnotationsAddressMapper.getAddressByUserId" fetchType="eager" ></association>
  </resultMap>

    <!--多对一-->
  <resultMap id="BooksResultMap" type="com.example.model.User" extends="BaseResultMap">
    <collection column="id" property="books" select="com.example.dao.AnnotationsBookMapper.getBooksByUserId" fetchType="eager" ></collection>
  </resultMap>

  <!--  只支持单继承 ，符合java特性 -->
  <resultMap id="UserBooksResultMap" type="com.example.model.User" extends="BaseResultMap">
    <association column="id" property="userAddress" select="com.example.dao.AnnotationsAddressMapper.getAddressByUserId" fetchType="eager" ></association>
    <collection column="id" property="books" select="com.example.dao.AnnotationsBookMapper.getBooksByUserId" fetchType="eager" ></collection>
  </resultMap>
```

## 注解
```java
    //one to one
    @Select("select * from user where id = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="name",property="name"),
            @Result(column="age",property="age"),
            @Result(column="email",property="email"),
            @Result(column="del",property="del"),
            //这里column="id" 是user表的主键
            @Result(column="id",property="userAddress",one=@One(select="com.example.dao.AnnotationsAddressMapper.getAddressByUserId",fetchType= FetchType.EAGER))
    })
    public User getUserById(Long userId);

    //one to many
    @Select("select * from user where id = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="name",property="name"),
            @Result(column="age",property="age"),
            @Result(column="email",property="email"),
            @Result(column="del",property="del"),
            //这里column="id" 是user表的主键
            @Result(column="id",property="books",many=@Many(select="com.example.dao.AnnotationsBookMapper.getBooksByUserId",fetchType= FetchType.EAGER))
    })
    public User getUserAndBooksById(Long userId);


    //one to many
    @Select("select * from user where id = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="name",property="name"),
            @Result(column="age",property="age"),
            @Result(column="email",property="email"),
            @Result(column="del",property="del"),
            //这里column="id" 是user表的主键,可以理解为当前表发送给getAddressByUserId方法的参数
            @Result(column="id",property="userAddress",one=@One(select="com.example.dao.AnnotationsAddressMapper.getAddressByUserId",fetchType= FetchType.EAGER)),
            //这里column="id" 是user表的主键,可以理解为当前表发送给getBooksByUserId方法的参数
            @Result(column="id",property="books",many=@Many(select="com.example.dao.AnnotationsBookMapper.getBooksByUserId",fetchType= FetchType.EAGER))
    })
    public User getUserAddressAndBooksById(Long userId);
```