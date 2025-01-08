package org.example.commands;

import org.example.Main;
import org.example.handlers.CommandHandler;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand {
    private final String commandName;
    private final String commandDescription;
    protected OkHttpTelegramClient telegramClient;
    protected List<String> stateList = new ArrayList<>();

    public AbstractCommand(String commandName, String commandDescription) {
        this.telegramClient = Main.BOTSERVICE.getTelegramClient();
        this.commandName = commandName;
        this.commandDescription = commandDescription;
        CommandHandler.addCommandToMap(this);
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getStateList() {
        return stateList;
    }

    protected String getCommandUpperCase() {
        return getCommandName().toUpperCase() + "_";
    }

    public abstract void execute(Update update);
}
