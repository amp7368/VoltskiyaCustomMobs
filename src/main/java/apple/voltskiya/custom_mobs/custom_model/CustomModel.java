package apple.voltskiya.custom_mobs.custom_model;

import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagFloat;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
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
        public double x;
        public double y;
        public double z;
        public final double facingX;
        public final double facingY;
        public final double facingZ;
        public final EntityType type;
        public final NBTTagCompound nbt;
        public final Map<String, Object> otherData;

        public CustomEntity(double x, double y, double z, double facingX, double facingY, double facingZ, EntityType type, NBTTagCompound nbt, Map<String, Object> otherData) {
            this.x = x;
            this.y = y;
            this.z = z;
            if (nbt.hasKey("Rotation")) {
                NBTTagList rotation = nbt.getList("Rotation", NBTTagFloat.a.getTypeId()); //float list
                if (rotation.size() == 2) {
                    float yaw = Float.parseFloat(rotation.get(0).asString());
                    float pitch = Float.parseFloat(rotation.get(1).asString());
                    Vector facing = new Location(null, 0, 0, 0, yaw, pitch).getDirection();
                    double facingXZ = Math.atan2(facing.getZ(), facing.getX());
//                            Math.atan2(facingX, facingY);
                    // todo do the y rotation

                    this.facingX = Math.cos(facingXZ);
                    this.facingZ = Math.sin(facingXZ);
                    this.facingY = facing.getY();
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
    }
}
