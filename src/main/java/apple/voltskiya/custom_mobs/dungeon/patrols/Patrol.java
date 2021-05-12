package apple.voltskiya.custom_mobs.dungeon.patrols;

import apple.voltskiya.custom_mobs.dungeon.gui.patrol.PatrolGui;
import apple.voltskiya.custom_mobs.dungeon.product.Dungeon;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonMobScanned;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Patrol {
    private final String name;
    private final Dungeon dungeon;
    private final List<PatrolStep> steps = new ArrayList<>();
    private int currentStep = 0;

    public Patrol(Dungeon dungeon, DungeonMobScanned mob) {
        this.dungeon = dungeon;
        this.name = mob.getUUID();
        this.dungeon.getScanned().addPatrol(this);
    }

    public void openGui(Player player) {
        player.openInventory(new PatrolGui(player, this).getInventory());
    }

    public void addStep(Block clickedBlock) {
        steps.add(new PatrolStep(clickedBlock));
        if (currentStep == steps.size() - 2) currentStep++;
    }

    public String getName() {
        return name;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public String getUID() {
        return dungeon.getName() + ":" + name;
    }

    @Nullable
    public PatrolStep getCurrentStep() {
        if (steps.isEmpty()) return null;
        currentStep %= steps.size();
        return steps.get(currentStep);
    }

    public void currenStepIndexAdd(int indexToAdd) {
        currentStep += indexToAdd;
        currentStep = Math.max(currentStep, 0);
        currentStep = Math.min(currentStep, steps.size());
    }
}
