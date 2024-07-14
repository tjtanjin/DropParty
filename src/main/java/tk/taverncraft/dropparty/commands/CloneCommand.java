package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * CloneCommand contains the execute method for when a user clones a party.
 */
public class CloneCommand {
    private final Main main;
    private final PermissionsManager permissionsManager;

    /**
     * Constructor for CLoneCommand.
     *
     * @param main plugin class
     */
    public CloneCommand(Main main) {
        this.main = main;
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Clones the party identified with given name to a given output name.
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

        if (!permissionsManager.hasCloneCmdPerm(sender)) {
            return true;
        }

        String partyNameToClone = args[1];
        String newClonedPartyName = args[2];

        // check if party to clone from exist
        if (!main.getPartyManager().partyExist(partyNameToClone)) {
            MessageManager.sendMessage(sender, "party-not-exist",
                new String[]{"%party%"},
                new String[]{partyNameToClone});
            return true;
        }

        // check if party to clone to exist
        if (main.getPartyManager().partyExist(newClonedPartyName)) {
            MessageManager.sendMessage(sender, "party-exist",
                new String[]{"%party%"},
                new String[]{newClonedPartyName});
            return true;
        }

        MessageManager.sendMessage(sender, "party-clone-success",
            new String[]{"%oldparty%", "%newparty%"},
            new String[]{partyNameToClone, newClonedPartyName});
        main.getPartyManager().cloneParty(partyNameToClone, newClonedPartyName);
        return true;
    }
}
