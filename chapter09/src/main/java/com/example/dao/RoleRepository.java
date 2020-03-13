package com.example.dao;

import com.example.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hanqf on 2020/3/13 15:06.
 */


public interface RoleRepository  extends JpaRepository<Role, Long> {
}
