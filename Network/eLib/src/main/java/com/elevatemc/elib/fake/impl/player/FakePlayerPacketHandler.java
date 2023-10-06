package com.elevatemc.elib.fake.impl.player;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.spigot.handler.PacketHandler;
import lombok.AllArgsConstructor;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import com.elevatemc.elib.eLib;
import org.bukkit.entity.Player;
import com.elevatemc.elib.fake.FakeEntity;
import com.elevatemc.elib.fake.FakeEntityHandler;

/**
 * @author ImHacking
 */
@AllArgsConstructor
public class FakePlayerPacketHandler implements PacketHandler {
    private FakeEntityHandler handler;

    @Override
    public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        if (packet instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            if (useEntity.a()  != PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                return;
            }

            Entity entity = useEntity.a(playerConnection.player.world);
            if (entity != null) {
                return;
            }

           int id = useEntity.a;
            FakeEntity fakeEntity = handler.getEntityByEntityId(id);

            if (!(fakeEntity instanceof FakePlayerEntity)) {
                return;
            }
            FakePlayerEntity fakePlayer = (FakePlayerEntity) fakeEntity;
            Player player = playerConnection.player.getBukkitEntity();

            if (player.getLocation().distanceSquared(fakePlayer.getCurrentLocation()) > 36) {
                return;
            }

            FakePlayerInteractEvent interactEvent = new FakePlayerInteractEvent(player, fakePlayer.getName(), fakePlayer.getCommand());
            interactEvent.call(eLib.getInstance());

            TaskUtil.runSync(() -> {
                String command = interactEvent.getCommand();

                if (command == null) {
                    return;
                }

                if (!command.startsWith("/")) {
                    command = "/" + command;
                }

                if (command.equalsIgnoreCase("/")) {
                    return;
                }

//                player.performCommand(command);
                player.chat(command);
            });
        }
    }
}
