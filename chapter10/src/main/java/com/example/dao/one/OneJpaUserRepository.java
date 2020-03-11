package com.example.dao.one;


import com.example.model.one.OneUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by hanqf on 2020/3/8 16:04.
 */


public interface OneJpaUserRepository extends JpaRepository<OneUser, Long> {


    //这里表明要用实体名称，因为是hql，而且应为设置了实体的别名，所以这里要用@Entity(name = "oneUser")
    @Query("select p from oneUser p")
    public Page<OneUser> findAllByPage(Pageable pageable);
}
