package com.elevatemc.potpvp.party.command;

import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.UUIDUtils;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public final class PartyInfoCommand {

    @Command(names = {"party info", "p info", "t info", "team info", "f info", "p i", "t i", "f i", "party i", "team i"}, permission = "")
    public static void partyInfo(Player sender, @Parameter(name = "player", defaultValue = "self") Player target) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(target);

        if (party == null) {
            if (sender == target) {
                sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + " isn't in a party right now.");
            }

            return;
        }

        String leaderName = UUIDUtils.name(party.getLeader());
        int memberCount = party.getMembers().size();
        String members = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(party.getMembers()));
        String privacy = "";
        switch (party.getAccessRestriction()) {
            case PUBLIC:
                privacy = ChatColor.GREEN + "Open";
                break;
            case INVITE_ONLY:
                privacy = ChatColor.GOLD + "Invite-Only";
                break;
            case PASSWORD:
                privacy = ChatColor.RED + "Password Protected";
                break;
            default:
                break;
        }


        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lParty Info"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3❘ &fLeader&7: &3" + leaderName));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3❘ &fMembers&7&7: " + "&3(" + memberCount + ") " + ChatColor.GRAY + members));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3❘ &fPrivacy&7: " + privacy));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));

    }
}