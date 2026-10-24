package me.libraryaddict.disguise.utilities;

import me.libraryaddict.disguise.utilities.reflection.FakeBoundingBox;

/**
 * Compile-time stand-in for LibsDisguises' {@code DisguiseValues}, which lives in its unpublished {@code shared}
 * module and so is missing from the Maven artifact. The real class ships in the LibsDisguises plugin jar.
 * <p>
 * Only javac's {@code -sourcepath} sees this file ({@code -implicit:none}), so no class file is produced and it never
 * ends up in Fabled's jar. Keep signatures in sync with upstream:
 * https://github.com/libraryaddict/LibsDisguises/blob/master/shared/src/main/java/me/libraryaddict/disguise/utilities/DisguiseValues.java
 */
public class DisguiseValues {
    public DisguiseValues(double maxHealth, int ambientSoundInterval) {
        throw new UnsupportedOperationException("stub");
    }

    public FakeBoundingBox getAdultBox() {
        throw new UnsupportedOperationException("stub");
    }

    public void setAdultBox(FakeBoundingBox adultBox) {
        throw new UnsupportedOperationException("stub");
    }

    public FakeBoundingBox getBabyBox() {
        throw new UnsupportedOperationException("stub");
    }

    public void setBabyBox(FakeBoundingBox babyBox) {
        throw new UnsupportedOperationException("stub");
    }

    public double getMaxHealth() {
        throw new UnsupportedOperationException("stub");
    }

    public int getAmbientSoundInterval() {
        throw new UnsupportedOperationException("stub");
    }
}
