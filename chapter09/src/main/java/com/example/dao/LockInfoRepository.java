package com.example.dao;

import com.example.model.LockInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * <p></p>
 * Created by hanqf on 2020/7/17 14:57.
 */


public interface LockInfoRepository extends JpaRepository<LockInfo, Long> {

    LockInfo findByTag(String tag);

    void deleteByTag(String tag);

    void deleteByTagAndExpirationTime(String tag, LocalDateTime expirationTime);

    //@Query(value = "update lock_info set  expiration_time = :newExpirationTime where tag = :tag and expiration_time = :oldExpirationTime",nativeQuery = true)
    @Query(value = "update LockInfo set  expirationTime = :newExpirationTime where tag = :tag and expirationTime = :oldExpirationTime")
    @Modifying
    Integer updateByTagAndExpirationTime(@Param("tag") String tag, @Param("newExpirationTime") LocalDateTime newExpirationTime, @Param("oldExpirationTime") LocalDateTime oldExpirationTime);
}
