package org.example;

import org.example.handlers.CallBackHandler;
import org.example.handlers.CommandHandler;
import org.example.handlers.ReplyHandler;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MyTestBot implements LongPollingSingleThreadUpdateConsumer {
    private final CommandHandler commandHandler = new CommandHandler();
    private final CallBackHandler callBackHandler = new CallBackHandler();
    private final ReplyHandler replyHandler = new ReplyHandler();

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith(CommandHandler.commandSymbol)) {
            commandHandler.validateCommand(update);
        } else if (update.hasCallbackQuery()) {
            callBackHandler.validateCallBack(update);
        } else if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().isReply()) {
            replyHandler.validateReply(update);
        }
    }
}
