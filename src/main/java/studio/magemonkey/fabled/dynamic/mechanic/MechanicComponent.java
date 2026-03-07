package studio.magemonkey.fabled.dynamic.mechanic;

import studio.magemonkey.fabled.dynamic.ComponentType;
import studio.magemonkey.fabled.dynamic.EffectComponent;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent
 */
public abstract class MechanicComponent extends EffectComponent {
    @Override
    public ComponentType getType() {
        return ComponentType.MECHANIC;
    }
}
