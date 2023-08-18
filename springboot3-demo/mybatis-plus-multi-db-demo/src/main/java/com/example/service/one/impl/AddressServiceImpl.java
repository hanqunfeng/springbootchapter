package com.example.service.one.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.model.one.Address;
import com.example.service.one.AddressService;
import com.example.dao.one.AddressMapper;
import org.springframework.stereotype.Service;

/**
* @author hanqf
* @description 针对表【address】的数据库操作Service实现
* @createDate 2023-08-18 09:46:59
*/
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address>
    implements AddressService{

}




