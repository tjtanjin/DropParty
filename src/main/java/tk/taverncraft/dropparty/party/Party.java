package tk.taverncraft.dropparty.party;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.taverncraft.dropparty.Main;

/**
 * Party object containing options for a drop party.
 */
public class Party {
    private final String[] dropOrderOptions = {"SEQUENTIAL", "RANDOM"};
    private final String[] effectsOptions;
    private final String[] soundTypeOptions;
    private final int[] itemSlots;

    // party options
    private final Main main;
    private final String partyName;
    private final String displayName;
    private final ConcurrentHashMap<String, List<ItemStack>> items;
    private final boolean dropStack;
    private final String dropOrder;
    private final int dropOrderIndex;
    private final int minPlayers;
    private final boolean fireworks;
    private final String effects;
    private final int effectsIndex;
    private final int effectsCount;
    private final int delay;
    private final String soundType;
    private final int soundTypeIndex;
    private final boolean broadcast;
    private final int limitAmount;
    private final int expiryTime;
    private final boolean expires;
    private final boolean perPlayerLimit;
    private final boolean perItemMessage;

    /**
     * Constructor for Party.
     *
     * @param main plugin class
     * @param partyName name of the party
     * @param displayName display name for the party
     * @param items items that are thrown in the party
     * @param dropStack boolean indicating whether to drop items in a stack
     * @param dropOrder determines if items are thrown randomly or sequentially
     * @param minPlayers minimum number of players to throw party
     * @param fireworks determines if fireworks are enabled
     * @param effects determines if effects are enabled
     * @param effectsCount determines the effects count if effects is enabled
     * @param delay initial delay before party is thrown
     * @param soundType choice of sound to play when items are thrown
     * @param broadcast boolean indicating if party is broadcast in chat
     * @param limitAmount amount of items per player if per-player limit is true
     * @param expiryTime time before drop party expires if expires is true
     * @param expires boolean indicating if drop party expires
     * @param perPlayerLimit boolean indicating if number of items is limited per player
     * @param perItemMessage boolean indicating if a message is sent per-item
     */
    public Party(Main main, String partyName, String displayName, ConcurrentHashMap<String, List<ItemStack>> items,
            boolean dropStack, String dropOrder, int minPlayers,
            boolean fireworks, String effects, int effectsCount, int delay,
            String soundType, boolean broadcast, int limitAmount, int expiryTime, boolean expires,
            boolean perPlayerLimit, boolean perItemMessage) {
        this.main = main;
        this.partyName = partyName;
        this.displayName = displayName;
        this.items = items;
        this.dropStack = dropStack;
        this.dropOrder = dropOrder;
        this.dropOrderIndex = findIndex(dropOrderOptions, dropOrder);
        this.minPlayers = minPlayers;
        this.fireworks = fireworks;
        this.effectsOptions = Stream.concat(main.getConfigManager().getConfig().getStringList("effects").stream(), Stream.of("NONE"))
            .filter(effectName -> isValidEffect(effectName))
            .toArray(String[]::new);
        this.effects = effects;
        this.effectsIndex = findIndex(effectsOptions, effects);
        this.effectsCount = effectsCount;
        this.delay = delay;
        this.soundTypeOptions = Stream.concat(main.getConfigManager().getConfig().getStringList("sound-types").stream(), Stream.of("NONE"))
            .filter(soundName -> isValidSound(soundName))
            .toArray(String[]::new);
        this.soundType = soundType;
        this.soundTypeIndex = findIndex(soundTypeOptions, soundType);
        this.broadcast = broadcast;
        this.limitAmount = limitAmount;
        this.expiryTime = expiryTime;
        this.expires = expires;
        this.perPlayerLimit = perPlayerLimit;
        this.perItemMessage = perItemMessage;
        this.itemSlots = main.getConfigManager().getPartyMenuConfig().getIntegerList("items.slots").stream()
            .mapToInt(Integer::intValue)
            .toArray();
    }

    // Copy constructor for deep copy
    public Party(Party original, String newPartyName) {
        this.main = original.main;
        this.partyName = newPartyName;
        this.displayName = partyName;
        this.items = new ConcurrentHashMap<>();
        for (String key : original.items.keySet()) {
            List<ItemStack> originalItems = original.items.get(key);
            List<ItemStack> copyItems = new ArrayList<>();
            for (ItemStack item : originalItems) {
                // Create a deep copy of each ItemStack
                ItemStack itemCopy = new ItemStack(item);
                copyItems.add(itemCopy);
            }
            this.items.put(key, copyItems);
        }
        this.dropStack = original.dropStack;
        this.dropOrder = original.dropOrder;
        this.dropOrderIndex = original.dropOrderIndex;
        this.minPlayers = original.minPlayers;
        this.fireworks = original.fireworks;
        this.effectsOptions = Stream.concat(main.getConfigManager().getConfig().getStringList("effects").stream(), Stream.of("NONE"))
            .filter(effectName -> isValidEffect(effectName))
            .toArray(String[]::new);
        this.effects = original.effects;
        this.effectsIndex = original.effectsIndex;
        this.effectsCount = original.effectsCount;
        this.delay = original.delay;
        this.soundTypeOptions = Stream.concat(main.getConfigManager().getConfig().getStringList("sound-types").stream(), Stream.of("NONE"))
            .filter(soundName -> isValidSound(soundName))
            .toArray(String[]::new);
        this.soundType = original.soundType;
        this.soundTypeIndex = original.soundTypeIndex;
        this.broadcast = original.broadcast;
        this.limitAmount = original.limitAmount;
        this.expiryTime = original.expiryTime;
        this.expires = original.expires;
        this.perPlayerLimit = original.perPlayerLimit;
        this.perItemMessage = original.perItemMessage;
        this.itemSlots = original.itemSlots.clone();
    }

    /**
     * Constructor for a default template party during initial creation.
     *
     * @param main plugin class
     * @param partyName name of party
     */
    public Party(Main main, String partyName) {
        this.main = main;
        this.partyName = partyName;
        this.displayName = partyName;
        this.items = new ConcurrentHashMap<>();
        this.dropStack = true;
        this.dropOrder = "SEQUENTIAL";
        this.dropOrderIndex = 0;
        this.minPlayers = 0;
        this.fireworks = false;
        this.effectsOptions = Stream.concat(main.getConfigManager().getConfig().getStringList("effects").stream(), Stream.of("NONE"))
            .filter(effectName -> isValidEffect(effectName))
            .toArray(String[]::new);
        this.effects = "VILLAGER_HAPPY";
        this.effectsIndex = 0;
        this.effectsCount = 10;
        this.delay = 0;
        this.soundTypeOptions = Stream.concat(main.getConfigManager().getConfig().getStringList("sound-types").stream(), Stream.of("NONE"))
            .filter(soundName -> isValidSound(soundName))
            .toArray(String[]::new);
        this.soundType = "NONE";
        this.soundTypeIndex = 0;
        this.broadcast = true;
        this.limitAmount = 1;
        this.expiryTime = 600;
        this.expires = true;
        this.perPlayerLimit = false;
        this.perItemMessage = true;
        this.itemSlots = main.getConfigManager().getPartyMenuConfig().getIntegerList("items.slots").stream()
            .mapToInt(Integer::intValue)
            .toArray();
    }

    /**
     * Gets party name
     *
     * @return name of party
     */
    public String getName() {
        return this.partyName;
    }

    /**
     * Gets gui for party with given page number.
     *
     * @param pageNum page number to get
     *
     * @return party menu with specified page
     */
    public Inventory getGui(int pageNum) {
        return main.getGuiManager().getPartyGui(this, pageNum);
    }

    /**
     * Gets a list of items included in the party.
     *
     * @return list of party items
     */
    public List<ItemStack> getFlattenedItems() {
        return items.values()
            .stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    /**
     * Gets a list of items for the party grouped by page number.
     *
     * @return list of party items grouped by page number
     */
    public ConcurrentHashMap<String, List<ItemStack>> getItems() {
        return this.items;
    }

    /**
     * Gets a list of party items with given page number.
     *
     * @param pageNum page number to get
     *
     * @return list of party items with specified page number
     */
    public List<ItemStack> getItemsOnPage(int pageNum) {
        return items.get(String.valueOf(pageNum));
    }

    // functions below gets/modifies the options of party

    public boolean getDropStack() {
        return this.dropStack;
    }

    public Party toggleDropStack() {
        return new Party(main, partyName, displayName, items, !dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public String getDropOrder() {
        return this.dropOrder;
    }

    public Party cycleForwardDropOrder() {
        int newIndex = (dropOrderIndex + 1) % dropOrderOptions.length;
        return new Party(main, partyName, displayName, items, dropStack, dropOrderOptions[newIndex], minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public Party cycleBackwardDropOrder() {
        int newIndex = (dropOrderIndex - 1 + dropOrderOptions.length) % dropOrderOptions.length;
        return new Party(main, partyName, displayName, items, dropStack, dropOrderOptions[newIndex], minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public Party addMinPlayers() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers + 1, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public Party deductMinPlayers() {
        int newMinPlayers = minPlayers - 1;
        if (newMinPlayers < 0) {
            newMinPlayers = 0;
        }
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, newMinPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public boolean getFireworks() {
        return this.fireworks;
    }

    public Party toggleFireworks() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, !fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public String getEffects() {
        return this.effects;
    }

    public Party cycleForwardEffects() {
        int newIndex = (effectsIndex + 1) % effectsOptions.length;
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effectsOptions[newIndex], effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public Party cycleBackwardEffects() {
        int newIndex = (effectsIndex - 1 + effectsOptions.length) % effectsOptions.length;
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effectsOptions[newIndex], effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public int getEffectsCount() {
        return this.effectsCount;
    }

    public Party addEffectsCount() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount + 1, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public Party deductEffectsCount() {
        int newEffectsCount = effectsCount - 1;
        if (newEffectsCount < 0) {
            newEffectsCount = 0;
        }
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, newEffectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public int getDelay() {
        return this.delay;
    }

    public Party addDelay() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay + 1, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public Party deductDelay() {
        int newDelay = delay - 1;
        if (newDelay < 0) {
            newDelay = 0;
        }
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, newDelay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public String getSoundType() {
        return this.soundType;
    }

    public Party cycleForwardSoundType() {
        int newIndex = (soundTypeIndex + 1) % soundTypeOptions.length;
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundTypeOptions[newIndex], broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public Party cycleBackwardSoundType() {
        int newIndex = (soundTypeIndex - 1 + soundTypeOptions.length) % soundTypeOptions.length;
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundTypeOptions[newIndex], broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public boolean getBroadcast() {
        return this.broadcast;
    }

    public Party toggleBroadcast() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, !broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public int getLimitAmount() {
        return this.limitAmount;
    }

    public Party addLimitAmount() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount + 1, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public Party deductLimitAmount() {
        int newLimitAmount = limitAmount - 1;
        if (newLimitAmount < 0) {
            newLimitAmount = 0;
        }
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, newLimitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public int getExpiryTime() {
        return this.expiryTime;
    }

    public Party addExpiryTime() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime + 1, expires, perPlayerLimit, perItemMessage);
    }

    public Party deductExpiryTime() {
        int newExpiryTime = expiryTime - 1;
        if (newExpiryTime < 0) {
            newExpiryTime = 0;
        }
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, newExpiryTime, expires, perPlayerLimit, perItemMessage);
    }

    public boolean getExpires() {
        return this.expires;
    }

    public Party toggleExpires() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, !expires, perPlayerLimit, perItemMessage);
    }

    public boolean getPerPlayerLimit() {
        return this.perPlayerLimit;
    }

    public Party togglePerPlayerLimit() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, !perPlayerLimit, perItemMessage);
    }

    public boolean getPerItemMessage() {
        return this.perItemMessage;
    }

    public Party togglePerItemMessage() {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, !perItemMessage);
    }

    public Party updateItems(Inventory inv, int page) {
        List<ItemStack> pageItems = new ArrayList<>();
        for (int slot : itemSlots) {
            if (slot >= 0 && slot < inv.getSize()) {
                ItemStack item = inv.getItem(slot);
                if (item != null && item.getType() != Material.AIR) {
                    pageItems.add(item);
                }
            }
        }

        ConcurrentHashMap<String, List<ItemStack>> newItems = new ConcurrentHashMap<>();
        for (String key : items.keySet()) {
            List<ItemStack> value = items.get(key);
            List<ItemStack> valueCopy = new ArrayList<>(value);
            newItems.put(key, valueCopy);
        }

        newItems.put(String.valueOf(page), pageItems);
        return new Party(main, partyName, displayName, newItems, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    private int findIndex(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return 0;
    }

    public boolean hasEnoughMinPlayers() {
        return Bukkit.getServer().getOnlinePlayers().size() >= this.minPlayers;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Party setDisplayName(String displayName) {
        return new Party(main, partyName, displayName, items, dropStack, dropOrder, minPlayers, fireworks, effects, effectsCount, delay, soundType, broadcast, limitAmount, expiryTime, expires, perPlayerLimit, perItemMessage);
    }

    private boolean isValidSound(String soundName) {
        try {
            // Attempt to get the Sound object based on the provided sound name
            Sound.valueOf(soundName);
            return true;
        } catch (IllegalArgumentException e) {
            // If an exception is thrown, the sound is not valid
            return false;
        }
    }

    private boolean isValidEffect(String effect) {
        try {
            Particle.valueOf(effect);
            return true;
        } catch (IllegalArgumentException e) {
            // If an exception is thrown, the effect is not valid
            return false;
        }
    }
}