/**
 * SkillAPI
 * com.sucy.skill.hook.DisguiseHook
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.hook;

import com.sucy.skill.log.Logger;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.*;
import me.libraryaddict.disguise.disguisetypes.watchers.ArmorStandWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.utilities.DisguiseValues;
import me.libraryaddict.disguise.utilities.reflection.FakeBoundingBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Handles calling functions from Lib's Disguise
 */
public class DisguiseHook
{
    /**
     * Disguises the target as a mob
     *
     * @param target target to disguise
     * @param type   type of mob to disguise as
     * @param adult  whether or not the mob is an adult
     */
    public static void disguiseMob(LivingEntity target, String type, boolean adult)
    {
        try
        {
            String name = target.getCustomName();
            DisguiseType disguise = DisguiseType.valueOf(type.toUpperCase().replace(" ", "_"));
            MobDisguise mobDisguise = new MobDisguise(disguise, adult);
            DisguiseAPI.disguiseToAll(target, mobDisguise);
            if (name != null)
                target.setCustomName(name);
        }
        catch (Exception ex)
        {
            Logger.invalid("Invalid mob disguise type: " + type);
        }
    }

    /**
     * Disguises the target as a player
     *
     * @param target target to disguise
     * @param player player to disguise as
     */
    public static void disguisePlayer(LivingEntity target, String player)
    {
        try
        {
            String name = target.getCustomName();
            PlayerDisguise playerDisguise = new PlayerDisguise(player);
            DisguiseAPI.disguiseToAll(target, playerDisguise);
            if (name != null)
                target.setCustomName(name);
        }
        catch (Exception ex)
        {
            Logger.invalid("Invalid player disguise: " + player);
        }
    }

    /**
     * Disguises the target as a miscellaneous thing
     *
     * @param target target to disguise
     * @param type   disguise type
     * @param data   disguise data value
     */
    public static void disguiseMisc(LivingEntity target, String type, int data)
    {
        try
        {
            String name = target.getCustomName();
            DisguiseType disguise = DisguiseType.valueOf(type.toUpperCase().replace(" ", "_"));
            MiscDisguise miscDisguise = new MiscDisguise(disguise, data);
            DisguiseAPI.disguiseToAll(target, miscDisguise);
            if (name != null)
                target.setCustomName(name);
        }
        catch (Exception ex)
        {
            Logger.invalid("Invalid misc disguise type: " + type);
        }
    }

    /**
     * Removes all disguises from the target
     *
     * @param target target to remove disguises from
     */
    public static void removeDisguise(LivingEntity target)
    {
        for (Disguise disguise : DisguiseAPI.getDisguises(target))
            disguise.removeDisguise();
    }

    public static FakeBoundingBox getFakeBoundingBox(Entity entity) {
        Disguise disguise = DisguiseAPI.getDisguise(entity);
        if (disguise instanceof MiscDisguise) {
            DisguiseValues values = DisguiseValues.getDisguiseValues(disguise.getType());
            if (values == null) return null;
            else return values.getAdultBox();
        } else if (disguise instanceof MobDisguise) {
            MobDisguise mobDisguise = (MobDisguise) disguise;
            DisguiseValues values = DisguiseValues.getDisguiseValues(mobDisguise.getType());
            if (values != null && values.getAdultBox() != null) {
                if (!mobDisguise.isAdult() && values.getBabyBox() != null) {
                    return values.getBabyBox();
                } else {
                    if (mobDisguise.getWatcher() != null) {
                        if (mobDisguise.getType() == DisguiseType.ARMOR_STAND) {
                            return (((ArmorStandWatcher)mobDisguise.getWatcher()).isSmall() ? values.getBabyBox() : values.getAdultBox());
                        }

                        if (mobDisguise.getType() == DisguiseType.SLIME || mobDisguise.getType() == DisguiseType.MAGMA_CUBE) {
                            double a = 0.51D * 0.255D * (double)((SlimeWatcher)mobDisguise.getWatcher()).getSize();
                            return new FakeBoundingBox(a, a, a);
                        }
                    }
                    return values.getAdultBox();
                }
            } else { return null; }
        } else if (disguise instanceof ModdedDisguise) { return null;
        } else if (disguise instanceof PlayerDisguise) {
            if (disguise.getWatcher() == null) {
                return new FakeBoundingBox(0.6, 0.6, 1.8);
            } else if (disguise.getEntity() != null && !disguise.getWatcher().getModifiedEntityAnimations()[1]) {
                return disguise.getEntity() instanceof Player && ((Player)disguise.getEntity()).isSneaking() ? new FakeBoundingBox(0.6, 0.6, 1.5) : new FakeBoundingBox(0.6, 0.6, 1.8);
            } else {
                return disguise.getWatcher().isSneaking() ? new FakeBoundingBox(0.6, 0.6, 1.5) : new FakeBoundingBox(0.6, 0.6, 1.8);
            }
        } else return null;
    }
}
