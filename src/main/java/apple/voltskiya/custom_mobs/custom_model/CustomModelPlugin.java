package apple.voltskiya.custom_mobs.custom_model;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CustomModelPlugin extends VoltskiyaModule {
    private static final String YML_FILENAME = "models.yml";
    private static CustomModelPlugin instance;

    public static CustomModelPlugin get() {
        return instance;
    }

    public void saveSchematic(CustomModelGui gui) throws IOException {
        File file = new File(this.getDataFolder(), YML_FILENAME);
        if (!file.exists()) file.createNewFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (!yml.contains("models")) yml.createSection("models");
        @Nullable ConfigurationSection config = yml.getConfigurationSection("models");
        if (config == null) return;
        List<Entity> entities = gui.getEntities();
        final int size = entities.size();
        for (int i = 0; i < size; i++) {
            Entity entity = entities.get(i);
            @NotNull ConfigurationSection c = config.createSection("entity" + i);
            c.set("x", entity.getLocation().getX());
            c.set("y", entity.getLocation().getY());
            c.set("z", entity.getLocation().getZ());
            c.set("facingX", entity.getLocation().getX());
            c.set("facingY", entity.getLocation().getY());
            c.set("facingZ", entity.getLocation().getZ());
            c.set("entityType", entity.getType().name());
            CraftEntity e = (CraftEntity) entity;
            NBTTagCompound nbt = new NBTTagCompound();
            e.getHandle().save(nbt);
            new NBTTagCompound();
            c.set("nbt", nbt.toString());
        }
        yml.save(file);
    }

    @Override
    public void enable() {
        instance = this;
        new CustomModelIO();
    }

    @Override
    public String getName() {
        return "Custom Model";
    }
}
