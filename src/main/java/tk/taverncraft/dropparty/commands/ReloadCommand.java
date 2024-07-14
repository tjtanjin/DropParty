package tk.taverncraft.dropparty.commands;

import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.permissions.PermissionsManager;

/**
 * ReloadCommand contains the execute method for when a user reloads the plugin.
 */
public class ReloadCommand {
    Main main;
    PermissionsManager permissionsManager;


    /**
     * Constructor for ReloadCommand.
     *
     * @param main plugin class
     */
    public ReloadCommand(Main main) {
        this.main = main;
        this.permissionsManager = new PermissionsManager(main);
    }

    /**
     * Reloads the plugin.
     *
     * @param sender user who sent the command
     *
     * @return true at end of execution
     */
    public boolean execute(CommandSender sender) {
        if (!permissionsManager.hasReloadCmdPerm(sender)) {
            return true;
        }

        try {
            // stop existing tasks
            main.getPartyManager().stopAllParticleVisuals();
            main.getPartyThrower().stopAllDrops();

            // reload configs and reinitialize managers
            main.getConfigManager().createConfigs();
            main.getPartyManager().loadParties();
            main.getGuiManager().initializeMenuOptions();

            // re-register events
            main.getEventManager().unregisterEvents();
            main.getEventManager().registerEvents();

            // recreate factories
            main.getPartyThrower().createFactories();

            MessageManager.sendMessage(sender, "reload-success");
        } catch (Exception e) {
            main.getLogger().info(e.getMessage());
            MessageManager.sendMessage(sender, "reload-fail");
        }
        return true;
    }
}
