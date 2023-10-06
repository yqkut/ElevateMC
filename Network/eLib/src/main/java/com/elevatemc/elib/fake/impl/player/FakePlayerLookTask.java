package com.elevatemc.elib.fake.impl.player;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.elevatemc.elib.fake.FakeEntity;
import com.elevatemc.elib.fake.FakeEntityHandler;

import java.util.UUID;

@AllArgsConstructor
public class FakePlayerLookTask implements Runnable {
    private FakeEntityHandler handler;
    @Override
    public void run() {
        try {
            this.runLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runLoop() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            UUID uuid = online.getUniqueId();
            for (FakeEntity fakeEntity : this.handler.getAllEntitiesPlayerCanSee(online.getPlayer().getUniqueId())) {
                if (!(fakeEntity instanceof FakePlayerEntity )) {
                    continue;
                }
                FakePlayerEntity fakePlayer = (FakePlayerEntity) fakeEntity;
                if (!(online.getLocation().getWorld().getUID().equals(fakePlayer.getLocation().getWorld().getUID()))) {
                    continue;
                }

                FakePlayerLookGoal goal = fakePlayer.getLookGoals().computeIfAbsent(uuid, (id) ->
                        new FakePlayerLookGoal(fakePlayer.getCurrentAngles(), fakePlayer.getGoalAngles(online.getLocation())));

                if (goal.isEnabled() || !goal.hasReachedGoal()) {
                    goal.tick();
                    goal.sendFacing(fakePlayer, online);
                }

                if (goal.tick == 0) {
                    Location playerLoc = online.getLocation();
                    if (playerLoc.distanceSquared(fakePlayer.getLocation()) <= 100) {
                        final float[] goalangles = fakePlayer.getGoalAngles(playerLoc);
                        goal.updateGoal(goalangles[0], goalangles[1]);
                        goal.enable();
                    } else {
                        goal.disable();
                        float[] original = fakePlayer.getCurrentAngles();
                        goal.updateGoal(original[0], original[1]);
                    }
                }

                goal.tick = (goal.tick + 1) % FakePlayerLookGoal.GOAL_UPDATE_TICKS;
            }
        }
    }
}
