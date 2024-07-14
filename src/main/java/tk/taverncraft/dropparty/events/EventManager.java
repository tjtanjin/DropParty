package tk.taverncraft.dropparty.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import tk.taverncraft.dropparty.Main;

/**
 * Manages the registering and unregistering of events.
 */
public class EventManager {
    private final Main main;
    private List<Listener> listeners;

    /**
     * Constructor for EventManager.
     *
     * @param main plugin class
     */
    public EventManager(Main main) {
        this.main = main;
        registerEvents();
    }

    /**
     * Registers events.
     */
    public void registerEvents() {
        listeners = new ArrayList<>();
        listeners.add(new ClickChatDropEvent(main));
        listeners.add(new MenuClickEvent(main));

        for (Listener listener : listeners) {
            main.getServer().getPluginManager().registerEvents(listener, main);
        }
    }

    /**
     * Unregisters events.
     */
    public void unregisterEvents() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
        listeners.clear();
    }
}