package com.example.dao.two;

import com.example.model.two.Province;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hanqf on 2020/3/16 14:03.
 */


public interface ProvinceRepository extends MongoRepository<Province, String> {

}
