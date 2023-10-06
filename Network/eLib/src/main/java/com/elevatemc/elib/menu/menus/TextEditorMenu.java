package com.elevatemc.elib.menu.menus;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.elevatemc.elib.util.TaskUtil;
import com.google.common.collect.Maps;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

// Ported from Kotlin to Java from MineXD/Joeleoli's Cubed
public abstract class TextEditorMenu extends PaginatedMenu {

    private final LinkedList<String> lines;

    private boolean changed = false;
    private boolean colors = false;

    public TextEditorMenu(LinkedList<String> lines) {
        this.lines = lines;
        setUpdateAfterClick(true);
    }

    public TextEditorMenu(Collection<String> lines) {
        this(new LinkedList<>(lines));
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = Maps.newHashMap();



        return buttons;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return null;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        return null;
    }

    abstract void onSave(Player player, List<String> list);

    @Override
    public void onClose(Player player) {
        if(changed) {
            TaskUtil.runTaskLater(() -> {
                new ConfirmMenu("Discard Changes?", confirmed -> {
                    if(confirmed) {
                        player.sendMessage(Color.translate("&eDiscarded changes."));
                        changed = false;
                        onClose(player);
                    } else {
                        TaskUtil.runTaskLater(() -> openMenu(player), 1);
                    }
                }).openMenu(player);
            }, 1);
        }
    }

    private final class AddLineButton extends Button {

        @Override
        public String getName(Player var1) {
            return Color.translate("&b&lCreate New Line");
        }

        @Override
        public List<String> getDescription(Player var1) {
            return Color.translate(Arrays.asList(
                    "",
                    "&7Create a new line by completing",
                    "&7the setup procedure.",
                    "",
                    "&a&lLEFT-CLICK &ato create new line"
            ));
        }

        @Override
        public Material getMaterial(Player var1) {
            return null;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            super.clicked(player, slot, clickType);
        }
    }

    private final class SaveButton extends Button {

        @Override
        public String getName(Player var1) {
            return Color.translate("&aSave Changes");
        }

        @Override
        public List<String> getDescription(Player var1) {
            return Color.translate(Arrays.asList(
                    "",
                    "&7Save any changes made to the",
                    "&7lines of text.",
                    "",
                    "&a&lLEFT-CLICK &ato save changes"
            ));
        }

        @Override
        public Material getMaterial(Player var1) {
            return Material.WOOL;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 13;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            onSave(player, lines);
            changed = false;
        }
    }

    private final class NoChangesButton extends Button {

        @Override
        public String getName(Player var1) {
            return Color.translate("&7&lNo Changes Made");
        }

        @Override
        public List<String> getDescription(Player var1) {
            return Color.translate(Arrays.asList(
                    "",
                    "&7No changes have been made to the",
                    "&7lines of text."
            ));
        }

        @Override
        public Material getMaterial(Player var1) {
            return Material.WOOL;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 8;
        }
    }
}
