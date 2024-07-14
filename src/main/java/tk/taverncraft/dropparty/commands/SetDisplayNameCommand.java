package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * SetDisplayNameCommand contains the execute method for when a user sets display name for a party.
 */
public class SetDisplayNameCommand {
    private final Main main;
    private final PermissionsManager permissionsManager;

    /**
     * Constructor for SetDisplayNameCommand.
     *
     * @param main plugin class
     */
    public SetDisplayNameCommand(Main main) {
        this.main = main;
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Sets the display name for a specified party.
     *
     * @param sender user who sent the command
     * @param args command arguments
     *
     * @return true at end of execution
     */
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageManager.sendMessage(sender, "invalid-command");
            return true;
        }

        if (!permissionsManager.hasSetDisplayNameCmdPerm(sender)) {
            return true;
        }

        String partyName = args[1];

        // check if party exist
        if (!main.getPartyManager().partyExist(partyName)) {
            MessageManager.sendMessage(sender, "party-not-exist",
                new String[]{"%party%"},
                new String[]{partyName});
            return true;
        }

        String displayName = args[2];
        boolean success = main.getPartyManager().setPartyDisplayName(partyName, displayName);
        if (success) {
            MessageManager.sendMessage(sender, "set-display-name-success",
                new String[]{"%party%", "%party_display_name%"},
                new String[]{partyName, displayName});
        } else {
            MessageManager.sendMessage(sender, "set-display-name-fail",
                new String[]{"%party%", "%party_display_name%"},
                new String[]{partyName, displayName});
        }
        return true;
    }
}
