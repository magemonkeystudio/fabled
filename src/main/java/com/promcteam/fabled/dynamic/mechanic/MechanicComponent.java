package com.promcteam.fabled.dynamic.mechanic;

import com.promcteam.fabled.dynamic.ComponentType;
import com.promcteam.fabled.dynamic.EffectComponent;

/**
 * Fabled © 2023
 * com.promcteam.fabled.dynamic.mechanic.MechanicComponent
 */
public abstract class MechanicComponent extends EffectComponent {
    @Override
    public ComponentType getType() {
        return ComponentType.MECHANIC;
    }
}
