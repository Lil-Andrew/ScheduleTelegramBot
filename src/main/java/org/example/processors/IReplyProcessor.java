package org.example.processors;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IReplyProcessor {
    void processReply(Update update, String stateWithBot, String userAnswer);
}
