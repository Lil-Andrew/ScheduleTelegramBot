package org.example.setup;

import org.example.Main;
import org.example.commands.AbstractCommand;
import org.example.handlers.CommandHandler;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Map;

public class BotMenu {

    static {
        try {
            Main.BOTSERVICE.getTelegramClient().execute(DeleteMyCommands.builder().build());
            Main.LOGGER.info("(BotMenu) Command was cleared from bot command list");
        } catch (TelegramApiException e) {
            Main.LOGGER.error("(BotMenu) Failed to clear bot commands: {}", e.getMessage(), e);
        }
    }

    public static void botMenuShow() throws TelegramApiException {
        Map<String, AbstractCommand> commandMap = CommandHandler.getCommandsMap();
        if (commandMap.isEmpty()) {
            Main.LOGGER.warn("(BotMenu) No commands found for BotMenu");
        }

        ArrayList<BotCommand> botCommands = new ArrayList<>();
        for (AbstractCommand command : commandMap.values()) {
            if (command.getCommandDescription() != null) {
                botCommands.add(new BotCommand(command.getCommandName(), command.getCommandDescription()));
            }
        }

        try {
            Main.BOTSERVICE.getTelegramClient().execute(SetMyCommands.builder().commands(botCommands).build());
            Main.LOGGER.info("(BotMenu) BotCommand was successfully loaded!");
        } catch (TelegramApiException e) {
            Main.LOGGER.error("(BotMenu) Failed to set bot commands: {}", e.getMessage(), e);
        }
    }
}
