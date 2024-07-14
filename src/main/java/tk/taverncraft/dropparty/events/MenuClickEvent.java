package tk.taverncraft.dropparty.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import tk.taverncraft.dropparty.Main;
import tk.taverncraft.dropparty.gui.options.PartyMenuOptions;
import tk.taverncraft.dropparty.messages.MessageManager;
import tk.taverncraft.dropparty.party.Party;

import java.util.Arrays;

/**
 * MenuClickEvent checks for when a player clicks on GUI menu.
 */
public class MenuClickEvent implements Listener {
    private final Main main;
    private final int[] itemSlots;

    // identifier for party menu
    private final String pageIdentifier = "§d§p§f§p";

    // buttons for party menu
    private final int nextPageSlot;
    private final int prevPageSlot;
    private final int saveSlot;
    private final int dropStackSlot;
    private final int dropOrderSlot;
    private final int minPlayersSlot;
    private final int fireworksSlot;
    private final int effectsSlot;
    private final int effectsCountSlot;
    private final int delaySlot;
    private final int soundTypeSlot;
    private final int broadcastSlot;
    private final int limitAmountSlot;
    private final int expiryTimeSlot;
    private final int expiresSlot;
    private final int perPlayerLimitSlot;
    private final int perItemMessageSlot;

    /**
     * Constructor for MenuClickEvent.
     *
     * @param main plugin class
     */
    public MenuClickEvent(Main main) {
        this.main = main;
        PartyMenuOptions partyOptions = main.getGuiManager().getPartyOptions();
        this.itemSlots = main.getConfigManager().getPartyMenuConfig().getIntegerList("items.slots").stream()
            .mapToInt(Integer::intValue)
            .toArray();
        nextPageSlot = partyOptions.getNextPageSlot();
        prevPageSlot = partyOptions.getPrevPageSlot();
        saveSlot = partyOptions.getSaveSlot();
        dropStackSlot = partyOptions.getDropStackSlot();
        dropOrderSlot = partyOptions.getDropOrderSlot();
        minPlayersSlot = partyOptions.getMinPlayersSlot();
        fireworksSlot = partyOptions.getFireworksSlot();
        effectsSlot = partyOptions.getEffectsSlot();
        effectsCountSlot = partyOptions.getEffectsCountSlot();
        delaySlot = partyOptions.getDelaySlot();
        soundTypeSlot = partyOptions.getSoundTypeSlot();
        broadcastSlot = partyOptions.getBroadcastSlot();
        limitAmountSlot = partyOptions.getLimitAmountSlot();
        expiryTimeSlot = partyOptions.getExpiryTimeSlot();
        expiresSlot = partyOptions.getExpiresSlot();
        perPlayerLimitSlot = partyOptions.getPerPlayerLimitSlot();
        perItemMessageSlot = partyOptions.getPerItemMessageSlot();
    }

    @EventHandler
    private void onPageItemClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (!title.endsWith(pageIdentifier)) {
            return;
        }

        // cancel events that move items
        InventoryAction action = e.getAction();
        int slot = e.getRawSlot();
        if (!isItemSlot(slot) && checkInventoryEvent(action, e)) {
            e.setCancelled(true);
        }

        handleMenuClick(slot, e, title);
    }

    /**
     * Checks if an inventory click event has to be cancelled.
     *
     * @param action inventory action from user
     * @param e inventory click event
     *
     * @return true if event has to be cancelled, false otherwise
     */
    private boolean checkInventoryEvent(InventoryAction action, InventoryClickEvent e) {
        return (action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.PICKUP_SOME || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || action == InventoryAction.CLONE_STACK || action == InventoryAction.HOTBAR_SWAP
                || action == InventoryAction.SWAP_WITH_CURSOR) || e.isShiftClick();
    }

    /**
     * Handles inventory click events on menu.
     *
     * @param slot slot that user clicked on
     * @param e inventory click event
     * @param title title of inventory
     *
     * @return true if click is handled here, false otherwise
     */
    private boolean handleMenuClick(int slot, InventoryClickEvent e, String title) {
        if (!title.endsWith(pageIdentifier)) {
            return false;
        }

        String[] metaData = title.split("§");
        int currPage = Integer.parseInt(metaData[1]);
        String partyName = metaData[2].substring(1);
        HumanEntity humanEntity = e.getWhoClicked();
        if (slot == prevPageSlot || slot == nextPageSlot) {
            int page = getPageToGoTo(e.getView().getTitle(), slot == nextPageSlot);
            Party party = main.getPartyManager().getEditableParty(partyName);
            party = party.updateItems(e.getClickedInventory(), currPage);
            main.getPartyManager().updateEditableParty(party);
            Inventory inv = main.getPartyManager().getEditPartyGui(partyName, page, true);
            if (inv == null) {
                return false;
            }
            humanEntity.openInventory(inv);
            return true;
        }

        if (slot == dropStackSlot) {
            handleDropStackClick(e, partyName, currPage);
        } else if (slot == dropOrderSlot) {
            handleDropOrderClick(e, partyName, currPage);
        } else if (slot == minPlayersSlot) {
            handleMinPlayersClick(e, partyName, currPage);
        } else if (slot == fireworksSlot) {
            handleFireworksClick(e, partyName, currPage);
        } else if (slot == effectsSlot) {
            handleEffectsClick(e, partyName, currPage);
        } else if (slot == effectsCountSlot) {
            handleEffectsCountClick(e, partyName, currPage);
        } else if (slot == delaySlot) {
            handleDelayClick(e, partyName, currPage);
        } else if (slot == soundTypeSlot) {
            handleSoundTypeClick(e, partyName, currPage);
        } else if (slot == broadcastSlot) {
            handleBroadcastClick(e, partyName, currPage);
        } else if (slot == limitAmountSlot) {
            handleLimitAmountClick(e, partyName, currPage);
        } else if (slot == expiryTimeSlot) {
            handleExpiryTimeClick(e, partyName, currPage);
        } else if (slot == expiresSlot) {
            handleExpiresClick(e, partyName, currPage);
        } else if (slot == perPlayerLimitSlot) {
            handlePerPlayerLimitClick(e, partyName, currPage);
        } else if (slot == perItemMessageSlot) {
            handlePerItemMessageClick(e, partyName, currPage);
        } else if (slot == saveSlot) {
            handleSaveClick(e, partyName, currPage);
        }
        return false;
    }

    /**
     * Gets the current page number
     *
     * @param title title of inventory
     * @param isNextPage whether slot clicked is for next page
     *
     * @return current page number
     */
    private int getPageToGoTo(String title, boolean isNextPage) {
        int currPage = Integer.parseInt(title.split("§", 3)[1]);
        if (!isNextPage && currPage == 1) {
            return 1;
        }

        int maxPage = main.getConfigManager().getConfig().getInt("max-inv-pages", 10);
        if (isNextPage && currPage >= maxPage) {
            return maxPage;
        }
        return isNextPage ? currPage + 1 : currPage - 1;
    }

    /**
     * Checks if slot clicked is an item slot.
     *
     * @param slot slot to check
     *
     * @return true if is item slot, false otherwise
     */
    private boolean isItemSlot(int slot) {
        return Arrays.stream(itemSlots).anyMatch(x -> x == slot) || slot > 53;
    }

    /**
     * Handles logic for when drop stack option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleDropStackClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        party = party.toggleDropStack();
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when drop order option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleDropOrderClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        if (e.getClick() == ClickType.LEFT) {
            party = party.cycleForwardDropOrder();
        } else {
            party = party.cycleBackwardDropOrder();
        }
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when min players option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleMinPlayersClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        if (e.getClick() == ClickType.LEFT) {
            party = party.addMinPlayers();
        } else {
            party = party.deductMinPlayers();
        }
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when fireworks option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleFireworksClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        party = party.toggleFireworks();
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when effects option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleEffectsClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        if (e.getClick() == ClickType.LEFT) {
            party = party.cycleForwardEffects();
        } else {
            party = party.cycleBackwardEffects();
        }
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when effects count option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleEffectsCountClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        if (e.getClick() == ClickType.LEFT) {
            party = party.addEffectsCount();
        } else {
            party = party.deductEffectsCount();
        }
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when delay option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleDelayClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        if (e.getClick() == ClickType.LEFT) {
            party = party.addDelay();
        } else {
            party = party.deductDelay();
        }
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when sound type option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleSoundTypeClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        if (e.getClick() == ClickType.LEFT) {
            party = party.cycleForwardSoundType();
        } else {
            party = party.cycleBackwardSoundType();
        }
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when broadcast option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleBroadcastClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        party = party.toggleBroadcast();
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when limit amount option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleLimitAmountClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        if (e.getClick() == ClickType.LEFT) {
            party = party.addLimitAmount();
        } else {
            party = party.deductLimitAmount();
        }
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when expiry time option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleExpiryTimeClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        if (e.getClick() == ClickType.LEFT) {
            party = party.addExpiryTime();
        } else {
            party = party.deductExpiryTime();
        }
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when expires option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleExpiresClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        party = party.toggleExpires();
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when per-player limit option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handlePerPlayerLimitClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        party = party.togglePerPlayerLimit();
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when per-item message option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handlePerItemMessageClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        party = party.togglePerItemMessage();
        e.getWhoClicked().openInventory(party.getGui(currPage));
        main.getPartyManager().updateEditableParty(party);
    }

    /**
     * Handles logic for when save option is clicked.
     *
     * @param e inventory click event
     * @param partyName name of party that menu is open for
     * @param currPage current page of menu
     */
    private void handleSaveClick(InventoryClickEvent e, String partyName, int currPage) {
        Party party = main.getPartyManager().getEditableParty(partyName);
        party = party.updateItems(e.getClickedInventory(), currPage);
        main.getPartyManager().updateEditableParty(party);
        main.getPartyManager().saveParty(partyName);
        e.getWhoClicked().closeInventory();
        MessageManager.sendMessage(e.getWhoClicked(), "save-party-changes",
            new String[]{"%party%"},
            new String[]{partyName});
    }
}
