package dev.apposed.prime.spigot.module.server.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.packet.ServerUpdatePacket;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.server.ServerGroup;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMenu extends PaginatedMenu {

    private final Prime plugin = Prime.getInstance();
    private final ServerHandler serverHandler = plugin.getModuleHandler().getModule(ServerHandler.class);
    private final JedisModule jedisModule = plugin.getModuleHandler().getModule(JedisModule.class);

    private final ServerGroup group;

    public ServerMenu(ServerGroup group) {
        this.group = group;
        setUpdateAfterClick(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Prime Servers (" + group.getId() + ")";
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 9;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        this.serverHandler.getServersWithGroup(this.group).forEach(server -> {
            buttons.put(slot.getAndIncrement(), new Button() {
                @Override
                public String getName(Player player) {
                    return Color.translate("&6&l" + server.getName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Color.translate(ImmutableList.of(
                            "&7(ID: " + server.getName() + ")",
                            " ",
                            "&7Players: &f" + server.getPlayers() + "/" + server.getMaxPlayers(),
                            "&7Whitelisted: &r" + (server.isWhitelisted() ? "&aYes" : "&cNo"),
                            "&7Alive: &r" + (server.isAlive() ? "&aYes" : "&cNo"),
                            "&7Last Heartbeat: &f" + ((System.currentTimeMillis() - server.getLastHeartbeat()) / 1000) + "s ago",
                            " ",
                            "&a&lLeft Click &7to reload config"
                    ));
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.WATER_LILY;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    jedisModule.sendPacket(new ServerUpdatePacket(
                            server.getName()
                    ));
                    player.sendMessage(Color.translate("&aSent a redis packet to reload &e" + server.getName() + "&a."));

                }
            });
        });

        return buttons;
    }
}