package apple.voltskiya.custom_mobs.ai;

import apple.voltskiya.custom_mobs.ai.bowlike.BowlikeMoveManager;
import com.voltskiya.lib.AbstractModule;

public class AiModule extends AbstractModule {

    @Override
    public void enable() {
        new BowlikeMoveManager();
    }

    @Override
    public String getName() {
        return "Ai";
    }
}
