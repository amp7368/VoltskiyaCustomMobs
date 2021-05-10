package apple.voltskiya.custom_mobs.dungeon;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class DungeonMobInfo {
    public NBTTagCompound nbt;
    public EntityTypes<?> mobType;

    public DungeonMobInfo(Entity entity) {
        nbt = ((CraftEntity) entity).getHandle().save(new NBTTagCompound());
        mobType = ((CraftEntity) entity).getHandle().getEntityType();

    }

    public Material getSpawnEgg() {
        return Material.BAT_SPAWN_EGG;
    }

    public String getName() {
        String name = nbt.getString("CustomName");
        return name.isEmpty() ? "No name" : name;
    }
}
