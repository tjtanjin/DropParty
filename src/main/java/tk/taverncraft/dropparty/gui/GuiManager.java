package tk.taverncraft.dropparty.gui;

import org.bukkit.inventory.Inventory;

import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.gui.options.PartyMenuOptions;
import tk.taverncraft.dropparty.party.Party;

/**
 * GuiManager handles all logic related to showing information in a GUI.
 */
public class GuiManager {
    private final Main main;

    // party menu options
    private PartyMenuOptions partyOptions;

    /**
     * Constructor for GuiManager.
     *
     * @param main plugin class
     */
    public GuiManager(Main main) {
        this.main = main;
        initializeMenuOptions();
    }

    /**
     * Loads all menu options and set common info gui.
     */
    public void initializeMenuOptions() {
        this.partyOptions = new PartyMenuOptions(main);
    }

    /**
     * Returns the party gui for a given party.
     *
     * @param party party to get gui for
     * @param pageNum page num to get for gui
     *
     * @return party gui
     */
    public Inventory getPartyGui(Party party, int pageNum) {
        return partyOptions.createPartyGui(party, pageNum);
    }

    /**
     * Gets the options for party menu to handle inventory click events.
     *
     * @return options for party menu
     */
    public PartyMenuOptions getPartyOptions() {
        return partyOptions;
    }
}
