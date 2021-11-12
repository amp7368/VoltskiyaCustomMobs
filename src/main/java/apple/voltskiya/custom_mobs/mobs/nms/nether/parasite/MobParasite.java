package apple.voltskiya.custom_mobs.mobs.nms.nether.parasite;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.entity.DecodeEnumMonsterType;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig.ModelConfigName;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelEntityConfig;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.nms.utils.UtilsPacket;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalCraveBlock;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftZombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MobParasite extends EntityZombie implements RegisteredCustomMob {
    public static final ModelConfigName REGISTERED_MODEL = ModelConfigName.PARASITE;
    public static final String REGISTERED_NAME = REGISTERED_MODEL.getName();
    private static EntityTypes<MobParasite> entityTypes;
    private List<MobPartChild> children = null;
    private static NmsModelEntityConfig selfModel;
    private AttributeMapBase attributeMap = null;

    /**
     * registers the NetherParasite as an entity
     */
    public static void initialize() {
        Map<? super Object, Type<?>> types = NmsMobRegister.getMinecraftTypes();
        final Type<?> zombieType = types.get("minecraft:zombie");
        types.put(registeredNameId(), zombieType);

        // build it
        EntityTypes.Builder<MobParasite> entitytypesBuilder = EntityTypes.Builder.a(MobParasite::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(DecodeIRegistry.getEntityType(), DecodeIRegistry.getEntityType().getId(DecodeEntityTypes.ZOMBIE), REGISTERED_NAME, entityTypes); // this is good

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
        final NmsModelConfig model = NmsModelConfig.parts(REGISTERED_MODEL);
        selfModel = model.mainPart();

    }

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobParasite(EntityTypes<MobParasite> entityTypes, World world) {
        super(DecodeEntityTypes.ZOMBIE, world);
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return entityTypes;
    }


    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     * @param velocity the velocity to set the mob with
     */
    public static void spawn(Location location, NBTTagCompound oldNbt, Vec3D velocity) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobParasite parasite = new MobParasite(entityTypes, world.getHandle());
        parasite.prepare(location, oldNbt);
        parasite.addChildren();
        parasite.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        parasite.addScoreboardTag(REGISTERED_NAME);
        if (velocity != null)
            parasite.setMot(velocity);
        world.getHandle().addEntity(parasite);
    }

    private void prepare(Location location, NBTTagCompound oldNbt) {
        final NBTTagCompound newNbt = selfModel.getEntity().nbt;
        final NBTTagCompound mergedNbt = oldNbt == null ? newNbt : oldNbt.a(newNbt);
        this.load(mergedNbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        spawn(location, ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound()), null);
        event.setCancelled(true);
    }

    private void addChildren() {
        if (this.children != null) {
            for (MobPartChild child : children) {
                child.die();
            }
        }
        this.children = MobPartMother.getChildren(this.getUniqueID(), this, selfModel, REGISTERED_MODEL);
    }


    @Override
    public void load(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("id", registeredNameId());
        super.load(nbttagcompound);
        final boolean invisible = nbttagcompound.getBoolean("Invisible");
        ((CraftZombie) this.getBukkitEntity()).setInvisible(invisible);
        this.setInvisible(invisible);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        NBTTagCompound data = super.save(nbttagcompound);
        data.setBoolean("Invisible", this.isInvisible());
        data.setString("id", registeredNameId());
        return data;
    }

    @NotNull
    private static String registeredNameId() {
        return "minecraft" + ":" + REGISTERED_NAME;
    }


    @Override
    public AttributeMapBase getAttributeMap() {
        return this.attributeMap == null ? this.attributeMap = new AttributeMapBase(getAttributeProvider()) : this.attributeMap;
    }

    /**
     * @return the default attributeMap
     */
    private static AttributeProvider getAttributeProvider() {
        return AttributeDefaults.a(DecodeEntityTypes.ZOMBIE);
    }

    @Override
    protected void initPathfinder() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            // if this is the overworld, do the overworld AI, otherwise, get to the overworld
            if (this.getWorld().getDimensionManager().isNatural()) {
                this.initPathfinderOverworld();
            } else {
                this.initPathfinderOtherWorld();
            }
        }, 5 * 10);
    }

    private void initPathfinderOverworld() {
        final int chanceToCheck = 20;
        int chanceToCheckPlayer = 60;
        PathfinderGoalSelector targetSelector = DecodeEntity.getTargetSelector(this);
        targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityAnimal.class, chanceToCheck, true, false, (entityLiving) -> !entityLiving.getScoreboardTags().contains(MobInfected.PARASITE_INFECTED_TAG)));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, chanceToCheckPlayer, true, false, null));
        double speed = 1.2;
        PathfinderGoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        goalSelector.a(1, new PathfinderGoalMeleeAttack(this, speed, true));
        goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.003F));
    }

    @Override
    public boolean attackEntity(Entity e) {
        if (e instanceof EntityPlayer) return super.attackEntity(e);
        if (e instanceof EntityCreature) {
            if (!e.getScoreboardTags().contains(MobInfected.PARASITE_INFECTED_TAG)) {
                EntityCreature entity = (EntityCreature) e;
                new MobInfected(entity);
                die();
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> this.setGoalTarget(null), 40);
        return false;
    }

    private void initPathfinderOtherWorld() {
        // new PathfinderGoalRandomStrollLand(entityToMove, speed, chanceToActivate)
        PathfinderGoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        goalSelector.a(0, new PathfinderGoalFloat(this));
        goalSelector.a(1, new PathfinderGoalCraveBlock(this, Collections.singletonList(org.bukkit.Material.NETHER_PORTAL), 512, 20, 2, 1.0));
        goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.003F));
    }

    /**
     * @return EnumMonsterType.ARTHROPOD || EnumMonsterType.ILLAGER || ...
     */
    @Override
    public EnumMonsterType getMonsterType() {
        return DecodeEnumMonsterType.ARTHROPOD.encode();
    }

    @Override
    public void setOnFire(int i) {

    }

    @Override
    public void a(RemovalReason removalReason) {
        super.a(removalReason);
        if (children != null)
            for (MobPartChild child : children) {
                child.die();
            }
    }


    /**
     * move and move children
     *
     * @param enummovetype gift to super
     * @param vec3d        gift to super
     */
    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
        List<Packet<?>> packetsToSend = new ArrayList<>();
        if (this.children == null) {
            this.addChildren();
        }
        for (MobPartChild child : children) {
            packetsToSend.add(child.moveFromMother(true));
        }
        UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend, this.getBukkitEntity().getLocation());
    }


    /**
     * change worlds
     */
    @Override
    public @Nullable
    Entity b(WorldServer worldserver) {
        final Entity result = super.b(worldserver);
        if (result instanceof MobParasite) {
            for (MobPartChild child : children) {
                child.die();
            }
            ((MobParasite) result).addChildren();
        }
        return result;
    }
}
