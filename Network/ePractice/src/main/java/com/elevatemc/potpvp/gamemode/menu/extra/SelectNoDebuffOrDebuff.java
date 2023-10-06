package com.elevatemc.potpvp.gamemode.menu.extra;

import com.google.common.base.Preconditions;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class SelectNoDebuffOrDebuff extends Menu {

    private final Callback<Boolean> callback;

    public SelectNoDebuffOrDebuff(Callback<Boolean> callback) {

        this.callback = Preconditions.checkNotNull(callback, "callback");
        setPlaceholder(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(getSlot(2, 1), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&bNo Debuff");
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.singletonList(ChatColor.GRAY + "Select this for the regular teamfight kit.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.POTION;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                ItemStack superItem = super.getButtonItem(player);

                superItem.setDurability((short) 16421);

                return superItem;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                callback.callback(false);
            }
        });

        buttons.put(getSlot(6, 1), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&bDebuff");
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.singletonList(ChatColor.GRAY + "Select this for the debuff teamfight kit.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.POTION;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                ItemStack superItem = super.getButtonItem(player);

                superItem.setDurability((short) 16388);

                return superItem;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                callback.callback(true);
            }
        });

        return buttons;
    }

    @Override
    public int size(Player buttons) {
        return 9 * 3;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + "Select No Debuff or Debuff";
    }
}