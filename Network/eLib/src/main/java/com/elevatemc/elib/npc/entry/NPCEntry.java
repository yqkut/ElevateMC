package com.elevatemc.elib.npc.entry;

import com.elevatemc.elib.skin.MojangSkin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NPCEntry {

    private int id;
    private String displayName, command;
    private Location location;
    private ItemStack[] inventory;
    private MojangSkin mojangSkin;
    private List<String> hologramLines;
    private boolean sitting;

}
