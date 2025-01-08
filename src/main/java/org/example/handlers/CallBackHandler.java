package org.example.handlers;

import org.example.processors.ICallbackProcessor;
import org.example.commands.AbstractCommand;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CallBackHandler {

    public void validateCallBack(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        String textBeforeUnderscore = callbackData.split("_")[0];
        AbstractCommand command = CommandHandler.getCommand(textBeforeUnderscore.toLowerCase());
        if (command instanceof ICallbackProcessor) {
            ((ICallbackProcessor) command).processCallback(update, callbackQuery);
        }
    }

}
