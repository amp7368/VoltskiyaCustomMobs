package apple.voltskiya.custom_mobs.custom_model;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.nbt.DecodeNBT;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.utilities.util.VectorUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomModelPlugin extends PluginManagedModule {
    private static final String YML_FILENAME = "model.yml";
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

    @Override
    public void init() {
        instance = this;
    }


    public void saveSchematic(CustomModelGui gui) throws IOException {
        File file = new File(this.getDataFolder(), YML_FILENAME);
        file.delete();
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
            if (entity.getType() == EntityType.PLAYER) continue;
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
            DecodeEntity.save(e.getHandle());
            c.set("nbt", nbt.toString());
        }
        yml.save(file);
    }

    @Nullable
    public CustomModelData loadSchematic(File file) {
        if (!file.exists()) return null;
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection config = yml.getConfigurationSection("models");
        if (config == null) return null;
        CustomModelData model = new CustomModelData();
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
                nbt = DecodeNBT.parse(entity.getString("nbt"));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                continue;
            }
            Map<String, Object> otherData = new HashMap<>();
            for (String otherKey : entity.getKeys(false)) {
                if (!normalData.contains(otherKey)) {
                    otherData.put(otherKey, entity.get(otherKey));
                }
            }
            model.add(new CustomModelDataEntity(key, x, y, z, facingX, facingY, facingZ, type, nbt, otherData));
        }
        return model;
    }

    public void adjustSchematicOffsetXYZ(double x, double y, double z) {
        File file = new File(this.getDataFolder(), YML_FILENAME);
        @Nullable CustomModelData schematic = loadSchematic(file);
        if (schematic != null) {
            schematic.adjust(x, y, z);
        } else {
            return;
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (!yml.contains("models")) yml.createSection("models");
        @Nullable ConfigurationSection config = yml.getConfigurationSection("models");
        if (config == null) return;
        List<CustomModelDataEntity> entities = schematic.entities;
        for (CustomModelDataEntity entity : entities) {
            ConfigurationSection c = config.getConfigurationSection(entity.nameInYml);
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

    public void adjustSchematicRotateXYZ(double rotation, String fileName) throws IOException {
        rotation = Math.toRadians(rotation);
        CustomModelData model = loadSchematic(fileName);
        File file = new File(this.getDataFolder(), fileName);
        if (!file.exists() || model == null) {
            throw new IllegalArgumentException("The provided model file " + fileName + " does not exist");
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (!yml.contains("models")) yml.createSection("models");
        @Nullable ConfigurationSection config = yml.getConfigurationSection("models");
        if (config == null) return;
        List<CustomModelDataEntity> entities = model.entities;
        // convert the center.getDirection() vector to <1,0,0> and with it, all of the entities
        for (CustomModelDataEntity entity : entities) {
            double x = entity.x;
            double z = entity.z;
            double radius = Math.sqrt(z * z + x * x);
            double angle = Math.atan2(z, x) + rotation;
            double xNew = Math.cos(angle) * radius;
            double zNew = Math.sin(angle) * radius;
            @NotNull Vector facingNew = VectorUtils.rotateVector(x, z, entity.facingX, entity.facingZ, entity.facingY, rotation);
            @NotNull ConfigurationSection c = config.createSection(entity.nameInYml);
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


    @Override
    public void enable() {
        new CustomModelIO();
    }

    @Override
    public String getName() {
        return "CustomModel";
    }

    @Nullable
    public CustomModelData loadSchematic(String schematicFile) {
        File file = new File(this.getDataFolder(), schematicFile);
        return loadSchematic(file);
    }

    public void adjustSchematicRotateAll(double rotation, String fileName) throws IOException {
        rotation = Math.toRadians(rotation);
        CustomModelData model = loadSchematic(fileName);
        File file = new File(this.getDataFolder(), fileName);
        if (!file.exists() || model == null) {
            throw new IllegalArgumentException("The provided model file " + fileName + ".yml" + " does not exist");
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (!yml.contains("models")) yml.createSection("models");
        @Nullable ConfigurationSection config = yml.getConfigurationSection("models");
        if (config == null) return;
        List<CustomModelDataEntity> entities = model.entities;
        for (CustomModelDataEntity entity : entities) {
            double x = entity.x;
            double z = entity.z;
            double radius = Math.sqrt(z * z + x * x);
            double angle = Math.atan2(z, x) + rotation;
            double xNew = Math.cos(angle) * radius;
            double zNew = Math.sin(angle) * radius;
            @NotNull Vector facingNew = VectorUtils.rotateVector(entity.facingX, entity.facingZ, entity.facingY, rotation);
            ConfigurationSection c = config.getConfigurationSection(entity.nameInYml);
            if (c == null) c = config.createSection(entity.nameInYml);
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
        file.delete();
        yml.save(file);

    }
}
