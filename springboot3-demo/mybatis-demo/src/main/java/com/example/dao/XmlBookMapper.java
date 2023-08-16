package com.example.dao;

import com.example.model.Book;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by hanqf on 2020/3/12 11:37.
 */

@Mapper
public interface XmlBookMapper
{

    public List<Book> getBooksByUserId(Long userId);

    public List<Book> getBooksAndUserByUserId(Long userId);
}
