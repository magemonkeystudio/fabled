package com.sucy.skill.manager;

import lombok.AllArgsConstructor;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.dynamic.EffectComponent;
import studio.magemonkey.fabled.manager.FabledAttribute;
import studio.magemonkey.fabled.manager.IAttributeManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles loading and accessing individual
 * attributes from the configuration.
 * This class is strictly a wrapper for {@link IAttributeManager} and will be removed in the future.
 * This should only be used by legacy plugins lacking modern support for Fabled
 *
 * @deprecated Use {@link IAttributeManager} instead from {@link Fabled#getAttributesManager()}
 */
@AllArgsConstructor
@Deprecated(forRemoval = true)
public class AttributeManager implements IAttributeManager {
    private final IAttributeManager attributeManager;

    /**
     * @deprecated Use {@link IAttributeManager#getAttributes()} instead
     * @return map of attributes
     */
    @Override
    @Deprecated(forRemoval = true)
    public Map<String, FabledAttribute> getAttributes() {
        return attributeManager.getAttributes();
    }

    /**
     * @deprecated Use {@link IAttributeManager#getAttribute(String)} instead
     * @param key attribute key
     * @return template for the attribute
     */
    @Override
    @Deprecated(forRemoval = true)
    public FabledAttribute getAttribute(String key) {
        return attributeManager.getAttribute(key);
    }

    /**
     * @deprecated Use {@link IAttributeManager#forStat(String)} instead
     * @param key key to get attributes for
     * @return list of attributes for the stat
     */
    @Override
    @Deprecated(forRemoval = true)
    public List<FabledAttribute> forStat(String key) {
        return attributeManager.forStat(key);
    }

    /**
     * @deprecated Use {@link IAttributeManager#forComponent(EffectComponent, String)} instead
     * @param component component to get attributes for
     * @param key key to get attributes for
     * @return list of attributes for the component
     */
    @Override
    @Deprecated(forRemoval = true)
    public List<FabledAttribute> forComponent(EffectComponent component, String key) {
        return attributeManager.forComponent(component, key);
    }

    /**
     * @deprecated Use {@link IAttributeManager#getKeys()} instead
     * @return set of available attribute keys
     */
    @Override
    @Deprecated(forRemoval = true)
    public Set<String> getKeys() {
        return attributeManager.getKeys();
    }

    /**
     * @deprecated Use {@link IAttributeManager#getLookupKeys()} instead
     * @return display name and config keys for attributes
     */
    @Override
    @Deprecated(forRemoval = true)
    public Set<String> getLookupKeys() {
        return attributeManager.getLookupKeys();
    }

    /**
     * @deprecated Use {@link IAttributeManager#normalize(String)} instead
     * @param key key to normalize
     * @return config key
     */
    @Override
    @Deprecated(forRemoval = true)
    public String normalize(String key) {
        return attributeManager.normalize(key);
    }

    /**
     * @deprecated Use {@link IAttributeManager#addByComponent(String, FabledAttribute)} instead
     * @param key key to add by
     * @param fabledAttribute attribute to add
     */
    @Override
    @Deprecated(forRemoval = true)
    public void addByComponent(String key, FabledAttribute fabledAttribute) {
        attributeManager.addByComponent(key, fabledAttribute);
    }

    /**
     * @deprecated Use {@link IAttributeManager#addByStat(String, FabledAttribute)} instead
     * @param key key to add by
     * @param fabledAttribute attribute to add
     */
    @Override
    @Deprecated(forRemoval = true)
    public void addByStat(String key, FabledAttribute fabledAttribute) {
        attributeManager.addByStat(key, fabledAttribute);
    }
}
