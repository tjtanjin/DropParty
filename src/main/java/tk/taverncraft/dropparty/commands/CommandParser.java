package tk.taverncraft.dropparty.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import tk.taverncraft.dropparty.Main;

/**
 * CommandParser contains the onCommand method that handles user command input.
 */
public class CommandParser implements CommandExecutor {
    private final Main main;

    /**
     * Constructor for CommandParser.
     *
     * @param main plugin class
     */
    public CommandParser(Main main) {
        this.main = main;
    }

    /**
     * Entry point of commands.
     *
     * @param sender user who sent command
     * @param cmd command which was executed
     * @param label alias of the command
     * @param args arguments following the command
     *
     * @return true at end of execution
     */
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("dropparty")) {

            // If no arguments provided or is null, return invalid command
            if (args.length == 0) {
                return new InvalidCommand().execute(sender);
            }

            final String chatCmd = args[0];

            if (chatCmd == null) {
                return new InvalidCommand().execute(sender);
            } else if (chatCmd.equalsIgnoreCase("create")) {
                return new CreateCommand(main).execute(sender, args);
            } else if (chatCmd.equalsIgnoreCase("edit")) {
                return new EditCommand(main).execute(sender, args);
            } else if (chatCmd.equalsIgnoreCase("delete")) {
                return new DeleteCommand(main).execute(sender, args);
            } else if (chatCmd.equalsIgnoreCase("setdisplayname")) {
                return new SetDisplayNameCommand(main).execute(sender, args);
            } else if (chatCmd.equalsIgnoreCase("setpoint")) {
                return new SetPointCommand().execute(sender);
            } else if (chatCmd.equalsIgnoreCase("unsetpoint")) {
                return new UnsetPointCommand().execute(sender);
            } else if (chatCmd.equalsIgnoreCase("unsetallpoints")) {
                return new UnsetAllPointsCommand().execute(sender);
            } else if (chatCmd.equalsIgnoreCase("togglepoints")) {
                return new TogglePointsCommand().execute(sender);
            } else if (chatCmd.equalsIgnoreCase("throw")) {
                return new ThrowCommand(main).execute(sender, args);
            } else if (chatCmd.equalsIgnoreCase("stop")) {
                return new StopCommand(main).execute(sender, args);
            } else if (chatCmd.equalsIgnoreCase("clone")) {
                return new CloneCommand(main).execute(sender, args);
            } else if (chatCmd.equalsIgnoreCase("list")) {
                return new ListCommand(main).execute(sender, args);
            } else if (chatCmd.equalsIgnoreCase("help")) {
                return new HelpCommand(main).execute(sender, args);
            } else if (chatCmd.equalsIgnoreCase("reload")) {
                return new ReloadCommand(main).execute(sender);
            } else {
                return new InvalidCommand().execute(sender);
            }
        }
        return true;
    }
}
