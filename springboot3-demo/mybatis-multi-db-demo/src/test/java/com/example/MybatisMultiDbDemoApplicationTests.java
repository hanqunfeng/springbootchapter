package com.example;

import com.example.dao.one.AddressMapper;
import com.example.dao.one.OrderMapper;
import com.example.dao.one.UserMapper;
import com.example.dao.two.SysUserMapper;
import com.example.model.one.Address;
import com.example.model.one.Order;
import com.example.model.two.SysUser;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MybatisMultiDbDemoApplicationTests {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Test
    void testOneAddressAdd(){
        Address address = new Address();
        address.setCity("beijing");
        address.setProvince("beijing");
        address.setUserid(18L);
        addressMapper.insertSelective(address);
    }

    @Test
    void testOneAddress(){
        final List<Address> addressList = addressMapper.selectAll();
        addressList.forEach(System.out::println);

        addressMapper.selectAllByCity("beijing").forEach(System.out::println);
    }

    @Test
    void testOne() {
        System.out.println(orderMapper.selectByPrimaryKey(1));
    }

    @Test
    void testOneUser() {
        System.out.println(userMapper.selectByPrimaryKey(65L));
    }

    @Test
    void testOnePage() {
        // 分页从1开始计算，这里0和1是一回事
        int pageNum = 2;
        int pageSize = 10;

        //结果分页,PageHelper是分页的关键
        PageHelper.startPage(pageNum, pageSize);
        //此时查询的结果已经是按分页的了
        List<Order> userDomains = orderMapper.selectAll();
        userDomains.forEach(System.out::println);

        //以下是为了获得分页信息
        System.out.println("PageInfo============");
        PageInfo<Order> result = new PageInfo<>(userDomains);
        System.out.println(result);
        System.out.println("getTotal = " + result.getTotal());
        System.out.println("getPages = " + result.getPages());
    }

    @Test
    void testTwo(){
        System.out.println(sysUserMapper.selectByPrimaryKey("admin"));
    }

    @Test
    void testTwoPage(){
        // 分页从1开始计算，这里0和1是一回事
        int pageNum = 1;
        int pageSize = 10;

        //结果分页,PageHelper是分页的关键
        PageHelper.startPage(pageNum, pageSize);
        //此时查询的结果已经是按分页的了
        List<SysUser> userDomains = sysUserMapper.selectAll();
        userDomains.forEach(System.out::println);

        //以下是为了获得分页信息
        System.out.println("PageInfo============");
        PageInfo<SysUser> result = new PageInfo<>(userDomains);
        System.out.println(result);
        System.out.println("getTotal = " + result.getTotal());
        System.out.println("getPages = " + result.getPages());
    }

}
