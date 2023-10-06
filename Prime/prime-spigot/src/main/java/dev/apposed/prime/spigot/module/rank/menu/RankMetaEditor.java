package dev.apposed.prime.spigot.module.rank.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RankMetaEditor extends Menu {

    private final Prime plugin = Prime.getInstance();
    private final RankHandler rankHandler = plugin.getModuleHandler().getModule(RankHandler.class);

    private final Rank rank;

    public RankMetaEditor(Rank rank) {
        this.rank = rank;
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Rank Meta Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        for(RankMeta meta : RankMeta.values()) {
            buttons.put(slot.getAndIncrement(), new Button() {
                @Override
                public String getName(Player player) {
                    return Color.translate("&6" + meta.getDisplay());
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Color.translate(ImmutableList.of(
                            "&eCurrent: &r" + (rank.hasMeta(meta, true) ? "&aYes" : "&cNo"),
                            " ",
                            rank.hasMeta(meta, true) ? "&c&lRight Click &7to remove" : "&a&lLeft Click &7to add"
                    ));
                }

                @Override
                public Material getMaterial(Player player) {
                    return meta.getMaterial();
                }

                @Override
                public void clicked(Player player, int slot, ClickType type) {
                    switch(type) {

                        // enable
                        case LEFT: {
                            if(!rank.hasMeta(meta, true)) {
                                rank.getMeta().add(meta);
                                rankHandler.save(rank);
                                player.sendMessage(Color.translate("&aSuccessfully added &6" + meta.getDisplay() + " &ato &r" + rank.getColoredDisplay() + "'s &ameta."));
                            }
                            break;
                        }

                        // disable
                        case RIGHT: {
                            if(rank.hasMeta(meta, true)) {
                                rank.getMeta().remove(meta);
                                rankHandler.save(rank);
                                player.sendMessage(Color.translate("&aSuccessfully removed &6" + meta.getDisplay() + " &afrom &r" + rank.getColoredDisplay() + "'s &ameta."));
                            }
                            break;
                        }
                    }
                }
            });
        }

        return buttons;
    }
}