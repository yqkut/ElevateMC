package com.elevatemc.potpvp.gamemode.kit.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public final class KitSetEditorItemsCommand {

    @Command(names = "kit seteditoritems", permission = "op")
    public static void kitSaveDefault(Player sender, @Parameter(name="gamemode kit") GameModeKit kit) {
        ArrayList<ItemStack> editorItems = new ArrayList<>();
        for (ItemStack item : sender.getInventory().getContents()) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                editorItems.add(item);
            }
        }
        kit.setEditorItems(editorItems.toArray(new ItemStack[0]));
        kit.saveAsync();

        sender.sendMessage(ChatColor.YELLOW + "Saved default editor items for " + kit.getId() + ".");
    }

}