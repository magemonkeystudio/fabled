package studio.magemonkey.fabled.dynamic.mechanic;

import studio.magemonkey.fabled.dynamic.ComponentType;
import studio.magemonkey.fabled.dynamic.EffectComponent;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent
 */
public abstract class MechanicComponent extends EffectComponent {
    @Override
    public ComponentType getType() {
        return ComponentType.MECHANIC;
    }
}
