package dev.apposed.prime.spigot.module.profile.punishment.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class BaseHistoryMenu extends Menu {

    private final Profile profile;
    private final List<Punishment> punishments;
    private boolean showReceived = false;

    public BaseHistoryMenu(Profile profile, List<Punishment> punishments) {
        this.profile = profile;
        this.punishments = punishments;

    }

    @Override
    public String getTitle(Player player) {
        return "Punishment Type";
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for(PunishmentType type : PunishmentType.values()) {
            if(player.hasPermission(type.getPermission())) {
                buttons.put(type.getSlot(), new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.GOLD + type.getMenu();
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        return Collections.emptyList();
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return type.getMenuStack().getType();
                    }

                    @Override
                    public byte getDamageValue(Player player) {
                        return (byte) type.getMenuStack().getDurability();
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType) {
                        new HistoryMenu(type, profile, punishments, showReceived).openMenu(player);
                    }
                });
            }
        }

        return buttons;
    }

    public BaseHistoryMenu showWhoReceived() {
        this.showReceived = true;
        return this;
    }
}
