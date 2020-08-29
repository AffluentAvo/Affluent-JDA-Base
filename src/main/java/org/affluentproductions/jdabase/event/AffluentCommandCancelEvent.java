package org.affluentproductions.jdabase.event;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.api.command.AffluentCommand;
import org.affluentproductions.jdabase.enums.Cancel;

public class AffluentCommandCancelEvent extends AffluentEvent {

    private final User author;
    private final AffluentCommand command;
    private final Cancel cancel;
    private final MessageReceivedEvent messageReceivedEvent;

    public AffluentCommandCancelEvent(JDABase jdaBase, AffluentCommand command, User author, Cancel cancel,
                                      MessageReceivedEvent messageReceivedEvent) {
        super(jdaBase);
        this.author = author;
        this.command = command;
        this.cancel = cancel;
        this.messageReceivedEvent = messageReceivedEvent;
    }

    public AffluentCommand getCommand() {
        return command;
    }

    public User getAuthor() {
        return author;
    }

    public Cancel getCancel() {
        return cancel;
    }

    public MessageReceivedEvent getMessageReceivedEvent() {
        return messageReceivedEvent;
    }
}