package tk.taverncraft.dropparty;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import tk.taverncraft.dropparty.commands.CommandParser;
import tk.taverncraft.dropparty.commands.CommandTabCompleter;
import tk.taverncraft.dropparty.events.EventManager;
import tk.taverncraft.dropparty.gui.GuiManager;
import tk.taverncraft.dropparty.party.PartyManager;
import tk.taverncraft.dropparty.config.ConfigManager;
import tk.taverncraft.dropparty.thrower.PartyThrower;
import tk.taverncraft.dropparty.utils.UpdateChecker;

public class Main extends JavaPlugin implements Listener {

    // managers
    private ConfigManager configManager;
    private PartyManager partyManager;
    private GuiManager guiManager;
    private EventManager eventManager;

    // party thrower
    private PartyThrower partyThrower;

    public void onEnable() {
        new UpdateChecker(this, 113746).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("You are using the latest version of DropParty!");
            } else {
                getLogger().info("A new version of DropParty is now available on spigot!");
            }
        });

        // config/managers setup
        this.configManager = new ConfigManager(this);
        configManager.createConfigs();
        this.partyManager = new PartyManager(this);
        this.guiManager = new GuiManager(this);
        this.eventManager = new EventManager(this);

        // party thrower setup
        this.partyThrower = new PartyThrower(this);

        // initialize commands
        this.getCommand("dropparty").setTabCompleter(new CommandTabCompleter(this));
        this.getCommand("dropparty").setExecutor(new CommandParser(this));
    }

    public void onDisable() {
        partyManager.stopAllParticleVisuals();
        partyThrower.stopAllDrops();
        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    public boolean isLegacyVersion_1_11() {
        return Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10")
            || Bukkit.getVersion().contains("1.11");
    }

    public boolean isLegacyVersion_1_12() {
        return Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10")
            || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12");
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public PartyManager getPartyManager() {
        return this.partyManager;
    }

    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public PartyThrower getPartyThrower() {
        return this.partyThrower;
    }
}
