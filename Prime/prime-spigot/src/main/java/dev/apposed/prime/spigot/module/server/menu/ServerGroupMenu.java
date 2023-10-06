package dev.apposed.prime.spigot.module.server.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.packet.ServerUpdatePacket;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerGroupMenu extends PaginatedMenu {

    private final Prime plugin = Prime.getInstance();
    private final ServerHandler serverHandler = plugin.getModuleHandler().getModule(ServerHandler.class);
    private final JedisModule jedisModule = plugin.getModuleHandler().getModule(JedisModule.class);

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Prime Server Groups";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        this.serverHandler.getServerGroups().forEach(group -> {
            buttons.put(slot.getAndIncrement(), new Button() {
                @Override
                public String getName(Player player) {
                    return Color.translate("&6&l" + group.getId() + (serverHandler.isActive(group) ? " &a(Active)" : ""));
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Color.translate(ImmutableList.of(
                            "&eServers: &7" + serverHandler.getServersWithGroup(group).size(),
                            " ",
                            "&a&lRight Click &7to reload server configs"
                    ));
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.HOPPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType type) {
                    switch(type) {
                        case LEFT: {
                            new ServerMenu(group).openMenu(player);
                            break;
                        }
                        case RIGHT: {
                            serverHandler.getServersWithGroup(group).forEach(server -> jedisModule.sendPacket(new ServerUpdatePacket(
                                    server.getName()
                            )));
                            player.sendMessage(Color.translate("&aSent a redis packet to reload &e" + serverHandler.getServersWithGroup(group).size() + " &aservers with the group &e" + group.getId() + "&a."));
                            break;
                        }
                    }
                }
            });
        });

        return buttons;
    }
}
