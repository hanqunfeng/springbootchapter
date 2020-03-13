# jpa,springboot，一对一、一对多、多对一、多对多关联映射

注意：一定要开启@Transactional，否则延迟加载会报no session错误

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