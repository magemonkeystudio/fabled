package studio.magemonkey.fabled;

public class DependencyRequirement {

    public static final String MIN_CORE_VERSION = "1.2.0";

    /**
     * Whether providedVersion is at least requiredVersion, see {@link #compareVersions}.
     * An empty requirement is always met; an empty provided version never meets one.
     */
    public static boolean meetsVersion(String requiredVersion, String providedVersion) {
        if (requiredVersion == null || requiredVersion.isBlank()) {
            return true;
        } else if (providedVersion == null || providedVersion.isBlank()) {
            return false;
        }
        return compareVersions(providedVersion, requiredVersion) >= 0;
    }

    /**
     * Orders versions the way semantic versioning does, so a minimum can name a dev build:
     * <pre>1.2.0 &lt; 1.2.1-dev.1 &lt; 1.2.1-dev.2 &lt; 1.2.1-SNAPSHOT &lt; 1.2.1 &lt; 1.2.2-dev.1</pre>
     * X.Y.Z compares by number. A version with a pre-release part (dev.N, SNAPSHOT, the
     * legacy R0.N-SNAPSHOT) comes before the same X.Y.Z without one, and pre-release parts
     * compare piece by piece: numbers by value, words alphabetically, numbers before words.
     * Legacy releases (X.Y.Z-RN) count as X.Y.Z, and build metadata after '+' is ignored.
     *
     * @return negative, zero or positive as a is lower than, equal to or higher than b
     */
    public static int compareVersions(String a, String b) {
        String[] versionA = splitPreRelease(a);
        String[] versionB = splitPreRelease(b);

        String[] coreA = versionA[0].split("\\.");
        String[] coreB = versionB[0].split("\\.");
        for (int i = 0; i < Math.max(coreA.length, coreB.length); i++) {
            int cmp = Long.compare(number(coreA, i), number(coreB, i));
            if (cmp != 0) {
                return cmp;
            }
        }

        if (versionA[1] == null || versionB[1] == null) {
            // A release is higher than any pre-release of the same version
            return versionA[1] == null ? (versionB[1] == null ? 0 : 1) : -1;
        }
        String[] partsA = versionA[1].split("[.-]");
        String[] partsB = versionB[1].split("[.-]");
        for (int i = 0; i < Math.min(partsA.length, partsB.length); i++) {
            int cmp = comparePart(partsA[i], partsB[i]);
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(partsA.length, partsB.length);
    }

    /** {X.Y.Z, pre-release part or null} */
    private static String[] splitPreRelease(String version) {
        version = version.trim();
        int plus = version.indexOf('+');
        if (plus >= 0) {
            version = version.substring(0, plus);
        }
        int dash = version.indexOf('-');
        if (dash < 0) {
            return new String[]{version, null};
        }
        String preRelease = version.substring(dash + 1);
        // Legacy releases were tagged X.Y.Z-RN (dev builds were X.Y.Z-R0.N-SNAPSHOT)
        if (preRelease.matches("R[1-9]\\d*")) {
            preRelease = null;
        }
        return new String[]{version.substring(0, dash), preRelease};
    }

    private static long number(String[] parts, int i) {
        if (i >= parts.length) {
            return 0;
        }
        try {
            return Long.parseLong(parts[i]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int comparePart(String a, String b) {
        boolean numericA = a.matches("\\d+");
        boolean numericB = b.matches("\\d+");
        if (numericA && numericB) {
            return Long.compare(Long.parseLong(a), Long.parseLong(b));
        } else if (numericA != numericB) {
            return numericA ? -1 : 1;
        }
        return a.compareToIgnoreCase(b);
    }
}
