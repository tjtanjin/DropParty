package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * DeleteCommand contains the execute method for when a user deletes a party.
 */
public class DeleteCommand {
    private final Main main;
    private final PermissionsManager permissionsManager;

    /**
     * Constructor for DeleteCommand.
     *
     * @param main plugin class
     */
    public DeleteCommand(Main main) {
        this.main = main;
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Deletes a new party with given name.
     *
     * @param sender user who sent the command
     * @param args command arguments
     *
     * @return true at end of execution
     */
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageManager.sendMessage(sender, "invalid-command");
            return true;
        }

        if (!permissionsManager.hasDeleteCmdPerm(sender)) {
            return true;
        }

        String partyName = args[1];

        // check if party to delete exist
        if (!main.getPartyManager().partyExist(partyName)) {
            MessageManager.sendMessage(sender, "party-not-exist",
                new String[]{"%party%"},
                new String[]{partyName});
            return true;
        }

        main.getPartyManager().removeParty(partyName);
        MessageManager.sendMessage(sender, "party-deleted",
            new String[]{"%party%"},
            new String[]{partyName});
        return true;
    }
}
