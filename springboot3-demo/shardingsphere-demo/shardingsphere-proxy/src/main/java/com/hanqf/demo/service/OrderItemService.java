package com.hanqf.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanqf.demo.entity.OrderItem;
import com.hanqf.demo.mapper.OrderItemMapper;
import org.springframework.stereotype.Service;

/**
 *
 * Created by hanqf on 2025/8/28 17:45.
 */


@Service
public class OrderItemService extends ServiceImpl<OrderItemMapper, OrderItem> {
}
