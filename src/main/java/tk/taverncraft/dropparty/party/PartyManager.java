package tk.taverncraft.dropparty.party;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import tk.taverncraft.dropparty.Main;

/**
 * PartyManager organizes and tracks all parties.
 */
public class PartyManager {
    private final Main main;

    // map of existing parties
    private ConcurrentHashMap<String, Party> savedParties = new ConcurrentHashMap<>();

    // map of existing open menu identified by player
    private ConcurrentHashMap<String, Party> editableParties = new ConcurrentHashMap<>();

    // map to track task for toggling of party location point visuals
    private ConcurrentHashMap<String, BukkitTask> ongoingPartyVisualsTasks = new ConcurrentHashMap<>();

    /**
     * Constructor for PartyManager.
     *
     * @param main plugin class
     */
    public PartyManager(Main main) {
        this.main = main;
        loadParties();
    }

    /**
     * Validates if inputted party exist and sends a message if not.
     *
     * @param name the name of the party to check for
     */
    public boolean partyExist(String name) {
        return savedParties.containsKey(name);
    }

    /**
     * Gets the list of all party names.
     *
     * @return list of all party names
     */
    public List<String> getPartyNames() {
        return new ArrayList<>(savedParties.keySet());
    }

    /**
     * Saves a party configuration.
     *
     * @param partyName name of party to save
     */
    public void saveParty(String partyName) {
        Party party = editableParties.get(partyName);
        main.getConfigManager().savePartyConfig(party);
        editableParties.remove(partyName);
        savedParties.put(partyName, party);
    }

    /**
     * Removes a party configuration.
     *
     * @param partyName name of party to remove
     */
    public void removeParty(String partyName) {
        editableParties.remove(partyName);
        savedParties.remove(partyName);
        main.getConfigManager().removePartyConfig(partyName);
    }

    /**
     * Gets a saved party by name (used to start throwing a party).
     *
     * @param partyName name of party
     *
     * @return party object
     */
    public Party getSavedParty(String partyName) {
        return savedParties.get(partyName);
    }

    /**
     * Gets an editable party by name (used to edit an existing party).
     *
     * @param partyName name of party
     *
     * @return party object
     */
    public Party getEditableParty(String partyName) {
        return editableParties.get(partyName);
    }

    /**
     * Updates an editable party.
     *
     * @param party party to update
     */
    public void updateEditableParty(Party party) {
        editableParties.put(party.getName(), party);
    }

    /**
     * Stops all particle visuals for party locations.
     */
    public void stopAllParticleVisuals() {
        for (BukkitTask task : ongoingPartyVisualsTasks.values()) {
            task.cancel();
        }
        ongoingPartyVisualsTasks.clear();
    }

    /**
     * Loads all parties.
     */
    public void loadParties() {
        File[] listOfFiles = main.getConfigManager().getPartiesConfig();
        if (listOfFiles == null) {
            return;
        }

        ConcurrentHashMap<String, Party> parties = new ConcurrentHashMap<>();
        for (File file : listOfFiles) {
            String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                FileConfiguration partyConfig = YamlConfiguration.loadConfiguration(file);
                String partyName = fileName.substring(0, fileName.length() - 4);
                ConcurrentHashMap<String, List<ItemStack>> items = new ConcurrentHashMap<>();
                ConfigurationSection itemSection = partyConfig.getConfigurationSection("items");
                Set<String> keys = itemSection != null ? itemSection.getKeys(false) : new HashSet<>();
                for (String key : keys) {
                    List<ItemStack> pageItems = (List<ItemStack>) partyConfig.getList("items." + key);
                    if (pageItems == null) {
                        pageItems = new ArrayList<>();
                    }
                    items.put(key, pageItems);
                }
                String displayName =  partyConfig.getString("display-name", partyName);
                boolean dropStack = partyConfig.getBoolean("drop-stack", true);
                String dropOrder = partyConfig.getString("drop-order", "SEQUENTIAL");
                int minPlayers = partyConfig.getInt("min-players", 0);
                boolean fireworks = partyConfig.getBoolean("fireworks", true);
                String effects = partyConfig.getString("effects", "VILLAGER_HAPPY");
                int effectsCount = partyConfig.getInt("effects-count", 10);
                int delay = partyConfig.getInt("delay", 0);
                String soundType = partyConfig.getString("sound-type", "NONE");
                try {
                    Sound.valueOf(soundType);
                } catch (Exception e) {
                    soundType = "NONE";
                }
                boolean broadcast = partyConfig.getBoolean("broadcast", true);
                int limitAmount = partyConfig.getInt("limit-amount", 1);
                int expiryTime = partyConfig.getInt("expiry-time", 600);
                boolean expires = partyConfig.getBoolean("expires", true);
                boolean perPlayerLimit = partyConfig.getBoolean("per-player-limit", false);
                boolean perItemMessage = partyConfig.getBoolean("per-item-message", true);
                parties.put(partyName, new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage));
            }
        }
        this.savedParties = parties;
    }

    /**
     * Gets the GUI for creating a new party.
     *
     * @param partyName name of new party to create
     *
     * @return inventory gui for creating new party
     */
    public Inventory getCreatePartyGui(String partyName) {
        Party party = new Party(main, partyName);
        editableParties.put(partyName, party);
        return party.getGui(1);
    }

    /**
     * Gets the GUI for editing an existing party.
     *
     * @param partyName name of new party to edit
     * @param pageNum page number to get gui
     * @param isNotCommand boolean indicating if this gui is fetched from edit command (alternatively comes from menu click event)
     *
     * @return inventory gui for editing party
     */
    public Inventory getEditPartyGui(String partyName, int pageNum, boolean isNotCommand) {
        Party party;
        if (editableParties.containsKey(partyName) && isNotCommand) {
            party = editableParties.get(partyName);
        } else {
            party = savedParties.get(partyName);
            editableParties.put(partyName, party);
        }
        return party.getGui(pageNum);
    }

    /**
     * Sets the display name for a party.
     *
     * @param partyName name of party to set display name for
     * @param displayName display name to use
     *
     * @return true if successfully set, false otherwise
     */
    public boolean setPartyDisplayName(String partyName, String displayName) {
        Party party = savedParties.get(partyName);
        party = party.setDisplayName(displayName);
        savedParties.put(partyName, party);
        main.getConfigManager().savePartyConfig(party);
        return true;
    }

    /**
     * Clones an existing party.
     *
     * @param partyNameToClone name of party to clone from
     * @param newClonedPartyName name of party to clone to
     */
    public void cloneParty(String partyNameToClone, String newClonedPartyName) {
        Party party = savedParties.get(partyNameToClone);
        Party newParty = new Party(party, newClonedPartyName);
        savedParties.put(newClonedPartyName, newParty);
        main.getConfigManager().savePartyConfig(newParty);
    }
}
