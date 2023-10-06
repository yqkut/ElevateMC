package com.elevatemc.potpvp.kit.menu.kits;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.gamemode.menu.editing.EditGameModeMenu;
import com.elevatemc.potpvp.gamemode.menu.editing.EditGameModeKitMenu;
import com.elevatemc.potpvp.kit.Kit;
import com.elevatemc.potpvp.kit.KitHandler;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.util.menu.MenuBackButton;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class KitsMenu extends Menu {

    private final GameModeKit gameModeKit;

    public KitsMenu(GameModeKit gameModeKit) {
        setPlaceholder(true);
        setAutoUpdate(true);

        this.gameModeKit = gameModeKit;
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + "Your " + gameModeKit.getDisplayName() + " kits";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        Map<Integer, Button> buttons = new HashMap<>();

        // kit slots are 1-indexed
        for (int kitSlot = 1; kitSlot <= KitHandler.KITS_PER_TYPE; kitSlot++) {
            Optional<Kit> kitOpt = kitHandler.getKit(player, gameModeKit, kitSlot);
            int column = (kitSlot * 2) - 1; // - 1 to compensate for this being 0-indexed

            buttons.put(getSlot(column, 1), new KitEditButton(kitOpt, gameModeKit, kitSlot));

            /*if (kitOpt.isPresent()) {
                buttons.put(getSlot(column, 2), new KitRenameButton(kitOpt.get()));
                buttons.put(getSlot(column, 3), new KitDeleteButton(gameModeKit, kitSlot));
            } else {
                buttons.put(getSlot(column, 2), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.GRAY.getWoolData(), ""));
                buttons.put(getSlot(column, 3), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.GRAY.getWoolData(), ""));
            }*/
        }

        buttons.put(getSlot(0, 2), new MenuBackButton(p -> {
            new EditGameModeMenu(gameMode -> {
                if (gameMode.getKits().size() > 1) {
                    new EditGameModeKitMenu(gameModeKit -> {
                        new KitsMenu(gameModeKit).openMenu(player);
                    }, gameMode).openMenu(player);
                } else {
                    new KitsMenu(GameModeKit.byId(gameMode.getId())).openMenu(player);
                }
            }).openMenu(player);
        }));

        return buttons;
    }

}