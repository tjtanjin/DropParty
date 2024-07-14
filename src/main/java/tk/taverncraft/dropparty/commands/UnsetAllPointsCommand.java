package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.messages.MessageManager;

/**
 * UnsetAllPointsCommand contains the execute method for when a user unsets all locations for item drop party.
 */
public class UnsetAllPointsCommand {
    /**
     * Unsets all locations for item drop parties (premium feature).
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
