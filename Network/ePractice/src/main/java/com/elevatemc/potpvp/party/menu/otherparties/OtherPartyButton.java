package com.elevatemc.potpvp.party.menu.otherparties;

import com.google.common.base.Preconditions;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.duel.command.DuelCommand;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class OtherPartyButton extends Button {

    private final Party party;

    OtherPartyButton(Party party) {
        this.party = Preconditions.checkNotNull(party, "party");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_AQUA + UUIDUtils.name(party.getLeader());
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        description.add("");
        description.add(ChatColor.DARK_AQUA + "◎ " + ChatColor.WHITE + "Party Members");

        for (UUID member : party.getMembers()) {
            ChatColor color = party.isLeader(member) ? ChatColor.DARK_AQUA : ChatColor.AQUA;
            description.add(color + "  " + UUIDUtils.name(member));
        }

        description.add("");
        description.add(ChatColor.DARK_AQUA + "❘ " + ChatColor.WHITE + "Click here to duel this party");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3; // player head
    }

    @Override
    public int getAmount(Player player) {
        return party.getMembers().size();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Party senderParty = PotPvPSI.getInstance().getPartyHandler().getParty(player);

        if (senderParty == null) {
            return;
        }

        if (senderParty.isLeader(player.getUniqueId())) {
            DuelCommand.duel(player, Bukkit.getPlayer(party.getLeader()));
        } else {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Only the leader can duel other parties!");
        }
    }

}