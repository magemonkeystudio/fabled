package com.promcteam.fabled.api.player;

import lombok.Data;

@Data
public class SkillAttribute {
    private final String name;
    private       int    invested;
    private       int    max;

    public final String getKey() {
        return name.toLowerCase();
    }
}
