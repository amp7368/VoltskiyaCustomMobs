package apple.voltskiya.custom_mobs.abilities.nether.charger;

import apple.mc.utilities.item.material.MaterialUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalCharge;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.minecraft.TagConstants;

public class ChargerChargeSpell {

    protected final Charger charger;
    protected final Mob chargerMob;
    protected final net.minecraft.world.entity.Mob chargerHandle;
    protected Location finalLocation;
    protected ChargingState state = ChargingState.CHARGE_UP;

    public ChargerChargeSpell(Charger charger, Entity target) {
        this.charger = charger;
        this.chargerMob = charger.getEntity();
        this.chargerHandle = ((CraftMob) chargerMob).getHandle();
        setFinalLocation(charger, target);
        stateChoice();
    }

    protected void setFinalLocation(Charger charger, Entity target) {
        Location chargerLocation = charger.getEntity().getLocation();
        Location targetLocation = target.getLocation();
        double x = targetLocation.getX() - chargerLocation.getX();
        double y = targetLocation.getY() - chargerLocation.getY();
        double z = targetLocation.getZ() - chargerLocation.getZ();
        x *= 3;
        y *= 3;
        z *= 3;
        Vector overshoot = new Vector(x, y, z).normalize()
            .multiply(this.charger.getType().getOvershootDistance());
        this.finalLocation = chargerLocation.clone().add(overshoot).add(x, y, z);
    }

    public void stateChoice() {
        switch (state) {
            case CHARGE_UP -> new ChargeUp().run();
            case RUN -> new ChargeRun().run();
            case HIT_WALL -> new ChargeStun(this.charger.getType().getChargeStunTime()).run();
            case TIRED -> new ChargeStun(this.charger.getType().getChargeTiredTime()).run();
        }
    }

    protected enum ChargingState {
        CHARGE_UP,
        RUN,
        HIT_WALL,
        TIRED
    }

    protected class ChargeUp implements Runnable {

        private int count = 0;

        public ChargeUp() {
            ChargerChargeHelper.chargeUpSound(chargerMob.getLocation());
            chargerMob.setAI(false);
        }

        @Override
        public void run() {
            if (chargerMob.isDead())
                return;
            Material below = chargerMob.getLocation().clone().subtract(0, 1, 0).getBlock()
                .getType();
            ChargerChargeHelper.runFeetParticles(chargerMob.getLocation(), below, 25);
            count++;
            if (count == charger.getType().getChargeUpTime()) {
                state = ChargingState.RUN;
                stateChoice();
                return;
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
        }
    }

    protected class ChargeRun implements Runnable {

        public void run() {
            PotionEffect strength = new PotionEffect(PotionEffectType.STRENGTH,
                charger.getType().getMaxChargeTime(), 1, false, false);
            chargerMob.addPotionEffect(strength);
            ChargerChargeHelper.chargeSound(chargerMob.getLocation());
            chargerMob.setAI(true);
            chargerHandle.goalSelector.addGoal(-1,
                new PathfinderGoalCharge(chargerHandle, finalLocation,
                    charger.getType().getOvershootSpeed(), 1000,
                    Collections.singletonList(PathfinderGoalCharge.ChargeResult.HIT_ENTITY),
                    this::dealWithResult));
        }

        public void dealWithResult(PathfinderGoalCharge.ChargeResult result) {
            switch (result) {
                case HIT_WALL -> {
                    state = ChargingState.HIT_WALL;
                    stateChoice();
                }
                case HIT_NOTHING -> {
                    state = ChargingState.TIRED;
                    stateChoice();
                }
            }
        }
    }

    protected class ChargeStun {

        private final int chargeStunTime;

        public ChargeStun(int chargeStunTime) {
            this.chargeStunTime = chargeStunTime;
        }

        public void run() {
            Location here = chargerMob.getEyeLocation();
            final World world = here.getWorld();
            here.add(finalLocation.clone().subtract(here).toVector().setY(0).normalize());
            Material blockInFront = world.getBlockAt(here).getType();
            if (MaterialUtils.isWalkThroughable(blockInFront))
                blockInFront = world.getBlockAt(here.add(0, 1, 0)).getType();

            chargerMob.setVelocity(new Vector(0, 0, 0));
            ChargerChargeHelper.runFeetParticles(chargerMob.getEyeLocation(), blockInFront, 50);
            chargerMob.removePotionEffect(PotionEffectType.STRENGTH);
            chargerMob.setAI(false);
            Location newLocation = chargerMob.getLocation();
            newLocation.setDirection(newLocation.getDirection().setY(-1));
            chargerMob.teleport(newLocation);
            ChargerChargeHelper.stunned(newLocation);
            Location eyeLocation = chargerMob.getEyeLocation();
            ChargerChargeHelper.stunParticles(eyeLocation, chargeStunTime);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                TagConstants.removeIsDoingAbility(chargerMob);
                chargerMob.setAI(true);
                ChargerManagerTicker.get().giveCharger(charger);
            }, chargeStunTime);

        }
    }
}
