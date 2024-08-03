package studio.magemonkey.fabled.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.util.Lists
 */
public class Lists {
    public static <T> List<T> asList(final T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
}
