package com.hanqf.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanqf.demo.entity.Address;
import com.hanqf.demo.mapper.AddressMapper;
import org.springframework.stereotype.Service;

/**
 *
 * Created by hanqf on 2025/8/27 17:37.
 */

@Service
public class AddressService extends ServiceImpl<AddressMapper, Address> {
}
