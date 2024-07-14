package tk.taverncraft.dropparty.thrower;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.scheduler.BukkitRunnable;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.inventory.InventoryDrop;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.party.Party;

/**
 * Creates a new ChatDrop object for a inventory drop party.
 */
public class InventoryDropFactory {
    private final Main main;
    private final ConcurrentHashMap<String, List<BukkitRunnable>> queuedDropTasks;

    /**
     * Constructor for InventoryDropFactory.
     *
     * @param main plugin class
     */
    public InventoryDropFactory(Main main) {
        this.main = main;
        this.queuedDropTasks = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new inventory drop party with given party.
     *
     * @param party party to create inventory drop party with
     */
    public void createInventoryDrop(Party party) {
        if (party.getBroadcast() && party.getDelay() > 0) {
            MessageManager.broadcastMessage("inventory-party-delay", new String[]{"%party%", "%delay%", "%party_display_name%"},
                new String[]{party.getName(), String.valueOf(party.getDelay()), party.getDisplayName()});
        }

        BukkitRunnable dropTask = new BukkitRunnable() {
            @Override
            public void run() {
                new InventoryDrop(party).dropItems();
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
     * Stops all queued inventory drop parties.
     */
    public void stopAllDrops() {
        queuedDropTasks.forEach((name, queuedTasks) -> {
            for (BukkitRunnable task : queuedTasks) {
                task.cancel();
            }
            queuedDropTasks.remove(name);
        });
    }
}
