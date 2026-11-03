package studio.magemonkey.fabled.task;

import studio.magemonkey.fabled.api.displayentity.DisplayEntityManager;
import studio.magemonkey.fabled.thread.RepeatThreadTask;

public class DisplayEntityTask extends RepeatThreadTask {

    public DisplayEntityTask() {
        super(1, 1);
    }

    @Override
    public void run() {
        DisplayEntityManager.tick();
    }
}
