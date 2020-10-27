package com.example.dao;


import com.example.model.Artical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * <h1>数据接口</h1>
 * Created by hanqf on 2020/10/22 09:53.
 */

@RepositoryRestResource(path = "articals")
public interface ArticalRepository extends JpaRepository<Artical, Long>, JpaSpecificationExecutor<Artical> {


}
