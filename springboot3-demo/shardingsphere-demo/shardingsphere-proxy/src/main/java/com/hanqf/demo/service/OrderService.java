package com.hanqf.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanqf.demo.entity.Order;
import com.hanqf.demo.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
 *
 * Created by hanqf on 2025/8/28 17:45.
 */


@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {
}
