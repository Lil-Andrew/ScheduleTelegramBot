package org.example.handlers;

import org.example.commands.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

import static org.example.Main.LOGGER;

public class CommandHandler {
    public static final String commandSymbol = "/";
    private static final Map<String, AbstractCommand> commandsMap = new HashMap<>();

    public void validateCommand(Update update) {
        String message = update.getMessage().getText();
        String commandName = message.substring(1);
        AbstractCommand command = commandsMap.get(commandName);
        if (command.getCommandName().equals(commandName)) {
            command.execute(update);
        }
    }

    public static void initializeCommands() {
        LOGGER.info("(CommandHandler) Command initialization...");
        new SetGroupCommand();
        new RemoveGroupCommand();
        new StartCommand();
        new ShowScheduleSelectionCommand();

        //? add new commands

        LOGGER.info("(CommandHandler) Command initialization finished: Total commands: {}", commandsMap.size());
    }

    public static void addCommandToMap(AbstractCommand command) {
        commandsMap.put(command.getCommandName(), command);
    }

    public static AbstractCommand getCommand(String commandName) {
        return commandsMap.get(commandName);
    }

    public static Map<String, AbstractCommand> getCommandsMap() {
        return commandsMap;
    }
}
