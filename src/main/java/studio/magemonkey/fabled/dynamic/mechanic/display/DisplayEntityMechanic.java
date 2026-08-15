package studio.magemonkey.fabled.dynamic.mechanic.display;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.api.items.ItemType;
import studio.magemonkey.codex.api.items.exception.MissingItemException;
import studio.magemonkey.codex.api.items.exception.MissingProviderException;
import studio.magemonkey.codex.items.CodexItemManager;
import studio.magemonkey.codex.util.StringUT;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.displayentity.DisplayEntityInstance;
import studio.magemonkey.fabled.api.displayentity.DisplayEntityManager;
import studio.magemonkey.fabled.api.displayentity.DisplayEntityTransform;
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
 *
 * <p>All transformation settings ({@code scale-x/y/z}, {@code translate-x/y/z},
 * {@code left-rotation-x/y/z}, {@code right-rotation-x/y/z}) accept formula
 * expressions with two variables:
 * <ul>
 *   <li>{@code t} – elapsed ticks since the entity was spawned</li>
 *   <li>{@code l} – skill level</li>
 * </ul>
 * A plain constant (e.g. {@code "2.5"}) or a level-scaled expression
 * (e.g. {@code "1+0.5*(l-1)"}) works as before. A time-driven expression
 * (e.g. {@code "t*5"} for left-rotation-y) animates the entity continuously.
 *
 * <p>Requires Minecraft 1.19.4 or later.
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
    /**
     * When {@code false} the spawn location (and follow-teleport locations) have their
     * yaw and pitch zeroed so the entity does not inherit the caster's facing direction.
     * Defaults to {@code true} for backwards compatibility.
     */
    private static final String INHERIT_ROTATION = "inherit-rotation";

    // ── Interpolation ─────────────────────────────────────────────────────────
    /**
     * Client-side interpolation ticks for smooth animated transforms (0 = instant).
     */
    private static final String INTERPOLATION_DURATION = "interpolation-duration";
    /**
     * Client-side interpolation ticks for follow position updates (0 = instant teleport).
     * Defaults to {@code 3} when {@code follow} is enabled so movement looks smooth.
     */
    private static final String TELEPORT_DURATION = "teleport-duration";

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

        String  key             = settings.getString(KEY, skill.getName());
        int     duration        = (int) (20 * parseValues(caster, DURATION, level, 5));
        String  typeName        = settings.getString(TYPE, "BLOCK").toUpperCase(Locale.US);
        boolean follow          = settings.getBool(FOLLOW, false);
        boolean inheritRotation = settings.getBool(INHERIT_ROTATION, true);
        double  forward         = parseValues(caster, FORWARD, level, 0);
        double  upward          = parseValues(caster, UPWARD, level, 0);
        double  right           = parseValues(caster, RIGHT, level, 0);

        // ── Transformation ────────────────────────────────────────────────────
        DisplayEntityTransform entityTransform       = new DisplayEntityTransform(settings);
        int                    interpolationDuration = settings.getInt(INTERPOLATION_DURATION, 0);
        int                    teleportDuration      = settings.getInt(TELEPORT_DURATION, follow ? 3 : 0);

        // ── Other appearance settings ─────────────────────────────────────────
        Billboard billboard       = parseBillboard(settings.getString(BILLBOARD, "FIXED"));
        float     viewRange       = (float) parseValues(caster, VIEW_RANGE, level, 64.0);
        float     shadowRadius    = (float) parseValues(caster, SHADOW_RADIUS, level, 0.0);
        float     shadowStrength  = (float) parseValues(caster, SHADOW_STRENGTH, level, 1.0);
        boolean   glow            = settings.getBool(GLOW, false);
        Color     glowColor       = parseColor(settings.getString(GLOW_COLOR, ""), null);
        int       brightnessBlock = (int) parseValues(caster, BRIGHTNESS_BLOCK, level, -1);
        int       brightnessSky   = (int) parseValues(caster, BRIGHTNESS_SKY, level, -1);

        List<Entity> spawnedEntities = new ArrayList<>();

        for (LivingEntity target : targets) {
            Location loc  = target.getLocation().clone();
            // Compute offset direction from actual facing BEFORE zeroing yaw/pitch,
            // so forward/right always point relative to the entity's real orientation.
            Vector   dir  = loc.getDirection().setY(0).normalize();
            Vector   side = dir.clone().crossProduct(UP);
            loc.add(dir.clone().multiply(forward)).add(0, upward, 0).add(side.clone().multiply(right));
            if (!inheritRotation) {
                loc.setYaw(0);
                loc.setPitch(0);
            }

            Entity entity = spawnDisplay(typeName, loc, caster, target, level,
                    entityTransform.compute(0, level), billboard, viewRange,
                    shadowRadius, shadowStrength, glow, glowColor,
                    brightnessBlock, brightnessSky);

            if (entity == null) continue;

            DisplayEntityManager.tag(entity);
            spawnedEntities.add(entity);

            DisplayEntityInstance instance = new DisplayEntityInstance(
                    entity, target, follow,
                    forward, upward, right,
                    entityTransform, dir, side,
                    level, interpolationDuration, teleportDuration, inheritRotation);
            DisplayEntityManager.register(instance, target, key);
        }

        if (!spawnedEntities.isEmpty()) {
            new RemoveEntitiesTask(spawnedEntities, duration);
        }

        return !targets.isEmpty();
    }

    // ── Spawn helpers ─────────────────────────────────────────────────────────

    private Entity spawnDisplay(String typeName,
                                Location loc,
                                LivingEntity caster,
                                LivingEntity target,
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
                    entity = spawnBlockDisplay(loc, caster, target, level);
                    break;
                case "ITEM":
                    entity = spawnItemDisplay(loc, caster, target, level);
                    break;
                case "TEXT":
                    entity = spawnTextDisplay(loc, caster, target, level);
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

    private Entity spawnBlockDisplay(Location loc, LivingEntity caster, LivingEntity target, int level) {
        String   blockTypeName = settings.getString(BLOCK_TYPE, "STONE");
        Material material;
        try {
            material = Material.valueOf(blockTypeName.toUpperCase(Locale.US).replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            Logger.invalid(
                    "Invalid block-type '" + blockTypeName + "' for display entity mechanic – defaulting to STONE.");
            material = Material.STONE;
        }
        Material finalMaterial = material;
        return loc.getWorld().spawn(loc, BlockDisplay.class,
                entity -> entity.setBlock(Bukkit.createBlockData(finalMaterial)));
    }

    private Entity spawnItemDisplay(Location loc, LivingEntity caster, LivingEntity target, int level) {
        String itemMaterialName = settings.getString(ITEM_MATERIAL, "STONE");

        // Resolve via Codex ItemManager first so custom providers (Divinity, Nexo, etc.) are supported.
        CodexItemManager itemManager = CodexEngine.get().getItemManager();
        ItemType         itemType    = null;
        try {
            itemType = itemManager.getItemType(itemMaterialName);
        } catch (MissingItemException | MissingProviderException ignored) {
        }

        ItemStack itemStack;
        if (itemType != null) {
            itemStack = itemType.create();
        } else {
            Material material = Material.matchMaterial(itemMaterialName);
            if (material == null) {
                try {
                    material = Material.valueOf(itemMaterialName.toUpperCase(Locale.US).replace(" ", "_"));
                } catch (IllegalArgumentException e) {
                    Logger.invalid("Invalid item-material '" + itemMaterialName
                            + "' for display entity mechanic – defaulting to STONE.");
                    material = Material.STONE;
                }
            }
            itemStack = new ItemStack(material);
        }


        String transformName = settings.getString(ITEM_TRANSFORM, "GROUND")
                .toUpperCase(Locale.US).replace(" ", "_");
        ItemDisplay.ItemDisplayTransform transform;
        try {
            transform = ItemDisplay.ItemDisplayTransform.valueOf(transformName);
        } catch (IllegalArgumentException e) {
            transform = ItemDisplay.ItemDisplayTransform.GROUND;
        }
        ItemDisplay.ItemDisplayTransform finalTransform = transform;

        final ItemStack finalItemStack = itemStack;
        return loc.getWorld().spawn(loc, ItemDisplay.class, entity -> {
            entity.setItemStack(finalItemStack);
            entity.setItemDisplayTransform(finalTransform);
        });
    }

    @Nullable
    private Entity spawnTextDisplay(Location loc, LivingEntity caster, LivingEntity target, int level) {
        String  rawText     = settings.getString(TEXT, "");
        String  coloredText = filter(caster, target, StringUT.color(rawText));
        int     textOpacity = (int) Math.round(parseValues(caster, TEXT_OPACITY, level, -1) * 255);
        boolean seeThrough  = settings.getBool(TEXT_SEE_THROUGH, false);
        boolean textShadow  = settings.getBool(TEXT_SHADOW, false);
        int     lineWidth   = (int) parseValues(caster, TEXT_LINE_WIDTH, level, 200);
        String  alignName   = settings.getString(TEXT_ALIGNMENT, "CENTER").toUpperCase(Locale.US);
        Color   bgColor     = parseColor(settings.getString(TEXT_BG_COLOR, ""), null);

        TextAlignment alignment;
        try {
            alignment = TextAlignment.valueOf(alignName);
        } catch (IllegalArgumentException e) {
            alignment = TextAlignment.CENTER;
        }

        TextAlignment finalAlignment = alignment;
        World         world          = loc.getWorld();
        if (world == null) {
            Fabled.inst().getLogger().warning("Failed to spawn text display: location world is null");
            return null;
        }

        return loc.getWorld().spawn(loc, TextDisplay.class, entity -> {
            entity.setText(coloredText);
            entity.setSeeThrough(seeThrough);
            entity.setShadowed(textShadow);
            entity.setLineWidth(lineWidth);
            entity.setAlignment(finalAlignment);
            if (textOpacity >= 0) {
                entity.setTextOpacity((byte) Math.min(255, textOpacity));
            }
            if (bgColor != null) {
                entity.setBackgroundColor(bgColor);
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
        double  fwd             = parseValues(caster, FORWARD, level, 0);
        double  upw             = parseValues(caster, UPWARD, level, 0);
        double  rgt             = parseValues(caster, RIGHT, level, 0);
        boolean inheritRotation = settings.getBool(INHERIT_ROTATION, true);
        super.playPreview(onPreviewStop, caster, level, () -> {
            List<LivingEntity> newTargets = new ArrayList<>();
            for (LivingEntity target : targetSupplier.get()) {
                Location loc  = target.getLocation().clone();
                Vector   dir  = loc.getDirection().setY(0).normalize();
                Vector   side = dir.clone().crossProduct(UP);
                loc.add(dir.multiply(fwd)).add(0, upw, 0).add(side.multiply(rgt));
                if (!inheritRotation) {
                    loc.setYaw(0);
                    loc.setPitch(0);
                }
                newTargets.add(new TempEntity(loc));
            }
            return newTargets;
        });
    }
}
