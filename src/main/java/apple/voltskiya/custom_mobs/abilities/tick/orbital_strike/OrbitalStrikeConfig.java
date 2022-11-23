package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike;

import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.OrbitalStrike.OrbitalStrikeType;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;
import apple.voltskiya.mob_manager.mob.ability.activation.Activation;
import apple.voltskiya.mob_manager.mob.ability.activation.ActivationRange;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public abstract class OrbitalStrikeConfig extends MMAbilityConfig {

    public double radius = 6;
    public double minHeight = 20;
    public double totalHeight = 20;
    public int totalTime = 300;
    public int targetTime = 30;
    public double shootInterval = 1;
    public double movementSpeed = 2.2;
    public int movementTargetLag = 2;

    public int particles = 100;

    public float particleSize = 0.7f;
    public boolean doesModify = false;
    public ActivationRange range = new ActivationRange(100);
    private static final Random random = new Random();

    @Override
    public void doSpawn(MMSpawned mob) {
        new MobOrbitalStriker<>(mob, this);
    }

    @Nullable
    protected EntityType possibleFailFirst() {
        synchronized (random) {
            return random.nextBoolean() ? EntityType.SMALL_FIREBALL : null;
        }
    }

    @Nullable
    protected EntityType possibleBigSecond() {
        synchronized (random) {
            return random.nextBoolean() ? null : EntityType.FIREBALL;
        }
    }

    @Nullable
    public abstract EntityType fireballEntityType(int fireballIndex);

    @Override
    public Collection<Activation> getActivations() {
        return List.of(this.range);
    }

    public abstract OrbitalStrikeType getType();

    public static class OrbitalStrikeConfigSmall extends OrbitalStrikeConfig {

        public double targetVariationRadius = 7;

        @Nullable
        public EntityType fireballEntityType(int fireballIndex) {
            if (fireballIndex == 0)
                return possibleFailFirst();
            return EntityType.SMALL_FIREBALL;
        }

        @Override
        public OrbitalStrikeType getType() {
            return OrbitalStrikeType.SMALL;
        }


        @Override
        public String getBriefTag() {
            return "orbital_strike.small";

        }
    }

    public static class OrbitalStrikeConfigMedium extends OrbitalStrikeConfig {

        @Nullable
        public EntityType fireballEntityType(int fireballIndex) {
            if (fireballIndex == 0)
                return possibleFailFirst();
            return EntityType.SMALL_FIREBALL;
        }

        @Override
        public OrbitalStrikeType getType() {
            return OrbitalStrikeType.MEDIUM;
        }

        @Override
        public String getBriefTag() {
            return "orbital_strike.medium";
        }
    }

    public static class OrbitalStrikeConfigLarge extends OrbitalStrikeConfig {

        @Nullable
        public EntityType fireballEntityType(int fireballIndex) {
            if (fireballIndex == 1)
                return possibleBigSecond();
            return EntityType.SMALL_FIREBALL;
        }

        @Override
        public OrbitalStrikeType getType() {
            return OrbitalStrikeType.LARGE;
        }

        @Override
        public String getBriefTag() {
            return "orbital_strike.large";
        }
    }
}
