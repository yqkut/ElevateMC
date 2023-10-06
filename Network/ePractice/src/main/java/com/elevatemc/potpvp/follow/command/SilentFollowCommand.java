package com.elevatemc.potpvp.follow.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.command.LeaveCommand;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class SilentFollowCommand {

    @Command(names = {"sfollow", "sf", "silentfollow"}, permission = "core.staffteam")
    public static void silentfollow(Player sender, @Parameter(name = "target") Player target) {
        sender.setMetadata("modmode", new FixedMetadataValue(PotPvPSI.getInstance(), true));
        sender.setMetadata("invisible", new FixedMetadataValue(PotPvPSI.getInstance(), true));

        if (PotPvPSI.getInstance().getPartyHandler().hasParty(sender)) {
            LeaveCommand.leave(sender);
        }

        FollowCommand.follow(sender, target);
    }
}