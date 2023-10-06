package com.elevatemc.potpvp.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EntityCommand {

    @Command(names = {"ec clearall items", "ec clearall", "ec clear items", "items clear"}, permission = "op", description = "Clear all items")
    public static void clearItems(Player player, @Parameter(name = "radius", wildcard = true, defaultValue = "0") double radius) {
        final List<Entity> entities = new ArrayList<>();
        if(radius == 0) {
            entities.addAll(player.getWorld().getEntities());
        } else {
            entities.addAll(player.getNearbyEntities(radius, radius, radius));
        }
        entities.stream().filter(entity -> entity.getType() == EntityType.DROPPED_ITEM).forEach(Entity::remove);

        player.sendMessage(Color.translate("&aCleared " + entities.size() + " entities."));
        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(op -> {
            op.sendMessage(Color.translate("&c[A] &a" + entities.size() + " entities &3have been cleared."));
        });
    }
}
