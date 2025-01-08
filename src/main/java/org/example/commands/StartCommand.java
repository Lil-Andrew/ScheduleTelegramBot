package org.example.commands;

import org.example.database.tables.User;
import org.example.handlers.CommandHandler;
import org.example.processors.ICallbackProcessor;
import org.example.Main;
import org.example.utils.Utils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.lang.Math.toIntExact;

public class StartCommand extends AbstractCommand implements ICallbackProcessor {

    public StartCommand() {
        super("start", "Welcome to the chat!");
    }

    @Override
    public void execute(Update update) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Вітаю! 🎉\n\n"
                        + "Цей бот допоможе вам завжди бути в курсі актуального розкладу на кожен день. 📅\n\n"
                        + "Також ви можете створювати завдання для кожної пари, щоб нічого не забути. 📝\n\n"
                        + "Та увімкнути функцію нагадування про невиконанні завдання і налаштувати на будь який час. 🔊\n\n"
                        + "Для початку використовуйте меню або введіть відповідну команду.")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton
                                .builder()
                                .text("Зрозуміло")
                                .callbackData(getCommandUpperCase() + "GOT_IT_BUTTON")
                                .build())
                        ).build()
                ).build();

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processCallback(Update update, CallbackQuery callbackQuery) {
        String callData = callbackQuery.getData();
        long messageId = callbackQuery.getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        try {
            switch (callData) {
                case  "START_GOT_IT_BUTTON":
                    EditMessageText new_message = EditMessageText.builder()
                            .chatId(chatId)
                            .messageId(toIntExact(messageId))
                            .text("Добре! ✌️\n\n"
                                    + "Тоді можете скористатися меню" +
                                    "\nабо ввести потрібну команду вручну." +
                                    "\n\nЯкщо виникнуть запитання, не соромтеся звертатися до: @Andrrr_Nikol")
                            .replyMarkup(InlineKeyboardMarkup.builder()
                                    .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton
                                            .builder()
                                            .text("Перейти до вибору групи!")
                                            .callbackData(getCommandUpperCase() + "CHOOSE_GROUP")
                                            .build())
                                    ).build()
                            ).build();

                    telegramClient.execute(new_message);
                    break;
                case "START_CHOOSE_GROUP":
                    CommandHandler.getCommand("setgroup").execute(update);
                    if (User.isInDatabase(callbackQuery.getFrom().getId())) {
                        Utils.executeMethod(Utils.answerCallbackQuery("", callbackQuery.getId(), 3));
                        return;
                    }
                    Utils.executeMethod(Utils.answerCallbackQuery("Now you can choose group:\n\n" + "For example, you can type: Ла-23\n\n" + "In order to find all groups that matches that argument.", callbackQuery.getId(), 5, true));
                    break;
            }

        } catch (TelegramApiException e) {
            Main.LOGGER.error(e);
        }
    }
}
