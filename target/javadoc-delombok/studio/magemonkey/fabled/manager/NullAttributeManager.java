package studio.magemonkey.fabled.manager;

import studio.magemonkey.fabled.dynamic.EffectComponent;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NullAttributeManager implements IAttributeManager {
    @Override
    public Map<String, FabledAttribute> getAttributes() {
        return Map.of();
    }

    @Override
    public FabledAttribute getAttribute(String key) {
        return null;
    }

    @Override
    public List<FabledAttribute> forStat(String key) {
        return List.of();
    }

    @Override
    public List<FabledAttribute> forComponent(EffectComponent component, String key) {
        return List.of();
    }

    @Override
    public Set<String> getKeys() {
        return Set.of();
    }

    @Override
    public Set<String> getLookupKeys() {
        return Set.of();
    }

    @Override
    public String normalize(String key) {
        return key;
    }

    @Override
    public void addByComponent(String key, FabledAttribute fabledAttribute) {}

    @Override
    public void addByStat(String key, FabledAttribute fabledAttribute) {}
}
