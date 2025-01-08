package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.api.ScheduleApiClient;
import org.example.handlers.CommandHandler;
import org.example.handlers.ReplyHandler;
import org.example.service.BotService;
import org.example.setup.BotMenu;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static final Dotenv DOTENV = Dotenv.load();
    public static final Logger LOGGER = LogManager.getLogger();
    public static ScheduleApiClient scheduleApiClient = new ScheduleApiClient();
    public static BotService BOTSERVICE = null;

    public static void main(String[] args) {
        try {
            BOTSERVICE = new BotService(); // starting bot and getting updates and handle them.
            scheduleApiClient.getData();
            CommandHandler.initializeCommands();
            ReplyHandler.initialize();
            BotMenu.botMenuShow();

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
