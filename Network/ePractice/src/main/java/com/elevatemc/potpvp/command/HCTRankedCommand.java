package com.elevatemc.potpvp.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.entity.Player;

public class HCTRankedCommand {

    @Command(names = {"hctranked"}, permission = "")
    public static void hctRanked(Player player) {
        player.sendMessage(Color.translate("&b&lHCT Ranked Discord: &ehttps://discord.gg/HCTRanked"));
    }

    @Command(names = {"website"}, permission = "")
    public static void website(Player player) {
        player.sendMessage(Color.translate("&b&lWebsite: &ehttps://elevatemc.com"));
    }

    @Command(names = {"tele", "telegram"}, permission = "")
    public static void telegram(Player player) {
        player.sendMessage(Color.translate("&b&lTelegram: &ehttps://telegram.com/ElevateMC"));
    }

    @Command(names = {"store", "shop"}, permission = "")
    public static void store(Player player) {
        player.sendMessage(Color.translate("&b&lStore: &ehttps://store.elevatemc.com"));
    }
}
