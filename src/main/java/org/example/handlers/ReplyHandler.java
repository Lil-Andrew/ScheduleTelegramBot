package org.example.handlers;

import org.example.Main;
import org.example.commands.AbstractCommand;
import org.example.database.DataBase;
import org.example.database.tables.User;
import org.example.processors.IReplyProcessor;
import org.hibernate.Session;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReplyHandler {
    private static List<AbstractCommand> commands;

   public static void initialize() {
       commands = CommandHandler.getCommandsMap().values().stream()
               .filter(command -> command instanceof IReplyProcessor)
               .collect(Collectors.toCollection(ArrayList::new));
   }

    public void validateReply(Update update) {
        try(Session session = DataBase.getSession()) {
            User user = session.createQuery("From User Where userId = :userId", User.class)
                    .setParameter("userId", update.getMessage().getFrom().getId()).uniqueResult();
            if (user.getStateWithBot() == null) return;
            String userAnswer = update.getMessage().getText();
            String stateWithBot = user.getStateWithBot();
            for (AbstractCommand command : commands) {
                if (command.getStateList().contains(stateWithBot)) {
                    ((IReplyProcessor)command).processReply(update, stateWithBot, userAnswer);
                    break;
                }
            }
        } catch (Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
    }
}
