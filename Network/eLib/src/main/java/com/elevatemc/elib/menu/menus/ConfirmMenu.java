package com.elevatemc.elib.menu.menus;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.menu.buttons.BooleanButton;
import com.elevatemc.elib.util.Callback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ConfirmMenu extends Menu {

    private String title;
    @Getter private Callback<Boolean> response;

    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap();

        for(int i = 0; i < 9; ++i) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, this.response));
            } else if (i == 5) {
                buttons.put(i, new BooleanButton(false, this.response));
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, new String[0]));
            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }

}
