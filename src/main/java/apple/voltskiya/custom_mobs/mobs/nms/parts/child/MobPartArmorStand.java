package apple.voltskiya.custom_mobs.mobs.nms.parts.child;

import apple.nms.decoding.entity.DecodeEntityArmorStand;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.entity.DecodeEnumMainHand;
import apple.nms.decoding.entity.DecodeEnumMonsterType;
import apple.nms.decoding.iregistry.DecodeDamageSource;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.custom_model.CustomModel;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelEntityConfig;
import apple.voltskiya.custom_mobs.mobs.nms.utils.NbtConstants;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.core.Vector3f;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.util.EntityLocation;
import voltskiya.apple.utilities.util.VectorUtils;

import java.util.Map;
import java.util.logging.Level;

public class MobPartArmorStand extends EntityArmorStand implements MobPartChild {
    private static final String REGISTERED_NAME = "mobpart_armorstand";
    private static final double DEATH_PARTICLES_DENSITY = 0.001d;
    private static EntityTypes<MobPartArmorStand> entityTypes;
    private MobPartMother mainMob;
    private NmsModelEntityConfig entityConfig;
    private EntityLocation entityLocation;
    private AttributeMapBase attributeMap;

    public MobPartArmorStand(EntityTypes<MobPartArmorStand> entityTypes, World world, MobPartMother mother, NmsModelEntityConfig config) {
        super(DecodeEntityTypes.ARMOR_STAND, world);
        prepare(mother, config);
    }

    public static void initialize() {
        Map<? super Object, Type<?>> types = NmsMobRegister.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:armor_stand");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobPartArmorStand> entitytypesBuilder = EntityTypes.Builder.a(MobPartArmorStand::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(DecodeIRegistry.getEntityType(), DecodeIRegistry.getEntityType().getId(DecodeEntityTypes.ARMOR_STAND), REGISTERED_NAME, entityTypes); // this is good

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
    }

    public void prepare(MobPartMother mother, NmsModelEntityConfig config) {
        this.mainMob = mother;
        this.entityConfig = config;
        final CustomModel.CustomEntity entity = entityConfig.getEntity();
        this.entityLocation = new EntityLocation(
                this.getUniqueID(),
                entity.x,
                entity.y,
                entity.z,
                entity.facingX,
                entity.facingY,
                entity.facingZ
        ); // for simpler rotations
        this.loadData(entity.nbt);
        this.setInvisible(true);
        this.moveFromMother(false);
        this.addScoreboardTag(mother.scoreboardTag);
        this.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
    }


    public MobPartArmorStand(EntityTypes<MobPartArmorStand> entityTypes, World world) {
        super(DecodeEntityTypes.ARMOR_STAND, world);
        this.die(); // just die on restarts because we'll be remade
    }

    @NotNull
    private static String registeredNameId() {
        return "minecraft" + ":" + REGISTERED_NAME;
    }

    public static MobPartArmorStand spawnMobPart(MobPartMother mother, NmsModelEntityConfig config) {
        final World world = mother.entity.getWorld();
        final MobPartArmorStand bodyPart = new MobPartArmorStand(entityTypes, world, mother, config);
        world.addEntity(bodyPart);
        return bodyPart;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("id", registeredNameId());
        super.load(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (this.entityConfig == null)
            this.entityConfig = new NmsModelEntityConfig(nbttagcompound.getCompound(NbtConstants.ENTITY_LOCATION_RELATIVE_CONFIG));
    }


    @Override
    public Entity getThisEntity() {
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        NBTTagCompound data = super.save(nbttagcompound);
        data.setString("id", registeredNameId());
        return data;
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.set(NbtConstants.ENTITY_LOCATION_RELATIVE_CONFIG, entityConfig.toNbt());
    }

    @Override
    public AttributeMapBase getAttributeMap() {
        return this.attributeMap == null ? this.attributeMap = new AttributeMapBase(getAttributeProvider()) : this.attributeMap;
    }

    /**
     * @return the default attributeMap
     */
    private static AttributeProvider getAttributeProvider() {
        return AttributeDefaults.a(DecodeEntityTypes.ARMOR_STAND);
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
            yaw = this.mainMob.entity.getBukkitYaw() + this.mainMob.entity.getHeadRotation();
        } else {
            yaw = this.mainMob.entity.getBukkitYaw();
        }
        return moveFromMother(yaw);
    }

    @NotNull
    public PacketPlayOutEntityStatus moveFromMother(float yaw) {
        Location newLocation = VectorUtils.rotate(this.entityLocation, yaw, this.mainMob.location, false);
        newLocation.add(this.mainMob.entity.locX(), this.mainMob.entity.locY(), this.mainMob.entity.locZ());

        double nowX = newLocation.getX();
        double nowY = newLocation.getY();
        double nowZ = newLocation.getZ();
        this.setLocation(nowX, nowY, nowZ, newLocation.getYaw(), newLocation.getPitch());
        return new PacketPlayOutEntityStatus(this, (byte) 9);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return damagesource != DecodeDamageSource.STUCK && this.mainMob.entity.damageEntity(damagesource, f); // send this to the main mob
    }

    @Override
    public void movementTick() {
        // do nothing because I'll manually call move this when mainMob moves
        // and it would just cause unnecessary lag
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        // do nothing because I'll manually call move this when mainMob moves
        // and it would just cause unnecessary lag
    }

    @Override
    public EnumMainHand getMainHand() {
        return DecodeEnumMainHand.RIGHT.encode();
    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);

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
        int particlesToSpawn = (int) (volume / DEATH_PARTICLES_DENSITY + 1);

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
    public EnumMonsterType getMonsterType() {
        return DecodeEnumMonsterType.ILLAGER.encode();
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        // do nuffin because this part will never drop anything or give any animation
    }

    @Override
    protected void doPortalTick() {
        // do nothing with going through a portal, because the mainMob should take care of it
    }

    @Override
    public boolean canPortal() {
        // this can never portal
        return false;
    }

    /**
     * sorry about f1,f2,f3. It's just the headPose
     *
     * @param f1 amount to add to headPose[f1,-,-]
     * @param f2 amount to add to headPose[-,f2,-]
     * @param f3 amount to add to headPose[-,-,f3]
     */
    public void rotateHead(float f1, float f2, float f3) {
        Vector3f pose = DecodeEntityArmorStand.getHeadPose(this);
        f1 += pose.getX();
        f2 += pose.getY();
        f3 += pose.getZ();
        f1 %= 360;
        f2 %= 360;
        f3 %= 360;
        this.setHeadPose(new Vector3f(f1, f2, f3));
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        return this.mainMob.entity.a(entityhuman, enumhand);
    }
}
