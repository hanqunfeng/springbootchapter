package com.example.dao;

import com.example.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by hanqf on 2020/3/16 14:03.
 */


public interface UserRepository extends MongoRepository<User, String> {

    //类似jpa，这里也可以根据属性名称自定义方法
    /**
     * 通过用户名查询
     * @param userName 用户名
     * @return
     */
    //这里是实体类中的属性名称
    List<User> findAllByUserName(String userName);

    //这里是实体类中的属性名称
    List<User> findAllByUserNameAndAge(String userName, Integer age);


    //value对应的是:db.user.find({name:{$regex:"zhangsan"},age:25}).pretty() 中，find中的过滤内容，字段按参数下标传递，不支持@Param的形式
    //注意，这里的name就是集合中的属性名称，而不是实体类中的属性名称
    //$regex 正则，这里类似于 like
    @Query(value = "{name:{$regex:?0},age:?1}")
    List<User> getUserByMyProperties(String userName, Integer age);

    //大于，小于
    List<User> getUserByAgeBetween(Integer start, Integer end);

    //大于等于，小于等于
    @Query(value = "{age:{$gte:?0,$lte:?1}}")
    List<User> getUserByAgeProperties(Integer start, Integer end);


}
