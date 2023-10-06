package dev.apposed.prime.spigot.module.rank.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RankInheritEditor extends PaginatedMenu {

    private final Prime plugin = Prime.getInstance();
    private final RankHandler rankHandler = plugin.getModuleHandler().getModule(RankHandler.class);

    private final Rank rank;

    public RankInheritEditor(Rank rank) {
        this.rank = rank;
        setUpdateAfterClick(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Rank Inheritance Editor";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        this.rankHandler.getCache()
                .stream()
                .sorted(Comparator.comparingInt(Rank::getWeight))
                .filter(rank -> !rank.getName().equalsIgnoreCase(this.rank.getName()))
                .forEach(inheritRank -> {
                    buttons.put(slot.getAndIncrement(), new Button() {
                        @Override
                        public String getName(Player player) {
                            return Color.translate(inheritRank.getColoredDisplay());
                        }

                        @Override
                        public List<String> getDescription(Player player) {
                            return Color.translate(ImmutableList.of(
                                    " ",
                                    rank.inherits(inheritRank) ? "&c&lRight Click &7to remove" : "&a&lLeft Click &7to add"
                            ));
                        }

                        @Override
                        public Material getMaterial(Player player) {
                            return inheritRank.getWool().getType();
                        }

                        @Override
                        public byte getDamageValue(Player player) {
                            return (byte) inheritRank.getWool().getDurability();
                        }

                        @Override
                        public void clicked(Player player, int slot, ClickType type) {
                            switch(type) {
                                case LEFT: {
                                    if(!rank.inherits(inheritRank)) {
                                        rank.getInherits().add(inheritRank);
                                        rankHandler.save(rank);
                                        player.sendMessage(Color.translate("&aSuccessfully added &r" + inheritRank.getColoredDisplay() + " &ato &r" + rank.getColoredDisplay() + "'s &ainheritances."));
                                    }
                                    break;
                                }
                                case RIGHT: {
                                    if(rank.inherits(inheritRank)) {
                                        rank.getInherits().remove(inheritRank);
                                        rankHandler.save(rank);
                                        player.sendMessage(Color.translate("&aSuccessfully removed &r" + inheritRank.getColoredDisplay() + " &afrom &r" + rank.getColoredDisplay() + "'s &ainheritances."));
                                    }
                                    break;
                                }
                            }
                        }
                    });
                });

        return buttons;
    }
}
