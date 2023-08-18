package com.example;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dao.one.AddressMapper;
import com.example.dao.one.UserMapper;
import com.example.dao.two.SysUserDao;
import com.example.model.one.Address;
import com.example.model.one.BooksEntity;
import com.example.model.one.User;
import com.example.model.two.SysUser;
import com.example.service.one.AddressService;
import com.example.service.one.BooksService;
import com.example.service.one.UserService;
import com.example.service.two.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MybatisPlusMultiDbDemoApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private BooksService booksService;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private AddressService addressService;

    @Test
    void testOneAddress(){
        addressMapper.selectList(new Page<>(1, 10), new LambdaQueryWrapper<Address>()
                        .eq(Address::getCity, "PERSIST")
                        .isNull(Address::getUserid)
                        .orderByDesc(Address::getId))
                .forEach(System.out::println);

        Address address = addressService.getOne(new LambdaQueryWrapper<Address>().eq(Address::getId, 6));
        System.out.println(address);
        address.setProvince("abc");
        addressService.updateById(address);

        addressService.list(new Page<>(1, 10), new LambdaQueryWrapper<Address>()
                .eq(Address::getCity, "PERSIST")
                .isNull(Address::getUserid)
                .orderByDesc(Address::getId))
                .forEach(System.out::println);
    }

    @Test
    void testOneBook() {
        List<BooksEntity> listService = booksService.list();
    }

    @Test
    void testOneUserAndBooks() {
        User user = userMapper.selectByIdAndBooks(65L);
        System.out.println(user);
    }

    @Test
    void testOne() {
        List<User> listService = userService.list();
        List<User> listMapper = userMapper.selectList(null);
    }

    @Test
    void testOnePage() {
        final Page<User> userPage = userService.page(new Page<>(2, 10));
        List<User> listpage = userPage.getRecords();
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
    void testTwo() {
        List<SysUser> listService = sysUserService.list();
        List<SysUser> listMapper = sysUserDao.selectList(null);

        sysUserDao.selectAll().forEach(System.out::println);

        System.out.println(sysUserDao.selectByPrimaryKey("admin"));
    }

    @Test
    void testTwoPage() {
        final Page<SysUser> userPage = sysUserService.page(new Page<>(1, 10));
        List<SysUser> listpage = userPage.getRecords();
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


}
