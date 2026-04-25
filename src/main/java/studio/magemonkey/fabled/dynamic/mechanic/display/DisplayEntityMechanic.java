package studio.magemonkey.fabled.dynamic.mechanic.display;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import studio.magemonkey.codex.util.StringUT;
import studio.magemonkey.fabled.api.displayentity.DisplayEntityInstance;
import studio.magemonkey.fabled.api.displayentity.DisplayEntityManager;
import studio.magemonkey.fabled.dynamic.TempEntity;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;
import studio.magemonkey.fabled.log.Logger;
import studio.magemonkey.fabled.task.RemoveEntitiesTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Spawns a Display entity (block, item, or text) at the target location.
 * Supports per-axis scale, local-space translation, and left/right rotation
 * expressed as Euler angles in degrees.
 * Requires Minecraft 1.19.4 or later.
 */
public class DisplayEntityMechanic extends MechanicComponent {

    private static final Vector UP = new Vector(0, 1, 0);

    // ── Common settings ───────────────────────────────────────────────────────
    private static final String KEY              = "key";
    private static final String DURATION         = "duration";
    private static final String TYPE             = "display-type";
    private static final String FOLLOW           = "follow";
    private static final String FORWARD          = "forward";
    private static final String UPWARD           = "upward";
    private static final String RIGHT            = "right";
    private static final String BILLBOARD        = "billboard";
    private static final String VIEW_RANGE       = "view-range";
    private static final String SHADOW_RADIUS    = "shadow-radius";
    private static final String SHADOW_STRENGTH  = "shadow-strength";
    private static final String GLOW             = "glow";
    private static final String GLOW_COLOR       = "glow-color";
    private static final String BRIGHTNESS_BLOCK = "brightness-block";
    private static final String BRIGHTNESS_SKY   = "brightness-sky";

    // ── Transformation settings ───────────────────────────────────────────────
    // Scale (multiplier per axis, default 1)
    private static final String SCALE_X = "scale-x";
    private static final String SCALE_Y = "scale-y";
    private static final String SCALE_Z = "scale-z";

    // Local-space translation offset applied inside the Transformation
    // (distinct from the world-space forward/upward/right spawn offset)
    private static final String TRANSLATE_X = "translate-x";
    private static final String TRANSLATE_Y = "translate-y";
    private static final String TRANSLATE_Z = "translate-z";

    // Left rotation – applied before scale – Euler angles in degrees
    private static final String LEFT_ROTATION_X = "left-rotation-x";
    private static final String LEFT_ROTATION_Y = "left-rotation-y";
    private static final String LEFT_ROTATION_Z = "left-rotation-z";

    // Right rotation – applied after scale – Euler angles in degrees
    private static final String RIGHT_ROTATION_X = "right-rotation-x";
    private static final String RIGHT_ROTATION_Y = "right-rotation-y";
    private static final String RIGHT_ROTATION_Z = "right-rotation-z";

    // ── Block display settings ────────────────────────────────────────────────
    private static final String BLOCK_TYPE = "block-type";

    // ── Item display settings ─────────────────────────────────────────────────
    private static final String ITEM_MATERIAL  = "item-material";
    private static final String ITEM_TRANSFORM = "item-display-transform";

    // ── Text display settings ─────────────────────────────────────────────────
    private static final String TEXT             = "text";
    private static final String TEXT_OPACITY     = "text-opacity";
    private static final String TEXT_BG_COLOR    = "background-color";
    private static final String TEXT_SEE_THROUGH = "see-through";
    private static final String TEXT_SHADOW      = "text-shadow";
    private static final String TEXT_LINE_WIDTH  = "line-width";
    private static final String TEXT_ALIGNMENT   = "text-alignment";

    @Override
    public String getKey() {
        return "display entity";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (!DisplayEntityManager.isSupported()) {
            Logger.invalid("Display entities require Minecraft 1.19.4+. "
                    + "The 'display entity' mechanic will not work on this server.");
            return false;
        }

        String  key      = settings.getString(KEY, skill.getName());
        int     duration = (int) (20 * parseValues(caster, DURATION, level, 5));
        String  typeName = settings.getString(TYPE, "BLOCK").toUpperCase(Locale.US);
        boolean follow   = settings.getBool(FOLLOW, false);
        double  forward  = parseValues(caster, FORWARD, level, 0);
        double  upward   = parseValues(caster, UPWARD, level, 0);
        double  right    = parseValues(caster, RIGHT, level, 0);

        // ── Transformation ────────────────────────────────────────────────────
        Transformation transformation = buildTransformation(caster, level);

        // ── Other appearance settings ─────────────────────────────────────────
        Billboard billboard    = parseBillboard(settings.getString(BILLBOARD, "FIXED"));
        float     viewRange    = (float) parseValues(caster, VIEW_RANGE, level, 64.0);
        float     shadowRadius = (float) parseValues(caster, SHADOW_RADIUS, level, 0.0);
        float     shadowStrength = (float) parseValues(caster, SHADOW_STRENGTH, level, 1.0);
        boolean   glow         = settings.getBool(GLOW, false);
        Color     glowColor    = parseColor(settings.getString(GLOW_COLOR, ""), null);
        int       brightnessBlock = (int) parseValues(caster, BRIGHTNESS_BLOCK, level, -1);
        int       brightnessSky   = (int) parseValues(caster, BRIGHTNESS_SKY, level, -1);

        List<Entity> spawnedEntities = new ArrayList<>();

        for (LivingEntity target : targets) {
            Location loc  = target.getLocation().clone();
            Vector   dir  = loc.getDirection().setY(0).normalize();
            Vector   side = dir.clone().crossProduct(UP);
            loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));

            Entity entity = spawnDisplay(typeName, loc, caster, level,
                    transformation, billboard, viewRange,
                    shadowRadius, shadowStrength, glow, glowColor,
                    brightnessBlock, brightnessSky);

            if (entity == null) continue;

            DisplayEntityManager.tag(entity);
            spawnedEntities.add(entity);

            DisplayEntityInstance instance = follow
                    ? new DisplayEntityInstance(entity, target, true, forward, upward, right)
                    : new DisplayEntityInstance(entity, target, false);
            DisplayEntityManager.register(instance, target, key);
        }

        if (!spawnedEntities.isEmpty()) {
            new RemoveEntitiesTask(spawnedEntities, duration);
        }

        return !targets.isEmpty();
    }

    // ── Transformation builder ────────────────────────────────────────────────

    /**
     * Reads scale, local translation, and left/right rotation from settings and
     * assembles a {@link Transformation}.
     *
     * <p>Rotation values are Euler angles in <b>degrees</b> applied in X→Y→Z order.</p>
     */
    private Transformation buildTransformation(LivingEntity caster, int level) {
        // Scale
        float scaleX = (float) parseValues(caster, SCALE_X, level, 1.0);
        float scaleY = (float) parseValues(caster, SCALE_Y, level, 1.0);
        float scaleZ = (float) parseValues(caster, SCALE_Z, level, 1.0);

        // Local-space translation
        float transX = (float) parseValues(caster, TRANSLATE_X, level, 0.0);
        float transY = (float) parseValues(caster, TRANSLATE_Y, level, 0.0);
        float transZ = (float) parseValues(caster, TRANSLATE_Z, level, 0.0);

        // Left rotation (degrees → quaternion, XYZ order)
        float lrX = (float) Math.toRadians(parseValues(caster, LEFT_ROTATION_X, level, 0.0));
        float lrY = (float) Math.toRadians(parseValues(caster, LEFT_ROTATION_Y, level, 0.0));
        float lrZ = (float) Math.toRadians(parseValues(caster, LEFT_ROTATION_Z, level, 0.0));

        // Right rotation (degrees → quaternion, XYZ order)
        float rrX = (float) Math.toRadians(parseValues(caster, RIGHT_ROTATION_X, level, 0.0));
        float rrY = (float) Math.toRadians(parseValues(caster, RIGHT_ROTATION_Y, level, 0.0));
        float rrZ = (float) Math.toRadians(parseValues(caster, RIGHT_ROTATION_Z, level, 0.0));

        Quaternionf leftRotation  = new Quaternionf().rotationXYZ(lrX, lrY, lrZ);
        Quaternionf rightRotation = new Quaternionf().rotationXYZ(rrX, rrY, rrZ);

        return new Transformation(
                new Vector3f(transX, transY, transZ),
                leftRotation,
                new Vector3f(scaleX, scaleY, scaleZ),
                rightRotation
        );
    }

    // ── Spawn helpers ─────────────────────────────────────────────────────────

    private Entity spawnDisplay(String typeName,
                                Location loc,
                                LivingEntity caster,
                                int level,
                                Transformation transformation,
                                Billboard billboard,
                                float viewRange,
                                float shadowRadius,
                                float shadowStrength,
                                boolean glow,
                                Color glowColor,
                                int brightnessBlock,
                                int brightnessSky) {
        try {
            Entity entity;
            switch (typeName) {
                case "BLOCK":
                    entity = spawnBlockDisplay(loc, caster, level);
                    break;
                case "ITEM":
                    entity = spawnItemDisplay(loc, caster, level);
                    break;
                case "TEXT":
                    entity = spawnTextDisplay(loc, caster, level);
                    break;
                default:
                    Logger.invalid("Unknown display-type '" + typeName + "' – expected BLOCK, ITEM, or TEXT.");
                    return null;
            }

            if (entity == null) return null;

            Display display = (Display) entity;
            display.setTransformation(transformation);
            display.setBillboard(billboard);
            display.setViewRange(viewRange);
            display.setShadowRadius(shadowRadius);
            display.setShadowStrength(shadowStrength);
            display.setInvulnerable(true);
            display.setPersistent(false);

            if (glowColor != null) {
                display.setGlowing(true);
                display.setGlowColorOverride(glowColor);
            } else {
                display.setGlowing(glow);
            }

            if (brightnessBlock >= 0 && brightnessSky >= 0) {
                display.setBrightness(new Brightness(
                        Math.min(15, brightnessBlock),
                        Math.min(15, brightnessSky)
                ));
            }

            return entity;
        } catch (Exception e) {
            Logger.invalid("Failed to spawn display entity: " + e.getMessage());
            return null;
        }
    }

    private Entity spawnBlockDisplay(Location loc, LivingEntity caster, int level) {
        String   blockTypeName = settings.getString(BLOCK_TYPE, "STONE");
        Material material;
        try {
            material = Material.valueOf(blockTypeName.toUpperCase(Locale.US).replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            Logger.invalid("Invalid block-type '" + blockTypeName + "' for display entity mechanic – defaulting to STONE.");
            material = Material.STONE;
        }
        Material finalMaterial = material;
        return loc.getWorld().spawn(loc, BlockDisplay.class,
                entity -> entity.setBlock(Bukkit.createBlockData(finalMaterial)));
    }

    private Entity spawnItemDisplay(Location loc, LivingEntity caster, int level) {
        String   itemMaterialName = settings.getString(ITEM_MATERIAL, "STONE");
        Material material;
        try {
            material = Material.valueOf(itemMaterialName.toUpperCase(Locale.US).replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            Logger.invalid("Invalid item-material '" + itemMaterialName + "' for display entity mechanic – defaulting to STONE.");
            material = Material.STONE;
        }
        Material finalMaterial = material;

        String transformName = settings.getString(ITEM_TRANSFORM, "GROUND")
                .toUpperCase(Locale.US).replace(" ", "_");
        ItemDisplay.ItemDisplayTransform transform;
        try {
            transform = ItemDisplay.ItemDisplayTransform.valueOf(transformName);
        } catch (IllegalArgumentException e) {
            transform = ItemDisplay.ItemDisplayTransform.GROUND;
        }
        ItemDisplay.ItemDisplayTransform finalTransform = transform;

        return loc.getWorld().spawn(loc, ItemDisplay.class, entity -> {
            entity.setItem(new ItemStack(finalMaterial));
            entity.setItemDisplayTransform(finalTransform);
        });
    }

    private Entity spawnTextDisplay(Location loc, LivingEntity caster, int level) {
        String        rawText     = settings.getString(TEXT, "");
        String        coloredText = StringUT.color(rawText);
        int           textOpacity = (int) parseValues(caster, TEXT_OPACITY, level, -1);
        boolean       seeThrough  = settings.getBool(TEXT_SEE_THROUGH, false);
        boolean       textShadow  = settings.getBool(TEXT_SHADOW, false);
        int           lineWidth   = (int) parseValues(caster, TEXT_LINE_WIDTH, level, 200);
        String        alignName   = settings.getString(TEXT_ALIGNMENT, "CENTER").toUpperCase(Locale.US);
        Color         bgColor     = parseColor(settings.getString(TEXT_BG_COLOR, ""), null);

        TextAlignment alignment;
        try {
            alignment = TextAlignment.valueOf(alignName);
        } catch (IllegalArgumentException e) {
            alignment = TextAlignment.CENTER;
        }

        TextAlignment finalAlignment = alignment;
        Color         finalBgColor   = bgColor;

        return loc.getWorld().spawn(loc, TextDisplay.class, entity -> {
            entity.text(LegacyComponentSerializer.legacyAmpersand().deserialize(coloredText));
            entity.setSeeThrough(seeThrough);
            entity.setShadowed(textShadow);
            entity.setLineWidth(lineWidth);
            entity.setAlignment(finalAlignment);
            if (textOpacity >= 0) {
                entity.setTextOpacity((byte) Math.min(255, textOpacity));
            }
            if (finalBgColor != null) {
                entity.setBackgroundColor(finalBgColor);
            }
        });
    }

    // ── Parsing helpers ───────────────────────────────────────────────────────

    private Billboard parseBillboard(String value) {
        try {
            return Billboard.valueOf(value.toUpperCase(Locale.US).replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return Billboard.FIXED;
        }
    }

    /**
     * Parses a hex color string (e.g. {@code "#RRGGBB"}, {@code "RRGGBB"}, or {@code "none"}).
     *
     * @param hex          hex string to parse
     * @param defaultColor fallback color if parsing fails or string is blank/none
     * @return parsed {@link Color}, or {@code defaultColor}
     */
    private Color parseColor(String hex, Color defaultColor) {
        if (hex == null || hex.isBlank() || hex.equalsIgnoreCase("none")) return defaultColor;
        try {
            hex = hex.replace("#", "").trim();
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return Color.fromRGB(r, g, b);
        } catch (Exception e) {
            return defaultColor;
        }
    }

    // ── Preview ───────────────────────────────────────────────────────────────

    @Override
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        double fwd = parseValues(caster, FORWARD, level, 0);
        double upw = parseValues(caster, UPWARD, level, 0);
        double rgt = parseValues(caster, RIGHT, level, 0);
        super.playPreview(onPreviewStop, caster, level, () -> {
            List<LivingEntity> newTargets = new ArrayList<>();
            for (LivingEntity target : targetSupplier.get()) {
                Location loc  = target.getLocation().clone();
                Vector   dir  = loc.getDirection().setY(0).normalize();
                Vector   side = dir.clone().crossProduct(UP);
                loc.add(dir.multiply(fwd)).add(0, upw, 0).add(side.multiply(rgt));
                newTargets.add(new TempEntity(loc));
            }
            return newTargets;
        });
    }
}
