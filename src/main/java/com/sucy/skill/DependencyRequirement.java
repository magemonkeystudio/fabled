package com.sucy.skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyRequirement {

    public static final String MIN_CORE_VERSION = "1.1.1-R0.1-SNAPSHOT";

    public static boolean meetsVersion(String requiredVersion, String providedVersion) {
        List<Integer> required = splitVersion(requiredVersion);
        List<Integer> provided = splitVersion(providedVersion);
        if (required.size() == 0) {
            return true;
        } else if (provided.size() == 0) {
            return false;
        }

        for (int i = 0, size = Math.min(required.size(), provided.size()); i < size; i++) {
            if (provided.get(i) > required.get(i)) {
                return true;
            } else if (provided.get(i) < required.get(i)) {
                return false;
            }
        }
        return true;
    }

    private static List<Integer> splitVersion(String version) {
        List<Integer> result = new ArrayList<>();
        for (String a : version.split("-")) {
            result.addAll(
                    Arrays.asList(a.split("\\.")).stream().map(
                            str -> {
                                try {
                                    return Integer.parseInt(str.replace("R", "").replace("SNAPSHOT", ""));
                                } catch (NumberFormatException e) {
                                    return 0;
                                }
                            }).collect(Collectors.toList())
            );
        }
        return result;
    }
}
