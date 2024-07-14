package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.messages.MessageManager;

/**
 * TogglePointsCommand contains the execute method for when a user toggles location particles.
 */
public class TogglePointsCommand {
    /**
     * Toggles particles for showing the locations for item drop parties (premium feature).
     *
     * @param sender user who sent the command
     *
     * @return true at end of execution
     */
    public boolean execute(CommandSender sender) {
        MessageManager.sendMessage(sender, "premium-feature");
        return true;
    }
}
