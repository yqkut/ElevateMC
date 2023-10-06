package com.elevatemc.potpvp.setting;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public enum Setting {

    SHOW_SCOREBOARD(
            "Toggle Scoreboard",
            ImmutableList.of(
                    ChatColor.GRAY + "Enable or disable scoreboard"
            ),
            Material.SIGN,
            ChatColor.GREEN + "Enabled",
            ChatColor.RED + "Enabled",
            true,
            null // no permission required
    ),
    RECEIVE_DUELS(
            "Toggle Duel Requests",
            ImmutableList.of(
                    ChatColor.GRAY + "Enable or disable duel requests"
            ),
            Material.DIAMOND_SWORD,
            ChatColor.GREEN + "Enabled",
            ChatColor.RED + "Disabled",
            true,
            null // no permission required
    ),
    ALLOW_SPECTATORS(
            "Allow Spectators",
            ImmutableList.of(
                    ChatColor.GRAY + "Enable or disable spectators"
            ),
            Material.REDSTONE_COMPARATOR,
            ChatColor.GREEN + "Enabled",
            ChatColor.RED + "Disabled",
            true,
            null // no permission required
    ),
    VIEW_OTHER_SPECTATORS(
            "Other Spectators",
            ImmutableList.of(
                    ChatColor.GRAY + "If enabled, you can see spectators",
                    ChatColor.GRAY + "in the same match as you.",
                    "",
                    ChatColor.GRAY + "Disable to only see alive players in match."
            ),
            Material.GLASS_BOTTLE,
            ChatColor.YELLOW + "Show other spectators",
            ChatColor.YELLOW + "Hide other spectators",
            true,
            null // no permission required
    ),
    SHOW_SPECTATOR_JOIN_MESSAGES(
            "Toggle Spectator Join Messages",
            ImmutableList.of(
                    ChatColor.GRAY + "Enabled or disable spectator join messages"
            ),
            Material.PAPER,
            ChatColor.GREEN + "Enabled",
            ChatColor.RED + "Disabled",
            true,
            null// no permission required
    ),
    NIGHT_MODE(
            "Toggle Time",
            ImmutableList.of(
                    ChatColor.GRAY + "Set the time"
            ),
            Material.WATCH,
            ChatColor.GREEN + "Night",
            ChatColor.RED + "Day",
            false,
            null // no permission required
    ),
    SEE_TOURNAMENT_JOIN_MESSAGE(
            "Tournament Join Messages",
            ImmutableList.of(
                    ChatColor.GRAY + "If enabled, you will see messages",
                    ChatColor.GRAY + "when people join the tournament",
                    "",
                    ChatColor.GRAY + "Disable to only see your own party join messages."
            ),
            Material.IRON_DOOR,
            ChatColor.YELLOW + "Tournament join messages are shown",
            ChatColor.YELLOW + "Tournament join messages are hidden",
            true,
            null // no permission required
    ),
    SEE_TOURNAMENT_ELIMINATION_MESSAGES(
            "Tournament Elimination Messages",
            ImmutableList.of(
                    ChatColor.GRAY + "If enabled, you will see messages when",
                    ChatColor.GRAY + "people are eliminated the tournament",
                    "",
                    ChatColor.GRAY + "Disable to only see your own party elimination messages."
            ),
            Material.SKULL_ITEM,
            ChatColor.YELLOW + "Tournament elimination messages are shown",
            ChatColor.YELLOW + "Tournament elimination messages are hidden",
            true,
            null // no permission required
    );


    /**
     * Friendly (colored) display name for this setting
     */
    @Getter private final String name;

    /**
     * Friendly (colored) description for this setting
     */
    @Getter private final List<String> description;

    /**
     * Material to be used when rendering an icon for this setting
     * @see com.elevatemc.potpvp.setting.menu.SettingButton
     */
    @Getter private final Material icon;

    /**
     * Text to be shown when rendering an icon for this setting, while enabled
     * @see com.elevatemc.potpvp.setting.menu.SettingButton
     */
    @Getter private final String enabledText;

    /**
     * Text to be shown when rendering an icon for this setting, while enabled
     * @see com.elevatemc.potpvp.setting.menu.SettingButton
     */
    @Getter private final String disabledText;

    /**
     * Default value for this setting, will be used for players who haven't
     * updated the setting and if a player's settings fail to load.
     */
    private final boolean defaultValue;

    /**
     * The permission required to be able to see and update this setting,
     * null means no permission is required to update/see.
     */
    private final String permission;

    // Using @Getter means the method would be 'isDefaultValue',
    // which doesn't correctly represent this variable.
    public boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean canUpdate(Player player) {
        return permission == null || player.hasPermission(permission);
    }
}