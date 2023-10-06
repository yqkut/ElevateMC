package com.elevatemc.ehub.type.armor;

import com.elevatemc.ehub.utils.CC;
import com.elevatemc.elib.util.cosmetics.ArmorUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


@Getter
public enum ArmorType {

    BASIC("Basic", CC.GRAY, Color.GRAY, 8, null),
    VIP("VIP", CC.GREEN, Color.LIME, 5, null),
    MVP("MVP", CC.BLUE, Color.BLUE, 11, null),
    PRO("PRO", CC.GOLD, Color.ORANGE, 1, null),

    ELEVATE("Elevate", CC.DARK_AQUA, Color.BLUE, 11, (Runnable) () -> {
        ArmorType type = ArmorType.ELEVATE;

        if (type.helper >= 600) {
            type.reverse = true;
        } else if (type.helper <= 0) {
            type.reverse = false;
        }

        if (type.reverse) {
            type.helper -= 2;
        } else {
            type.helper += 2;
        }

        Color color = ArmorUtil.COLORS.get(type.helper);

        if (color != null) {
            type.r = color.getRed();
            type.g = color.getGreen();
            type.b = color.getBlue();

            int glassColor = ArmorUtil.parseColor(color);

            if (glassColor != -1) {
                type.astronaut = glassColor;
            }
        }
    }),

    MEDIA("Media", CC.PINK, Color.fromRGB(255, 0, 255), 6, (Runnable) () -> {
        ArmorType type = ArmorType.MEDIA;

        if (type.r >= 255) {
            type.reverse = true;
        } else if (type.r <= 170) {
            type.reverse = false;
        }

        if (type.reverse) {
            type.r -= 2;
            type.b -= 2;
        } else {
            type.r += 2;
            type.b += 2;
        }
    }),

    MOD("Moderator", CC.DARK_PURPLE, Color.PURPLE, 2, (Runnable) () -> {
        ArmorType type = ArmorType.MOD;

        if (type.b <= 50) {
            type.reverse = true;
        } else if (type.b >= 102) {
            type.reverse = false;
        }

        if (type.reverse) {
            if (type.r > 51) {
                type.r--;
            }

            type.b++;
        } else {
            if (type.r < 80) {
                type.r++;
            }

            type.b--;
        }
    }),

    ADMIN("Admin", CC.RED, Color.RED, 14, (Runnable) () -> {
        ArmorType type = ArmorType.ADMIN;

        if (type.r >= 255) {
            type.reverse = true;
        } else if (type.r <= 161) {
            type.reverse = false;
        }

        if (type.reverse) {
            type.r -= 2;
        } else {
            type.r += 2;
        }
    }),

    DEVELOPER("Developer", CC.AQUA, Color.AQUA, 3, (Runnable) () -> {
        ArmorType type = ArmorType.DEVELOPER;

        if (type.b >= 255) {
            type.reverse = true;
        } else if (type.b <= 153) {
            type.reverse = false;
        }

        if (type.reverse) {
            if (type.g > 76) {
                type.g -= 2;
            }

            type.b -= 3;
        } else {
            if (type.g < 128) {
                type.g += 2;
            }

            type.b += 2;
        }
    }),

    OWNER("Owner", CC.D_RED, Color.MAROON, 14, (Runnable) () -> {
        ArmorType type = ArmorType.OWNER;

        if (type.r >= 200) {
            type.reverse = true;
        } else if (type.r <= 128) {
            type.reverse = false;
        }

        if (type.reverse) {
            type.r -= 2;
        } else {
            type.r += 2;
        }
    });

    private final String name, displayColor;
    private final Color color;
    private int astronaut;
    private int r, g, b, helper;

    @Setter
    private Runnable runnable;

    private String armorType;

    @Setter
    private boolean reverse;

    private ItemStack[] items;

    ArmorType(String name, String displayColor, Color color, int astronaut, Object object) {
        this.name = name;
        this.displayColor = displayColor;
        this.color = color;
        this.astronaut = astronaut;

        if(object != null) {
            if (object instanceof String) {
                armorType = (String) object;

            } else {
                runnable = (Runnable) object;
            }
        }

        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();

        reverse = false;
    }

    public boolean isDonator() {
        return ordinal() <= 3;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(getPermissionForAll()) || player.hasPermission(getPermission());
    }

    public String getPermissionForAll() {
        return "core.cosmetic.armor.*";
    }

    public String getPermission() {
        return "core.cosmetic.armor." + name().toLowerCase();
    }

    public ItemStack[] getItems() {
        if (items != null) {
            return items;
        }

        items = new ItemStack[4];

        items[3] = new ItemStack(Material.LEATHER_HELMET);
        items[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        items[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        items[0] = new ItemStack(Material.LEATHER_BOOTS);

        return items;
    }
}

