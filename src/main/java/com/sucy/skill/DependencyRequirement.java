package com.sucy.skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DependencyRequirement {

    public static final String MIN_CORE_VERSION = "1.0.4-R0.5-SNAPSHOT";

    public static boolean meetsVersion(String requiredVersion, String providedVersion) {
        List<String> required = splitVersion(requiredVersion);
        List<String> provided = splitVersion(providedVersion);
        if (required.size() == 0) {
            return true;
        } else if (provided.size() == 0) {return false;}

        for (int i = 0, size = Math.min(required.size(), provided.size()); i < size; i++) {
            int comparison = required.get(i).compareTo(provided.get(i));
            if (comparison > 0) {return true;
            } else if (comparison < 0) {return false;}
        }
        return true;
    }

    private static List<String> splitVersion(String version) {
        List<String> result = new ArrayList<>();
        for (String a : version.split("-")) {result.addAll(Arrays.asList(a.split("\\.")));}
        return result;
    }
}
