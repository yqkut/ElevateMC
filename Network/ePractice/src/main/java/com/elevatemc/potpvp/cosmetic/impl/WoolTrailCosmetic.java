package com.elevatemc.potpvp.cosmetic.impl;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.cosmetic.Cosmetic;
import com.elevatemc.potpvp.cosmetic.type.CosmeticType;
import com.elevatemc.potpvp.util.Color;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WoolTrailCosmetic extends Cosmetic {

    private final List<BlockRestore> blockRestores;

    public WoolTrailCosmetic() {
        super("WOOL_TRAIL");
        this.blockRestores = new ArrayList<>();
        new BlockRestoreTask().runTaskTimer(PotPvPSI.getInstance(), 0L, 20L);
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public void onEnable(Player player) {
        player.sendMessage(Color.translate("&eYou have &aenabled &ethe &fWool Trail &ecosmetic!"));
    }

    @Override
    public void onDisable(Player player) {
        player.sendMessage(Color.translate("&eYou have &cdisabled &ethe &fWool Trail &ecosmetic!"));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if(!PotPvPSI.getInstance().getCosmeticHandler().isEnabled(player, getType())) return;
        if(event.getTo().equals(event.getFrom())) return;
        final Location location = event.getTo();
        final ItemStack wool = getWool(player);

        // set 3x3 block radius below players feet
        final Location belowLocation = location.clone().subtract(0, 1, 0);
        for(int x=-1; x<=1; x++) {
            for(int z=-1; z<=1; z++) {
                final Block block = belowLocation.getBlock().getRelative(x, 0, z);
                if(block.getType() == wool.getType()) continue;
                this.blockRestores.add(new BlockRestore(
                        block.getLocation(),
                        block.getType(),
                        block.getState().getData()
                ));

                block.setType(wool.getType());
                block.getState().setData(wool.getData());
            }
        }
    }

    public ItemStack getWool(Player player) {
        final Optional<Profile> profileOptional = PotPvPSI.getInstance().getPrime().getModuleHandler().getModule(ProfileHandler.class).getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) return new ItemStack(Material.WOOL);

        final Profile profile = profileOptional.get();
        return profile.getHighestActiveGrant().getRank().getWool();
    }

    @Override
    public CosmeticType getType() {
        return CosmeticType.TRAIL;
    }

    private class BlockRestoreTask extends BukkitRunnable {

        @Override
        public void run() {
            Iterator<BlockRestore> itr = blockRestores.iterator();
            while(itr.hasNext()) {
                final BlockRestore blockRestore = itr.next();
                if(System.currentTimeMillis() >= blockRestore.getExpireAt()) {
                    final Block block = blockRestore.getBlockLocation().getWorld().getBlockAt(blockRestore.getBlockLocation());
                    block.setType(blockRestore.getPreviousBlock());
                    block.getState().setData(blockRestore.getPreviousData());
                    itr.remove();
                }
            }
        }
    }

    @Getter @RequiredArgsConstructor
    private static class BlockRestore {
        private final Location blockLocation;
        private final Material previousBlock;
        private final MaterialData previousData;
        private final long expireAt = System.currentTimeMillis() + 1500;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockRestore that = (BlockRestore) o;
            return com.google.common.base.Objects.equal(blockLocation, that.blockLocation);
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(blockLocation);
        }
    }
}
