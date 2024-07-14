package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * ListCommand contains the execute method for when a user views the list of parties.
 */
public class ListCommand {
    private final Main main;
    private final PermissionsManager permissionsManager;

    /**
     * Constructor for ListCommand.
     *
     * @param main plugin class
     */
    public ListCommand(Main main) {
        this.main = main;
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Lists all parties to the user.
     *
     * @param sender user who sent the command
     * @param args command args possibly containing page number
     *
     * @return true at end of execution
     */
    public boolean execute(CommandSender sender, String[] args) {
        if (!permissionsManager.hasListCmdPerm(sender)) {
            return true;
        }

        // show first page if no page number provided
        int pageNum = 1;
        try {
            pageNum = Integer.parseInt(args[1]);
        } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
        }

        MessageManager.showParties(sender, main.getPartyManager().getPartyNames(), pageNum, main.getConfigManager().getConfig().getInt("parties-per-page", 10));
        return true;
    }
}

