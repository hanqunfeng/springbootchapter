# jpa,springboot，一对一、一对多、多对一、多对多关联映射，分布式锁

注意：一定要开启@Transactional，否则延迟加载会报no session错误

```java
    //映射Address类中的user属性的关联关系，onetoxxx关系中one的一方不要设置为预先抓取，否则可能造成无限循环
    @OneToOne(cascade={CascadeType.DETACH},mappedBy = "user",fetch = FetchType.LAZY)
    private Address userAddress;

    //映射Book类中的user属性的关联关系
    @OneToMany(cascade={CascadeType.DETACH},mappedBy = "user",fetch = FetchType.LAZY)
    private List<Book> books;


    //多对多,任意一方设置即可，另一方使用mappedBy关联即可
    //FetchType.LAZY，一定要开启事务，否则会报no session错误，@Transactional
    @ManyToMany(cascade={CascadeType.DETACH},fetch=FetchType.LAZY)
    @JoinTable(name="USERROLE",joinColumns={@JoinColumn(name="userId")},inverseJoinColumns={@JoinColumn(name="roleId")})
    @OrderBy("id ASC")
    private Set<Role> roles;
```

```java
    @OneToOne(fetch=FetchType.EAGER)
    //因为上面声明了独立的属性userId，所以这里必须设置insertable = false,updatable = false
    //@JoinColumn(name="userId",nullable=true,insertable = false,updatable = false)
    @JoinColumn(name="userId",nullable=true)
    private User user;

    @ManyToOne(fetch=FetchType.EAGER)
    //因为上面声明了独立的属性userId，所以这里必须设置insertable = false,updatable = false
    @JoinColumn(name="userId",nullable=true,insertable = false,updatable = false)
    private User user;

    //FetchType.LAZY，一定要开启事务，否则会报no session错误，@Transactional
    @ManyToMany(mappedBy="roles",cascade={CascadeType.DETACH},fetch=FetchType.LAZY)
    private Set<User> users;
```


## Spring Boot Jpa之CascadeType

JPA允许您传播从父实体到子级的状态转换。为此，JPA javax.persistence.CascadeType定义了各种级联类型：

### ALL 
级联所有实体状态转换

### PERSIST 
级联实体持久化操作。
```java
User user = new User();
        user.setName("PERSIST");
        user.setAge(10);
        user.setEmail("jpa@email.com");
        user.setDel(DelEnum.one);

        Address address = new Address();
        address.setCity("PERSIST");
        address.setUser(user);

        user.setUserAddress(address);

        //保存前会再查询一次，如果没有变化，则不会发送修改sql
        jpaUserRepository.save(user);
```
### MERGE 
级联实体合并操作。
```java
User user = jpaUserRepository.findById(65L).get();
        user.setName("222");

        Address address = user.getUserAddress();
        address.setCity("222");

        user.setUserAddress(address);
        //保存前会再查询一次，如果没有变化，则不会发送修改sql
        jpaUserRepository.save(user);
```
### REMOVE 
级联实体删除操作。

### REFRESH 
级联实体刷新操作。

### DETACH 
级联实体分离操作。就是没有级联操作，推荐这种吧，手工处理更安全