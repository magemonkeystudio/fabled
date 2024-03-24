package com.promcteam.fabled.task;

import org.bukkit.entity.Entity;

public interface EntityTask<T extends Entity> {
    void apply(T entity);
}
