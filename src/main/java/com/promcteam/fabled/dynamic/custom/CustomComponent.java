package com.promcteam.fabled.dynamic.custom;

import com.promcteam.fabled.dynamic.ComponentType;

import java.util.List;

/**
 * Fabled © 2023
 * com.promcteam.fabled.dynamic.custom.CustomComponent
 */
public interface CustomComponent {

    /**
     * @return unique key for your component (what is used in skill .yml files)
     */
    String getKey();

    /**
     * @return type of the component, describing it's general purpose
     * @see ComponentType
     */
    ComponentType getType();

    /**
     * @return A description for your trigger that's shown in the editor
     */
    String getDescription();

    /**
     * @return settings to show in the editor
     */
    List<EditorOption> getOptions();

    default String getDisplayName() {
        return getKey();
    }

    default boolean isContainer() {
        return getType() != ComponentType.MECHANIC;
    }
}
