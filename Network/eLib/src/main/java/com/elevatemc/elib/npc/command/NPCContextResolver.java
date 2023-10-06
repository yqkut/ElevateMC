package com.elevatemc.elib.npc.command;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.elib.fake.FakeEntity;
import com.elevatemc.elib.npc.NPC;
import com.elevatemc.elib.util.message.MessageBuilder;
import org.bukkit.command.CommandSender;

public class NPCContextResolver implements ParameterType<NPC> {




    @Override
    public NPC transform(CommandSender sender, String source) {
        int id;

        try {
            id = Integer.parseInt(source);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageBuilder.constructError("That is not a valid id"));
            return null;
        }

        FakeEntity fakeEntity = eLib.getInstance().getFakeEntityHandler().getEntityById(id);

        if (!(fakeEntity instanceof NPC)) {
            sender.sendMessage(MessageBuilder.constructError("That is not an npc"));
            return null;
        }

        return (NPC) fakeEntity;
    }
}
