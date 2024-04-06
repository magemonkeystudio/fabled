/**
 * Fabled
 * studio.magemonkey.fabled.api.particle.EffectPlayer
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.api.particle;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.particle.target.EffectTarget;
import studio.magemonkey.fabled.data.Point3D;
import studio.magemonkey.fabled.data.formula.Formula;
import studio.magemonkey.fabled.data.formula.value.CustomValue;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Handles playing effects based on configuration settings
 */
public class EffectImage {
    private static final String IMG            = "img";
    public static final  String INTERVAL       = "interval";
    public static final  String ITER_PER_FRAME = "iterations-per-frame";
    public static final  String VIEW_RANGE     = "view-range";
    private static final String WIDTH          = "width";
    private static final String HEIGHT         = "height";
    private static final String LOCK_ASPECT    = "lock-aspect";
    private static final String RESOLUTION     = "resolution";
    private static final String DUST_SIZE      = "dust-size";
    private static final String WITH_ROTATION  = "with-rotation";

    private final Settings settings;

    /**
     * Sets up an effect player that applies effects based of the values in the provided settings.
     * All the available settings are provided as static values in this class.
     *
     * @param settings settings to read from
     */
    public EffectImage(Settings settings) {
        this.settings = settings;
    }

    /**
     * Plays a particle effect, grabbing values from the settings data
     *
     * @param target target to play for
     * @param key    effect key to use
     * @param ticks  duration of effect in ticks
     * @param level  level of the effect
     */
    public void start(EffectTarget target, String key, int ticks, int level) {
        // If the effect is already running, just refresh it
        EffectInstance instance = EffectManager.getEffect(target, key);
        if (instance != null) {
            instance.extend(ticks);
            return;
        }

        // If the effect is not registered, make it
        if (EffectManager.getEffect(key) == null)
            makeEffect(key);

        // Play the effect
        EffectManager.runEffect(EffectManager.getEffect(key), target, ticks, level);
    }

    /**
     * Creates and registers an effect
     *
     * @param key effect key
     */
    private void makeEffect(String key) {
        String fileName = settings.getString(IMG, "default.png");
        File file = new File(Fabled.inst().getDataFolder(),
                "images" + File.separator + fileName);
        if (!file.exists()) {
            Fabled.inst().getLogger().warning("Image file not found: " + file.getPath());
            return;
        }

        Formula dustSizeFormula = new Formula(
                settings.getString(DUST_SIZE, "1"),
                new CustomValue("t"),
                new CustomValue("l")
        );

        ImageData imgData = readImage(file);
        if (imgData == null) {
            Fabled.inst().getLogger().warning("Failed to read image file: " + file.getPath());
            return;
        }

        // Make the effect
        ParticleImage effect = new ParticleImage(
                key,
                imgData.getColors(),
                imgData.getPoints(),
                dustSizeFormula,
                settings.getInt(INTERVAL, 5),
                settings.getInt(ITER_PER_FRAME, 3),
                settings.getInt(VIEW_RANGE, 25),
                settings.getBool(WITH_ROTATION, false),
                new TimeBasedTransform(settings)
        );

        // Register the effect
        EffectManager.register(effect);
    }

    private ImageData readImage(File file) {
        if (file.getName().endsWith(".gif")) {
            return readGif(file);
        }

        BufferedImage original;
        try {
            original = ImageIO.read(file);
        } catch (IOException e) {
            Fabled.inst()
                    .getLogger()
                    .warning("Failed to read image file: " + file.getPath() + " (" + e.getMessage() + ")");
            e.printStackTrace();
            return null;
        }

        // Scale the image to the desired size
        int     resolution = settings.getInt(RESOLUTION, 6);
        boolean lockAspect = settings.getBool(LOCK_ASPECT, true);
        int     width      = settings.getInt(WIDTH, 3) * resolution;
        int     height     = settings.getInt(HEIGHT, width / resolution) * resolution;

        if (lockAspect) {
            double aspect = (double) original.getWidth() / original.getHeight();
            if (aspect > 1) {
                width = (int) (width * aspect);
            } else {
                height = (int) (height / aspect);
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics()
                .drawImage(original.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH), 0, 0, null);
        Color[][]   colors = new Color[1][width * height];
        Point3D[][] points = new Point3D[1][width * height];

        FrameData frameData = getFrameData(image, resolution);
        colors[0] = frameData.getColors();
        points[0] = frameData.getPoints();

        return new ImageData(colors, points);
    }

    private ImageData readGif(File file) {
        try {
            ImageReader      reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream stream = ImageIO.createImageInputStream(file);
            reader.setInput(stream);
            BufferedImage image = reader.read(0);

            // Scale the image to the desired size
            int     resolution = settings.getInt(RESOLUTION, 6);
            boolean lockAspect = settings.getBool(LOCK_ASPECT, true);
            int     width      = settings.getInt(WIDTH, 3) * resolution;
            int     height     = settings.getInt(HEIGHT, width / resolution) * resolution;

            if (lockAspect) {
                double aspect = (double) image.getWidth() / image.getHeight();
                if (aspect > 1) {
                    width = (int) (width * aspect);
                } else {
                    height = (int) (height / aspect);
                }
            }

            int         frames = reader.getNumImages(true);
            Color[][]   colors = new Color[frames][width * height];
            Point3D[][] points = new Point3D[frames][width * height];
            for (int index = 0; index < frames; index++) {
                BufferedImage frameImage = reader.read(index);
                BufferedImage frame      = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                frame.getGraphics()
                        .drawImage(frameImage.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH), 0, 0, null);
                FrameData data = getFrameData(frame, resolution);
                colors[index] = data.getColors();
                points[index] = data.getPoints();
            }

            return new ImageData(colors, points);
        } catch (IOException ex) {
            // An I/O problem has occurred
            return null;
        }
    }

    private FrameData getFrameData(BufferedImage img, int resolution) {
        int       width  = img.getWidth();
        int       height = img.getHeight();
        Color[]   colors = new Color[width * height];
        Point3D[] points = new Point3D[width * height];
        // Get the image's pixels
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Shift x/y so that 0,0 is the center of the image
                double relativeX = (x - width / 2D) / resolution;
                double relativeY = (y - height / 2D) / resolution;
                points[x + y * width] = new Point3D(relativeX, relativeY, 0);

                // Get the color of the pixel
                int color = img.getRGB(width - x - 1, height - y - 1);
                // If the color is transparent, skip it
                if ((color & 0xFF000000) == 0) continue;

                // convert to non-alpha
                color = color & 0x00FFFFFF;

                // Convert to MC Color
                Color col = Color.fromRGB(color);
                colors[x + y * width] = col;
            }
        }

        return new FrameData(colors, points);
    }

    @Data
    @RequiredArgsConstructor
    private static class FrameData {
        private final Color[]   colors;
        private final Point3D[] points;
    }

    @Data
    @RequiredArgsConstructor
    private static class ImageData {
        private final Color[][]   colors;
        private final Point3D[][] points;
    }
}
