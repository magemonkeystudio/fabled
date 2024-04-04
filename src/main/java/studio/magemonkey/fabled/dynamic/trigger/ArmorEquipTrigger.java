package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.codex.api.armor.ArmorEquipEvent;
import org.bukkit.entity.LivingEntity;

public class ArmorEquipTrigger implements Trigger<ArmorEquipEvent> {

    @Override
    public String getKey() {return "ARMOR_EQUIP";}

    @Override
    public Class<ArmorEquipEvent> getEvent() {return ArmorEquipEvent.class;}

    @Override
    public boolean shouldTrigger(ArmorEquipEvent event, int level, Settings settings) {
        String armorType = event.getType().name();
        return settings.getStringList("slots").stream().anyMatch(str -> {
            str = str.replace(" ", "_");
            return str.equalsIgnoreCase("Any") || str.equalsIgnoreCase(armorType);
        });
    }

    @Override
    public void setValues(ArmorEquipEvent event, final CastData data) {

    }

    @Override
    public LivingEntity getCaster(ArmorEquipEvent event) {return event.getPlayer();}

    @Override
    public LivingEntity getTarget(ArmorEquipEvent event, Settings settings) {return event.getPlayer();}
}
