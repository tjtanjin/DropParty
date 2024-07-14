package tk.taverncraft.dropparty.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * CreateCommand contains the execute method for when a user creates a party.
 */
public class CreateCommand {
    private final Main main;
    private final PermissionsManager permissionsManager;

    /**
     * Constructor for CreateCommand.
     *
     * @param main plugin class
     */
    public CreateCommand(Main main) {
        this.main = main;
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Creates a new party with given name.
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

        if (!permissionsManager.hasCreateCmdPerm(sender)) {
            return true;
        }

        String partyName = args[1];

        // check if party already exist
        if (main.getPartyManager().partyExist(partyName)) {
            MessageManager.sendMessage(sender, "party-exist",
                new String[]{"%party%"},
                new String[]{partyName});
            return true;
        }

        Inventory inv = main.getPartyManager().getCreatePartyGui(partyName);
        Bukkit.getPlayer(((Player) sender).getUniqueId()).openInventory(inv);
        return true;
    }
}
