package com.example.dao;

import com.example.model.LockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * <p></p>
 * Created by hanqf on 2020/7/17 14:57.
 */


public interface LockInfoRepository extends JpaRepository<LockInfo, Long> {

    LockInfo findByTag(String tag);

    void deleteByTag(String tag);

    void deleteByTagAndExpirationTime(String tag, LocalDateTime expirationTime);
}
