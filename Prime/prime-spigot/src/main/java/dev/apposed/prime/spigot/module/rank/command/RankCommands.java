package dev.apposed.prime.spigot.module.rank.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.ModuleHandler;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.module.rank.menu.RankEditorMenu;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class RankCommands {

    private static final Prime plugin = Prime.getInstance();
    private static final ModuleHandler moduleHandler = plugin.getModuleHandler();
    
    private static final RankHandler rankHandler = moduleHandler.getModule(RankHandler.class);

    @Command(names = {"rank editor"}, permission = "prime.command.rank.editor")
    public static void executeEditor(Player player) {
        new RankEditorMenu().openMenu(player);
    }

    @Command(names = {"rank list"}, permission = "prime.command.rank.list")
    public static void executeList(CommandSender sender) {
        rankHandler.getCache().stream().sorted(Comparator.comparingInt(Rank::getWeight).reversed()).forEach(rank -> sender.sendMessage(Color.translate("&7- &r" + rank.getColoredDisplay() + " &7(Weight: &f" + rank.getWeight() + "&7) (Prefix: &f" + rank.getPrefix() + "&7)")));
    }

    @Command(names = {"rank create"}, permission = "prime.command.rank.create")
    public static void executeCreate(CommandSender sender, @Parameter(name = "name") String name) {
        Rank rank = rankHandler.create(new Rank(name));
        sender.sendMessage(Color.translate("&aCreated rank &r" + rank.getColoredDisplay() + "&a."));
    }

    @Command(names = {"rank delete", "rank remove"}, permission = "prime.command.rank.delete")
    public static void executeDelete(CommandSender sender, @Parameter(name = "rank", wildcard = true) Rank rank) {
        rankHandler.delete(rank);
        sender.sendMessage(Color.translate("&aDeleted rank &r" + rank.getColoredDisplay() + "&a."));
    }

    @Command(names = {"rank setweight"}, permission = "prime.command.rank.setweight")
    public static void executePrefix(CommandSender sender, @Parameter(name = "rank") Rank rank, @Parameter(name = "weight") int weight) {
        rank.setWeight(weight);
        rankHandler.save(rank);
        sender.sendMessage(Color.translate("&aSet &r" + rank.getColoredDisplay() + "'s &aweight."));
    }

    @Command(names = {"rank setprefix"}, permission = "prime.command.rank.setprefix")
    public static void executePrefix(CommandSender sender, @Parameter(name = "rank") Rank rank, @Parameter(name = "prefix", wildcard = true) String prefix) {
        rank.setPrefix(prefix);
        rankHandler.save(rank);
        sender.sendMessage(Color.translate("&aSet &r" + rank.getColoredDisplay() + "'s &aprefix."));
    }

    @Command(names = {"rank setcolor"}, permission = "prime.command.rank.setcolor")
    public static void executeColor(CommandSender sender, @Parameter(name = "rank") Rank rank, @Parameter(name = "color") String color) {
        rank.setColor(Color.translate(color));
        rankHandler.save(rank);
        sender.sendMessage(Color.translate("&aSet &r" + rank.getColoredDisplay() + "'s &acolor."));
    }

    @Command(names = {"rank addperm"}, permission = "prime.command.rank.addperm")
    public static void executeAddPerm(CommandSender sender, @Parameter(name = "rank") Rank rank, @Parameter(name = "permission", wildcard = true) String permission) {
        rank.getPermissions().add(permission);
        rankHandler.save(rank);
        sender.sendMessage(Color.translate("&aAdded &7" + permission + " &ato &r" + rank.getColoredDisplay() + "&a."));
    }

    @Command(names = {"rank delperm"}, permission = "prime.command.rank.delperm")
    public static void executeDelPerm(CommandSender sender, @Parameter(name = "rank") Rank rank, @Parameter(name = "permission", wildcard = true) String permission) {
        rank.getPermissions().remove(permission);
        rankHandler.save(rank);
        sender.sendMessage(Color.translate("&aRemoved &7" + permission + " &afrom &r" + rank.getColoredDisplay() + "&a."));
    }
}
