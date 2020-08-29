package org.affluentproductions.jdabase.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.api.command.AffluentCommand;
import org.affluentproductions.jdabase.manager.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CommandListener extends ListenerAdapter {

    private final JDABase jdaBase;
    private boolean botsAllowed = false;
    private String prefix = "a-";
    private Consumer<MessageReceivedEvent> onCooldown = mre -> mre.getChannel().sendMessage(
            mre.getAuthor().getAsMention() + " Please wait, you are on cooldown!").queue();

    public CommandListener(JDABase jdaBase) {
        this.jdaBase = jdaBase;
        System.out.println("Command Listener initialized - listening...");
    }

    /**
     * @param onCooldown Specify what happens when a command is being executed, when the executor is on cooldown.
     *                   Default: Response: {@code @mention Please wait, you are on cooldown!}
     */
    public void setOnCooldown(Consumer<MessageReceivedEvent> onCooldown) {
        this.onCooldown = onCooldown;
    }

    /**
     * @param prefix Specify what prefix the bot should listen to.
     *               Default: {@code a-}
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @param botsAllowed Specify if commands from bot-users should be handled. Set this to {@code true} if you want
     *                    bot users to be able to run commands too.
     *                    Default: {@code false}
     */
    public void setBotsAllowed(boolean botsAllowed) {
        this.botsAllowed = botsAllowed;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot() && !botsAllowed) return;
        Message message = event.getMessage();
        String raw = message.getContentRaw();
        CommandManager commandManager = jdaBase.getCommandManager();
        String[] raw_split = raw.split("\\s+");
        boolean isPrefixed = raw.startsWith(prefix);
        if (isPrefixed) {
            String command = raw_split[0];
            if (command.length() > prefix.length()) {
                String commandName = command.substring(prefix.length());
                if (commandManager.isCommand(commandName)) {
                    final String userId = author.getId();
                    jdaBase.getCommandManager().getExecutorService().submit(() -> {
                        AffluentCommand affluentCommand = commandManager.getCommand(commandName);
                        final String cooldownName = "command_cooldown_" + affluentCommand.getCommandName()
                                                                                         .toLowerCase();
                        boolean isOnCooldown = jdaBase.getCooldownUtil().hasUserCooldown(userId, cooldownName);
                        if (isOnCooldown) {
                            onCooldown.accept(event);
                            return;
                        }
                        double cooldown = affluentCommand.getCooldown();
                        long cooldownEnd = System.currentTimeMillis() + new Double(cooldown * 1000).longValue();
                        jdaBase.getCooldownUtil().setUserCooldown(userId, cooldownName, cooldownEnd, false);
                        affluentCommand.onCommand(
                                new AffluentCommand.CommandEvent(jdaBase, affluentCommand, event.getAuthor(), event));
                    });
                }
            }
        }
    }
}
