package com.sucy.skill;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

public class DataGenerator {
    public static void main(String[] args) {
        if (args.length != 1) { throw new IllegalArgumentException(); }
        String version = args[0].replace('.', '_');
        File file = new File("output/"+args[0]+".js");
        file.delete();
        file.getParentFile().mkdirs();

        try (OutputStream out = new FileOutputStream(file)) {
            out.write(("var DATA_"+version+" = {\n    MATERIALS: [\n").getBytes());
            for (Material material : Material.values()) {
                writeEnumConstant(out, material);
            }
            out.write(("    ],\n    DAMAGEABLE_MATERIALS: [\n").getBytes());
            for (Material material : Material.values()) {
                if (material.getMaxDurability() > 0) { writeEnumConstant(out, material); }
            }
            out.write(("    ],\n    SOUNDS: [\n").getBytes());
            for (Sound sound : Sound.values()) {
                writeEnumConstant(out, sound);
            }
            out.write(("    ],\n    ENTITIES: [\n").getBytes());
            for (EntityType entityType : EntityType.values()) {
                writeEnumConstant(out, entityType);
            }
            out.write(("    ],\n    BIOMES: [\n").getBytes());
            for (Biome biome : Biome.values()) {
                writeEnumConstant(out, biome);
            }
            out.write(("    ],\n    POTIONS: [\n").getBytes());
            for (Field field : PotionEffectType.class.getDeclaredFields()) {
                if (field.getType().isAssignableFrom(PotionEffectType.class) && !field.isAnnotationPresent(Deprecated.class)) {
                    out.write(("        \""+sentenceCase(field.getName())+"\",\n").getBytes());
                }
            }
            out.write(("    ],\n    PARTICLES: [\n").getBytes());
            for (Particle particle : Particle.values()) {
                writeEnumConstant(out, particle);
            }
            out.write(("    ],\n    DAMAGE_TYPES: [\n").getBytes());
            for (EntityDamageEvent.DamageCause damageCause : EntityDamageEvent.DamageCause.values()) {
                writeEnumConstant(out, damageCause);
            }
            out.write(("    ],\n    PROJECTILES: [\n").getBytes());
            for (EntityType entityType : EntityType.values()) {
                Class<? extends Entity> entityClass = entityType.getEntityClass();
                if (entityClass != null && Projectile.class.isAssignableFrom(entityClass)) {
                    writeEnumConstant(out, entityType);
                }
            }
            out.write(("    ],\n    MOB_DISGUISES: [\n").getBytes());
            for (EntityType entityType : EntityType.values()) {
                if (DisguiseType.getType(entityType).isMob()) { writeEnumConstant(out, entityType); }
            }
            out.write(("    ],\n    MOB_DISGUISES: [\n").getBytes());
            for (EntityType entityType : EntityType.values()) {
                if (DisguiseType.getType(entityType).isMisc()) { writeEnumConstant(out, entityType); }
            }
            out.write(("    ]\n"+
                    "};\n\n"+
                    "var keys = Object.keys(DATA_"+version+");\n"+
                    "for (var i = 0; i < keys.length; i++) {\n"+
                    "    DATA_"+version+"[keys[i]].sort();\n"+
                    "}\n"+
                    "DATA_"+version+".ANY_POTION = DATA_"+version+".POTIONS.slice().splice(0, 0, 'Any');").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public static void writeEnumConstant(OutputStream out, Enum<?> enumConstant) throws IOException {
        try {
            Field field = enumConstant.getClass().getField(enumConstant.name());
            if (field.isAnnotationPresent(Deprecated.class)) { return; }
            String name = enumConstant.name();
            if (name.contains("LEGACY") || name.equals("UNKNOWN")) { return; }
            out.write(("        \""+sentenceCase(enumConstant.name())+"\",\n").getBytes());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static String sentenceCase(String value) {
        if (value.isEmpty()) { return value; }
        return value.substring(0, 1).replace('_', ' ').toUpperCase()+value.substring(1).replace('_', ' ').toLowerCase();
    }
}
