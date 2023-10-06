package dev.apposed.prime.packet;

import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.packet.Packet;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;

@AllArgsConstructor
public class ConsoleCommandPacket extends Packet {

    private final String command;

    @Override
    public void onReceive() {
        Bukkit.getScheduler().runTask(Prime.getInstance(), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command + " -c");
        });
    }

    @Override
    public void onSend() {

    }
}
