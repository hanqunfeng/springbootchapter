package com.example.dao.one;

import com.example.model.one.Address;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author hanqf
* @description 针对表【address】的数据库操作Mapper
* @createDate 2023-08-17 17:08:44
* @Entity com.example.model.one.Address
*/
public interface AddressMapper {

    List<Address> selectAll();
    Address selectOneById(@Param("id") Long id);

    int insertSelective(Address address);

    List<Address> selectAllByCity(@Param("city") String city);
}




