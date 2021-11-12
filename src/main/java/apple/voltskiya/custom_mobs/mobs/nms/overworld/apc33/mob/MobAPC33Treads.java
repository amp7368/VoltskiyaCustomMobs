package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.mob;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config.MobAPC33ConfigTreads;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobHolder;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class MobAPC33Treads extends EntityZombie implements NmsMob<MobAPC33Treads, MobAPC33ConfigTreads> {
    private static NmsMobRegister<MobAPC33Treads, MobAPC33ConfigTreads> register;
    private NmsMobHolder<MobAPC33Treads, MobAPC33ConfigTreads> mobManager = null;


    public MobAPC33Treads(EntityTypes<MobAPC33Treads> entityTypes, World world) {
        super(DecodeEntityTypes.ZOMBIE, world);
    }

    public static NmsMobRegister<MobAPC33Treads, MobAPC33ConfigTreads> getRegisterStatic() {
        return register;
    }

    @Override
    public NmsMobHolder<MobAPC33Treads, MobAPC33ConfigTreads> getMobManager() {
        return mobManager;
    }

    @Override
    public void setMobManager(NmsMobHolder<MobAPC33Treads, MobAPC33ConfigTreads> mobManager) {
        this.mobManager = mobManager;
    }

    @Override
    public NmsMobRegister<MobAPC33Treads, MobAPC33ConfigTreads> getRegister() {
        return register;
    }

    public static void setRegister(NmsMobRegister<MobAPC33Treads, MobAPC33ConfigTreads> register) {
        MobAPC33Treads.register = register;
    }

    @Override
    public MobAPC33Treads getEntity() {
        return this;
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
        nmsmove(enummovetype, vec3d);
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
