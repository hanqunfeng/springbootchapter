package com.example.mapper;/**
 * Created by hanqf on 2020/3/11 11:27.
 */


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author hanqf
 * @date 2020/3/11 11:27
 */
//特别注意，该接口不能被扫描到，否则会出错
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
