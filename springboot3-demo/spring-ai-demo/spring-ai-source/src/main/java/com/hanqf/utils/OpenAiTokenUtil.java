package com.hanqf.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2024/5/24 17:40.
 */


public class OpenAiTokenUtil {

    private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

    public static int countMessageTokens(
            String model, // gpt-4 or gpt-3.5-turbo
            List<Message> messages // consists of role, content and an optional name
    ) {
        Encoding encoding = registry.getEncodingForModel(model).orElseThrow();
        int tokensPerMessage;
        int tokensPerName;
        if (model.startsWith("gpt-4")) {
            tokensPerMessage = 3;
            tokensPerName = 1;
        } else if (model.startsWith("gpt-3.5-turbo")) {
            tokensPerMessage = 4; // every message follows <|start|>{role/name}\n{content}<|end|>\n
            tokensPerName = -1; // if there's a name, the role is omitted
        } else {
            throw new IllegalArgumentException("Unsupported model: " + model);
        }

        int sum = 0;
        for (final var message : messages) {
            sum += tokensPerMessage;
            sum += encoding.countTokens(message.getContent());
            sum += encoding.countTokens(message.getMessageType().getValue());
            if (message.getProperties().get("name") != null) {
                sum += encoding.countTokens(message.getProperties().get("name").toString());
                sum += tokensPerName;
            }
        }

        sum += 3; // every reply is primed with <|start|>assistant<|message|>

        return sum;
    }
}
