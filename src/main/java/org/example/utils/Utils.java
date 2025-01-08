package org.example.utils;

import org.example.Main;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Utils {
    private static OkHttpTelegramClient client = Main.BOTSERVICE.getTelegramClient();

    public static SendMessage createSendMessage(String chatId, String text) {
        return new SendMessage(chatId, text);
    }
    public static DeleteMessage deleteMessage(CallbackQuery callbackQuery) {
        return DeleteMessage.builder().chatId(callbackQuery.getMessage().getChatId()).messageId(callbackQuery.getMessage().getMessageId()).build();
    }

    public static DeleteMessage deleteMessage(Long chatId, Integer messageId) {
        return DeleteMessage.builder().chatId(chatId).messageId(messageId).build();
    }

    public static AnswerCallbackQuery answerCallbackQuery(String text, String callbackId, Integer cacheTime) {
        return AnswerCallbackQuery.builder().text(text).callbackQueryId(callbackId).cacheTime(cacheTime != null ? cacheTime : 0).build();
    }

    public static AnswerCallbackQuery answerCallbackQuery(String text, String callbackId, Integer cacheTime, Boolean showAlert) {
        showAlert = showAlert != null && showAlert;
        return AnswerCallbackQuery.builder().text(text).callbackQueryId(callbackId).cacheTime(cacheTime).showAlert(showAlert).build();
    }

    public static void executeMethod(BotApiMethod<?> botApiMethod) {
        try {
            client.execute(botApiMethod);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendWarningText(String text, Long chatId) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        message.enableMarkdownV2(true);
        executeMethod(message);
    }
}

