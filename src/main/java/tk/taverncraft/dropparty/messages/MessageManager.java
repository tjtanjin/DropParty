package tk.taverncraft.dropparty.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

import tk.taverncraft.dropparty.types.TextComponentPair;
import tk.taverncraft.dropparty.utils.StringUtils;

import static org.bukkit.util.ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH;

/**
 * MessageManager handles all formatting and sending of messages to the command sender.
 */
public class MessageManager {
    private static final HashMap<String, String> messageKeysMap = new HashMap<>();

    private static ArrayList<String> helpBoard;

    /**
     * Sets the messages to use.
     *
     * @param lang the configuration to base the messages on
     */
    public static void setMessages(FileConfiguration lang) {
        Set<String> messageKeysSet = lang.getConfigurationSection("").getKeys(false);

        for (String messageKey : messageKeysSet) {
            messageKeysMap.put(messageKey, StringUtils.formatStringColor(lang.get(messageKey).toString() + " "));
        }
        setUpHelpBoard();
    }

    /**
     * Sends message to the sender.
     *
     * @param sender sender to send message to
     * @param messageKey key to get message with
     */
    public static void sendMessage(CommandSender sender, String messageKey) {
        // if sender is command block, then nothing to send
        if (sender instanceof BlockCommandSender) {
            return;
        }
        String message = getPrefixedMessage(messageKey);
        sender.sendMessage(message);
    }

    /**
     * Sends message to the sender, replacing placeholders.
     *
     * @param sender sender to send message to
     * @param messageKey key to get message with
     * @param keys placeholder keys
     * @param values placeholder values
     */
    public static void sendMessage(CommandSender sender, String messageKey, String[] keys, String[] values) {
        // if sender is command block, then nothing to send
        if (sender instanceof BlockCommandSender) {
            return;
        }
        String message = getPrefixedMessage(messageKey);
        boolean messageHasDisplayName = message.contains("%party_display_name%");
        for (int i = 0; i < keys.length; i++) {
            message = message.replaceAll(keys[i], values[i]);
        }


        // if message has display name need to parse colors again
        if (messageHasDisplayName) {
            message = StringUtils.formatStringColor(message);
        }
        sender.sendMessage(message);
    }

    /**
     * Broadcasts message to all players, replacing placeholders.
     *
     * @param messageKey key to get message with
     * @param keys placeholder keys
     * @param values placeholder values
     */
    public static void broadcastMessage(String messageKey, String[] keys, String[] values) {
        String message = getPrefixedMessage(messageKey);
        boolean messageHasDisplayName = message.contains("%party_display_name%");
        for (int i = 0; i < keys.length; i++) {
            message = message.replaceAll(keys[i], values[i]);
        }

        // if message has display name need to parse colors again
        if (messageHasDisplayName) {
            message = StringUtils.formatStringColor(message);
        }

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player p : onlinePlayers) {
            p.sendMessage(message);
        }
    }

    /**
     * Retrieves message value given the message key and appends prefix.
     *
     * @param messageKey key to retrieve message with
     */
    public static String getPrefixedMessage(String messageKey) {
        String prefix = messageKeysMap.get("prefix");
        return prefix.substring(0, prefix.length() - 1) + messageKeysMap.get(messageKey);
    }

    public static void showParties(CommandSender sender, List<String> partyNames, int pageNum, int partiesPerPage) {
        int linesPerPage = partiesPerPage + 2;

        String header = getPrefixedMessage("party-list-header");
        String footer = messageKeysMap.get("party-list-footer");
        String body = messageKeysMap.get("party-list-body");
        StringBuilder message = new StringBuilder(header);
        int partyLineNum = 1;
        int currentPage = 1;
        for (String party: partyNames) {
            message.append(body.replaceAll("%party%", party));
            if (partyLineNum % partiesPerPage == 0) {
                currentPage++;
                message = new StringBuilder(message.append(footer).append("\n").toString().replaceAll("%page%", String.valueOf(currentPage)));
                message.append(header).append("\n");
            }
            partyLineNum++;
        }

        ChatPaginator.ChatPage page = ChatPaginator.paginate(message.toString(), pageNum, GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH, linesPerPage);
        for (String line : page.getLines()) {
            sender.sendMessage(line);
        }
    }

    /**
     * Shows help menu to the user.
     *
     * @param sender sender to send message to
     * @param pageNum page number to view
     */
    public static void showHelpBoard(CommandSender sender, int pageNum) {
        if (helpBoard == null) {
            return;
        }

        int index = pageNum - 1;
        if (pageNum > helpBoard.size()) {
            sender.sendMessage(helpBoard.get(helpBoard.size() - 1));
        } else {
            sender.sendMessage(helpBoard.get(index));
        }
    }

    public static void setUpHelpBoard() {
        int positionsPerPage = 10;

        helpBoard = new ArrayList<>();
        String header = getPrefixedMessage("help-header");
        String footer = messageKeysMap.get("help-footer");
        String[] messageBody = messageKeysMap.get("help-body").split("\n", -1);
        StringBuilder message = new StringBuilder();
        int position = 1;
        int currentPage = 1;
        for (String body : messageBody) {
            if (position % positionsPerPage == 1) {
                message = new StringBuilder(header + "\n");
            }

            message.append(body).append("\n");

            if (position % positionsPerPage == 0) {
                currentPage++;
                message.append(footer.replaceAll("%page%", String.valueOf(currentPage)));
                helpBoard.add(message.toString());
            }
            position++;
        }
        helpBoard.add(message.toString());
    }

    /**
     * Creates and returns base component array for a message.
     *
     * @param message to put in base component array
     *
     * @return base component array for message
     */
    public static BaseComponent[] getBaseComponentArrMessage(String message) {
        char[] chars = message.toCharArray();
        int lastIndex = chars.length - 1;
        List<TextComponentPair> textComponentPairs = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.WHITE;
        for (int i = 0 ; i <= lastIndex; i++) {
            char c = chars[i];
            if (c == '&' && i != lastIndex) {
                net.md_5.bungee.api.ChatColor nextColor = net.md_5.bungee.api.ChatColor
                    .getByChar(chars[i + 1]);
                if (color == null) {
                    sb.append(c);
                } else {
                    textComponentPairs.add(new TextComponentPair(sb.toString(), color));
                    color = nextColor;
                    sb = new StringBuilder();
                }
            } else {
                sb.append(c);
            }
        }
        textComponentPairs.add(new TextComponentPair(sb.toString(), color));

        ComponentBuilder componentBuilder = new ComponentBuilder("");
        int numPairs = textComponentPairs.size();
        for (int i = 0; i < numPairs; i++) {
            TextComponentPair textComponentPair = textComponentPairs.get(i);
            componentBuilder.append(textComponentPair.getMessage()).color(
                textComponentPair.getColor());
        }
        return componentBuilder.create();
    }

    /**
     * Creates and returns text component for a message.
     *
     * @param messageKey key to get message to put in text component
     *
     * @return text component for message
     */
    public static TextComponent getTextComponentMessage(String messageKey) {
        String message = messageKeysMap.get(messageKey);
        BaseComponent[] baseComponent = getBaseComponentArrMessage(message);

        TextComponent textComponent = new TextComponent();
        for (BaseComponent bc : baseComponent) {
            textComponent.addExtra(bc);
        }
        return textComponent;
    }
}
