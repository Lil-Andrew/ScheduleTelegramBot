package org.example.commands;

import org.example.Main;
import org.example.database.DataBase;
import org.example.database.tables.User;
import org.example.processors.ICallbackProcessor;
import org.example.utils.Utils;
import org.hibernate.Session;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class RemoveGroupCommand extends AbstractCommand implements ICallbackProcessor {

    public RemoveGroupCommand() {
        super("removegroup","Remove your current group");
    }

    @Override
    public void execute(Update update) {
        if (!User.isInDatabase(update.getMessage().getFrom().getId())) {
            Utils.sendWarningText("First you have to select group!\n" + "Type /setgroup to start using bot", update.getMessage().getChatId());
            return;
        }
        User user = User.findById(update.getMessage().getFrom().getId());
        SendMessage message = Utils.createSendMessage(update.getMessage().getChatId().toString(), "Clear your current group? 🗑️\n" + "Your group now is: " + user.getGroup());
        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder().build();
        InlineKeyboardRow row = new InlineKeyboardRow();
        InlineKeyboardButton yesButton = InlineKeyboardButton.builder().text("Yes").callbackData(getCommandUpperCase() + "YES_BUTTON").build();
        InlineKeyboardButton noButton = InlineKeyboardButton.builder().text("No").callbackData(getCommandUpperCase() + "NO_BUTTON").build();
        row.add(yesButton);
        row.add(noButton);
        keyboardMarkup.setKeyboard(List.of(row));
        message.setReplyMarkup(keyboardMarkup);
        Utils.executeMethod(message);
    }

    @Override
    public void processCallback(Update update, CallbackQuery callbackQuery) {
        switch (callbackQuery.getData()) {
            case "REMOVEGROUP_YES_BUTTON":
                try (Session session = DataBase.getSession()) {
                    session.beginTransaction();
                    User user = User.findById(callbackQuery.getFrom().getId());
                    user.setGroup(null);
                    user.setGroupId(null);
                    session.merge(user);
                    session.getTransaction().commit();
                } catch (Exception e) {
                    Main.LOGGER.error(e.getMessage());
                }
                Utils.executeMethod(Utils.answerCallbackQuery("You successfully clear your current group", callbackQuery.getId(), 0));
                Utils.executeMethod(Utils.deleteMessage(callbackQuery));
                break;
            case "REMOVEGROUP_NO_BUTTON":
                Utils.executeMethod(Utils.deleteMessage(callbackQuery));
                break;
        }
    }

}
