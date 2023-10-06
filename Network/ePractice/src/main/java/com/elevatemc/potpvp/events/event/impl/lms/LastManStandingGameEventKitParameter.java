package com.elevatemc.potpvp.events.event.impl.lms;

import com.elevatemc.potpvp.events.parameter.GameParameter;
import com.elevatemc.potpvp.events.parameter.GameParameterOption;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LastManStandingGameEventKitParameter implements GameParameter {

    private static final String DISPLAY_NAME = "Kit";
    private static final List<GameParameterOption> OPTIONS = ImmutableList.of(
            new LastManStandingKitOption(GameModeKit.byId("NO_DEBUFF")),
            new LastManStandingKitOption(GameModeKit.byId("SOUP")),
            new LastManStandingKitOption(GameModeKit.byId("VANILLA"))
//            new LastManStandingKitOption(GameModeKit.byId("AXE")),
//            new LastManStandingKitOption(GameModeKit.byId("CLASSIC"))
    );

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public List<GameParameterOption> getOptions() {
        return OPTIONS;
    }

    public static final class LastManStandingKitOption implements GameParameterOption {

        private GameModeKit kit;

        public LastManStandingKitOption(GameModeKit kit) {
            this.kit = kit;
        }

        @Override
        public String getDisplayName() {
            return kit.getDisplayName();
        }

        @Override
        public ItemStack getIcon() {
            final ItemStack icon = new ItemStack(kit.getIcon().getItemType());

            icon.setData(kit.getIcon());

            return icon;
        }

        private ItemStack[] getItems() {
            return kit.getDefaultInventory();
        }

        private ItemStack[] getArmor() {
            return kit.getDefaultArmor();
        }

        public void apply(Player player) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.getInventory().setArmorContents(getArmor());
            player.getInventory().setContents(getItems());

            player.updateInventory();
        }
    }
}
