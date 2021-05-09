package apple.voltskiya.custom_mobs.pathfinders.goal_selector;

import net.minecraft.server.v1_16_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_16_R3.PathfinderGoalWrapped;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

public class UtilsPathfinderGoalSelector {
    @NotNull
    public static Collection<PathfinderGoalWrapped> remove(PathfinderGoalSelector selector, Class<?>... typeToRemove) {
        try {
            List<Class<?>> typeToRemoveList = Arrays.asList(typeToRemove);
            Field pathfindersField = selector.getClass().getDeclaredField("d");
            pathfindersField.setAccessible(true);
            Collection<PathfinderGoalWrapped> pathfinders = new ArrayList<>((Collection<PathfinderGoalWrapped>) pathfindersField.get(selector));
            pathfinders.stream().filter((var1) -> typeToRemoveList.contains(var1.j().getClass())).forEach(pathfinderGoalWrapped -> selector.a(pathfinderGoalWrapped.j()));
            return pathfinders;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static void add(PathfinderGoalSelector selector, Collection<PathfinderGoalWrapped> pathfindersToAdd) {
        for (PathfinderGoalWrapped pathfinderToAdd : pathfindersToAdd) {
            selector.a(pathfinderToAdd.h(), pathfinderToAdd.j());
        }
    }
}
