package com.hanqf.demo.dao;

import com.hanqf.demo.model.ChatMemoryRecord;
import com.hanqf.support.jpa.BaseJpaRepository;

/**
 * Description: <用户解梦请求表 JpaRepository>. <br>
 * <p>
 * generate time:2023-6-9 17:56:32
 *
 * @author hanqf
 * @version V1.0
 */
public interface ChatMemoryJpaRepository extends BaseJpaRepository<ChatMemoryRecord, Long> {

    /**
     * 根据对话id获取对话记录
     */
    ChatMemoryRecord getChatMemoryRecordByChatMemoryId(String chatMemoryId);

    /**
     * 根据用户id删除数据
//     */
//    @Modifying
//    @Query(value = "delete from tbl_dream_record where user_id = :userId", nativeQuery = true)
//    void deleteDateAllByUserId(Long userId);
}
