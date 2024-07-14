package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.messages.MessageManager;

/**
 * UnsetPointCommand contains the execute method for when a user unsets a location for item drop party.
 */
public class UnsetPointCommand {
    /**
     * Unsets a location for item drop parties (premium feature).
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
