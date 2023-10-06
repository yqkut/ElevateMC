package com.elevatemc.potpvp.arena.menu.manageschematic;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.potpvp.arena.menu.manageschematics.ManageSchematicsMenu;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.util.menu.BooleanTraitButton;
import com.elevatemc.potpvp.util.menu.MenuBackButton;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ManageSchematicMenu extends Menu {

    private final ArenaSchematic schematic;

    public ManageSchematicMenu(ArenaSchematic schematic) {
        setAutoUpdate(true);

        this.schematic = schematic;
    }

    @Override
    public String getTitle(Player player) {
        return "Manage " + schematic.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new SchematicStatusButton(schematic));
        buttons.put(1, new ToggleEnabledButton(schematic));

        buttons.put(3, new TeleportToModelButton(schematic));
        buttons.put(4, new SaveModelButton(schematic));

        if (PotPvPSI.getInstance().getArenaHandler().getGrid().isBusy()) {
            Button busyButton = Button.placeholder(Material.WOOL, DyeColor.SILVER.getWoolData(), ChatColor.GRAY.toString() + ChatColor.BOLD + "Grid is busy");

            buttons.put(7, busyButton);
            buttons.put(8, busyButton);
        } else {
            buttons.put(7, new CreateCopiesButton(schematic));
            buttons.put(8, new RemoveCopiesButton(schematic));
        }

        buttons.put(9, new MenuBackButton(p -> new ManageSchematicsMenu().openMenu(p)));
        buttons.put(17, new EventNameButton(schematic));

        Consumer<ArenaSchematic> save = schematic -> {
            try {
                PotPvPSI.getInstance().getArenaHandler().saveSchematics();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        int i = 18;
        buttons.put(i++, new BooleanTraitButton<>(schematic, "Supports Ranked", ArenaSchematic::setSupportsRanked, ArenaSchematic::isSupportsRanked, save));
        buttons.put(i++, new BooleanTraitButton<>(schematic, "Pearls Allowed", ArenaSchematic::setPearlsAllowed, ArenaSchematic::isPearlsAllowed, save));
        i = 36;
        for (GameMode gameMode : GameMode.getAll()) {
            buttons.put(i, new BooleanTraitButton<>(schematic, gameMode.getName(), (schem, bool) -> {
                schem.getEnabledGameModes().put(gameMode.getId(), bool);
            }, (schem) -> {
                return schem.getEnabledGameModes().get(gameMode.getId());
            }, save));
            i++;
        }

        return buttons;
    }

}