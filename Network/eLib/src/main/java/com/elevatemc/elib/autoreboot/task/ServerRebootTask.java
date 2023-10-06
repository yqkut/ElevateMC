package com.elevatemc.elib.autoreboot.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.elib.util.TimeUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import com.elevatemc.elib.util.message.MessageBuilder;
import com.elevatemc.elib.util.message.MessageTranslator;

public class ServerRebootTask extends BukkitRunnable {
    private static final String line = MessageTranslator.translate("&4&m---------------------------------");
    @Getter private int secondsRemaining;
    @Getter private boolean wasWhitelisted;

    public ServerRebootTask(long time) {
        this.secondsRemaining = (int)(time / 1000);
        this.wasWhitelisted = eLib.getInstance().getServer().hasWhitelist();
    }

    public void run() {

        if (this.secondsRemaining == 300) {
            eLib.getInstance().getServer().setWhitelist(true);
        } else if (this.secondsRemaining == 5) {
            eLib.getInstance().getServer().setWhitelist(this.wasWhitelisted);

            eLib.getInstance().getLogger().info("Sending everyone to hub...");
            TaskUtil.runSync(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.kickPlayer(ChatColor.RED + "The server was shutdown...");
                }
            });
        }

        if (secondsRemaining > 0 && (secondsRemaining <= 10 || (secondsRemaining <= 60 && secondsRemaining % 5 == 0) || (secondsRemaining % 30 == 0))) {
            String message = MessageBuilder
                    .error("Server is rebooting in {}.")
                    .prefix("⚠")
                    .element(TimeUtils.formatIntoMMSS(secondsRemaining))
                    .build();
            Bukkit.broadcastMessage(line);
            Bukkit.broadcastMessage(message);
            Bukkit.broadcastMessage(line);
        }

        if (secondsRemaining <= 0) {
            TaskUtil.runSync(Bukkit::shutdown);
        }
        secondsRemaining--;
    }

    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        eLib.getInstance().getServer().setWhitelist(this.wasWhitelisted);
    }
}
