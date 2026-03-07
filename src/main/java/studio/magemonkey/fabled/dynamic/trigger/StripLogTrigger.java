package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

import java.util.List;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class StripLogTrigger implements Trigger<PlayerInteractEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "STRIP_LOG";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerInteractEvent> getEvent() {
        return PlayerInteractEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerInteractEvent event, final int level, final Settings settings) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return false;

        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();

        if (block == null || item == null || item.getType() == Material.AIR) return false;
        if (!item.getType().toString().endsWith("_AXE")) return false;

        final List<String> types = settings.getStringList("material");
        final Material blockType = block.getType();
        final String blockName = blockType.name();

        boolean matchesList = types.isEmpty()
                || types.contains("Any")
                || types.stream().anyMatch(mat ->
                    blockName.equalsIgnoreCase(mat.trim().replace(' ', '_').toUpperCase())
                );

        // Add strippable check here:
        boolean isStrippableBlock = isStrippable(blockType);

        return matchesList && isStrippableBlock;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerInteractEvent event, final CastData data) {
        data.put("api-block-type", event.getClickedBlock().getType().name());
        data.put("api-block-loc", event.getClickedBlock().getLocation());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerInteractEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerInteractEvent event, final Settings settings) {
        return event.getPlayer();
    }

    public static boolean isStrippable(Material material) {
        String name = material.name();
        // Check it contains one of the key substrings but NOT "STRIPPED"
        return !name.contains("STRIPPED") && 
            (name.contains("LOG") || name.contains("WOOD") || 
                name.contains("STEM") || name.contains("HYPHAE"));
    }

}
