package com.example.dao;

import com.example.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by hanqf on 2020/3/16 14:03.
 */


public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> getBooksByUserId(String userId);

}
