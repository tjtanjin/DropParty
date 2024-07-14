package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.party.Party;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * ThrowCommand contains the execute method for when a user throws a drop party.
 */
public class ThrowCommand {
    private final Main main;
    private final PermissionsManager permissionsManager;

    /**
     * Constructor for ThrowCommand.
     *
     * @param main plugin class
     */
    public ThrowCommand(Main main) {
        this.main = main;
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Prompts the user with /towns help to view more options as command is invalid.
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

        String type = args[1].toUpperCase();
        String partyName;
        if (type.equals("LOCATION")) {
            MessageManager.sendMessage(sender, "premium-feature");
            return true;
        } else if (type.equals("PLAYER") && args.length >= 4) {
            MessageManager.sendMessage(sender, "premium-feature");
            return true;
        } else if (type.equals("MOB") && args.length >= 4) {
            MessageManager.sendMessage(sender, "premium-feature");
            return true;
        } else if (type.equals("CHAT")) {
            partyName = args[2];
        } else if (type.equals("INVENTORY")) {
            partyName = args[2];
        } else {
            MessageManager.sendMessage(sender, "invalid-command");
            return true;
        }

        if (!main.getPartyManager().partyExist(partyName)) {
            MessageManager.sendMessage(sender, "party-not-exist",
                new String[]{"%party%"},
                new String[]{partyName});
            return true;
        }

        Party party = main.getPartyManager().getSavedParty(partyName);
        if (!party.hasEnoughMinPlayers()) {
            MessageManager.sendMessage(sender, "not-enough-min-players",
                new String[]{"%party%"},
                new String[]{partyName});
            return true;
        }

        if (type.equals("CHAT") && permissionsManager.hasThrowChatCmdPerm(sender)) {
            if (main.isLegacyVersion_1_11()) {
                MessageManager.sendMessage(sender, "legacy-version-limitation");
                return true;
            }
            handleThrowChat(party);
        } else if (type.equals("INVENTORY") && permissionsManager.hasThrowInventoryCmdPerm(sender)) {
            handleThrowInventory(party);
        }
        return true;
    }

    private void handleThrowChat(Party party) {
        main.getPartyThrower().throwAtChat(party);
    }

    private void handleThrowInventory(Party party) {
        main.getPartyThrower().throwAtInventory(party);
    }
}
