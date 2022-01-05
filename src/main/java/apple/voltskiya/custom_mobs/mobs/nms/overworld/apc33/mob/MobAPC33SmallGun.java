package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.mob;

import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config.MobAPC33ConfigSmallGun;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegisterConfigable;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobWrappedConfigable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class MobAPC33SmallGun extends EntityZombie implements NmsMob<MobAPC33SmallGun, MobAPC33ConfigSmallGun> {
    private static NmsMobRegisterConfigable<MobAPC33SmallGun, MobAPC33ConfigSmallGun> register;
    private NmsMobWrappedConfigable<MobAPC33SmallGun, MobAPC33ConfigSmallGun> mobManager = null;

    public MobAPC33SmallGun(EntityTypes<MobAPC33SmallGun> entityTypes, World world) {
        super(DecodeEntityTypes.ZOMBIE, world);
    }

    public static NmsMobRegisterConfigable<MobAPC33SmallGun, MobAPC33ConfigSmallGun> getRegisterStatic() {
        return register;
    }

    @Override
    public NmsMobWrappedConfigable<MobAPC33SmallGun, MobAPC33ConfigSmallGun> getMobManager() {
        return mobManager;
    }

    @Override
    public void setMobManager(NmsMobWrappedConfigable<MobAPC33SmallGun, MobAPC33ConfigSmallGun> mobManager) {
        this.mobManager = mobManager;
    }

    @Override
    public NmsMobRegisterConfigable<MobAPC33SmallGun, MobAPC33ConfigSmallGun> getRegister() {
        return register;
    }

    public static void setRegister(NmsMobRegisterConfigable<MobAPC33SmallGun, MobAPC33ConfigSmallGun> register) {
        MobAPC33SmallGun.register = register;
    }

    @Override
    public MobAPC33SmallGun getSelfEntity() {
        return this;
    }

    @Override
    protected void u() {
        super.u();
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
    public void a(EnumMoveType enummovetype, Vec3D vec3d) {
        nmsmove(enummovetype, vec3d);
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
