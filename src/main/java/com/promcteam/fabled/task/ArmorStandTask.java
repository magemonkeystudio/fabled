package com.promcteam.fabled.task;

import com.promcteam.fabled.api.armorstand.ArmorStandManager;
import com.promcteam.fabled.thread.RepeatThreadTask;

public class ArmorStandTask extends RepeatThreadTask {

    public ArmorStandTask() {super(1, 1);}

    @Override
    public void run() {
        ArmorStandManager.tick();
    }
}
