package com.elevatemc.elib.nametag.provider;

import com.elevatemc.elib.eLib;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.elevatemc.elib.nametag.construct.NameTagInfo;
import org.bukkit.entity.Player;

@AllArgsConstructor
public abstract class NameTagProvider {

    @Getter private String name;
    @Getter private int weight;

    public abstract NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor);

    public final NameTagInfo createNameTag(String prefix,String suffix) {
        return eLib.getInstance().getNameTagHandler().getOrCreate(prefix, suffix);
    }

}