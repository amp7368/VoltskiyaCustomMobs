package apple.voltskiya.custom_mobs.custom_model;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomModelPlugin extends VoltskiyaModule {
    private static final String YML_FILENAME = "models.yml";
    private static CustomModelPlugin instance;
    private static final Set<String> normalData = new HashSet<>() {{
        add("x");
        add("y");
        add("z");
        add("facingX");
        add("facingY");
        add("facingZ");
        add("entityType");
        add("nbt");
    }};

    public static CustomModelPlugin get() {
        return instance;
    }

    public void rotate(double rotation) throws IOException {
        rotation = Math.toRadians(rotation);
        CustomModel model = loadSchematic();
        File file = new File(this.getDataFolder(), YML_FILENAME);
        if (!file.exists()) file.createNewFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (!yml.contains("models")) yml.createSection("models");
        @Nullable ConfigurationSection config = yml.getConfigurationSection("models");
        if (config == null) return;
        List<CustomModel.CustomEntity> entities = model.entities;
        final int size = entities.size();
        // convert the center.getDirection() vector to <1,0,0> and with it, all of the entities
        for (int i = 0; i < size; i++) {
            CustomModel.CustomEntity entity = entities.get(i);

            double x = entity.x;
            double z = entity.z;
            double radius = Math.sqrt(z * z + x * x);
            double angle = Math.atan2(z, x) - rotation;
            double xNew = Math.cos(angle) * radius;
            double zNew = Math.sin(angle) * radius;
            @NotNull Vector facingNew = VectorUtils.rotateVector(x, z, entity.facingX, entity.facingZ, entity.facingY, -rotation);
            @NotNull ConfigurationSection c = config.createSection("entity" + i);
            c.set("x", xNew);
            c.set("y", entity.y);
            c.set("z", zNew);
            c.set("facingX", facingNew.getX());
            c.set("facingY", facingNew.getY());
            c.set("facingZ", facingNew.getZ());
            c.set("entityType", entity.type.name());
            NBTTagCompound nbt = entity.nbt;
            c.set("nbt", nbt.toString());
        }
        yml.save(file);
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
        Location center = gui.getCenter();
        // convert the center.getDirection() vector to <1,0,0> and with it, all of the entities
        double standardizingAngle = Math.atan2(center.getDirection().getZ(), center.getDirection().getX());
        for (int i = 0; i < size; i++) {
            Entity entity = entities.get(i);
            final Location location = entity.getLocation();

            double x = location.getX() - center.getX();
            double z = location.getZ() - center.getZ();
            double radius = Math.sqrt(z * z + x * x);
            double angle = Math.atan2(z, x) - standardizingAngle;
            double xNew = Math.cos(angle) * radius;
            double zNew = Math.sin(angle) * radius;
            @NotNull Vector facingNew = VectorUtils.rotateVector(x, z, location.getDirection().getX(), location.getDirection().getZ(), location.getDirection().getY(), -standardizingAngle);
            @NotNull ConfigurationSection c = config.createSection("entity" + i);
            c.set("x", xNew);
            c.set("y", location.getY() - center.getY());
            c.set("z", zNew);
            c.set("facingX", facingNew.getX());
            c.set("facingY", facingNew.getY());
            c.set("facingZ", facingNew.getZ());
            c.set("entityType", entity.getType().name());
            CraftEntity e = (CraftEntity) entity;
            NBTTagCompound nbt = new NBTTagCompound();
            e.getHandle().save(nbt);
            c.set("nbt", nbt.toString());
        }
        yml.save(file);
    }

    @Nullable
    public CustomModel loadSchematic(File file) {
        if (!file.exists()) return null;
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection config = yml.getConfigurationSection("models");
        if (config == null) return null;
        CustomModel model = new CustomModel();
        for (String key : config.getKeys(false)) {
            ConfigurationSection entity = config.getConfigurationSection(key);
            if (entity == null) continue;
            double x = entity.getDouble("x");
            double y = entity.getDouble("y");
            double z = entity.getDouble("z");
            double facingX = entity.getDouble("facingX");
            double facingY = entity.getDouble("facingY");
            double facingZ = entity.getDouble("facingZ");
            EntityType type = EntityType.valueOf(entity.getString("entityType"));
            NBTTagCompound nbt;
            try {
                nbt = MojangsonParser.parse(entity.getString("nbt"));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                continue;
            }
            nbt.remove("UUID");
            Map<String, Object> otherData = new HashMap<>();
            for (String otherKey : entity.getKeys(false)) {
                if (!normalData.contains(otherKey)) {
                    otherData.put(otherKey, entity.get(otherKey));
                }
            }
            model.add(new CustomModel.CustomEntity(x, y, z, facingX, facingY, facingZ, type, nbt, otherData));
        }
        return model;
    }

    public void adjustSchematic(double x, double y, double z) {
        File file = new File(this.getDataFolder(), YML_FILENAME);
        @Nullable CustomModel schematic = loadSchematic(file);
        if (schematic != null) {
            schematic.adjust(x, y, z);
        } else {
            return;
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (!yml.contains("models")) yml.createSection("models");
        @Nullable ConfigurationSection config = yml.getConfigurationSection("models");
        if (config == null) return;
        List<CustomModel.CustomEntity> entities = schematic.entities;
        final int size = entities.size();
        for (int i = 0; i < size; i++) {
            CustomModel.CustomEntity entity = entities.get(i);
            ConfigurationSection c = config.getConfigurationSection("entity" + i);
            if (c != null) {
                c.set("x", entity.x);
                c.set("y", entity.y);
                c.set("z", entity.z);
                c.set("facingX", entity.facingX);
                c.set("facingY", entity.facingY);
                c.set("facingZ", entity.facingZ);
            }
        }
        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public CustomModel loadSchematic() {
        File file = new File(this.getDataFolder(), YML_FILENAME);
        @Nullable CustomModel schematic = loadSchematic(file);
        return schematic;
    }
}
