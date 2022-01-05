package apple.voltskiya.custom_mobs.mobs.nms.nether.eye_plant;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.sound.DecodeSoundEffects;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsMobWrapperQOLModel;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsModelHolderQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapperModel;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelHandler;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobParts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MobEyePlant extends EntityZombie implements RegisteredCustomMob, NmsModelHolderQOL<MobEyePlant> {
    private static NmsSpawnWrapperModel<MobEyePlant> spawner;
    private final NmsMobWrapperQOLModel<MobEyePlant> selfWrapper = new NmsMobWrapperQOLModel<>(this);

    public MobEyePlant(EntityTypes<?> entitytypes, World world) {
        super(DecodeEntityTypes.ZOMBIE, world);
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
                DecodeEntityTypes.ZOMBIE,
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
        DecodeEntity.getGoalSelector(this).a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
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
    public AttributeProvider getAttributeProvider() {
        return AttributeDefaults.a(DecodeEntityTypes.ZOMBIE);
    }

    @Override
    public EntityTypes<?> ad() {
        return nmsgetEntityType();
    }

    @Override
    public void a(EnumMoveType enummovetype, Vec3D vec3d) {
        nmsmove(enummovetype, vec3d);
    }

    @Override
    public AttributeMapBase ep() {
        return nmsgetAttributeMap();
    }

    @Override
    public Entity b(WorldServer worldserver) {
        return nmsChangeWorlds(worldserver);
    }

    @Override
    public void g(NBTTagCompound nbttagcompound) {
        nmsload(nbttagcompound);
    }

    @Override
    public NBTTagCompound f(NBTTagCompound nbttagcompound) {
        return nmssave(nbttagcompound);
    }

    @Override
    public void a(Entity.RemovalReason removalReason) {
        nmsRemove(removalReason);
    }
}
