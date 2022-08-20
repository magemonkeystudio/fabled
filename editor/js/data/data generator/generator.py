# Used to automatically generate data for new Minecraft versions
# Required .java files in workspace:
# - org.bukkit.block.Biome
# - org.bukkit.event.entity.EntityDamageEvent
# - org.bukkit.Material
# - org.bukkit.Sound
# - org.bukkit.entity.EntityType
# - org.bukkit.potion.PotionEffectType
# - org.bukkit.Particle
#
# Drop this in your BuildTools directory under Spigot/Spigot-API and run the script.
# Copy the generated .js file to the data directory.

import os
import re

locations = {}


def get_files(fold):
    global locations
    files_lst = []
    for (dirPath, dirNames, fileNames) in os.walk(fold):
        files_lst.extend(fileNames)
        intersections = []
        for i in requiredFiles:
            if i in fileNames:
                intersections.append(i)

        for int in intersections:
            path = os.path.join(dirPath, int)
            print("Found", int, "at", path)
            locations[int] = path

        # break
    return files_lst


folder = os.getcwd()
requiredFiles = {'Biome.java', 'EntityDamageEvent.java', 'EntityType.java', 'Material.java', 'Particle.java',
                 'PotionEffectType.java', 'Sound.java'}
files = get_files(folder)
assert requiredFiles.issubset(files), 'Missing .java files in workspace'
print()
version = input('Enter Minecraft version with the correct format (Example: 1.16): ')
version = version[2:]

damageable = []

def read_enum(file_name, enum_start):
    # Init sets
    last_chars = {',', ';'}
    enum_enders = {'private', 'public', '@NotNull', '@Override'}

    values = []
    file_name = file_name.replace('\\', '/')
    read_file = open(file_name, 'r')
    reached_enum = False
    is_deprecated = False
    for line in read_file.readlines():
        line = line.strip()
        if (not reached_enum) and (line == enum_start):
            # Reached enum
            reached_enum = True
        elif reached_enum:
            # Currently in enum
            if file_name.endswith('Material.java'):
                match = re.search(r'^\s*?(?!LEGACY_)([A-Z_]+)\(.*?, .*?, (\d*?)\)', line)
                if not match is None:
                    value = match.group(1)
                    damageable.append(value[0] + value[1:].lower().replace('_', ' '))
            if '(' in line:
                line = line[:line.find('(')]
            if line.isupper():
                # It´s a value

                items = [line]
                if ',' in line:
                    items = [it.strip() for it in line.split(",")]
                for it in items:
                    if len(it) == 0:
                        continue
                    if is_deprecated or it[:7] == 'LEGACY_':
                        # Is deprecated or legacy, don't add it
                        is_deprecated = False
                        continue
                    else:
                        # Add it
                        value = it[0].upper() + it[1:].lower().replace('_', ' ')
                        if it[-1] in last_chars:
                            # Must remove last char
                            value = value[:-1]
                        values.append(value)
            elif line.split(' ', 1)[0] in enum_enders:
                # Reached last value, stop
                break
            elif line == '@Deprecated':
                is_deprecated = True
    read_file.close()
    return values


def read_class(file_name, instance_definer):
    values = []
    file_name = file_name.replace('\\', '/')
    read_file = open(file_name, 'r')
    is_deprecated = False
    for line in read_file.readlines():
        line = line.strip()
        if instance_definer in line:
            line = line[37:]
            line = line[:line.find(' ')]
            if line.isupper():
                # It´s a value
                if is_deprecated or line[:7] == 'LEGACY_':
                    # Is deprecated or legacy, don't add it
                    is_deprecated = False
                    continue
                else:
                    # Add it
                    values.append(line[0].upper() + line[1:].lower().replace('_', ' '))
        elif line == '@Deprecated':
            is_deprecated = True
    read_file.close()
    return values


def list_to_string(list):
    s = '['
    for entry in list:
        s = s + '\n        "' + entry + '",'
    return s[:-1] + '\n    ]'


# Get Biomes
biomes = read_enum(locations['Biome.java'], 'public enum Biome implements Keyed {')
assert len(biomes) > 0, "Couldn't read any Biome in Biome.java"
# print(biomes)
print("Successfully read", len(biomes), "Biomes.")

# Get DamageTypes
damages = read_enum(locations['EntityDamageEvent.java'], 'public static enum DamageCause {')
assert len(damages) > 0, "Couldn't read any DamageType in EntityDamageEvent.java"
# print(damages)
print("Successfully read", len(damages), "DamageTypes.")

# Get EntityTypes
entities = read_enum(locations['EntityType.java'], 'public enum EntityType implements Keyed {')
assert len(entities) > 0, "Couldn't read any Entity in EntityType.java"
# print(entities)
print("Successfully read", len(entities), "EntityTypes.")

# Get Materials
materials = read_enum(locations['Material.java'], 'public enum Material implements Keyed {')
assert len(materials) > 0, "Couldn't read any Material in Material.java"
# print(materials)
print("Successfully read", len(materials), "Materials.")

# Get Particles
particles = read_enum(locations['Particle.java'], 'public enum Particle {')
assert len(particles) > 0, "Couldn't read any Particle in Particle.java"
# print(particles)
print("Successfully read", len(particles), "Particles.")

# Get Effects
effects = read_class(locations['PotionEffectType.java'], 'public static final PotionEffectType')
assert len(effects) > 0, "Couldn't read any PotionEffectType in PotionEffectType.java"
# print(effects)
print("Successfully read", len(effects), "PotionEffectTypes.")

# Get Sounds
sounds = read_enum(locations['Sound.java'], 'public enum Sound implements Keyed {')
assert len(sounds) > 0, "Couldn't read any Sound in Sound.java"
# print(sounds)
print("Successfully read", len(sounds), "Sounds.")

# Generate javascript file
file = open('../1.' + version + '.js', "w")
fileContent = 'var DATA_' + version + ' = {\n    MATERIALS: ' + list_to_string(
    materials) + ',\n    DAMAGEABLE_MATERIALS: ' + list_to_string(damageable) + ',\n    SOUNDS: ' + list_to_string(
    sounds) + ',\n    ENTITIES: ' + list_to_string(entities) + ',\n    BIOMES: ' + list_to_string(
    biomes) + ',\n    POTIONS: ' + list_to_string(effects) + ',\n    PARTICLES: ' + list_to_string(
    particles) + ',\n    DAMAGE_TYPES: ' + list_to_string(damages) + '\n};\n\nvar keys = Object.keys(DATA_' + version + ');\nfor (var i = 0; i < keys.length; i++) {\n    DATA_' + version + '[keys[i]].sort();\n}\nDATA_' + version + '.ANY_POTION = DATA_' + version + '.POTIONS.slice().splice(0, 0, \'Any\');'
file.write(fileContent)
file.close()
print('1.' + version + '.js succesfully generated.')
