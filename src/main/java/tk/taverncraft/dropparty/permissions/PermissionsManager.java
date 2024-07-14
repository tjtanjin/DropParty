package tk.taverncraft.dropparty.permissions;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;

/**
 * PermissionsManager performs basic validation operations for permissions.
 */
public class PermissionsManager {
    private final Main main;

    private final String allCmdPerm = "dp.*";
    private final String createCmdPerm = "dp.create";
    private final String editCmdPerm = "dp.edit";
    private final String deleteCmdPerm = "dp.delete";
    private final String cloneCmdPerm = "dp.clone";
    private final String listCmdPerm = "dp.list";
    private final String setDisplayNameCmdPerm = "dp.setdisplayname";
    private final String helpCmdPerm = "dp.help";
    private final String reloadCmdPerm = "dp.reload";
    private final String throwAllCmdPerm = "dp.throw.*";
    private final String throwChatCmdPerm = "dp.throw.chat";
    private final String throwInventoryCmdPerm = "dp.throw.inventory";
    private final String stopCmdPerm = "dp.stop";

    /**
     * Constructor for PermissionsManager.
     *
     * @param main plugin class
     */
    public PermissionsManager(Main main) {
        this.main = main;
    }

    /**
     * Validates if sender has permission and sends a message if not.
     *
     * @param permission permission node to check for
     * @param sender the player executing the command
     */
    public boolean hasPermission(String permission, CommandSender sender) {
        if (sender.hasPermission(allCmdPerm)) {
            return true;
        }

        if (sender.hasPermission(permission)) {
            return true;
        }
        MessageManager.sendMessage(sender, "no-permission");
        return false;
    }

    public boolean hasCreateCmdPerm(CommandSender sender) {
        return hasPermission(createCmdPerm, sender);
    }

    public boolean hasEditCmdPerm(CommandSender sender) {
        return hasPermission(editCmdPerm, sender);
    }

    public boolean hasDeleteCmdPerm(CommandSender sender) {
        return hasPermission(deleteCmdPerm, sender);
    }

    public boolean hasCloneCmdPerm(CommandSender sender) {
        return hasPermission(cloneCmdPerm, sender);
    }

    public boolean hasListCmdPerm(CommandSender sender) {
        return hasPermission(listCmdPerm, sender);
    }

    public boolean hasSetDisplayNameCmdPerm(CommandSender sender) {
        return hasPermission(setDisplayNameCmdPerm, sender);
    }

    public boolean hasHelpCmdPerm(CommandSender sender) {
        return hasPermission(helpCmdPerm, sender);
    }

    public boolean hasReloadCmdPerm(CommandSender sender) {
        return hasPermission(reloadCmdPerm, sender);
    }

    public boolean hasThrowChatCmdPerm(CommandSender sender) {
        return sender.hasPermission(throwAllCmdPerm) || hasPermission(throwChatCmdPerm, sender);
    }

    public boolean hasThrowInventoryCmdPerm(CommandSender sender) {
        return sender.hasPermission(throwAllCmdPerm) || hasPermission(throwInventoryCmdPerm, sender);
    }

    public boolean hasStopCmdPerm(CommandSender sender) {
        return hasPermission(stopCmdPerm, sender);
    }
}
