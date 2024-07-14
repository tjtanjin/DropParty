package tk.taverncraft.dropparty.commands;

import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * EditCommand contains the execute method for when a user edits a party.
 */
public class EditCommand {
    private final Main main;
    private final PermissionsManager permissionsManager;

    /**
     * Constructor for EditCommand.
     *
     * @param main plugin class
     */
    public EditCommand(Main main) {
        this.main = main;
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Edits a new party with given name.
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

        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender, "player-only-command");
            return true;
        }

        if (!permissionsManager.hasEditCmdPerm(sender)) {
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

        Inventory inv = main.getPartyManager().getEditPartyGui(partyName, 1, false);
        Bukkit.getPlayer(((Player) sender).getUniqueId()).openInventory(inv);
        return true;
    }
}
