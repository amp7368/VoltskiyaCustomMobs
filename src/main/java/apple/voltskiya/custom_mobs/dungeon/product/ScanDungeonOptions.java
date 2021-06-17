package apple.voltskiya.custom_mobs.dungeon.product;

public class ScanDungeonOptions {
    private final Boolean scanBlocks;
    private final Boolean scanMobs;
    private final Boolean scanChests;

    public ScanDungeonOptions(Boolean scanBlocks, Boolean scanMobs, Boolean scanChests) {
        this.scanBlocks = scanBlocks == null || scanBlocks;
        this.scanMobs = scanMobs == null || scanMobs;
        this.scanChests = scanChests == null || scanChests;
    }

    public boolean shouldScanAll() {
        return shouldScanBlocks() && shouldScanMobs() && shouldScanChests();
    }

    public boolean shouldScanBlocks() {
        return scanBlocks != null && scanBlocks;
    }

    public boolean shouldScanMobs() {
        return scanMobs != null && scanMobs;
    }

    public boolean shouldScanChests() {
        return scanChests != null && scanChests;
    }
}
