package com.elevatemc.potpvp.kit.menu.editkit;

import com.elevatemc.elib.eLib;
import com.google.common.base.Preconditions;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.kit.Kit;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

public final class EditKitMenu extends Menu {

    private static final Button ARMOR_NOT_AVAILABLE = Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, ChatColor.RED + "No Armor");
    private static final Button EDITOR_ITEM_NOT_AVAILABLE = Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, ChatColor.RED + "N/A");

    private final Kit kit;

    public EditKitMenu(Kit kit) {

        setPlaceholder(true);
        setNoncancellingInventory(true);
        setUpdateAfterClick(false);

        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public void onOpen(Player player) {
        player.getInventory().setContents(kit.getInventoryContents());

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + "Editing " + kit.getName() + " (" + kit.getType().getDisplayName() + ")";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        // Buttons to manage the kit
        buttons.put(getSlot(1, 1), new KitInfoButton(kit));
        buttons.put(getSlot(1, 2), new SaveButton(kit));
        buttons.put(getSlot(1, 3), new LoadDefaultKitButton(kit.getType()));
        buttons.put(getSlot(1, 4), new CancelKitEditButton(kit.getType()));

        // Load armor in the middle
        ItemStack[] armorItems = kit.getType().getDefaultArmor();
        int armorLength = armorItems.length;
        for (int i = 3; i >= 0; i--) {
            int slot = 4 - i;
            if (i < armorLength) {
                ItemStack armorItem = armorItems[i];
                if (armorItem != null && armorItem.getType() != Material.AIR) {
                    buttons.put(getSlot(4, slot), new ArmorButton(armorItem));
                    continue;
                }
            }
            buttons.put(getSlot(4, slot), ARMOR_NOT_AVAILABLE);
        }

        // Load editor items
        ItemStack[] editorItems = kit.getType().getEditorItems();
        int editorLength = editorItems.length;
        for (int i = 0; i < 4; i++) {
            int slot = 1 + i;
            if (i < editorLength) {
                ItemStack editorItem = editorItems[i];
                if (editorItem != null && editorItem.getType() != Material.AIR) {
                    buttons.put(getSlot(7, slot), new TakeItemButton(editorItem));
                    continue;
                }
            }
            buttons.put(getSlot( 7, slot), EDITOR_ITEM_NOT_AVAILABLE);
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 9*6;
    }
}