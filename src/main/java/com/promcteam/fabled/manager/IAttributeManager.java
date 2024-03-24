package com.promcteam.fabled.manager;

import com.promcteam.fabled.dynamic.EffectComponent;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IAttributeManager {
    Map<String, ProAttribute> getAttributes();

    /**
     * Retrieves an attribute template
     *
     * @param key attribute key
     * @return template for the attribute
     */
    ProAttribute getAttribute(String key);

    List<ProAttribute> forStat(final String key);

    List<ProAttribute> forComponent(final EffectComponent component, final String key);

    /**
     * Retrieves the available attribute keys. This
     * does not include display names for attributes.
     *
     * @return set of available attribute keys
     */
    Set<String> getKeys();

    /**
     * Retrieves the available attribute keys including
     * both display names and config keys.
     *
     * @return display name and config keys for attributes
     */
    Set<String> getLookupKeys();

    /**
     * Normalizes a config key or name into the config key
     * for a unified identifier to store stats under.
     *
     * @param key key to normalize
     * @return config key
     */
    String normalize(String key);

    void addByComponent(String key, ProAttribute proAttribute);

    void addByStat(String key, ProAttribute proAttribute);
}
