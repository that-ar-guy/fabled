package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.event.player.PlayerFishEvent;
import studio.magemonkey.fabled.api.Settings;

public class FishingReelTrigger extends FishingTrigger {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "FISHING_REEL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(PlayerFishEvent event, int level, Settings settings) {

        if (event.getState() == PlayerFishEvent.State.REEL_IN) {
            return true;
        }
        return false;
    }

}
