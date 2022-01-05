package apple.voltskiya.custom_mobs.mobs.nms.parts.child;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumMonsterType;
import apple.nms.decoding.iregistry.DecodeDamageSource;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.world.DecodeWorld;
import apple.voltskiya.custom_mobs.custom_model.CustomModelDataEntity;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsHolderQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsMobWrapperQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapper;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelEntityConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.util.EntityLocation;
import voltskiya.apple.utilities.util.VectorUtils;

import java.util.Objects;

public class MobPartArmorStand extends EntityArmorStand implements MobPartChild, RegisteredCustomMob, NmsHolderQOL<MobPartArmorStand> {
    private static final String REGISTERED_NAME = "mobpart_armorstand";

    private MobPartMother mainMob;
    private static NmsSpawnWrapper<MobPartArmorStand> spawner;
    private EntityLocation entityLocation;
    private NmsModelEntityConfig config;
    private NmsMobWrapperQOL<MobPartArmorStand> wrapper;

    public MobPartArmorStand(EntityTypes<MobPartArmorStand> entityTypes, World world, MobPartMother mother, NmsModelEntityConfig config) {
        super(DecodeEntityTypes.ARMOR_STAND, world);
        this.mainMob = mother;
        this.config = config;
        prepare(config);
    }

    public MobPartArmorStand(EntityTypes<?> entityTypes, World world) {
        super(DecodeEntityTypes.ARMOR_STAND, world);
        // just die on restarts because we'll be remade
        DecodeEntity.die(this, DecodeDamageSource.OUT_OF_WORLD);
    }

    public static MobPartArmorStand spawnMobPart(MobPartMother mother, NmsModelEntityConfig config) {
        final World world = DecodeWorld.getWorldServer(mother.entity.getBukkitEntity().getWorld());
        final MobPartArmorStand bodyPart = new MobPartArmorStand(spawner.entityTypes(), world, mother, config);
        world.addFreshEntity(bodyPart, CreatureSpawnEvent.SpawnReason.NATURAL);
        return bodyPart;
    }

    public static NmsSpawnWrapper<MobPartArmorStand> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobPartArmorStand::makeSpawner);
    }

    public static NmsSpawnWrapper<MobPartArmorStand> makeSpawner() {
        return new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobPartArmorStand::new,
                DecodeEntityTypes.ILLUSIONER
        );
    }

    public void prepare(NmsModelEntityConfig config) {
        CraftArmorStand bukkitEntity = (CraftArmorStand) this.getBukkitEntity();
        final CustomModelDataEntity entity = config.getData();
        this.entityLocation = new EntityLocation(
                bukkitEntity.getUniqueId(),
                entity.x,
                entity.y,
                entity.z,
                entity.facingX,
                entity.facingY,
                entity.facingZ
        ); // for simpler rotations
        DecodeEntity.load(this, entity.nbt);
        bukkitEntity.setInvisible(true);
        bukkitEntity.addScoreboardTag(mainMob.scoreboardTag);
        bukkitEntity.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        this.moveFromMother(false);
    }

    /**
     * This packet (PacketPlayOutRelEntityMove) that I'm using has documentation here: https://wiki.vg/Protocol
     *
     * @param isParentLookingRelavant whether i should turn based on looking direction as well
     * @return the packet to update this model to the client
     */
    @Override
    public Packet<?> moveFromMother(boolean isParentLookingRelavant) {
        float yaw;
        if (isParentLookingRelavant) {
            yaw = this.mainMob.entity.getBukkitYaw() + DecodeEntity.getHeadRotation(this.mainMob.entity);
        } else {
            yaw = this.mainMob.entity.getBukkitYaw();
        }
        return moveFromMother(yaw);
    }

    @NotNull
    public PacketPlayOutEntityStatus moveFromMother(float yaw) {
        Location newLocation = VectorUtils.rotate(this.entityLocation, yaw, this.mainMob.location, false);
        newLocation.add(this.mainMob.entity.getBukkitEntity().getLocation());
        this.getBukkitEntity().teleport(newLocation);
        return new PacketPlayOutEntityStatus(this, (byte) 9);
    }

    // damage entity
    @Override
    public boolean a(DamageSource damagesource, float f) {
        return damagesource != DecodeDamageSource.STUCK && this.mainMob.entity.a(damagesource, f); // send this to the main mob
    }

    // movementTick
    @Override
    public void w_() {
        // do nothing because I'll manually call move this when mainMob moves
        // and it would just cause unnecessary lag
    }

    @Override
    public void a(EnumMoveType enummovetype, Vec3D vec3d) {
        // do nothing because I'll manually call move this when mainMob moves
        // and it would just cause unnecessary lag
    }

    // die
    @Override
    public void a(DamageSource damagesource) {
        super.a(damagesource);

        // make particles for death
        final CraftEntity bukkitEntity = this.getBukkitEntity();
        BoundingBox hitbox = bukkitEntity.getBoundingBox();
        double x = hitbox.getWidthX();
        double y = hitbox.getHeight();
        double z = hitbox.getWidthZ();
        double volume = x * y * z;
        double minX = hitbox.getMinX();
        double minY = hitbox.getMinY();
        double minZ = hitbox.getMinZ();
        int particlesToSpawn = (int) (volume / 0.001d + 1);

        org.bukkit.World world = bukkitEntity.getWorld();
        for (int i = 0; i < particlesToSpawn; i++) {
            world.spawnParticle(org.bukkit.Particle.SMOKE_NORMAL,
                    minX + getRandom().nextDouble() * x,
                    minY + getRandom().nextDouble() * y,
                    minZ + getRandom().nextDouble() * z,
                    1
            );
        }
    }

    /**
     * @return EnumMonsterType.ARTHROPOD || EnumMonsterType.ILLAGER || ...
     */
    @Override
    public EnumMonsterType eq() {
        return DecodeEnumMonsterType.ILLAGER.encode();
    }

    @Override
    protected void a(DamageSource damagesource, int i, boolean flag) {
        // do nuffin because this part will never drop anything or give any animation
    }

    @Override
    protected void by() {
        // do nothing with going through a portal, because the mainMob should take care of it
    }

    @Override
    public boolean ci() {
        // this can never portal
        return false;
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        return this.mainMob.entity.a(entityhuman, enumhand);
    }

    public NmsModelEntityConfig getConfig() {
        return config;
    }

    /**
     * sorry about f1,f2,f3. It's just the headPose
     *
     * @param f1 amount to add to headPose[f1,-,-]
     * @param f2 amount to add to headPose[-,f2,-]
     * @param f3 amount to add to headPose[-,-,f3]
     */
    public void rotateHead(float f1, float f2, float f3) {
        ArmorStand armorStand = getBukkitEntityTyped(ArmorStand.class);
        armorStand.setHeadPose(armorStand.getHeadPose().add(f1, f2, f3));
    }

    @Override
    public MobPartArmorStand getSelfEntity() {
        return this;
    }

    @Override
    public NmsSpawnWrapper<MobPartArmorStand> getSpawner() {
        return spawner();
    }

    @Override
    public NmsMobWrapperQOL<MobPartArmorStand> getSelfWrapper() {
        return wrapper = Objects.requireNonNullElseGet(wrapper, NmsHolderQOL.super::makeSelfWrapper);
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
    public EntityTypes<?> ad() {
        return nmsgetEntityType();
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

    @Override
    public void die() {
        NmsHolderQOL.super.die();
    }
}
