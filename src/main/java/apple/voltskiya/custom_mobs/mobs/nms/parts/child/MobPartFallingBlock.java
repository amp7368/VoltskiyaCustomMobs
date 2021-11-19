package apple.voltskiya.custom_mobs.mobs.nms.parts.child;

import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeDamageSource;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.custom_model.CustomModelDataEntity;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelEntityConfig;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.util.EntityLocation;
import voltskiya.apple.utilities.util.VectorUtils;

import java.util.Map;
import java.util.logging.Level;

public class MobPartFallingBlock extends EntityFallingBlock implements MobPartChild {
    private static final String REGISTERED_NAME = "mobpart_falling_block";
    private static final double DEATH_PARTICLES_DENSITY = 0.001d;
    private static EntityTypes<MobPartFallingBlock> entityTypes;
    private MobPartMother mainMob;
    private EntityLocation entityLocation;
    private AttributeMapBase attributeMap;

    public MobPartFallingBlock(EntityTypes<MobPartFallingBlock> entityTypes, World world, MobPartMother mother, NmsModelEntityConfig config) {
        super(EntityTypes.C, world);
        prepare(mother, config);
    }

    public MobPartFallingBlock(EntityTypes<MobPartFallingBlock> entityTypes, World world) {
        super(DecodeEntityTypes.FALLING_BLOCK, world);
        this.die(); // just die on restarts because we'll be remade
    }

    public static void initialize() {
        Map<? super Object, Type<?>> types = NmsMobRegister.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:falling_block");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobPartFallingBlock> entitytypesBuilder = EntityTypes.Builder.a(MobPartFallingBlock::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(DecodeIRegistry.getEntityType(), DecodeIRegistry.getEntityType().getId(DecodeEntityTypes.FALLING_BLOCK), REGISTERED_NAME, entityTypes); // this is good

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
    }

    @NotNull
    private static String registeredNameId() {
        return "minecraft" + ":" + REGISTERED_NAME;
    }

    public static MobPartFallingBlock spawnMobPart(MobPartMother mother, NmsModelEntityConfig config) {
        final World world = mother.entity.getWorld();
        final MobPartFallingBlock bodyPart = new MobPartFallingBlock(entityTypes, world, mother, config);
        world.addEntity(bodyPart);
        return bodyPart;
    }

    public void prepare(MobPartMother mother, NmsModelEntityConfig config) {
        this.mainMob = mother;
        final CustomModelDataEntity entity = config.getData();
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
        this.setNoGravity(true);
        this.moveFromMother(false);
        this.addScoreboardTag(mother.scoreboardTag);
        this.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("id", registeredNameId());
        super.load(nbttagcompound);
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

    /**
     * This packet (PacketPlayOutRelEntityMove) that I'm using has documentation here: https://wiki.vg/Protocol
     *
     * @param isLookingRelevant whether i should turn based on looking direction as well
     * @return the packet to update this model to the client
     */
    @Override
    public Packet<?> moveFromMother(boolean isLookingRelevant) {
        float yaw1;
        if (isLookingRelevant) {
            yaw1 = this.mainMob.entity.getBukkitYaw();// + this.mainMob.entity.getHeadRotation();
        } else {
            yaw1 = this.mainMob.entity.getBukkitYaw();
        }
        Location newLocation = VectorUtils.rotate(this.entityLocation, yaw1, this.mainMob.location, false);
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
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        // do nothing because I'll manually call move this when mainMob moves
        // and it would just cause unnecessary lag
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

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        return this.mainMob.entity.a(entityhuman, enumhand);
    }
}
