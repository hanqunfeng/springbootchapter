package com.example.dao;

import com.example.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hanqf on 2020/3/13 10:45.
 */


public interface AddressRepository extends JpaRepository<Address, Long> {
}
