package me.libraryaddict.disguise.utilities.reflection;

/**
 * Compile-time stand-in for LibsDisguises' {@code FakeBoundingBox}, which lives in its unpublished {@code shared}
 * module and so is missing from the Maven artifact. The real class ships in the LibsDisguises plugin jar.
 * <p>
 * Only javac's {@code -sourcepath} sees this file ({@code -implicit:none}), so no class file is produced and it never
 * ends up in Fabled's jar. Keep signatures in sync with upstream:
 * https://github.com/libraryaddict/LibsDisguises/blob/master/shared/src/main/java/me/libraryaddict/disguise/utilities/reflection/FakeBoundingBox.java
 * <p>
 * Delete this stub once LibsDisguises publishes its {@code shared} classes:
 * https://github.com/libraryaddict/LibsDisguises/issues/844
 * <p>
 * Dimensions are (width, height, depth). Since LibsDisguises 26.x, {@link #getX()} and {@link #getZ()} return full
 * widths; use {@link #getHalvedX()} and {@link #getHalvedZ()} for half-widths.
 */
public class FakeBoundingBox {
    public FakeBoundingBox(double xMod, double yMod, double zMod) {
        throw new UnsupportedOperationException("stub");
    }

    public double getX() {
        throw new UnsupportedOperationException("stub");
    }

    public double getHalvedX() {
        throw new UnsupportedOperationException("stub");
    }

    public double getY() {
        throw new UnsupportedOperationException("stub");
    }

    public double getZ() {
        throw new UnsupportedOperationException("stub");
    }

    public double getHalvedZ() {
        throw new UnsupportedOperationException("stub");
    }
}
