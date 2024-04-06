package studio.magemonkey.fabled.task;

import studio.magemonkey.fabled.api.armorstand.ArmorStandManager;
import studio.magemonkey.fabled.thread.RepeatThreadTask;

public class ArmorStandTask extends RepeatThreadTask {

    public ArmorStandTask() {super(1, 1);}

    @Override
    public void run() {
        ArmorStandManager.tick();
    }
}
