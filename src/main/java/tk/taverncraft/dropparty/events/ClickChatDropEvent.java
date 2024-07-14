package tk.taverncraft.dropparty.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.chat.ChatDrop;
import tk.taverncraft.dropparty.messages.MessageManager;

/**
 * Handles the logic for when a player clicks on the message from chat drop party.
 */
public class ClickChatDropEvent implements Listener {
    private final Main main;

    /**
     * Constructor for ClickChatDropEvent.
     *
     * @param main plugin class
     */
    public ClickChatDropEvent(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if (message.startsWith("/collectdrop ")) {
            event.setCancelled(true);
            String uuidString = message.replace("/collectdrop ", "");
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                return;
            }

            Player player = event.getPlayer();

            // check if chat drop has been stopped
            if (main.getPartyThrower().getChatDrop(uuid) == null) {
                return;
            }

            ChatDrop chatDrop = main.getPartyThrower().getChatDrop(uuid);

            if (chatDrop.hasItems()) {
                ItemStack item = chatDrop.getAndRemoveItem(player);

                if (item == null) {
                    MessageManager.sendMessage(player, "chat-collection-limit-reached");
                    return;
                }

                if (chatDrop.perItemMessage()) {
                    ItemMeta meta = item.getItemMeta();
                    String displayName = meta.getDisplayName();
                    if (displayName == null || displayName.isEmpty()) {
                        displayName = item.getType().toString();  // Use the default item type name
                    }
                    MessageManager.broadcastMessage("chat-item-received",
                        new String[]{"%player%", "%item%"},
                        new String[]{player.getName(), displayName});
                }

                // check if the player's inventory is full (if not drop around player)
                if (player.getInventory().firstEmpty() >= 0) {
                    player.getInventory().addItem(item);
                } else {
                    player.getWorld().dropItem(player.getLocation(), item);
                }

                // if no more items, remove chat drop party
                if (!chatDrop.hasItems()) {
                    main.getPartyThrower().removeChatDrop(uuid);
                }
            }
        }
    }
}
