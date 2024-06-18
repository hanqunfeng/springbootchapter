package com.hanqf.demo.MemoryStore;

import com.hanqf.demo.dao.ChatMemoryJpaRepository;
import com.hanqf.demo.model.ChatMemoryRecord;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

/**
 * <h1>自定义对话历史存储类</h1>
 * Created by hanqf on 2024/6/17 16:14.
 * <p>
 * 这里使用 mysql 存储对话历史
 */

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private ChatMemoryJpaRepository chatMemoryJpaRepository;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        ChatMemoryRecord chatMemoryRecord = chatMemoryJpaRepository.getChatMemoryRecordByChatMemoryId((String) memoryId);
        if (chatMemoryRecord == null) {
            chatMemoryRecord = new ChatMemoryRecord();
            chatMemoryRecord.setChatMemoryId((String) memoryId);
            chatMemoryJpaRepository.save(chatMemoryRecord);
        }
        String json = chatMemoryRecord.getMemoryJson();
        return messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        ChatMemoryRecord chatMemoryRecord = chatMemoryJpaRepository.getChatMemoryRecordByChatMemoryId((String) memoryId);
        if (chatMemoryRecord != null) {
            String json = messagesToJson(messages);
            chatMemoryRecord.setMemoryJson(json);
            chatMemoryJpaRepository.save(chatMemoryRecord);
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        ChatMemoryRecord chatMemoryRecord = chatMemoryJpaRepository.getChatMemoryRecordByChatMemoryId((String) memoryId);
        if (chatMemoryRecord != null) {
            chatMemoryJpaRepository.delete(chatMemoryRecord);
        }

    }
}
