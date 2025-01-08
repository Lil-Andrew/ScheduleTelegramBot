package org.example.commands;

import org.example.Main;
import org.example.api.model.Group;
import org.example.database.DataBase;
import org.example.database.tables.User;
import org.example.processors.ICallbackProcessor;
import org.example.processors.IReplyProcessor;
import org.example.utils.Utils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetGroupCommand extends AbstractCommand implements IReplyProcessor, ICallbackProcessor {

    public SetGroupCommand() {
        super("setgroup", "Choose your group");

        stateList.add(getCommandUpperCase() + "WAITING_FOR_GROUP_NAME");
    }

    @Override
    public void execute(Update update) {
        org.telegram.telegrambots.meta.api.objects.User chatUser = update.getMessage() != null ? update.getMessage().getFrom() : update.getCallbackQuery().getFrom();
        Long chatId = chatUser.getId();
        //if user type first time, add him to database
        if (!User.isInDatabase(chatId)) {
            try (Session session = DataBase.getSession()) {
                Transaction transaction = session.beginTransaction();

                User newUser = new User();
                newUser.setUserId(chatUser.getId());
                newUser.setChatId(update.getMessage().getChatId());
                newUser.setUsername(chatUser.getUserName());
                newUser.setFirstName(chatUser.getFirstName());
                newUser.setLastName(chatUser.getLastName());

                session.persist(newUser);
                transaction.commit();
            } catch (Exception e) {
                Main.LOGGER.error(e);
            }
        }

        SendMessage sendMessage = Utils.createSendMessage(chatId.toString(), "Enter the group name: ");

        if (update.getMessage() != null) {
            sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
        }

        ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
        forceReply.setForceReply(true);
        forceReply.setInputFieldPlaceholder("Type group name here");
        sendMessage.setReplyMarkup(forceReply);

        User.setStateWithBotForUser(chatUser.getId(), stateList.getFirst());
        Utils.executeMethod(sendMessage);
    }

    @Override
    public void processReply(Update update, String stateWithBot, String userAnswer) {
        switch (stateWithBot) {
            case "SETGROUP_WAITING_FOR_GROUP_NAME":
                List<Group> matchesGroup = Main.scheduleApiClient.getGroupsManager().getGroupsByContainsText(userAnswer);
                if (matchesGroup != null && !matchesGroup.isEmpty()) {
                    paginateGroups(update, matchesGroup, 0);
                } else {
                    SendMessage message = Utils.createSendMessage(update.getMessage().getChatId().toString(), "No matching groups found.");
                    try {
                        telegramClient.execute(message);
                    } catch (TelegramApiException e) {
                        Main.LOGGER.error(("SetGroupCommand processReply ") + "{}", e.getMessage());
                    }
                }
                break;
        }
    }

    private void paginateGroups(Update update, List<Group> matchesGroup, int pageIndex) {
        int pageSize = 8;
        int totalPages = (int) Math.ceil((double) matchesGroup.size() / pageSize);

        if (pageIndex < 0) pageIndex = 0;
        if (pageIndex >= totalPages) pageIndex = totalPages - 1;

        int start = pageIndex * pageSize;
        int end = Math.min(start + pageSize, matchesGroup.size());
        List<Group> pageGroups = matchesGroup.subList(start, end);

        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder().build();
        List<InlineKeyboardRow> rowList = new ArrayList<>();

        //add buttons for group
        for (int i = 0; i < pageGroups.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(pageGroups.get(i).getName() + " " + pageGroups.get(i).getFaculty());
            button.setCallbackData(getCommandUpperCase() + "GROUP_SELECT_" + pageGroups.get(i).getId());
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(button);
            rowList.add(row);
        }

        //add navigation buttons
        InlineKeyboardRow navigationRow = new InlineKeyboardRow();
        if (pageIndex > 0) {
            InlineKeyboardButton prevButton = InlineKeyboardButton.builder().text("Previous page").build();
            prevButton.setCallbackData(getCommandUpperCase() + "NAVIGATE_PREVIOUS_" + (pageIndex - 1));
            navigationRow.add(prevButton);
        }
        if (pageIndex < totalPages - 1) {
            InlineKeyboardButton nextButton = InlineKeyboardButton.builder().text("Next page").build();
            nextButton.setCallbackData(getCommandUpperCase() + "NAVIGATE_NEXT_" + (pageIndex + 1));
            navigationRow.add(nextButton);
        }
        if (!navigationRow.isEmpty()) {
            rowList.add(navigationRow);
        }

        InlineKeyboardRow controlsRow = new InlineKeyboardRow();
        InlineKeyboardButton changeButton = new InlineKeyboardButton("✏️ Change group");
        changeButton.setCallbackData(getCommandUpperCase() + "CHANGE_GROUP");
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("❌ Cancel");
        cancelButton.setCallbackData(getCommandUpperCase() + "CANCEL");
        controlsRow.add(changeButton);
        controlsRow.add(cancelButton);

        rowList.add(controlsRow);

        keyboardMarkup.setKeyboard(rowList);

        userMap.put(update.getMessage() != null ? update.getMessage().getChatId() : update.getCallbackQuery().getMessage().getChatId(), matchesGroup);

        try {
            if (update.getCallbackQuery() != null) {
                EditMessageText editMessageText = new EditMessageText("Select your group from the list below");
                editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setReplyMarkup(keyboardMarkup);
                telegramClient.execute(editMessageText);

            } else {
                SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), "Select your group from the list below");
                sendMessage.setReplyMarkup(keyboardMarkup);
                telegramClient.execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            Main.LOGGER.error(("SetGroupCommand paginateGroups ") + "{}", e.getMessage());
        }
    }

    private final Map<Long, List<Group>> userMap = new HashMap<>();

    @Override
    public void processCallback(Update update, CallbackQuery callbackQuery) {
        String callData = callbackQuery.getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        try {
            if (callData.contains("NAVIGATE_PREVIOUS") || callData.contains("NAVIGATE_NEXT")) {
                paginateGroups(update, userMap.get(chatId), Integer.parseInt(getLastElementOfArray(callData)));
            } else if (callData.contains("CHANGE_GROUP")) {
                changeGroup(callbackQuery);
            } else if (callData.contains("GROUP_SELECT")) {
                selectGroup(callbackQuery);
            } else if (callData.contains("CANCEL")) {
                deleteGroupList(callbackQuery);
            } else if (callData.contains("YES_BUTTON")) {
                String groupId = getLastElementOfArray(callData);
                Group group = Main.scheduleApiClient.getGroupsManager().getGroupById(groupId);
                try(Session session = DataBase.getSession()) {
                    session.beginTransaction();
                    User user = User.findById(callbackQuery.getFrom().getId());
                    user.setGroup(group.getName());
                    user.setGroupId(groupId);
                    user.setStateWithBot(null);
                    session.merge(user);
                    session.getTransaction().commit();
                }
                telegramClient.execute(Utils.answerCallbackQuery("You have selected a group: " + group.getName(), callbackQuery.getId(), 0));
                telegramClient.execute(Utils.deleteMessage(callbackQuery));
            } else if (callData.contains(getCommandUpperCase() + "NO_BUTTON")) {
                telegramClient.execute(Utils.deleteMessage(callbackQuery));
            }

        } catch (Exception e) {
            Main.LOGGER.error(("SetGroupCommand processCallback ") + "{}", e.getMessage());
        }
    }

    public void deleteGroupList(CallbackQuery callbackQuery) {
        try {
            DeleteMessage delete = Utils.deleteMessage(callbackQuery);
            telegramClient.execute(delete);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeGroup(CallbackQuery callbackQuery) {
        deleteGroupList(callbackQuery);
        SendMessage message = Utils.createSendMessage(callbackQuery.getMessage().getChatId().toString(), "You can type new group name:");
        ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
        forceReply.setForceReply(true);
        forceReply.setInputFieldPlaceholder("New group name");
        message.setReplyMarkup(forceReply);
        Utils.executeMethod(message);
    }

    public void selectGroup(CallbackQuery callbackQuery) throws TelegramApiException {
        String groupId = getLastElementOfArray(callbackQuery.getData());
        Group group = Main.scheduleApiClient.getGroupsManager().getGroupById(groupId);
        Boolean userHasGroup = User.findById(callbackQuery.getFrom().getId()).getGroup() != null;
        SendMessage message = Utils.createSendMessage(callbackQuery.getMessage().getChatId().toString(),  userHasGroup ? "You want to change your current group? To:\n" + group.getName() + " | " + group.getFaculty()
                : "Are you sure that you want to choose this group?:\n" + group.getName() + " | " + group.getFaculty());

        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder().build();
        InlineKeyboardRow row = new InlineKeyboardRow();

        InlineKeyboardButton yesButton = InlineKeyboardButton.builder().text("Yes").callbackData(getCommandUpperCase() + "YES_BUTTON_" + groupId).build();
        InlineKeyboardButton noButton = InlineKeyboardButton.builder().text("No").callbackData(getCommandUpperCase() + "NO_BUTTON").build();
        row.add(yesButton);
        row.add(noButton);
        keyboardMarkup.setKeyboard(List.of(row));
        message.setReplyMarkup(keyboardMarkup);

        Utils.executeMethod(Utils.answerCallbackQuery("", callbackQuery.getId(), 5));
        telegramClient.execute(message);
    }

    public String getLastElementOfArray(String input) {
        String[] splittedData = input.split("_");
        return splittedData[splittedData.length - 1];
    }

}
