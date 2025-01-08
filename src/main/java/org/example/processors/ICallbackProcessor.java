package org.example.processors;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ICallbackProcessor {
    void processCallback(Update update, CallbackQuery callbackQuery);
}
