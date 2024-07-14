package tk.taverncraft.dropparty.thrower;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.chat.ChatDrop;
import tk.taverncraft.dropparty.party.Party;

/**
 * Handles throwing of a new drop party.
 */
public class PartyThrower {
    private final Main main;
    private ChatDropFactory chatDropFactory;
    private InventoryDropFactory inventoryDropFactory;

    /**
     * Constructor for PartyThrower.
     *
     * @param main plugin class
     */
    public PartyThrower(Main main) {
        this.main = main;
        createFactories();
    }

    /**
     * Creates factories for different drop party types.
     */
    public void createFactories() {
        this.chatDropFactory = new ChatDropFactory(main);
        this.inventoryDropFactory = new InventoryDropFactory(main);
    }

    /**
     * Throws a drop party in chat.
     *
     * @param party party to use
     */
    public void throwAtChat(Party party) {
        playThrowSound();
        chatDropFactory.createChatDrop(party);
    }

    /**
     * Throws a drop party in inventory.
     *
     * @param party party to use
     */
    public void throwAtInventory(Party party) {
        playThrowSound();
        inventoryDropFactory.createInventoryDrop(party);
    }

    /**
     * Checks if there is an ongoing drop party with given party name.
     *
     * @param partyName name of party to check
     *
     * @return true if there is an ongoing drop, false otherwise
     */
    public boolean hasOngoingParty(String partyName) {
        return chatDropFactory.hasOngoingDrop(partyName);
    }

    /**
     * Stops an ongoing chat drop party.
     *
     * @param partyName name of party to stop
     */
    public void stopOngoingChatDrop(String partyName) {
        chatDropFactory.stopOngoingDrop(partyName);
    }

    /**
     * Stops all queued and ongoing drop parties.
     */
    public void stopAllDrops() {
        chatDropFactory.stopAllDrops();
        inventoryDropFactory.stopAllDrops();
    }

    /**
     * Gets a chat drop party with given uuid.
     *
     * @param uuid uuid to identify chat drop party with
     *
     * @return ChatDrop object
     */
    public ChatDrop getChatDrop(UUID uuid) {
        return chatDropFactory.getChatDrop(uuid);
    }

    /**
     * Removes a chat drop party with given uuid.
     *
     * @param uuid uuid to identify chat drop party with
     */
    public void removeChatDrop(UUID uuid) {
        chatDropFactory.removeChatDrop(uuid);
    }

    /**
     * Plays an initial sound when drop party is thrown.
     */
    private void playThrowSound() {
        try {
            Sound dropSound = Sound.valueOf(main.getConfigManager().getConfig().getString("drop-sound", "ENTITY_PLAYER_LEVELUP"));
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (Player player : onlinePlayers) {
                player.playSound(player.getLocation(), dropSound, 1.0F, 1.0F);
            }
        } catch (Exception e) {
            // do nothing
        }
    }
}
