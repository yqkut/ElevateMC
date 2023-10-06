package dev.apposed.prime.spigot.module.profile.skin;

import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.Module;
import org.bukkit.entity.Player;

import java.util.*;

public class SkinHandler extends Module {

    private final Prime plugin = Prime.getInstance();
    private final Map<UUID, List<String>> usageCache = new HashMap<>();

    public void changeSkin(Player player, String username) {
        /* final CraftPlayer craftPlayer = (CraftPlayer) player;
        final EntityPlayer entityPlayer = craftPlayer.getHandle();
        final GameProfile gameProfile = craftPlayer.getProfile();

        final Location location = player.getLocation();
        final GameMode gameMode = player.getGameMode();
        final boolean allowFlight = player.getAllowFlight();
        final boolean flying = player.isFlying();
        final int level = player.getLevel();
        final float xp = player.getExp();
        final double maxHealth = player.getMaxHealth();
        final double health = player.getHealth();

        MojangUtils.getTextureAndSignature(username, ((texture, signature) -> {
            if(texture == null || signature == null) {
                player.sendMessage(Color.translate("&cFailed to load " + username + "'s skin."));
                return;
            }

            gameProfile.getProperties().get("textures").clear(); // remove old skin texture
            gameProfile.getProperties()
                    .put("textures",
                            new Property(
                                    "textures",
                                    texture,
                                    signature
                            ));

            final PacketPlayOutPlayerInfo removeInfo = PacketPlayOutPlayerInfo.removePlayer(entityPlayer);
            final PacketPlayOutEntityDestroy removeEntity = new PacketPlayOutEntityDestroy(entityPlayer.getId());
            final PacketPlayOutNamedEntitySpawn addNamed = new PacketPlayOutNamedEntitySpawn(entityPlayer);
            final PacketPlayOutPlayerInfo addInfo = PacketPlayOutPlayerInfo.addPlayer(entityPlayer);

            final PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(((WorldServer) entityPlayer.getWorld()).dimension,
                    entityPlayer.getWorld().difficulty, entityPlayer.getWorld().worldData.getType(),
                    WorldSettings.EnumGamemode.getById(player.getGameMode().getValue()));

            final PacketPlayOutPosition pos = new PacketPlayOutPosition(location.getX(), location.getY(), location.getZ(), location.getYaw(),
                    location.getPitch(), false);

            final PacketPlayOutEntityEquipment itemhand = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 0,
                    CraftItemStack.asNMSCopy(player.getItemInHand()));

            final PacketPlayOutEntityEquipment helmet = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 4,
                    CraftItemStack.asNMSCopy(player.getInventory().getHelmet()));

            final PacketPlayOutEntityEquipment chestplate = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 3,
                    CraftItemStack.asNMSCopy(player.getInventory().getChestplate()));

            final PacketPlayOutEntityEquipment leggings = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 2,
                    CraftItemStack.asNMSCopy(player.getInventory().getLeggings()));

            final PacketPlayOutEntityEquipment boots = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 1,
                    CraftItemStack.asNMSCopy(player.getInventory().getBoots()));

            final PacketPlayOutHeldItemSlot slot = new PacketPlayOutHeldItemSlot(player.getInventory().getHeldItemSlot());

            for (Player inWorld : player.getWorld().getPlayers()) {
                final CraftPlayer craftOnline = (CraftPlayer) inWorld;
                PlayerConnection con = craftOnline.getHandle().playerConnection;
                if (inWorld.equals(player)) {
                    con.sendPacket(removeInfo);
                    con.sendPacket(addInfo);
                    con.sendPacket(respawn);
                    con.sendPacket(pos);
                    con.sendPacket(slot);
                    craftOnline.updateScaledHealth();
                    craftOnline.getHandle().triggerHealthUpdate();
                    craftOnline.updateInventory();
                    Bukkit.getScheduler().runTask(plugin, () -> craftOnline.getHandle().updateAbilities());
                    continue;
                }
                con.sendPacket(removeEntity);
                con.sendPacket(removeInfo);
                if (inWorld.canSee(player)){
                    con.sendPacket(addInfo);
                    con.sendPacket(addNamed);
                    con.sendPacket(itemhand);
                    con.sendPacket(helmet);
                    con.sendPacket(chestplate);
                    con.sendPacket(leggings);
                    con.sendPacket(boots);
                }
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                entityPlayer.playerConnection.sendPacket(respawn);

                player.setGameMode(gameMode);
                player.setAllowFlight(allowFlight);
                player.setFlying(flying);
                player.updateInventory();
                player.setLevel(level);
                player.setExp(xp);
                player.setMaxHealth(maxHealth);
                player.setHealth(health);

                entityPlayer.playerConnection.sendPacket(addInfo);
            }, 1);

            final List<String> prev = usageCache.getOrDefault(player.getUniqueId(), new ArrayList<>());
            if(prev.stream().noneMatch(u -> u.equalsIgnoreCase(username))) prev.add(username);
            usageCache.put(player.getUniqueId(), prev);
            player.sendMessage(Color.translate("&aChanged your skin to &e" + username + "&a."));
        })); */
    }

    public List<String> getUsage(UUID uuid) {
        return usageCache.getOrDefault(uuid, new ArrayList<>());
    }
}
