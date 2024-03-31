package apple.voltskiya.custom_mobs.nms.nether.eye_plant;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.nms.decoding.sound.DecodeSoundEffects;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsMobWrapperQOLModel;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsModelHolderQOL;
import apple.voltskiya.custom_mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsSpawnWrapperModel;
import apple.voltskiya.custom_mobs.nms.parts.NmsModelHandler;
import apple.voltskiya.custom_mobs.nms.parts.child.MobParts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.GoalLookAtPlayer;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MobEyePlant extends EntityZombie implements RegisteredCustomMob, NmsModelHolderQOL<MobEyePlant> {

    private static NmsSpawnWrapperModel<MobEyePlant> spawner;
    private final NmsMobWrapperQOLModel<MobEyePlant> selfWrapper = new NmsMobWrapperQOLModel<>(this);

    public MobEyePlant(EntityType<?> EntityType, World world) {
        super(DecodeEntityType.ZOMBIE, world);
    }

    public static NmsSpawnWrapperModel<MobEyePlant> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobEyePlant::makeSpawner);
    }

    @NotNull
    private static NmsSpawnWrapperModel<MobEyePlant> makeSpawner() {
        NmsModelHandler.ModelConfigName model = NmsModelHandler.ModelConfigName.EYE_PLANT;
        return new NmsSpawnWrapperModel<>(
            model.getName(),
            MobEyePlant::new,
            DecodeEntityType.ZOMBIE,
            model
        );
    }

    public NmsSpawnWrapperModel<MobEyePlant> getSpawner() {
        return spawner;
    }

    @Override
    public NmsMobEntitySupers makeEntitySupers() {
        return new NmsMobEntitySupers(
            super::b, // change world
            super::a, // move
            super::g, //load
            super::f, //save
            super::a // die
        );
    }

    @Override
    public NmsMobWrapperQOLModel<MobEyePlant> getSelfWrapper() {
        return selfWrapper;
    }

    @Override
    public MobEyePlant getSelfEntity() {
        return this;
    }


    public void addChildrenPost() {
        this.bN = new MobParts.ControllerLookChildrenFollow(this, selfWrapper.verifyChildren());
    }

    //init pathfinder
    @Override
    protected void u() {
        // only look at the player
        DecodeEntity.getGoalSelector(this).a(0, new GoalLookAtPlayer(this, Player.class, 8.0F));
    }

    // getSoundAmbient
    @Override
    protected SoundEffect r() {
        if (this.getRandom().nextBoolean()) return null;
        ambientParticles();
        final double choice = this.getRandom().nextDouble();
        if (choice < 1 / 3f) {
            return DecodeSoundEffects.ENTITY_ENDERMAN_AMBIENT;
        } else if (choice < 2 / 3f) {
            return DecodeSoundEffects.BLOCK_ENDER_CHEST_CLOSE;
        } else {
            return DecodeSoundEffects.BLOCK_ENDER_CHEST_OPEN;
        }
    }


    private void ambientParticles() {
        CraftEntity me = getBukkitEntity();
        Location location = me.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        for (int i = 0; i < 10; i++) {
            double xi = this.getRandom().nextDouble() - .5;
            double yi = this.getRandom().nextDouble() - .5;
            double zi = this.getRandom().nextDouble() - .5;
            me.getWorld().spawnParticle(org.bukkit.Particle.CRIT_MAGIC, x + xi, y + yi, z + zi, 1);
        }
    }

    @Override
    public AttributeSupplier getAttributeSupplier() {
        return DefaultAttributes.a(DecodeEntityType.ZOMBIE);
    }

    @Override
    public EntityType<?> ad() {
        return nmsgetEntityType();
    }

    @Override
    public void a(EnumMoveType enummovetype, Vec3 Vec3) {
        nmsmove(enummovetype, Vec3);
    }

    @Override
    public AttributeMap ep() {
        return nmsgetAttributeMap();
    }

    @Override
    public Entity b(ServerLevel ServerLevel) {
        return nmsChangeWorlds(ServerLevel);
    }

    @Override
    public void g(CompoundTag CompoundTag) {
        nmsload(CompoundTag);
    }

    @Override
    public CompoundTag f(CompoundTag CompoundTag) {
        return nmssave(CompoundTag);
    }

    @Override
    public void a(Entity.RemovalReason removalReason) {
        nmsRemove(removalReason);
    }
}
