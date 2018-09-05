package net.Indyuce.moarbows.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.moarbows.GUI;
import net.Indyuce.moarbows.MoarBows;
import net.Indyuce.moarbows.api.Message;
import net.Indyuce.moarbows.api.MoarBow;

public class MoarBowsCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			if (!sender.hasPermission("moarbows.admin")) {
				sender.sendMessage(Message.NOT_ENOUGH_PERMS.translate());
				return true;
			}

			sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " MoarBows Help " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "<>" + ChatColor.GRAY + " = required");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "()" + ChatColor.GRAY + " = optional");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/mb " + ChatColor.WHITE + "shows the help page.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/mb get <bow> (player) " + ChatColor.WHITE + "gives a player a bow.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/mb getall " + ChatColor.WHITE + "gives you all the available bows.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/mb gui " + ChatColor.WHITE + "shows all available bows in a GUI.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/mb list " + ChatColor.WHITE + "shows all available bows.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/mb reload " + ChatColor.WHITE + "reloads the config file.");
			return true;
		}

		if (args[0].equalsIgnoreCase("gui")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command is for players only.");
				return true;
			}

			if (!sender.hasPermission("moarbows.gui")) {
				sender.sendMessage(Message.NOT_ENOUGH_PERMS.translate());
				return true;
			}

			GUI.openInv((Player) sender);
		}

		// perm for op commands
		if (!sender.hasPermission("moarbows.admin")) {
			sender.sendMessage(Message.NOT_ENOUGH_PERMS.translate());
			return true;
		}

		if (args[0].equalsIgnoreCase("reload")) {

			// reload config files
			MoarBows.plugin.reloadConfig();
			MoarBows.getLanguage().reloadConfigFiles();

			// reload bows
			for (MoarBow bow : MoarBows.getBows())
				bow.update(MoarBows.getLanguage().getBows());

			sender.sendMessage(ChatColor.YELLOW + "Config files & bows reloaded.");
		}

		if (args[0].equalsIgnoreCase("list")) {
			sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------------------------------------------------");
			sender.sendMessage(ChatColor.GREEN + "List of available bows:");
			if (!(sender instanceof Player)) {
				for (MoarBow bow : MoarBows.getBows())
					sender.sendMessage("* " + ChatColor.GREEN + " " + bow.getName());
				return true;
			}

			for (MoarBow bow : MoarBows.getBows())
				MoarBows.getNMS().sendJson((Player) sender, "{\"text\":\"* " + ChatColor.GREEN + bow.getName() + ChatColor.WHITE + ", use /mb get " + bow.getLowerCaseID() + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/mb get " + bow.getID() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click to get the " + ChatColor.GREEN + bow.getName() + ChatColor.WHITE + ".\",\"color\":\"white\"}]}}}");
		}

		if (args[0].equalsIgnoreCase("get")) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /mb get <bow> (player)");
				return true;
			}

			if (args.length < 3 && !(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Please specify a player.");
				return true;
			}

			// bow
			String bowFormat = args[1].toUpperCase().replace("-", "_");
			MoarBow bow = MoarBows.hasBow(bowFormat) ? MoarBows.getBow(bowFormat) : null;
			if (bow == null) {
				sender.sendMessage(ChatColor.RED + "Couldn't find the bow called " + bowFormat + ".");
				return true;
			}

			// player
			Player target = args.length > 2 ? null : ((Player) sender);
			if (args.length > 2)
				target = Bukkit.getPlayer(args[2]);
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "Couldn't find the player called " + args[2] + ".");
				return true;
			}

			// give item
			ItemStack item = bow.getItem();
			for (ItemStack drop : target.getInventory().addItem(item).values())
				target.getWorld().dropItem(target.getLocation(), drop);
			sender.sendMessage(ChatColor.YELLOW + target.getName() + " was given " + ChatColor.WHITE + bow.getName() + ChatColor.YELLOW + ".");

			// message
			String message = Message.RECEIVE_BOW.translate();
			if (!message.equals("") && sender != target)
				target.sendMessage(ChatColor.YELLOW + message.replace("%bow%", bow.getName()));

		}
		if (args[0].equalsIgnoreCase("getall")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is for players only.");
				return true;
			}

			Player player = (Player) sender;
			for (MoarBow bow : MoarBows.getBows())
				for (ItemStack drop : player.getInventory().addItem(bow.getItem()).values())
					player.getWorld().dropItem(player.getLocation(), drop);
		}

		return true;
	}
}