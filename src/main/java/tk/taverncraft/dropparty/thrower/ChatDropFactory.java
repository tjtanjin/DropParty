package tk.taverncraft.dropparty.thrower;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.scheduler.BukkitRunnable;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.chat.ChatDrop;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.party.Party;

/**
 * Creates a new ChatDrop object for a chat drop party.
 */
public class ChatDropFactory {
    private final Main main;
    private ConcurrentHashMap<UUID, ChatDrop> chatDropEvents = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<BukkitRunnable>> queuedDropTasks;

    /**
     * Constructor for ChatDropFactory.
     *
     * @param main plugin class
     */
    public ChatDropFactory(Main main) {
        this.main = main;
        this.queuedDropTasks = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new chat drop party with given party.
     *
     * @param party party to create chat drop party with
     */
    public void createChatDrop(Party party) {
        if (party.getBroadcast() && party.getDelay() > 0) {
            MessageManager.broadcastMessage("chat-party-delay", new String[]{"%party%", "%delay%", "%party_display_name%"},
                new String[]{party.getName(), String.valueOf(party.getDelay()), party.getDisplayName()});
        }

        BukkitRunnable dropTask = new BukkitRunnable() {
            @Override
            public void run() {
                UUID uuid = UUID.randomUUID();
                ChatDrop chatDrop = new ChatDrop(main, party, uuid);
                chatDropEvents.put(uuid, chatDrop);
                chatDrop.dropItems();
            }
        };
        dropTask.runTaskLater(main, party.getDelay() * 20L);
        queuedDropTasks.compute(party.getName(), (k, existingList) -> {
            if (existingList == null) {
                existingList = new ArrayList<>();
            }
            existingList.add(dropTask);
            return existingList;
        });
    }

    /**
     * Checks if there is an ongoing drop for given party.
     *
     * @param partyName name of party to check
     *
     * @return true if there is an ongoing drop, false otherwise
     */
    public boolean hasOngoingDrop(String partyName) {
        return queuedDropTasks.containsKey(partyName) || chatDropEvents.containsKey(partyName);
    }

    /**
     * Stops an ongoing chat drop party.
     *
     * @param partyName name of party to stop
     */
    public void stopOngoingDrop(String partyName) {
        queuedDropTasks.forEach((name, queuedTasks) -> {
            if (name.equalsIgnoreCase(partyName)) {
                for (BukkitRunnable task : queuedTasks) {
                    task.cancel();
                }
                queuedDropTasks.remove(name);
            }
        });

        chatDropEvents.forEach((uuid, chatDrop) -> {
            if (chatDrop.getPartyName().equalsIgnoreCase(partyName)) {
                chatDrop.stopDrop();
                chatDropEvents.remove(uuid);
            }
        });
    }

    /**
     * Stops all queued and ongoing chat drop parties.
     */
    public void stopAllDrops() {
        queuedDropTasks.forEach((name, queuedTasks) -> {
            for (BukkitRunnable task : queuedTasks) {
                task.cancel();
            }
            queuedDropTasks.remove(name);
        });

        chatDropEvents.forEach((uuid, chatDrop) -> {
            chatDrop.stopDrop();
            chatDropEvents.remove(uuid);
        });
    }

    public ChatDrop getChatDrop(UUID uuid) {
        return chatDropEvents.get(uuid);
    }

    public void removeChatDrop(UUID uuid) {
        chatDropEvents.remove(uuid);
    }
}
