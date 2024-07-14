package tk.taverncraft.dropparty.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.party.Party;

/**
 * ConfigManager handles the loading of all configuration files.
 */
public class ConfigManager {
    private final Main main;

    // config
    private FileConfiguration config;
    private FileConfiguration partyMenuConfig;
    private List<String> partyNames;

    /**
     * Constructor for ConfigManager.
     *
     * @param main plugin class
     */
    public ConfigManager(Main main) {
        this.main = main;
        loadPartyNames();
    }

    /**
     * Creates all configuration files from the plugin.
     */
    public void createConfigs() {
        createConfig();
        createMessageFile();
        createPartyMenuConfig();
    }

    /**
     * Creates the main config file.
     */
    public void createConfig() {
        config = getConfig("config.yml");
    }

    /**
     * Creates message file.
     */
    public void createMessageFile() {
        String langFileName = main.getConfigManager().getConfig().getString("lang-file", "en.yml");
        FileConfiguration langConfig = getConfig("lang/" + langFileName);
        MessageManager.setMessages(langConfig);
    }

    /**
     * Gets the configuration file with given name.
     *
     * @param configName name of config file
     *
     * @return file configuration for config
     */
    private FileConfiguration getConfig(String configName) {
        File configFile = new File(main.getDataFolder(), configName);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            main.saveResource(configName, false);
        }

        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return config;
    }

    /**
     * Creates party menu config file.
     */
    public void createPartyMenuConfig() {
        String internalConfigName;

        // Determine the appropriate internal configuration file based on server version
        if (main.isLegacyVersion_1_12()) {
            internalConfigName = "menu/party_legacy.yml";
        } else {
            internalConfigName = "menu/party.yml";
        }

        // Present users with the unified "party.yml"
        String userConfigName = "menu/party.yml";
        partyMenuConfig = getConfig(internalConfigName);

        // Rename the internal config file to the user-visible name if needed
        if (!internalConfigName.equals(userConfigName)) {
            File userConfigFile = new File(main.getDataFolder(), userConfigName);
            File internalConfigFile = new File(main.getDataFolder(), internalConfigName);

            if (!userConfigFile.exists()) {
                userConfigFile.getParentFile().mkdirs();
                main.saveResource(internalConfigName, false);
            }

            internalConfigFile.renameTo(userConfigFile);
        }
        partyMenuConfig = getConfig("menu/party.yml");
    }

    /**
     * Gets the main config file.
     *
     * @return configuration file
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Gets the party menu config file.
     *
     * @return configuration file
     */
    public FileConfiguration getPartyMenuConfig() {
        return partyMenuConfig;
    }

    /**
     * Gets all parties config files.
     *
     * @return list of each party's config
     */
    public File[] getPartiesConfig() {
        File folder = new File(this.main.getDataFolder() + "/parties");
        if (!folder.exists()) {
            folder.getParentFile().mkdirs();
            main.saveResource("parties/example.yml", false);
        }

        return folder.listFiles();
    }

    /**
     * Loads a list of party names.
     */
    public void loadPartyNames() {
        File folder = new File(this.main.getDataFolder() + "/parties");
        List<String> partyNames = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".yml")) {
                        // Extract party name from file name
                        String fileName = file.getName();
                        String partyName = fileName.substring(0, fileName.length() - 4); // Remove ".yml"
                        partyNames.add(partyName);
                    }
                }
            }
        }

        this.partyNames = partyNames;
    }

    /**
     * Gets all party names.
     *
     * @return List of all party names
     */
    public List<String> getPartyNames() {
        return this.partyNames;
    }

    /**
     * Saves a party to config file.
     *
     * @param party party to save config for
     */
    public void savePartyConfig(Party party) {
        File partyFile = new File(this.main.getDataFolder() + "/parties/" + party.getName() + ".yml");
        FileConfiguration partyConfig = new YamlConfiguration();
        if (!partyFile.exists()) {
            partyFile.getParentFile().mkdirs();
        } else {
            try {
                partyConfig.load(partyFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        partyConfig.set("display-name", party.getDisplayName());
        partyConfig.set("drop-stack", party.getDropStack());
        partyConfig.set("drop-order", party.getDropOrder());
        partyConfig.set("fireworks", party.getFireworks());
        partyConfig.set("effects", party.getEffects());
        partyConfig.set("effects-count", party.getEffectsCount());
        partyConfig.set("delay", party.getDelay());
        partyConfig.set("expires", party.getExpires());
        partyConfig.set("expiry-time", party.getExpiryTime());
        partyConfig.set("per-player-limit", party.getPerPlayerLimit());
        partyConfig.set("limit-amount", party.getLimitAmount());
        partyConfig.set("min-players", party.getMinPlayers());
        partyConfig.set("sound-type", party.getSoundType());
        partyConfig.set("broadcast", party.getBroadcast());
        partyConfig.set("per-item-message", party.getPerItemMessage());
        partyConfig.set("items", party.getItems());

        try {
            partyConfig.save(partyFile);
            loadPartyNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a party config file.
     *
     * @param partyName name of party to remove config for
     */
    public void removePartyConfig(String partyName) {
        File partyFile = new File(this.main.getDataFolder() + "/parties/" + partyName + ".yml");
        if (partyFile.exists()) {
            partyFile.delete();
            loadPartyNames();
        }
    }
}