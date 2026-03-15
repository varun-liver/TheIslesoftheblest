package com.isles.entity;

import com.isles.blest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.AnimationState;

public class TheinfectionEntity extends Monster {
    private static final float SMASH_DAMAGE = 5.0F;
    private static final int SMASH_HIT_TICK = 5;
    private static final int SMASH_LENGTH = 60;

    private static final float BLAST_DAMAGE = 7.0F;
    private static final int BLAST_HIT_TICK = 18;
    private static final int BLAST_LENGTH = 45;

    private static final int SUMMON_LENGTH = 40;

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState attack2AnimationState = new AnimationState();
    public final AnimationState attack3AnimationState = new AnimationState();
    private int currentAttackType = 0;
    private int currentAttackTick = 0;
    private boolean attackDidDamage = false;

    public TheinfectionEntity(EntityType<? extends TheinfectionEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TheInfectionMeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.tickCustomAttack();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attack2AnimationState.stop();
            this.attack3AnimationState.stop();
            this.attackAnimationState.start(this.tickCount);
        } else if (id == 5) {
            this.attackAnimationState.stop();
            this.attack3AnimationState.stop();
            this.attack2AnimationState.start(this.tickCount);
        } else if (id == 6) {
            this.attackAnimationState.stop();
            this.attack2AnimationState.stop();
            this.attack3AnimationState.start(this.tickCount);
            this.onSummonStart(this.level(),this.blockPosition());
        } else {
            super.handleEntityEvent(id);
        }
    }

    public boolean isPerformingCustomAttack() {
        return this.currentAttackType != 0;
    }

    public void startCustomAttack(int attackType) {
        if (this.level().isClientSide || this.isPerformingCustomAttack()) {
            return;
        }
        this.currentAttackType = attackType == 3 ? 3 : (attackType == 2 ? 2 : 1);
        this.currentAttackTick = 0;
        this.attackDidDamage = false;
        byte eventId = this.currentAttackType == 1 ? (byte) 4 : (this.currentAttackType == 2 ? (byte) 5 : (byte) 6);
        this.level().broadcastEntityEvent(this, eventId);
        if (this.currentAttackType == 3) {
            this.onSummonStart(this.level(),this.blockPosition());
        }
    }

    private void tickCustomAttack() {
        if (!this.isPerformingCustomAttack()) {
            return;
        }

        this.currentAttackTick++;
        LivingEntity target = this.getTarget();

        if (!this.attackDidDamage && target != null && target.isAlive()) {
            int hitTick = this.currentAttackType == 1 ? SMASH_HIT_TICK : BLAST_HIT_TICK;
            if (this.currentAttackTick >= hitTick && this.isWithinAttackRange(target)) {
                float damage = this.currentAttackType == 1 ? SMASH_DAMAGE : BLAST_DAMAGE;
                if (this.currentAttackType != 3) {
                    if (target.hurt(this.damageSources().mobAttack(this), damage)) {
                        this.doEnchantDamageEffects(this, target);
                    }
                }
                this.attackDidDamage = true;
            }
        }

        int attackLength = this.currentAttackType == 1 ? SMASH_LENGTH
                : (this.currentAttackType == 2 ? BLAST_LENGTH : SUMMON_LENGTH);
        if (this.currentAttackTick >= attackLength) {
            this.currentAttackType = 0;
            this.currentAttackTick = 0;
            this.attackDidDamage = false;
        }
    }

    private boolean isWithinAttackRange(LivingEntity target) {
        double attackReachSqr = (this.getBbWidth() * 2.0F) * (this.getBbWidth() * 2.0F) + target.getBbWidth();
        return this.distanceToSqr(target) <= attackReachSqr;
    }

    protected void onSummonStart(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            for(int i = 0; i < 10; i++) {
                // Replace 'YOUR_ENTITY_TYPE' with your registered entity
                Entity entity = blest.sky_guardian.get().create(level);
                if (entity != null) {
                    entity.moveTo(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
                    level.addFreshEntity(entity);
                }
            }
        }
    }

    private static class TheInfectionMeleeAttackGoal extends MeleeAttackGoal {
        private final TheinfectionEntity theinfection;

        public TheInfectionMeleeAttackGoal(TheinfectionEntity theinfection, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(theinfection, speedModifier, followingTargetEvenIfNotSeen);
            this.theinfection = theinfection;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double distToEnemySqr) {
            if (distToEnemySqr <= this.getAttackReachSqr(target) && this.getTicksUntilNextAttack() <= 0 && !this.theinfection.isPerformingCustomAttack()) {
                this.resetAttackCooldown();
                float roll = this.theinfection.getRandom().nextFloat();
                int attackType = roll < 0.5F ? 1 : (roll < 0.85F ? 2 : 3);
                this.theinfection.startCustomAttack(attackType);
            }
        }
    }
}
