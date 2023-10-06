package com.elevatemc.elib.command.param.defaults.offlineplayer;

import java.util.*;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.util.Callback;
import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.elib.util.UUIDUtils;
import lombok.Getter;
import org.bukkit.entity.*;

import org.bukkit.craftbukkit.v1_8_R3.*;
import com.mojang.authlib.*;

public class OfflinePlayerWrapper {

    private String source;

    @Getter private UUID uniqueId;
    @Getter private String name;

    public OfflinePlayerWrapper(String source) {
        this.source = source;
    }

    public void loadAsync(Callback<Player> callback) {
       TaskUtil.executeWithPoolIfRequired(() -> callback.callback(this.loadSync()));
    }

    public Player loadSync() {
        if ((this.source.charAt(0) == '\"' || this.source.charAt(0) == '\'') && (this.source.charAt(this.source.length() - 1) == '\"' ||
                this.source.charAt(this.source.length() - 1) == '\'')) {

            this.source = this.source.replace("'", "").replace("\"", "");

            this.uniqueId = UUIDUtils.uuid(this.source);

            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            }

            this.name = UUIDUtils.name(this.uniqueId);

            if (eLib.getInstance().getServer().getPlayer(this.uniqueId) != null) {
                return eLib.getInstance().getServer().getPlayer(this.uniqueId);
            }
            if (!eLib.getInstance().getServer().getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                return null;
            }

            final MinecraftServer server = ((CraftServer) eLib.getInstance().getServer()).getServer();
            final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name),
                    new PlayerInteractManager(server.getWorldServer(0)));
            final Player player = entity.getBukkitEntity();

            if (player != null) {
                player.loadData();
            }

            return player;
        } else {

            if (eLib.getInstance().getServer().getPlayer(this.source) != null) {
                return eLib.getInstance().getServer().getPlayer(this.source);
            }

            this.uniqueId = UUIDUtils.uuid(this.source);

            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            }

            this.name = UUIDUtils.name(this.uniqueId);

            if (eLib.getInstance().getServer().getPlayer(this.uniqueId) != null) {
                return eLib.getInstance().getServer().getPlayer(this.uniqueId);
            }

            if (!eLib.getInstance().getServer().getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                return null;
            }

            final MinecraftServer server = ((CraftServer) eLib.getInstance().getServer()).getServer();
            final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name),
                    new PlayerInteractManager(server.getWorldServer(0)));
            final Player player = entity.getBukkitEntity();
            if (player != null) {
                player.loadData();
            }
            return player;
        }
    }

}