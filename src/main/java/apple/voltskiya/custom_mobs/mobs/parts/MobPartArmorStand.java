package apple.voltskiya.custom_mobs.mobs.parts;

import apple.voltskiya.custom_mobs.custom_model.CustomModel;
import apple.voltskiya.custom_mobs.mobs.NmsMobsPlugin;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.utils.NbtConstants;
import apple.voltskiya.custom_mobs.mobs.utils.UtilsAttribute;
import apple.voltskiya.custom_mobs.util.EntityLocation;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MobPartArmorStand extends EntityArmorStand implements MobPartChild {
    private static final String REGISTERED_NAME = "mobpart_armorstand";
    private static final double DEATH_PARTICLES_DENSITY = 0.001d;
    private static EntityTypes<MobPartArmorStand> entityTypes;

    private MobPartMother mainMob;
    private NmsModelEntityConfig entityConfig;
    private EntityLocation entityLocation;
    private final List<ItemStack> armorItems = new ArrayList<>() {{
        for (EnumItemSlot slot : EnumItemSlot.values()) {
            if (slot.a() == EnumItemSlot.Function.ARMOR) add(ItemStack.b);
        }
    }};
    private final List<ItemStack> handItems = new ArrayList<>() {{
        for (EnumItemSlot slot : EnumItemSlot.values())
            if (slot.a() == EnumItemSlot.Function.HAND) add(ItemStack.b);
    }};

    public MobPartArmorStand(EntityTypes<MobPartArmorStand> mainMob, World world) {
        super(EntityTypes.ARMOR_STAND, world);
        UtilsAttribute.fillAttributes(this.getAttributeMap(), getAttributeProvider());
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
    }

    public static void initialize() {
        EntityTypes.Builder<MobPartArmorStand> entitytypesBuilder = EntityTypes.Builder.a(MobPartArmorStand::new, EnumCreatureType.MONSTER);

        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DataConverterTypes.ENTITY_TREE);


        // copy the zombie type to the warped gremlin type
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        final Type<?> zombieType = types.get("minecraft:armor_stand");
        types.put("minecraft:" + REGISTERED_NAME, zombieType);

        // build it
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);

        // log it
        NmsMobsPlugin.get().log(Level.INFO, "registered " + REGISTERED_NAME);
    }

    public static MobPartArmorStand spawnMobPart(MobPartMother mother, NmsModelEntityConfig config) {
        final World world = mother.entity.getWorld();
        final MobPartArmorStand bodyPart = entityTypes.a(world);
        if (bodyPart == null) return null;
        bodyPart.addScoreboardTag(mother.scoreboardTag);
        bodyPart.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        bodyPart.prepare(mother, config);
        world.addEntity(bodyPart);
        return bodyPart;
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType(NbtConstants.ARMOR_ITEMS, 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList(NbtConstants.ARMOR_ITEMS, 10);
            for (int i = 0; i < nbttaglist.size(); i++) {
                this.setSlot(EnumItemSlot.a(EnumItemSlot.Function.ARMOR, i), ItemStack.a(nbttaglist.getCompound(i)));
            }
        }
        if (nbttagcompound.hasKeyOfType(NbtConstants.HAND_ITEMS, 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList(NbtConstants.HAND_ITEMS, 10);
            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.setSlot(EnumItemSlot.a(EnumItemSlot.Function.HAND, i), ItemStack.a(nbttaglist.getCompound(i)));
            }
        }
        if (this.entityConfig == null)
            this.entityConfig = new NmsModelEntityConfig(nbttagcompound.getCompound(NbtConstants.ENTITY_LOCATION_RELATIVE_CONFIG));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);

        final NBTTagList armor = new NBTTagList();
        for (ItemStack armorItem : this.armorItems) armor.add(armorItem.save(new NBTTagCompound()));
        nbttagcompound.set(NbtConstants.ARMOR_ITEMS, armor);

        final NBTTagList hands = new NBTTagList();
        for (ItemStack handItem : this.handItems) hands.add(handItem.save(new NBTTagCompound()));
        nbttagcompound.set(NbtConstants.HAND_ITEMS, hands);

        nbttagcompound.set(NbtConstants.ENTITY_LOCATION_RELATIVE_CONFIG, entityConfig.toNbt());
    }

    /**
     * @return EnumMonsterType.ARTHROPOD || EnumMonsterType.ILLAGER || ...
     */
    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ILLAGER;
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return EntityTypes.ARMOR_STAND;
    }


    /**
     * @return the default attributeMap
     */
    private static AttributeProvider getAttributeProvider() {
        return AttributeDefaults.a(EntityTypes.ARMOR_STAND);
    }

    @Override
    public EnumMainHand getMainHand() {
        return EnumMainHand.RIGHT;
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
            yaw1 = this.mainMob.entity.yaw + this.mainMob.entity.getHeadRotation();
        } else {
            yaw1 = this.mainMob.entity.lastYaw;
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
        return damagesource != DamageSource.STUCK && this.mainMob.entity.damageEntity(damagesource, f); // send this to the main mob
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
                    minX + random.nextDouble() * x,
                    minY + random.nextDouble() * y,
                    minZ + random.nextDouble() * z,
                    1
            );
        }
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
        Vector3f pose = this.headPose;
        f1 += pose.getX();
        f2 += pose.getY();
        f3 += pose.getZ();
        f1 %= 360;
        f2 %= 360;
        f3 %= 360;
        this.setHeadPose(new Vector3f(f1, f2, f3));
    }

    @Override
    public Entity getThisEntity() {
        return this;
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        System.out.println("whoa");
        return this.mainMob.entity.a(entityhuman, enumhand);
    }

    @Override
    public MobPartChild remake(WorldServer worldserver, MobPartMother parent) {

        final NBTTagCompound oldNbt = this.save(new NBTTagCompound());
        @Nullable MobPartArmorStand newMobPart = entityTypes.a(worldserver);
        if (newMobPart != null) {
            newMobPart.prepare(parent, this.entityConfig);
            newMobPart.load(oldNbt);
            newMobPart.setLocation(parent.entity.locX(), parent.entity.locY(), parent.entity.locZ(), parent.entity.yaw, parent.entity.pitch);
            newMobPart.resetPortalCooldown();
            (worldserver).addEntity(newMobPart);
            this.bN();
            return newMobPart;
        }
        this.bN();
        return null;
    }


    @Override
    public Iterable<ItemStack> getArmorItems() {
        return armorItems;
    }

    @Override
    public ItemStack getEquipment(EnumItemSlot enumItemSlot) {
        EnumItemSlot.Function slotType = enumItemSlot.a();
        int subSlot = enumItemSlot.b();
        switch (slotType) {
            case ARMOR:
                return armorItems.get(subSlot);
            case HAND:
                return handItems.get(subSlot);
        }
        return ItemStack.b;
    }

    @Override
    public void setSlot(EnumItemSlot enumItemSlot, ItemStack itemStack) {
        EnumItemSlot.Function slotType = enumItemSlot.a();
        int subSlot = enumItemSlot.b();
        switch (slotType) {
            case ARMOR:
                if (subSlot >= armorItems.size()) return;
                armorItems.set(subSlot, itemStack);
                break;
            case HAND:
                if (subSlot >= handItems.size()) return;
                handItems.set(subSlot, itemStack);
                break;
        }
    }

}
