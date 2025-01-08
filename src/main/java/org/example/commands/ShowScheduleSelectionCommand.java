package org.example.commands;

import org.example.Main;
import org.example.api.managers.CurrentDataManager;
import org.example.api.managers.ScheduleManager;
import org.example.api.model.DaySchedule;
import org.example.api.model.Pair;
import org.example.database.tables.User;
import org.example.processors.ICallbackProcessor;
import org.example.utils.Utils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ShowScheduleSelectionCommand extends AbstractCommand implements ICallbackProcessor {

    public ShowScheduleSelectionCommand() {
        super("scheduleselection", "Show schedule selection list");
    }

    private final HashMap<User, ScheduleManager> userScheduleHashMap = new HashMap<>();

    @Override
    public void execute(Update update) {
        User user = User.findById(update.getMessage().getFrom().getId());
        if (!User.isInDatabase(user) && user.getGroupId() == null) {
            Utils.sendWarningText("First you have to select group!\n" + "Type /setgroup to start using bot", update.getMessage().getChatId());
            return;
        }
        String userId = update.getMessage().getChatId() != null ? update.getMessage().getChatId().toString() : update.getCallbackQuery().getMessage().getChatId().toString();
        SendMessage message = Utils.createSendMessage(userId, "📅 *Schedule Selection*\n\n" + "Please choose a schedule from the options below:");
        message.enableMarkdownV2(true);

        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder().build();
        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(createSingleButtonRow("Today", getCommandUpperCase() + "TODAY_0"));
        rows.add(createSingleButtonRow("Tomorrow", getCommandUpperCase() + "TOMORROW_1"));
        rows.add(createSingleButtonRow("This week", getCommandUpperCase() + "THIS_WEEK_0"));
        rows.add(createSingleButtonRow("First week", getCommandUpperCase() + "FIRST_WEEK_1"));
        rows.add(createSingleButtonRow("Second week", getCommandUpperCase() + "SECOND_WEEK_2"));
        keyboardMarkup.setKeyboard(rows);

        userScheduleHashMap.put(user, Main.scheduleApiClient.getScheduleManagerForGroup(user.getGroupId()));

        message.setReplyMarkup(keyboardMarkup);
        Utils.executeMethod(message);
    }

    private InlineKeyboardRow createSingleButtonRow(String text, String callbackData) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build());
        return row;
    }

    @Override
    public void processCallback(Update update, CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        User user = User.findById(callbackQuery.getFrom().getId());
        if (!userScheduleHashMap.containsKey(user)) {
            //maybe check if user have group? IDK
            userScheduleHashMap.put(user, Main.scheduleApiClient.getScheduleManagerForGroup(user.getGroupId()));
        }
        ScheduleManager scheduleManager = userScheduleHashMap.get(user);
        CurrentDataManager currentDataManager = Main.scheduleApiClient.getCurrentData();

        String[] split = callbackData.split("_");
        String lastElement = split[split.length - 1];
        String weekNumber = Objects.equals(lastElement, "0") ? String.valueOf(currentDataManager.getCurrentWeek()) : String.valueOf(Integer.parseInt(lastElement) - 1);

        List<DaySchedule> weekSchedule;
        if (callbackData.contains("WEEK")) {
            weekSchedule = weekNumber.equals("0") ? scheduleManager.getData().getScheduleFirstWeek() : scheduleManager.getData().getScheduleSecondWeek();
            showSchedule(weekSchedule, callbackQuery, lastElement, weekNumber);
            Utils.executeMethod(Utils.answerCallbackQuery("", callbackQuery.getId(), 2));
        } else if (callbackData.contains("_DAY")) {
            weekSchedule = Objects.equals(split[1], "0") ? scheduleManager.getData().getScheduleFirstWeek() : scheduleManager.getData().getScheduleSecondWeek();
            showSchedule(weekSchedule, callbackQuery, lastElement, split[1]);
            Utils.executeMethod(Utils.answerCallbackQuery("", callbackQuery.getId(), 2));
        } else {
            //get today or tomorrow schedule
            weekSchedule = scheduleManager.getDay(Integer.parseInt(lastElement));
            if (weekSchedule.isEmpty()) {
                Utils.sendWarningText("Schedule is *not available* for the selected *day*", callbackQuery.getMessage().getChatId());
                return;
            }
            showSchedule(weekSchedule, callbackQuery, lastElement, "0");
            Utils.executeMethod(Utils.answerCallbackQuery("", callbackQuery.getId(), 2));
        }
    }

    private void showSchedule(List<DaySchedule> dayScheduleList, CallbackQuery callbackQuery, String lastElement, String weekNumber) {
        if (dayScheduleList.size() < 2) {
            SendMessage message = Utils.createSendMessage(callbackQuery.getMessage().getChatId().toString(), "Schedule for " + (Objects.equals(lastElement, "0") ? "today" : "tomorrow") + ": " + dayScheduleList.getFirst().getDay());
            InlineKeyboardMarkup inlineKeyboardMarkup = createSingleDayKeyboard(dayScheduleList.getFirst());
            message.setReplyMarkup(inlineKeyboardMarkup);
            Utils.executeMethod(message);
        } else {
            int currentDayIndex = 0;
            EditMessageText editMessageText = null;

            if (callbackQuery.getData().contains("_DAY")) {
                currentDayIndex = Integer.parseInt(lastElement);
                editMessageText = EditMessageText.builder()
                        .chatId(callbackQuery.getMessage().getChatId().toString())
                        .messageId(callbackQuery.getMessage().getMessageId())
                        .text("Schedule for " + (Integer.parseInt(weekNumber) + 1) + " week: " + dayScheduleList.get(currentDayIndex).getDay())
                        .build();
            }

            InlineKeyboardMarkup inlineKeyboardMarkup = createSingleDayKeyboard(dayScheduleList.get(currentDayIndex));
            InlineKeyboardRow inlineKeyboardRow = new InlineKeyboardRow();

            if (currentDayIndex > 0) {
                inlineKeyboardRow.add(InlineKeyboardButton.builder().text("⬅️ Previous")
                        .callbackData(getCommandUpperCase() + weekNumber + "_PREV_DAY_" + (currentDayIndex - 1)).build());
            }
            if (currentDayIndex < dayScheduleList.size() - 1) {
                inlineKeyboardRow.add(InlineKeyboardButton.builder().text("➡️ Next")
                        .callbackData(getCommandUpperCase() + weekNumber + "_NEXT_DAY_" + (currentDayIndex + 1)).build());
            }
            inlineKeyboardMarkup.getKeyboard().add(inlineKeyboardRow);

            SendMessage message = new SendMessage(callbackQuery.getMessage().getChatId().toString(), "Schedule for " + (Integer.parseInt(weekNumber) + 1) + " week: " + dayScheduleList.get(currentDayIndex).getDay());
            message.setReplyMarkup(inlineKeyboardMarkup);

            if (editMessageText != null) {
                editMessageText.setReplyMarkup(inlineKeyboardMarkup);
                Utils.executeMethod(editMessageText);
                return;
            }
            Utils.executeMethod(message);
        }
    }

    private InlineKeyboardMarkup createSingleDayKeyboard(DaySchedule daySchedule) {
        List<InlineKeyboardRow> inlineKeyboardRows = new ArrayList<>();
        for (Pair pair : daySchedule.getPairs()) {
            inlineKeyboardRows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                    .text(pair.getTime() + "|" + pair.getName())
                    .callbackData(getCommandUpperCase() + "PAIR_")
                    .build()));
        }
        return new InlineKeyboardMarkup(inlineKeyboardRows);
    }
}
