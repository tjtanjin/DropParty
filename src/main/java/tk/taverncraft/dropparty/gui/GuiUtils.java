package tk.taverncraft.dropparty.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tk.taverncraft.dropparty.utils.StringUtils;

/**
 * GuiUtils contains generic functions that helps in creating a GUI.
 */
public class GuiUtils {

    /**
     * Creates an item to show in the GUI.
     *
     * @param material material to use
     * @param name name to show
     * @param isEnchanted whether the item should be enchanted
     * @param lore lore of the item
     *
     * @return item that is to be shown in the GUI
     */
    public static ItemStack createGuiItem(final Material material, final String name,
                                      boolean isEnchanted, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);

        if (isEnchanted) {
            item.addUnsafeEnchantment(Enchantment.LURE, 1);
        }

        final ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.values());

        if (isEnchanted) {
            try {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } catch (Exception ignored) {

            }
        }

        // Set the name of the item
        if (name != null) {
            meta.setDisplayName(StringUtils.formatStringColor(name));
        } else {
            meta.setDisplayName(ChatColor.RESET + "");
        }

        // Set the lore of the item
        if (lore != null && lore.length != 0) {
            List<String> colouredLore = parseWithColours(lore);
            meta.setLore(colouredLore);
        } else {
            meta.setLore(new ArrayList<>());
        }

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Parse colour for strings.
     *
     * @param lore lore to parse colour for
     *
     * @return lore with parsed colours
     */
    private static List<String> parseWithColours(String[] lore) {
        List<String> colouredLore = new ArrayList<>();
        for (String line : lore) {
            colouredLore.add(StringUtils.formatStringColor(line));
        }
        return colouredLore;
    }

    /**
     * Parses a name to replace placeholder with string value.
     *
     * @param lore lore to parse
     * @param placeholder placeholder to replace
     * @param name name to use
     *
     * @return parsed name
     */
    public static String parseName(String lore, String placeholder, String name) {
        return lore.replaceAll(placeholder, name);
    }

    /**
     * Parses attribute in lore.
     *
     * @param lore lore to parse
     * @param placeholder placeholder to replace
     * @param value value to use
     *
     * @return parsed lore for attribute
     */
    public static List<String> parseAttribute(List<String> lore, String placeholder, Object value) {
        List<String> parsedAttribute = new ArrayList<>();
        if (lore == null) {
            return parsedAttribute;
        }
        for (String s : lore) {
            parsedAttribute.add(s.replaceAll(placeholder, value.toString()));
        }
        return parsedAttribute;
    }

    /**
     * Parses attributes in lore.
     *
     * @param lore lore to parse
     * @param placeholders placeholders to replace
     * @param values values to use
     *
     * @return parsed lore for attributes
     */
    public static List<String> parseAttributes(List<String> lore, String[] placeholders, Object[] values) {
        List<String> parsedAttribute = new ArrayList<>();
        if (lore == null) {
            return parsedAttribute;
        }
        for (String line : lore) {
            for (int i = 0; i < placeholders.length; i++) {
                line = line.replaceAll(placeholders[i], values[i].toString());
            }
            parsedAttribute.add(line);
        }
        return parsedAttribute;
    }

    /**
     * Parses page placeholder in lore with int value.
     *
     * @param lore lore to parse
     * @param placeholder placeholder to replace
     * @param page page to use
     *
     * @return parsed page
     */
    public static List<String> parsePage(List<String> lore, String placeholder, int page) {
        List<String> parsedPage = new ArrayList<>();
        if (lore == null) {
            return parsedPage;
        }
        for (String s : lore) {
            parsedPage.add(s.replaceAll(placeholder, String.valueOf(page)));
        }
        return parsedPage;
    }
}
