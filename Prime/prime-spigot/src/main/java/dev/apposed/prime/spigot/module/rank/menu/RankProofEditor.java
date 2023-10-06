package dev.apposed.prime.spigot.module.rank.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RankProofEditor extends Menu {

    private final Prime plugin = Prime.getInstance();
    private final RankHandler rankHandler = plugin.getModuleHandler().getModule(RankHandler.class);

    private final Rank rank;

    public RankProofEditor(Rank rank) {
        this.rank = rank;
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Rank Proof Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        for(PunishmentType meta : PunishmentType.values()) {
            buttons.put(slot.getAndIncrement(), new Button() {
                @Override
                public String getName(Player player) {
                    return Color.translate("&6" + meta.getMenu());
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Color.translate(ImmutableList.of(
                            "&eCurrent: &r" + (rank.requiresProof(meta) ? "&aYes" : "&cNo"),
                            " ",
                            rank.requiresProof(meta) ? "&c&lRight Click &7to remove" : "&a&lLeft Click &7to add"

                    ));
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.DIAMOND_SWORD;
                }

                @Override
                public void clicked(Player player, int slot, ClickType type) {
                    switch(type) {

                        // enable
                        case LEFT: {
                            if (!rank.requiresProof(meta)) {
                                rank.getProofMeta().add(meta);
                                rankHandler.save(rank);

                                player.sendMessage(Color.translate("&aUpdated &r" + rank.getColoredDisplay() + "'s &a proof meta."));
                            }
                            break;
                        }

                        // disable
                        case RIGHT: {
                            if (rank.requiresProof(meta)) {
                                rank.getProofMeta().remove(meta);
                                rankHandler.save(rank);

                                player.sendMessage(Color.translate("&aUpdated &r" + rank.getColoredDisplay() + "'s &a proof meta."));
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