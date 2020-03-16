package com.example.dao;

import com.example.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by hanqf on 2020/3/16 14:03.
 */


public interface AddressRepository extends MongoRepository<Address, String> {

    List<Address> getAddressesByUserId(String userId);
}
