package com.example.dao.two;

import com.example.model.two.City;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by hanqf on 2020/3/16 14:03.
 */


public interface CityRepository extends MongoRepository<City, String> {

    List<City> getCitiesByProvinceId(String provinceId);
}
