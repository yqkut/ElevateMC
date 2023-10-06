package com.elevatemc.potpvp.hctranked.game.menu;

import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.UUIDUtils;
import com.elevatemc.potpvp.pvpclasses.PvPClasses;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.potpvp.hctranked.game.RankedGameTeam;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.stream.Collectors;

public class KitsMenu extends Menu {

    private final RankedGameTeam team;

    public KitsMenu(RankedGameTeam team) {
        this.team = team;

        setAutoUpdate(true);
        setUpdateAfterClick(true);
        setPlaceholder(true);
    }

    private static List<PvPClasses> classes = Arrays.stream(PvPClasses.values()).filter(c -> !c.equals(PvPClasses.ROGUE)).collect(Collectors.toList());

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();

        for (UUID uuid : new ArrayList<>(team.getKits().keySet())) {
            if (!(team.getPlayers().contains(uuid))) {
                team.getKits().remove(uuid);
            }
        }

        for (UUID uuid : team.getPlayers()) {
            PvPClasses selected = team.getKits().getOrDefault(uuid, PvPClasses.DIAMOND);

            toReturn.put(toReturn.isEmpty() ? 0 : toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.GOLD + UUIDUtils.name(uuid);
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> description = new ArrayList<>();

                    for (PvPClasses kit : classes) {
                        if (kit == selected) {
                            description.add(ChatColor.GREEN + "» " + kit.getName());
                        } else {
                            if (kit.allowed(team)) {
                                description.add(ChatColor.GRAY + kit.getName());
                            } else {
                                description.add(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + kit.getName());
                            }
                        }
                    }

                    return description;
                }

                @Override
                public Material getMaterial(Player player) {
                    return selected.getIcon();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (team.getCaptain().equals(player.getUniqueId())) {
                        if (team.isReady()) {
                            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't modify your kits while being ready.");
                            return;
                        }
                        int index = classes.indexOf(selected);
                        PvPClasses next = null;

                        int times = 0;
                        while (next == null && times <= 50) {
                            times++;
                            if (index+1 < classes.size()) {
                                next = classes.get(index+1);
                                if (!(next.allowed(team))) {
                                    next = null;
                                    index++;
                                }
                            } else {
                                index = -1;
                            }
                        }

                        if (next == null) {
                            next = PvPClasses.DIAMOND;
                        }
                        team.getKits().put(uuid, next);
                    }
                }
            });
        }

        return toReturn;
    }

    @Override
    public int size(Player buttons) {
        return 9*2;
    }

    @Override
    public String getTitle(Player player) {
        return "Kits Menu";
    }
}
