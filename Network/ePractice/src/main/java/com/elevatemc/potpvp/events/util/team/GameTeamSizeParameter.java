package com.elevatemc.potpvp.events.util.team;

import com.elevatemc.potpvp.events.parameter.GameParameter;
import com.elevatemc.potpvp.events.parameter.GameParameterOption;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GameTeamSizeParameter implements GameParameter {

    private static final String DISPLAY_NAME = "Team Size";
    private static final List<GameParameterOption> OPTIONS = ImmutableList.of(
            new Singles()
//            new Duos()
    );

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public List<GameParameterOption> getOptions() {
        return OPTIONS;
    }

    public static final class Singles implements GameParameterOption {

        private static final String DISPLAY_NAME = "1v1";
        private static final ItemStack ICON = new ItemStack(Material.DIAMOND_HELMET);

        @Override
        public String getDisplayName() {
            return DISPLAY_NAME;
        }

        @Override
        public ItemStack getIcon() {
            return ICON;
        }
    }

    public static final class Duos implements GameParameterOption {

        private static final String DISPLAY_NAME = "2v2";
        private static final ItemStack ICON = new ItemStack(Material.DIAMOND_HELMET, 2);

        @Override
        public String getDisplayName() {
            return DISPLAY_NAME;
        }

        @Override
        public ItemStack getIcon() {
            return ICON;
        }
    }
}
