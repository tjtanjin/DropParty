package tk.taverncraft.dropparty.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.party.Party;
import tk.taverncraft.dropparty.thrower.utils.ParticleSpawner;

/**
 * ChatDrop object representing an ongoing drop party.
 */
public class ChatDrop {
    private final ParticleSpawner particleSpawner = new ParticleSpawner();
    private final Main main;
    private final Party party;
    private final UUID uuid;
    private List<ItemStack> items;
    private boolean perPlayerLimit;
    private int limitAmount;
    private ConcurrentHashMap<UUID, Integer> collectionCount;
    private final int expiryTime;
    private BukkitTask expirationTask;
    private final boolean perItemMessage;
    private boolean fireworks;
    private String effects;
    private int effectsCount;
    private String soundType;

    /**
     * Constructor for ChatDrop.
     *
     * @param main plugin class
     * @param party party being dropped in chat
     * @param uuid uuid identifying current drop party
     */
    public ChatDrop(Main main, Party party, UUID uuid) {
        this.main = main;
        this.party = party;
        this.uuid = uuid;
        this.items = new ArrayList<>(party.getFlattenedItems());
        this.perPlayerLimit = party.getPerPlayerLimit();
        this.limitAmount = party.getLimitAmount();
        this.collectionCount = new ConcurrentHashMap<>();
        this.expiryTime = party.getExpiryTime();
        this.perItemMessage = party.getPerItemMessage();
        this.fireworks = party.getFireworks();
        this.effects= party.getEffects();
        this.effectsCount = party.getEffectsCount();
        this.soundType = party.getSoundType();

        if (party.getExpires()) {
            scheduleExpirationTask();
        }

        if (!party.getDropStack()) {
            List<ItemStack> expandedItemList = new ArrayList<>();
            for (ItemStack itemStack : items) {
                if (itemStack.getAmount() > 1) {
                    // Expand the item into individual items
                    for (int i = 0; i < itemStack.getAmount(); i++) {
                        ItemStack singleItem = new ItemStack(itemStack.getType(), 1);
                        expandedItemList.add(singleItem);
                    }
                } else {
                    expandedItemList.add(itemStack);
                }
            }
            items = expandedItemList;
        }

        if ("RANDOM".equalsIgnoreCase(party.getDropOrder())) {
            Collections.shuffle(items);
        }
    }

    /**
     * Gets the name of the party.
     *
     * @return name of the party
     */
    public String getPartyName() {
        return this.party.getName();
    }

    /**
     * Checks if the party still has items.
     *
     * @return true if party still has items, false otherwise
     */
    public boolean hasItems() {
        return !items.isEmpty();
    }

    /**
     * Checks if party sends a message per-item.
     *
     * @return true if enabled, false otherwise
     */
    public boolean perItemMessage() {
        return perItemMessage;
    }

    /**
     * Retrieves an item from the drop party list.
     *
     * @param player player who clicked on the chat drop message
     *
     * @return item retrieved for the player
     */
    public ItemStack getAndRemoveItem(Player player) {
        UUID uuid = player.getUniqueId();
        if (perPlayerLimit) {
            collectionCount.putIfAbsent(uuid, 0);
            if (collectionCount.get(uuid) >= limitAmount) {
                return null;
            }
            collectionCount.computeIfPresent(uuid, (key, value) -> value + 1);
        }

        if (!items.isEmpty()) {
            if (fireworks) {
                particleSpawner.displayFireworkEffect(player.getLocation());
            }

            if (effects != "NONE") {
                particleSpawner.displayParticleEffect(Particle.valueOf(effects), player.getLocation(), effectsCount);
            }

            if (soundType != "NONE") {
                player.playSound(player.getLocation(), Sound.valueOf(soundType), 1.0F, 1.0F);
            }
            ItemStack item = items.remove(0);
            return item;
        }
        return null;
    }

    /**
     * Commences dropping of party items.
     */
    public void dropItems() {
        if (party.getBroadcast()) {
            MessageManager.broadcastMessage("chat-party-thrown", new String[]{"%party%", "%party_display_name%"},
                new String[]{party.getName(), party.getDisplayName()});
        }

        TextComponent message = MessageManager.getTextComponentMessage("chat-drop-message");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/collectdrop " + uuid.toString()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(message);
        }
    }

    /**
     * Stops an ongoing drop party.
     */
    public void stopDrop() {
        if (expirationTask != null) {
            expirationTask.cancel();
        }
    }

    /**
     * Schedules expiry for drop party.
     */
    private void scheduleExpirationTask() {
        expirationTask = new BukkitRunnable() {
            @Override
            public void run() {
                main.getPartyThrower().removeChatDrop(uuid);
            }
        }.runTaskLater(main, expiryTime * 20L);
    }
}
