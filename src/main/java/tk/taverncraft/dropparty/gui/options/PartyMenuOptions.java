package tk.taverncraft.dropparty.gui.options;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.gui.GuiUtils;
import tk.taverncraft.dropparty.party.Party;

/**
 * PartyMenuOptions loads all configured menu options for party menu pages.
 */
public class PartyMenuOptions {
    private final int size;

    // identifiers
    private final String pageIdentifier = "§d§p§f§p";

    // menu titles
    private final String title;

    // button slots for use in inventory click events
    private final HashMap<String, Integer> subButtonSlots = new HashMap<>();

    // button items
    private final HashMap<Integer, ItemStack> subPageButtons = new HashMap<>();

    // backgrounds
    private final HashMap<Integer, ItemStack> subPageBackground = new HashMap<>();

    // sub page items
    private final List<Integer> subPageItemSlots;

    /**
     * Constructor for PartyMenuOptions.
     *
     * @param main plugin class
     */
    public PartyMenuOptions(Main main) {
        FileConfiguration config = main.getConfigManager().getPartyMenuConfig();
        size = (int) Math.round(config.getInt("size", 54) / 9.0) * 9;
        title = config.getString("title") + pageIdentifier;

        setUpMenuBackground(config);
        subPageItemSlots = config.getIntegerList("items.slots");

        for (String key: config.getConfigurationSection("buttons")
                .getKeys(false)) {
            setUpButton(config, key);
        }
    }

    /**
     * Sets up the background for the party menu.
     *
     * @param config config file to refer to for setting up background
     */
    private void setUpMenuBackground(FileConfiguration config) {
        for (String key : config.getConfigurationSection("background")
            .getKeys(false)) {
            int slot = Integer.parseInt(key);
            Material material = Material.valueOf(config.getString("background." + key));
            ItemStack itemStack = GuiUtils.createGuiItem(material, "", false, null);
            subPageBackground.put(slot, itemStack);
        }
    }

    /**
     * Sets up button for the party menu.
     *
     * @param config config file to refer to for setting up buttons
     * @param button button to set up
     */
    private void setUpButton(FileConfiguration config, String button) {
        ConfigurationSection configurationSection = config.getConfigurationSection(
                "buttons." + button);
        int slot = configurationSection.getInt("slot");
        Material material = Material.valueOf(configurationSection.getString("material"));
        String name = configurationSection.getString("name");
        boolean isEnchanted = configurationSection.getBoolean("enchanted", false);
        List<String> lore = configurationSection.getStringList("lore");

        ItemStack itemStack = GuiUtils.createGuiItem(material, name, isEnchanted,
                lore.toArray(new String[0]));
        subPageButtons.put(slot, itemStack);
        subButtonSlots.put(button, slot);
    }

    /**
     * Creates the gui for party menu.
     *
     * @param party name of party to get gui for
     * @param pageNum page to get gui
     *
     * @return gui for party menu
     */
    public Inventory createPartyGui(Party party, int pageNum) {

        // set up inventory gui
        Inventory entityView = buildGuiFromTemplate(party, pageNum);

        // if no entity, return empty inventory
        List<ItemStack> items = party.getItemsOnPage(pageNum);
        if (items == null ) {
            return entityView;
        }

        int counter = 0;
        for (ItemStack item: items) {
            int slot = subPageItemSlots.get(counter);
            entityView.setItem(slot, item);
            counter++;
        }

        return entityView;
    }

    /**
     * Creates template for party menu.
     *
     * @param party name of party to build gui for
     * @param pageNum page number to show
     *
     * @return an inventory gui template for party menu
     */
    public Inventory buildGuiFromTemplate(Party party, int pageNum) {
        Inventory inv;
        String pageNumPrefix = "§" + pageNum + "§8";
        inv = Bukkit.createInventory(null, size, pageNumPrefix +
            GuiUtils.parseName(title, "%party%", party.getName()));


        for (Map.Entry<Integer, ItemStack> map : subPageBackground.entrySet()) {
            inv.setItem(map.getKey(), map.getValue());
        }

        for (Map.Entry<Integer, ItemStack> map : subPageButtons.entrySet()) {
            int slot = map.getKey();
            if (pageNum == 1 && slot == getPrevPageSlot()) {
                continue;
            }
            if (slot == getNextPageSlot() || slot == getPrevPageSlot()) {
                int pageToUse = slot == getNextPageSlot() ? pageNum + 1 : pageNum - 1;
                ItemStack itemStack = new ItemStack(map.getValue());
                ItemMeta meta = itemStack.getItemMeta();
                List<String> parsedLore = GuiUtils.parsePage(meta.getLore(), "%page%",
                    pageToUse);
                meta.setLore(parsedLore);
                itemStack.setItemMeta(meta);
                inv.setItem(slot, itemStack);
            } else if (slot == getSummarySlot()) {
                ItemStack itemStack = getParsedSummary(map,
                    new Object[]{
                        party.getDropStack(),
                        party.getDropOrder(),
                        party.getFireworks(),
                        party.getEffects(),
                        party.getEffectsCount(),
                        party.getDelay(),
                        party.getExpires(),
                        party.getExpiryTime(),
                        party.getPerPlayerLimit(),
                        party.getLimitAmount(),
                        party.getMinPlayers(),
                        party.getSoundType(),
                        party.getBroadcast(),
                        party.getPerItemMessage()},
                    new String[]{
                        "%dropstack%",
                        "%droporder%",
                        "%fireworks%",
                        "%effects%",
                        "%effectscount%",
                        "%delay%",
                        "%expires%",
                        "%expirytime%",
                        "%perplayerlimit%",
                        "%limitamount%",
                        "%minplayers%",
                        "%soundtype%",
                        "%broadcast%",
                        "%peritemmessage%",
                    });
                inv.setItem(slot, itemStack);
            } else if (slot == getDropStackSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getDropStack(), "%dropstack%");
                inv.setItem(slot, itemStack);
            } else if (slot == getDropOrderSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getDropOrder(), "%droporder%");
                inv.setItem(slot, itemStack);
            } else if (slot == getMinPlayersSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getMinPlayers(), "%minplayers%");
                inv.setItem(slot, itemStack);
            } else if (slot == getFireworksSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getFireworks(), "%fireworks%");
                inv.setItem(slot, itemStack);
            } else if (slot == getEffectsSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getEffects(), "%effects%");
                inv.setItem(slot, itemStack);
            } else if (slot == getEffectsCountSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getEffectsCount(), "%effectscount%");
                inv.setItem(slot, itemStack);
            } else if (slot == getDelaySlot()) {
                ItemStack itemStack = getParsedItem(map, party.getDelay(), "%delay%");
                inv.setItem(slot, itemStack);
            } else if (slot == getSoundTypeSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getSoundType(), "%soundtype%");
                inv.setItem(slot, itemStack);
            } else if (slot == getBroadcastSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getBroadcast(), "%broadcast%");
                inv.setItem(slot, itemStack);
            } else if (slot == getLimitAmountSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getLimitAmount(), "%limitamount%");
                inv.setItem(slot, itemStack);
            } else if (slot == getExpiryTimeSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getExpiryTime(), "%expirytime%");
                inv.setItem(slot, itemStack);
            } else if (slot == getExpiresSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getExpires(), "%expires%");
                inv.setItem(slot, itemStack);
            } else if (slot == getPerPlayerLimitSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getPerPlayerLimit(), "%perplayerlimit%");
                inv.setItem(slot, itemStack);
            } else if (slot == getPerItemMessageSlot()) {
                ItemStack itemStack = getParsedItem(map, party.getPerItemMessage(), "%peritemmessage%");
                inv.setItem(slot, itemStack);
            } else {
                inv.setItem(slot, map.getValue());
            }
        }
        return inv;
    }

    /**
     * Gets a parsed item for displaying buttons in the party menu.
     *
     * @param map map of slot number to item
     * @param attribute attribute to show for button
     * @param placeholder placeholder to fill in with attribute
     * @return
     */
    public ItemStack getParsedItem(Map.Entry<Integer, ItemStack> map, Object attribute, String placeholder) {
        ItemStack itemStack = new ItemStack(map.getValue());
        ItemMeta meta = itemStack.getItemMeta();
        List<String> parsedLore = GuiUtils.parseAttribute(meta.getLore(), placeholder,
            attribute);
        meta.setLore(parsedLore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Gets an item representing a summary of current configurations for the party.
     *
     * @param map map of slot number to item
     * @param attributes list of attributes to show in summary
     * @param placeholders list of placeholders to fill in with attributes
     * @return
     */
    public ItemStack getParsedSummary(Map.Entry<Integer, ItemStack> map, Object[] attributes, String[] placeholders) {
        ItemStack itemStack = new ItemStack(map.getValue());
        ItemMeta meta = itemStack.getItemMeta();
        List<String> parsedLore = GuiUtils.parseAttributes(meta.getLore(), placeholders,
            attributes);
        meta.setLore(parsedLore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    // getters below are for information used in handling of inventory click events

    public int getNextPageSlot() {
        return Optional.ofNullable(subButtonSlots.get("next-page")).orElse(53);
    }

    public int getPrevPageSlot() {
        return Optional.ofNullable(subButtonSlots.get("previous-page")).orElse(50);
    }

    public int getDropStackSlot() {
        return Optional.ofNullable(subButtonSlots.get("drop-stack")).orElse(2);
    }

    public int getDropOrderSlot() {
        return Optional.ofNullable(subButtonSlots.get("drop-order")).orElse(3);
    }

    public int getFireworksSlot() {
        return Optional.ofNullable(subButtonSlots.get("fireworks")).orElse(18);
    }

    public int getEffectsSlot() {
        return Optional.ofNullable(subButtonSlots.get("effects")).orElse(19);
    }

    public int getEffectsCountSlot() {
        return Optional.ofNullable(subButtonSlots.get("effects-count")).orElse(20);
    }

    public int getDelaySlot() {
        return Optional.ofNullable(subButtonSlots.get("delay")).orElse(21);
    }

    public int getExpiresSlot() {
        return Optional.ofNullable(subButtonSlots.get("expires")).orElse(27);
    }

    public int getExpiryTimeSlot() {
        return Optional.ofNullable(subButtonSlots.get("expiry-time")).orElse(28);
    }

    public int getPerPlayerLimitSlot() {
        return Optional.ofNullable(subButtonSlots.get("per-player-limit")).orElse(29);
    }

    public int getLimitAmountSlot() {
        return Optional.ofNullable(subButtonSlots.get("limit-amount")).orElse(30);
    }

    public int getMinPlayersSlot() {
        return Optional.ofNullable(subButtonSlots.get("min-players")).orElse(45);
    }

    public int getSoundTypeSlot() {
        return Optional.ofNullable(subButtonSlots.get("sound-type")).orElse(46);
    }

    public int getBroadcastSlot() {
        return Optional.ofNullable(subButtonSlots.get("broadcast")).orElse(47);
    }

    public int getPerItemMessageSlot() {
        return Optional.ofNullable(subButtonSlots.get("per-item-message")).orElse(48);
    }

    public int getSaveSlot() {
        return Optional.ofNullable(subButtonSlots.get("save")).orElse(52);
    }

    public int getSummarySlot() {
        return Optional.ofNullable(subButtonSlots.get("summary")).orElse(51);
    }
}
