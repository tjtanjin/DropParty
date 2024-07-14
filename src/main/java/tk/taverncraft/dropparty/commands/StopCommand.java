package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * StopCommand contains the execute method for when a user stop a drop party (or all drop parties).
 */
public class StopCommand {
    private final Main main;
    private final PermissionsManager permissionsManager;

    /**
     * Constructor for StopCommand.
     *
     * @param main plugin class
     */
    public StopCommand(Main main) {
        this.main = main;
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Stops specified drop party (or stops all if not specified).
     *
     * @param sender user who sent the command
     * @param args command arguments
     *
     * @return true at end of execution
     */
    public boolean execute(CommandSender sender, String[] args) {
        if (!permissionsManager.hasStopCmdPerm(sender)) {
            return true;
        }

        if (args.length == 1) {
            MessageManager.sendMessage(sender, "all-party-stopped");
            main.getPartyThrower().stopAllDrops();
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

        // check if party is ongoing
        if (!main.getPartyThrower().hasOngoingParty(partyName)) {
            MessageManager.sendMessage(sender, "party-not-ongoing",
                new String[]{"%party%"},
                new String[]{partyName});
            return true;
        }

        // stops drop in chat
        main.getPartyThrower().stopOngoingChatDrop(partyName);

        MessageManager.sendMessage(sender, "party-stopped",
            new String[]{"%party%"},
            new String[]{partyName});
        // no need to stop inventory drops, those are basically instant
        return true;
    }
}
