package com.elevatemc.potpvp.command;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.menu.menus.ConfirmMenu;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.redis.RedisCommand;
import com.elevatemc.elib.util.Callback;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class StatsResetCommands {
    private static String REDIS_PREFIX = "PotPvP:statsResetToken:";

    @Command(names = { "statsreset addtoken" }, permission = "op", async = true)
    public static void addToken(CommandSender sender, @Parameter(name = "player") String playerName, @Parameter(name = "amount") int amount) {
        UUID uuid = UUIDUtils.uuid(playerName);

        if (uuid == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Unable to locate '" + playerName + "'.");
            return;
        }

        addTokens(uuid, amount);
        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " token" + (amount == 1 ? "" : "s") + " to " + UUIDUtils.name(uuid) + ".");
    }

    @Command(names = {"statsreset"}, permission = "", async = true)
    public static void reset(Player sender) {
        int tokens = getTokens(sender.getUniqueId());
        if (tokens <= 0) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You need at least one token to reset your stats.");
            return;
        }

        Bukkit.getScheduler().runTask(PotPvPSI.getInstance(), () -> {
            new ConfirmMenu("Stats reset", new Callback<Boolean>() {

                @Override
                public void callback(Boolean reset) {
                    if (!reset) {
                        sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Stats reset aborted.");
                        return;
                    }

                    Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
                        PotPvPSI.getInstance().getEloHandler().resetElo(sender.getUniqueId());
                        removeTokens(sender.getUniqueId(), 1);
                        int tokens = getTokens(sender.getUniqueId());
                        sender.sendMessage(ChatColor.GREEN + "Reset your stats! Used one token. " + tokens + " token" + (tokens == 1 ? "" : "s") + " left.");
                    });
                }
                
            }).openMenu(sender); 
        });
    }

    private static int getTokens(UUID player) {
        return eLib.getInstance().runBackboneRedisCommand(new RedisCommand<Integer>() {

            @Override
            public Integer execute(Jedis redis) {
                return Integer.valueOf(Objects.firstNonNull(redis.get(REDIS_PREFIX + player.toString()), "0"));
            }

        });
    }

    private static void addTokens(UUID player, int amountBy) {
        eLib.getInstance().runBackboneRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                redis.incrBy(REDIS_PREFIX + player.toString(), amountBy);
                return null;
            }
            
        });
    }

    public static void removeTokens(UUID player, int amountBy) {
        eLib.getInstance().runBackboneRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                redis.decrBy(REDIS_PREFIX + player.toString(), amountBy);
                return null;
            }
            
        });
    }
}
