package com.example.repository;

import com.example.model.Person;
import org.springframework.data.repository.CrudRepository;

/**
 * <h1>redisRepository</h1>
 * Created by hanqf on 2020/10/18 21:59.
 */

public interface PersonRepository extends CrudRepository<Person,String> {
}
