package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * HelpCommand contains the execute method for when a user inputs the command to get help for the plugin.
 */
public class HelpCommand {
    private final PermissionsManager permissionsManager;

    /**
     * Constructor for HelpCommand.
     *
     * @param main plugin class
     */
    public HelpCommand(Main main) {
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Shows a list of helpful commands to the user.
     *
     * @param sender user who sent the command
     * @param args command arguments
     *
     * @return true at end of execution
     */
    public boolean execute(CommandSender sender, String[] args) {
        if (!permissionsManager.hasHelpCmdPerm(sender)) {
            return true;
        }

        try {
            int pageNum = Integer.parseInt(args[1]);
            MessageManager.showHelpBoard(sender, pageNum);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            MessageManager.showHelpBoard(sender, 1);
        }

        return true;
    }
}

