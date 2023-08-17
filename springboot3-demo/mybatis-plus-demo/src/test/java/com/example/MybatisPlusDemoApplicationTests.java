package com.example;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.UserPlusMapper;
import com.example.model.UserPlus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MybatisPlusDemoApplicationTests {
    @Autowired
    private UserPlusMapper userPlusMapper;


    @Test
    void testSelectAll(){
        userPlusMapper.selectUsers().forEach(System.out::println);
    }
    @Test
    void testInsert() {
        System.out.println("==============mybatis-plus=====================");
        UserPlus userPlus = new UserPlus();
        userPlus.setAge(100);
        userPlus.setEmail("hanqf@163.com");
        userPlus.setName("hanqf");
        userPlusMapper.insert(userPlus);
    }

    @Test
    void testSelectList() {
        List<UserPlus> userList = userPlusMapper.selectList(null);
        //Assert.assertEquals(5, userList.size());
        userList.stream().forEach(System.out::println);
    }

    @Test
    void testDelete() {
        userPlusMapper.deleteById(19);
    }

    @Test
    void testUserPlusByName() {
        List<UserPlus> userListByName = userPlusMapper.getUserPlusByName("hanqf");
        //Assert.assertEquals(5, userList.size());
        userListByName.forEach(System.out::println);

    }

    @Test
    void getUserPlusByAge() {
        List<UserPlus> userListByAge = userPlusMapper.getUserPlusByAge(null);
        //Assert.assertEquals(5, userList.size());
        userListByAge.forEach(System.out::println);
        List<UserPlus> userListByAge2 = userPlusMapper.getUserPlusByAge(10);
        //Assert.assertEquals(5, userList.size());
        userListByAge2.forEach(System.out::println);
    }

    @Test
    void selectPage() {
        Page<UserPlus> userPage = new Page<>(2, 2);//参数一是当前页，参数二是每页个数

        userPage = userPlusMapper.selectPage(userPage, new LambdaQueryWrapper<>(UserPlus.class).orderByDesc(UserPlus::getId));
        List<UserPlus> listpage = userPage.getRecords();
        listpage.forEach(System.out::println);

        // 分页的所有数据都在userPage对象中封装着
        // 获取总页数
        long pages = userPage.getPages();
        //一页显示几条数据
        long size = userPage.getSize();
        // 获取当前页
        long current = userPage.getCurrent();
        // 获取总记录数
        long total = userPage.getTotal();
        // 当前页是否有下一页
        boolean hasNext = userPage.hasNext();
        // 当前页是否有上一页
        boolean hasPrevious = userPage.hasPrevious();

        System.out.println("总页数pages=" + pages);
        System.out.println("当前页current=" + current);
        System.out.println("当前页显示几条数据size=" + size);
        System.out.println("总记录数total=" + total);
        System.out.println("是否有下一页hasNext=" + hasNext);
        System.out.println("是否有上一页hasPrevious=" + hasPrevious);
    }



    @Test
    void queryWrapper() {
        IPage<UserPlus> userPage = new Page<>(1, 2);//参数一是当前页，参数二是每页个数
        QueryWrapper<UserPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("age", 9).eq("name", "aaa");
        queryWrapper.orderByDesc("id");
        userPage = userPlusMapper.selectPage(userPage, queryWrapper);
        List<UserPlus> listpage2 = userPage.getRecords();
        listpage2.stream().forEach(System.out::println);
    }

}
