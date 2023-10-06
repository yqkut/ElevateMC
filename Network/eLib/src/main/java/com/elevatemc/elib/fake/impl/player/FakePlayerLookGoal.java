package com.elevatemc.elib.fake.impl.player;

import org.bukkit.entity.Player;

/**
 * @author ImHacking
 */
public class FakePlayerLookGoal {
    public static final int GOAL_UPDATE_TICKS = 10;

    public float goalYaw;
    public float goalPitch;

    public float yawIncrements;
    public float pitchIncrements;

    public float currentYaw;
    public float currentPitch;

    public int tick;
    private boolean enabled;

    private int incrementsRemaining;

    public FakePlayerLookGoal(float[] current, float[] goals) {
        this.goalYaw = current[0];
        this.goalPitch = current[1];

        this.tick = 0;
        this.enabled = true;

        this.incrementsRemaining = GOAL_UPDATE_TICKS;

        this.updateGoal(goals[0], goals[1]);
    }

    public void updateGoal(float goalYaw, float goalPitch) {
        this.currentYaw = this.goalYaw;
        this.currentPitch = this.goalPitch;

        if (Math.abs(goalYaw - currentYaw) > 180) {
            if (goalYaw < currentYaw) {
                goalYaw += 360;
            } else {
                this.currentYaw += 360;
            }
        }

        this.goalYaw = goalYaw;
        this.goalPitch = goalPitch;

        this.yawIncrements = (this.goalYaw - currentYaw) / (float) GOAL_UPDATE_TICKS;
        this.pitchIncrements = (this.goalPitch - currentPitch) / (float) GOAL_UPDATE_TICKS;
        this.incrementsRemaining = GOAL_UPDATE_TICKS;
    }

    public void updateCurrent(float currentYaw, float currentPitch) {
        this.currentYaw = currentYaw;
        this.currentPitch = currentPitch;
    }

    public void tick() {
        if (this.incrementsRemaining > 0) {
            this.currentYaw = increaseAngle(this.currentYaw, this.yawIncrements);
            this.currentPitch = increaseAngle(this.currentPitch, this.pitchIncrements);
            this.incrementsRemaining--;
        }
    }

    private float increaseAngle(float angle, float increase) {
        if (angle + increase > 360) {
            return (angle + increase) % 360;
        } else if (angle + increase < 0) {
            float deduct = Math.abs(angle + increase);
            return 360 - deduct;
        } else {
            return angle + increase;
        }
    }

    public void sendFacing(FakePlayerEntity entity, Player player) {
        entity.updateRotation(player, currentYaw, currentPitch);
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean hasReachedGoal() {
        return this.incrementsRemaining <= 0;
    }

}
