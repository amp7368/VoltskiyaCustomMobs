package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.mob;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config.MobAPC33ConfigWhole;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobHolder;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.utils.UtilsPacket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.monster.EntityRavager;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

import java.util.List;

public class MobAPC33Whole extends EntityRavager implements NmsMob<MobAPC33Whole, MobAPC33ConfigWhole> {
    private static NmsMobRegister<MobAPC33Whole, MobAPC33ConfigWhole> register;
    private NmsMobHolder<MobAPC33Whole, MobAPC33ConfigWhole> mobManager = null;
    private MobAPC33Treads treadsMob;
    private MobAPC33SmallGun sideGunMob;
    private MobAPC33LargeCannon cannonMob;


    public MobAPC33Whole(EntityTypes<MobAPC33Whole> entityTypes, World world) {
        super(DecodeEntityTypes.RAVAGER, world);
    }

    @Override
    public NmsMobHolder<MobAPC33Whole, MobAPC33ConfigWhole> getMobManager() {
        return mobManager;
    }

    @Override
    public void setMobManager(NmsMobHolder<MobAPC33Whole, MobAPC33ConfigWhole> mobManager) {
        this.mobManager = mobManager;
    }

    @Override
    public NmsMobRegister<MobAPC33Whole, MobAPC33ConfigWhole> getRegister() {
        return register;
    }

    public static void setRegister(NmsMobRegister<MobAPC33Whole, MobAPC33ConfigWhole> register) {
        MobAPC33Whole.register = register;
    }

    @Override
    public MobAPC33Whole getEntity() {
        return this;
    }

    @Override
    public void prepareNms(Location location, NBTTagCompound oldNbt) {
        NmsMob.super.prepareNms(location, oldNbt);
        this.cannonMob = MobAPC33LargeCannon.getRegisterStatic().spawn(location, oldNbt);
        this.sideGunMob = MobAPC33SmallGun.getRegisterStatic().spawn(location, oldNbt);
        this.treadsMob = MobAPC33Treads.getRegisterStatic().spawn(location, oldNbt);

        VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> {
            cannonMob.startRiding(sideGunMob);
            sideGunMob.startRiding(this);
            this.startRiding(treadsMob);
        }, 1);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        PathfinderGoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
//        goalSelector.a(new PathfinderGoalShootSpell<>(new MobAPC33SpellCaster(this), MobAPC33Config.MobAPCMachineGunType.NORMAL));
    }

    @Override
    public NmsMobEntitySupers getEntitySupers() {
        return new NmsMobEntitySupers(
                super::b,
                super::move,
                super::load,
                super::save,
                super::a
        );
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return nmsgetEntityType();
    }

    @Override
    public AttributeMapBase getAttributeMap() {
        return nmsgetAttributeMap();
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        float oldHeadRotation = getHeadRotation();
        super.move(enummovetype, vec3d);
        // slow the head rotation
        float newHeadRotation = getHeadRotation();
        double differenceHeadRotation = newHeadRotation - oldHeadRotation;
        if (differenceHeadRotation < 0) {
            differenceHeadRotation = Math.max(differenceHeadRotation, getConfig().maxHeadRotationPerTick);
        } else {
            differenceHeadRotation = Math.min(differenceHeadRotation, getConfig().maxHeadRotationPerTick);
        }
        setHeadRotation((float) (oldHeadRotation + differenceHeadRotation));
        if (hasModel()) {
            if (verifyMobHolder().getChildren() == null) addChildren();
            List<Packet<?>> packetsToSend = verifyMobHolder().move(true);
            UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend, this.getEntity().getBukkitEntity().getLocation());
        }
    }

    @Override
    public Entity b(WorldServer worldserver) {
        return nmsb(worldserver);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        nmsload(nbttagcompound);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        return nmssave(nbttagcompound);
    }

    @Override
    public void a(Entity.RemovalReason removalReason) {
        nmsa(removalReason);
    }
}

