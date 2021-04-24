package apple.voltskiya.custom_mobs.custom_model;

import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagFloat;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

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
        public double rotationX;
        public double rotationY;
        public double rotationZ;

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
//                    l.setDirection(new Vector(facingX, facingY, facingZ));
                    l.setYaw( yaw);
                    l.setPitch(pitch);
//                    Vector facing = new Location(null, 0, 0, 0, yaw, pitch).getDirection();
//                    double facingXZ = Math.atan2(facing.getZ(), facing.getX());
//
//                    this.facingX = (this.rotationX = Math.cos(facingXZ)) + facingX;
//                    this.facingY = (this.rotationY = facing.getY()) + facingY;
//                    this.facingZ = (this.rotationZ = Math.sin(facingXZ)) + facingZ;
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
    }
}
