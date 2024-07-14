package tk.taverncraft.dropparty.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import tk.taverncraft.dropparty.Main;

/**
 * CommandTabCompleter prompts users to tab complete the possible commands based on current input.
 */
public class CommandTabCompleter implements TabCompleter {
    private static final String[] COMMANDS = {"create", "edit", "delete", "setdisplayname", "setpoint", "unsetpoint", "unsetallpoints", "togglepoints", "throw", "stop", "clone", "list", "help", "reload"};
    private final Main main;

    public CommandTabCompleter(Main main) {
        this.main = main;
    }


    /**
     * Overridden method from TabCompleter, entry point for checking of user command to suggest
     * tab complete.
     *
     * @param sender user who sent the command
     * @param mmd command sent by the user
     * @param label exact command name typed by the user
     * @param args arguments following the command name
     * @return list of values as suggestions to tab complete for the user
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command mmd, String label, String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("throw")) {
            completions.add("location");
            completions.add("player");
            completions.add("mob");
            completions.add("chat");
            completions.add("inventory");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("edit")
            || args[0].equalsIgnoreCase("delete")
            || args[0].equalsIgnoreCase("clone")
            || args[0].equalsIgnoreCase("stop")
            || args[0].equalsIgnoreCase("setdisplayname")
            || args[0].equalsIgnoreCase("setpoint")
            || args[0].equalsIgnoreCase("unsetpoint")
            || args[0].equalsIgnoreCase("unsetallpoints")
            || args[0].equalsIgnoreCase("togglepoints"))) {
            for (String party: main.getConfigManager().getPartyNames()) {
                completions.add(party);
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("location")) {
            for (String party: main.getConfigManager().getPartyNames()) {
                completions.add(party);
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("chat")) {
            for (String party: main.getConfigManager().getPartyNames()) {
                completions.add(party);
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("inventory")) {
            for (String party: main.getConfigManager().getPartyNames()) {
                completions.add(party);
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("player")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 4 && args[1].equalsIgnoreCase("player")) {
            for (String party: main.getConfigManager().getPartyNames()) {
                completions.add(party);
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("mob")) {
            for (EntityType entityType : EntityType.values()) {
                if (entityType.isAlive() && entityType.isSpawnable()) {
                    completions.add(entityType.name());
                }
            }
        } else if (args.length == 4 && args[1].equalsIgnoreCase("mob")) {
            for (String party: main.getConfigManager().getPartyNames()) {
                completions.add(party);
            }
        }
        return completions;
    }
}