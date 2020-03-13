package com.example.dao;

import com.example.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hanqf on 2020/3/13 10:46.
 */


public interface BookRepository extends JpaRepository<Book, Long> {
}
