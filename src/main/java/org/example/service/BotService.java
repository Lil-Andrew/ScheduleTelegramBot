package org.example.service;

import org.example.Main;
import org.example.MyTestBot;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.example.Main.*;

public class BotService {
    private final TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
    private final OkHttpTelegramClient telegramClient = new OkHttpTelegramClient(Main.DOTENV.get("BOT_TOKEN"));

    public BotService() throws TelegramApiException {
        botsApplication.registerBot(DOTENV.get("BOT_TOKEN"), new MyTestBot());
        LOGGER.info("(BotService) Bot started");
    }

    public boolean isBotRunning() {
        return botsApplication.isRunning();
    }

    public OkHttpTelegramClient getTelegramClient() {
        return telegramClient;
    }
}
