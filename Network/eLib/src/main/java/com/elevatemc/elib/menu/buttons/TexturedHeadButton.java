package com.elevatemc.elib.menu.buttons;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class TexturedHeadButton extends Button {

    private final String texture;

    public TexturedHeadButton(String texture) {
        this.texture = texture;
    }

    public String getTexture(Player player) {
        return texture;
    }

    @Override
    public Material getMaterial(Player var1) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 3;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return super.getButtonItem(player);
    }
}
