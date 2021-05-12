package apple.voltskiya.custom_mobs.dungeon.product;

public class SpawnDungeonOptions {
    private boolean spawnMobs = false;
    private boolean spawnBlocks = false;
    private boolean spawnChests = false;

    public void setSpawnAll() {
        this.spawnMobs = true;
        this.spawnBlocks = true;
        this.spawnChests = true;
    }

    public boolean isSpawnMobs() {
        return spawnMobs;
    }

    public void setSpawnMobs(boolean spawnMobs) {
        this.spawnMobs = spawnMobs;
    }

    public boolean isSpawnBlocks() {
        return spawnBlocks;
    }

    public void setSpawnBlocks(boolean spawnBlocks) {
        this.spawnBlocks = spawnBlocks;
    }

    public boolean isSpawnChests() {
        return spawnChests;
    }

    public void setSpawnChests(boolean spawnChests) {
        this.spawnChests = spawnChests;
    }
}
