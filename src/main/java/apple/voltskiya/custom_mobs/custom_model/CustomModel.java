package apple.voltskiya.custom_mobs.custom_model;

import apple.voltskiya.custom_mobs.mobs.utils.NbtConstants;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagFloat;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomModel {
    public List<CustomEntity> entities = new ArrayList<>();

    public void add(CustomEntity customEntity) {
        this.entities.add(customEntity);
    }

    public void adjust(double x, double y, double z) {
        for (CustomEntity e : entities) {
            e.x += x;
            e.y += y;
            e.z += z;
        }
    }

    public static class CustomEntity {
        public final String nameInYml;
        public double x;
        public double y;
        public double z;
        public final double facingX;
        public final double facingY;
        public final double facingZ;
        public final EntityType type;
        public final NBTTagCompound nbt;
        public final Map<String, Object> otherData;

        public CustomEntity(String nameInYml, double x, double y, double z, double facingX, double facingY, double facingZ, EntityType type, NBTTagCompound nbt, Map<String, Object> otherData) {
            this.nameInYml = nameInYml;
            this.x = x;
            this.y = y;
            this.z = z;
            if (nbt.hasKey("Rotation")) {
                NBTTagList rotation = nbt.getList("Rotation", NBTTagFloat.a.getTypeId()); //float list
                if (rotation.size() == 2) {
                    float yaw = Float.parseFloat(rotation.get(0).asString());
                    float pitch = Float.parseFloat(rotation.get(1).asString());

                    Location l = new Location(null, 0, 0, 0);
                    l.setYaw(yaw);
                    l.setPitch(pitch);
                    this.facingX = l.getDirection().getX();
                    this.facingY = l.getDirection().getY();
                    this.facingZ = l.getDirection().getZ();
                    nbt.remove("Rotation");
                } else {
                    this.facingX = facingX;
                    this.facingY = facingY;
                    this.facingZ = facingZ;
                }
            } else {
                this.facingX = facingX;
                this.facingY = facingY;
                this.facingZ = facingZ;
            }
            this.type = type;
            this.nbt = nbt;
            this.nbt.remove("UUID");
            this.otherData = otherData;
        }

        public CustomEntity(NBTTagCompound nbt) {
            this.nameInYml = nbt.getString(NbtConstants.EntityLocationRelative.NAME_IN_YML);
            this.x = nbt.getDouble(NbtConstants.EntityLocationRelative.X_OFFSET);
            this.y = nbt.getDouble(NbtConstants.EntityLocationRelative.Y_OFFSET);
            this.z = nbt.getDouble(NbtConstants.EntityLocationRelative.Z_OFFSET);
            this.facingX = nbt.getDouble(NbtConstants.EntityLocationRelative.X_FACING);
            this.facingY = nbt.getDouble(NbtConstants.EntityLocationRelative.Y_FACING);
            this.facingZ = nbt.getDouble(NbtConstants.EntityLocationRelative.Z_FACING);
            this.otherData = new HashMap<>();
            final NBTTagCompound otherData = nbt.getCompound(NbtConstants.EntityLocationRelative.OTHER_DATA);
            for (String key : otherData.getKeys()) {
                this.otherData.put(key, NbtConstants.toObject(otherData.getString(key)));
            }
            this.type = null;
            this.nbt = new NBTTagCompound();
        }

        public NBTBase toNbt() {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString(NbtConstants.EntityLocationRelative.NAME_IN_YML, nameInYml);
            nbt.setDouble(NbtConstants.EntityLocationRelative.X_OFFSET, x);
            nbt.setDouble(NbtConstants.EntityLocationRelative.Y_OFFSET, y);
            nbt.setDouble(NbtConstants.EntityLocationRelative.Z_OFFSET, z);
            nbt.setDouble(NbtConstants.EntityLocationRelative.X_FACING, facingX);
            nbt.setDouble(NbtConstants.EntityLocationRelative.Y_FACING, facingY);
            nbt.setDouble(NbtConstants.EntityLocationRelative.Z_FACING, facingZ);
            NBTTagCompound otherDataNbt = new NBTTagCompound();
            for (Map.Entry<String, Object> other : otherData.entrySet()) {
                otherDataNbt.setString(other.getKey(), other.getValue().toString());
            }
            nbt.set(NbtConstants.EntityLocationRelative.OTHER_DATA, otherDataNbt);
            return nbt;
        }
    }
}
