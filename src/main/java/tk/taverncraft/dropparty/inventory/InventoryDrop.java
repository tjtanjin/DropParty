package tk.taverncraft.dropparty.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.party.Party;
import tk.taverncraft.dropparty.thrower.utils.ParticleSpawner;

/**
 * InventoryDrop object representing an ongoing drop party.
 */
public class InventoryDrop {
    private final ParticleSpawner particleSpawner = new ParticleSpawner();
    private final Party party;
    private final boolean perItemMessage;
    private List<ItemStack> items;
    private ConcurrentHashMap<UUID, Integer> collectionCount;
    private boolean perPlayerLimit;
    private int limitAmount;

    /**
     * Constructor for ChatDrop.
     *
     * @param party party being dropped in inventory
     */
    public InventoryDrop(Party party) {
        this.party = party;
        this.perItemMessage = party.getPerItemMessage();
        this.items = new ArrayList<>(party.getFlattenedItems());
        this.collectionCount = new ConcurrentHashMap<>();
        this.perPlayerLimit = party.getPerPlayerLimit();
        this.limitAmount = party.getLimitAmount();

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
     * Commences dropping of party items.
     */
    public void dropItems() {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        int itemCount = items.size();
        int playerCount = onlinePlayers.size();
        boolean fireworks = party.getFireworks();
        String effects = party.getEffects();
        int effectsCount = party.getEffectsCount();
        String soundType = party.getSoundType();

        if (playerCount == 0 || itemCount == 0) {
            // no players or no items to distribute, nothing to do
            return;
        }

        if (perPlayerLimit) {
            itemCount = Math.min(limitAmount * playerCount, itemCount);
        }

        if (party.getBroadcast()) {
            MessageManager.broadcastMessage("inventory-party-thrown", new String[]{"%party%", "%party_display_name%"},
                new String[]{party.getName(), party.getDisplayName()});
        }

        int itemsPerPlayer = itemCount / playerCount;
        int itemsLeftover = itemCount % playerCount;

        int currentItemIndex = 0;

        for (Player player : onlinePlayers) {
            int itemsToGive = itemsPerPlayer;

            if (itemsLeftover > 0) {
                itemsToGive++;
                itemsLeftover--;
            }

            for (int i = 0; i < itemsToGive; i++) {
                if (currentItemIndex < itemCount) {
                    ItemStack item = items.get(currentItemIndex);
                    player.getInventory().addItem(item);
                    if (perItemMessage) {
                        ItemMeta meta = item.getItemMeta();
                        String displayName = meta.getDisplayName();
                        if (displayName == null || displayName.isEmpty()) {
                            displayName = item.getType().toString();  // Use the default item type name
                        }
                        MessageManager.sendMessage(player, "inventory-item-received",
                            new String[]{"%item%"},
                            new String[]{displayName});
                    }

                    if (fireworks) {
                        particleSpawner.displayFireworkEffect(player.getLocation());
                    }

                    if (effects != "NONE") {
                        particleSpawner.displayParticleEffect(Particle.valueOf(effects), player.getLocation(), effectsCount);
                    }

                    if (soundType != "NONE") {
                        player.playSound(player.getLocation(), Sound.valueOf(soundType), 1.0F, 1.0F);
                    }

                    currentItemIndex++;
                }
            }
        }
    }
}
