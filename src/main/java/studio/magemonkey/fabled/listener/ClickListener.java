/**
 * Fabled
 * studio.magemonkey.fabled.listener.ClickListener
 * <p>
 * The MIT License (MIT)
 * <p>
 * © 2026 VoidEdge
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package studio.magemonkey.fabled.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.event.KeyPressEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles transferring click actions by the player to
 * combos that cast skills.
 */
public class ClickListener extends FabledListener {
    private Map<UUID, Long> dropPlayers = new HashMap<>();

    /**
     * Registers clicks as they happen
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        UUID playerId = event.getPlayer().getUniqueId();
        Long dropTime = dropPlayers.get(playerId);

        if (dropTime != null) {
            long now = System.currentTimeMillis();

            // If a player dropped an item within 3 ticks ignore click event.
            if (now - dropTime < 150) {
                return;
            } else {
                dropPlayers.remove(playerId); // cleanup
            }
        }

        // Left clicks
        if (!Fabled.getSettings().isAnimationLeftClick()) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Bukkit.getServer()
                        .getPluginManager()
                        .callEvent(new KeyPressEvent(event.getPlayer(), KeyPressEvent.Key.LEFT));
                return;
            }
        }

        // Right clicks
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(new KeyPressEvent(event.getPlayer(), KeyPressEvent.Key.RIGHT));
        }
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (Fabled.getSettings().isInteractRightClick()) {
            KeyPressEvent keyEvent = new KeyPressEvent(event.getPlayer(), KeyPressEvent.Key.RIGHT);
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(keyEvent);
        }
    }

    @EventHandler
    public void animation(PlayerAnimationEvent event) {
        if (!Fabled.getSettings().isAnimationLeftClick()) return;

        KeyPressEvent keyEvent = new KeyPressEvent(event.getPlayer(), KeyPressEvent.Key.LEFT);
        Bukkit.getServer()
                .getPluginManager()
                .callEvent(keyEvent);
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        // Keep track of players who have dropped items.
        UUID playerId = event.getPlayer().getUniqueId();
        dropPlayers.put(playerId, System.currentTimeMillis());
        Bukkit.getScheduler()
                .runTaskLater(Fabled.getPlugin(Fabled.class),
                        () -> dropPlayers.remove(playerId),
                        3L); // 3 ticks = ~150ms

        Bukkit.getServer().getPluginManager().callEvent(new KeyPressEvent(event.getPlayer(), KeyPressEvent.Key.Q));
    }

}
