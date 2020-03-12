package com.example.dao;/**
 * Created by hanqf on 2020/3/12 11:36.
 */


import com.example.model.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hanqf
 * @date 2020/3/12 11:36
 */
@Mapper
public interface XmlAddressMapper
{
    public Address getAddressByUserId(Long userId);

    public Address getAddressAndUserByUserId(Long userId);
}
