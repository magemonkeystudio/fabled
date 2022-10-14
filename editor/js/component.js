var hoverSpace;

function canDrop(thing, target) {
    if (thing == target) return false;

    let temp = target;
    while (temp.parentNode) {
        temp = temp.parentNode;
        if (temp == thing) return false;
    }
    return true;
}

/**
 * Types of components
 */
var Type = {
    TRIGGER   : 'trigger',
    TARGET    : 'target',
    CONDITION : 'condition',
    MECHANIC  : 'mechanic'
};

/**
 * Available triggers for activating skill effects
 */
var Trigger = {
    BLOCK_BREAK          : { name: 'Block Break',          container: true, construct: TriggerBlockBreak         },
    BLOCK_PLACE          : { name: 'Block Place',          container: true, construct: TriggerBlockPlace         },
    CAST                 : { name: 'Cast',                 container: true, construct: TriggerCast               },
    CLEANUP              : { name: 'Cleanup',              container: true, construct: TriggerCleanup            },
    CROUCH               : { name: 'Crouch',               container: true, construct: TriggerCrouch             },
    DEATH                : { name: 'Death',                container: true, construct: TriggerDeath              },
    DROP_ITEM            : { name: 'Drop Item',            container: true, construct: TriggerDropItem           },
    ENVIRONMENT_DAMAGE   : { name: 'Environment Damage',   container: true, construct: TriggerEnvironmentDamage  },
    FISH                 : { name: 'Fishing',              container: true, construct: TriggerFishing            },
    FISH_BITE            : { name: 'Fishing Bite',         container: true, construct: TriggerFishingBite        },
    FISH_FAIL            : { name: 'Fishing Fail',         container: true, construct: TriggerFishingFail        },
    FISH_GRAB            : { name: 'Fishing Grab',         container: true, construct: TriggerFishingGrab        },
    FISH_GROUND          : { name: 'Fishing Ground',       container: true, construct: TriggerFishingGround      },
    FISH_REEL            : { name: 'Fishing Reel',         container: true, construct: TriggerFishingReel        },
    INITIALIZE           : { name: 'Initialize',           container: true, construct: TriggerInitialize         },
    ITEMSWAP             : { name: 'Item Swap',            container: true, construct: TriggerItemSwap           },
    KILL                 : { name: 'Kill',                 container: true, construct: TriggerKill               },
    LAND                 : { name: 'Land',                 container: true, construct: TriggerLand               },
    LAUNCH               : { name: 'Launch',               container: true, construct: TriggerLaunch             },
    LEFT_CLICK           : { name: 'Left Click',           container: true, construct: TriggerLeftClick          },
    MOVE                 : { name: 'Move',                 container: true, construct: TriggerMove               },
    PHYSICAL_DAMAGE      : { name: 'Physical Damage',      container: true, construct: TriggerPhysicalDamage     },
    RIGHT_CLICK          : { name: 'Right Click',          container: true, construct: TriggerRightClick         },
    SKILL_DAMAGE         : { name: 'Skill Damage',         container: true, construct: TriggerSkillDamage        },
    TOOK_PHYSICAL_DAMAGE : { name: 'Took Physical Damage', container: true, construct: TriggerTookPhysicalDamage },
    TOOK_SKILL_DAMAGE    : { name: 'Took Skill Damage',    container: true, construct: TriggerTookSkillDamage    }
};

/**
 * Available target component data
 */
var Target = {
    AREA     : { name: 'Area',     container: true, construct: TargetArea     },
    CONE     : { name: 'Cone',     container: true, construct: TargetCone     },
    LINEAR   : { name: 'Linear',   container: true, construct: TargetLinear   },
    LOCATION : { name: 'Location', container: true, construct: TargetLocation },
    NEAREST  : { name: 'Nearest',  container: true, construct: TargetNearest  },
    OFFSET   : { name: 'Offset',   container: true, construct: TargetOffset   },
    REMEMBER : { name: 'Remember', container: true, construct: TargetRemember },
    SELF     : { name: 'Self',     container: true, construct: TargetSelf     },
    SINGLE   : { name: 'Single',   container: true, construct: TargetSingle   }
};

/**
 * Available condition component data
 */
var Condition = {
    ALTITUDE       : { name: 'Altitude',       container: true, construct: ConditionAltitude      },
    ARMOR          : { name: 'Armor',          container: true, construct: ConditionArmor         },
    ATTRIBUTE      : { name: 'Attribute',      container: true, construct: ConditionAttribute     },
    BIOME          : { name: 'Biome',          container: true, construct: ConditionBiome         },
    BLOCK          : { name: 'Block',          container: true, construct: ConditionBlock         },
    BURNING        : { name: 'Burning',        container: true, construct: ConditionBurning       },
    CEILING        : { name: 'Ceiling',        container: true, construct: ConditionCeiling       },
    CHANCE         : { name: 'Chance',         container: true, construct: ConditionChance        },
    CLASS          : { name: 'Class',          container: true, construct: ConditionClass         },
    CLASS_LEVEL    : { name: 'Class Level',    container: true, construct: ConditionClassLevel    },
    COMBAT         : { name: 'Combat',         container: true, construct: ConditionCombat        },
    CROUCH         : { name: 'Crouch',         container: true, construct: ConditionCrouch        },
    DIRECTION      : { name: 'Direction',      container: true, construct: ConditionDirection     },
    ELEVATION      : { name: 'Elevation',      container: true, construct: ConditionElevation     },
    ELSE           : { name: 'Else',           container: true, construct: ConditionElse          },
    ENTITY_TYPE    : { name: 'Entity Type',    container: true, construct: ConditionEntityType    },
    FIRE           : { name: 'Fire',           container: true, construct: ConditionFire          },
    FLAG           : { name: 'Flag',           container: true, construct: ConditionFlag          },
    FOOD           : { name: 'Food',           container: true, construct: ConditionFood          },
    GROUND         : { name: 'Ground',         container: true, construct: ConditionGround        },
    HEALTH         : { name: 'Health',         container: true, construct: ConditionHealth        },
    INVENTORY      : { name: 'Inventory',      container: true, construct: ConditionInventory     },
    ITEM           : { name: 'Item',           container: true, construct: ConditionItem          },
    LIGHT          : { name: 'Light',          container: true, construct: ConditionLight         },
    MANA           : { name: 'Mana',           container: true, construct: ConditionMana          },
    MONEY          : { name: 'Money',          container: true, construct: ConditionMoney         },
    MOUNTED        : { name: 'Mounted',        container: true, construct: ConditionMounted       },
    MOUNTING       : { name: 'Mounting',       container: true, construct: ConditionMounting      },
    MYTHICMOB_TYPE : { name: 'MythicMob Type', container: true, construct: ConditionMythicMobType },
    NAME           : { name: 'Name',           container: true, construct: ConditionName          },
    OFFHAND        : { name: 'Offhand',        container: true, construct: ConditionOffhand       },
    PERMISSION     : { name: 'Permission',     container: true, construct: ConditionPermission    },
    POTION         : { name: 'Potion',         container: true, construct: ConditionPotion        },
    SKILL_LEVEL    : { name: 'Skill Level',    container: true, construct: ConditionSkillLevel    },
    SLOT           : { name: 'Slot',           container: true, construct: ConditionSlot          },
    STATUS         : { name: 'Status',         container: true, construct: ConditionStatus        },
    TIME           : { name: 'Time',           container: true, construct: ConditionTime          },
    TOOL           : { name: 'Tool',           container: true, construct: ConditionTool          },
    VALUE          : { name: 'Value',          container: true, construct: ConditionValue         },
    WATER          : { name: 'Water',          container: true, construct: ConditionWater         },
    WEATHER        : { name: 'Weather',        container: true, construct: ConditionWeather       },
    WORLD          : { name: 'World',          container: true, construct: ConditionWorld         }
};

/**
 * Available mechanic component data
 */
var Mechanic = {
    ARMOR               : { name: 'Armor',               container: false, construct: MechanicArmor              },
    ARMOR_STAND         : { name: 'Armor Stand',         container: true,  construct: MechanicArmorStand         },
    ARMOR_STAND_POSE    : { name: 'Armor Stand Pose',    container: false, construct: MechanicArmorStandPose     },
    ATTRIBUTE           : { name: 'Attribute',           container: false, construct: MechanicAttribute          },
    BLOCK               : { name: 'Block',               container: false, construct: MechanicBlock              },
    BUFF                : { name: 'Buff',                container: false, construct: MechanicBuff               },
    CANCEL              : { name: 'Cancel',              container: false, construct: MechanicCancel             },
    CHANNEL             : { name: 'Channel',             container: true,  construct: MechanicChannel            },
    CLEANSE             : { name: 'Cleanse',             container: false, construct: MechanicCleanse            },
    COMMAND             : { name: 'Command',             container: false, construct: MechanicCommand            },
    COOLDOWN            : { name: 'Cooldown',            container: false, construct: MechanicCooldown           },
    DAMAGE              : { name: 'Damage',              container: false, construct: MechanicDamage             },
    DAMAGE_BUFF         : { name: 'Damage Buff',         container: false, construct: MechanicDamageBuff         },
    DAMAGE_LORE         : { name: 'Damage Lore',         container: false, construct: MechanicDamageLore         },
    DEFENSE_BUFF        : { name: 'Defense Buff',        container: false, construct: MechanicDefenseBuff        },
    DELAY               : { name: 'Delay',               container: true,  construct: MechanicDelay              },
    DISGUISE            : { name: 'Disguise',            container: false, construct: MechanicDisguise           },
    DURABILITY          : { name: 'Durability',          container: false, construct: MechanicDurability         },
    EXPLOSION           : { name: 'Explosion',           container: false, construct: MechanicExplosion          },
    FIRE                : { name: 'Fire',                container: false, construct: MechanicFire               },
    FLAG                : { name: 'Flag',                container: false, construct: MechanicFlag               },
    FLAG_CLEAR          : { name: 'Flag Clear',          container: false, construct: MechanicFlagClear          },
    FLAG_TOGGLE         : { name: 'Flag Toggle',         container: false, construct: MechanicFlagToggle         },
    FOOD                : { name: 'Food',                container: false, construct: MechanicFood               },
    FORGET_TARGETS      : { name: 'Forget Targets',      container: false, construct: MechanicForgetTargets      },
    HEAL                : { name: 'Heal',                container: false, construct: MechanicHeal               },
    HEALTH_SET          : { name: 'Health Set',          container: false, construct: MechanicHealthSet          },
    HELD_ITEM           : { name: 'Held Item',           container: false, construct: MechanicHeldItem           },
    IMMUNITY            : { name: 'Immunity',            container: false, construct: MechanicImmunity           },
    INTERRUPT           : { name: 'Interrupt',           container: false, construct: MechanicInterrupt          },
    ITEM                : { name: 'Item',                container: false, construct: MechanicItem               },
    ITEM_DROP           : { name: 'Item Drop',           container: false, construct: MechanicItemDrop           },
    ITEM_PROJECTILE     : { name: 'Item Projectile',     container: true,  construct: MechanicItemProjectile     },
    ITEM_REMOVE         : { name: 'Item Remove',         container: false, construct: MechanicItemRemove         },
    LAUNCH              : { name: 'Launch',              container: false, construct: MechanicLaunch             },
    LIGHTNING           : { name: 'Lightning',           container: true,  construct: MechanicLightning          },
    MANA                : { name: 'Mana',                container: false, construct: MechanicMana               },
    MESSAGE             : { name: 'Message',             container: false, construct: MechanicMessage            },
    MINE                : { name: 'Mine',                container: false, construct: MechanicMine               },
    MONEY               : { name: 'Money',               container: false, construct: MechanicMoney              },
    PARTICLE            : { name: 'Particle',            container: false, construct: MechanicParticle           },
    PARTICLE_ANIMATION  : { name: 'Particle Animation',  container: false, construct: MechanicParticleAnimation  },
    PARTICLE_EFFECT     : { name: 'Particle Effect',     container: false, construct: MechanicParticleEffect     },
    CANCEL_EFFECT       : { name: 'Cancel Effect',       container: false, construct: MechanicCancelEffect       },
    PARTICLE_PROJECTILE : { name: 'Particle Projectile', container: true,  construct: MechanicParticleProjectile },
    PASSIVE             : { name: 'Passive',             container: true,  construct: MechanicPassive            },
    PERMISSION          : { name: 'Permission',          container: false, construct: MechanicPermission         },
    POTION              : { name: 'Potion',              container: false, construct: MechanicPotion             },
    POTION_PROJECTILE   : { name: 'Potion Projectile',   container: true,  construct: MechanicPotionProjectile   },
    PROJECTILE          : { name: 'Projectile',          container: true,  construct: MechanicProjectile         },
    PURGE               : { name: 'Purge',               container: false, construct: MechanicPurge              },
    PUSH                : { name: 'Push',                container: false, construct: MechanicPush               },
    REMEMBER_TARGETS    : { name: 'Remember Targets',    container: false, construct: MechanicRememberTargets    },
    REPEAT              : { name: 'Repeat',              container: true,  construct: MechanicRepeat             },
    SOUND               : { name: 'Sound',               container: false, construct: MechanicSound              },
    Stat                : { name: 'Stat',                container: false, construct: MechanicStat               },
    STATUS              : { name: 'Status',              container: false, construct: MechanicStatus             },
    TAUNT               : { name: 'Taunt',               container: false, construct: MechanicTaunt              },
    TRIGGER             : { name: 'Trigger',             container: true,  construct: MechanicTrigger            },
    VALUE_ADD           : { name: 'Value Add',           container: false, construct: MechanicValueAdd           },
    VALUE_ATTRIBUTE     : { name: 'Value Attribute',     container: false, construct: MechanicValueAttribute     },
    VALUE_COPY          : { name: 'Value Copy',          container: false, construct: MechanicValueCopy          },
    VALUE_DISTANCE      : { name: 'Value Distance',      container: false, construct: MechanicValueDistance      },
    VALUE_HEALTH        : { name: 'Value Health',        container: false, construct: MechanicValueHealth        },
    VALUE_LOCATION      : { name: 'Value Location',      container: false, construct: MechanicValueLocation      },
    VALUE_LORE          : { name: 'Value Lore',          container: false, construct: MechanicValueLore          },
    VALUE_LORE_SLOT     : { name: 'Value Lore Slot',     container: false, construct: MechanicValueLoreSlot      },
    VALUE_MANA          : { name: 'Value Mana',          container: false, construct: MechanicValueMana          },
    VALUE_MULTIPLY      : { name: 'Value Multiply',      container: false, construct: MechanicValueMultiply      },
    VALUE_PLACEHOLDER   : { name: 'Value Placeholder',   container: false, construct: MechanicValuePlaceholder   },
    VALUE_RANDOM        : { name: 'Value Random',        container: false, construct: MechanicValueRandom        },
    VALUE_SET           : { name: 'Value Set',           container: false, construct: MechanicValueSet           },
    WARP                : { name: 'Warp',                container: false, construct: MechanicWarp               },
    WARP_LOC            : { name: 'Warp Location',       container: false, construct: MechanicWarpLoc            },
    WARP_RANDOM         : { name: 'Warp Random',         container: false, construct: MechanicWarpRandom         },
    WARP_SWAP           : { name: 'Warp Swap',           container: false, construct: MechanicWarpSwap           },
    WARP_TARGET         : { name: 'Warp Target',         container: false, construct: MechanicWarpTarget         },
    WARP_VALUE          : { name: 'Warp Value',          container: false, construct: MechanicWarpValue          },
    WOLF                : { name: 'Wolf',                container: true,  construct: MechanicWolf               }
};

var saveIndex;

/**
 * Represents a component of a dynamic skill
 *
 * @param {string}    name      - name of the component
 * @param {string}    type      - type of the component
 * @param {boolean}   container - whether the component can contain others
 * @param {Component} [parent]  - parent of the component if any
 *
 * @constructor
 */
function Component(name, type, container, parent) {
    this.name       = name;
    this.type       = type;
    this.container  = container;
    this.parent     = parent;
    this.html       = undefined;
    this.components = [];
    this.data       = [new StringValue('Icon Key', 'icon-key', '').setTooltip('The key used by the component in the Icon Lore. If this is set to "example" and has a value name of "value", it can be referenced using the string "{attr:example.value}".')];
    if (this.type == Type.MECHANIC) {
        this.data.push(new ListValue('Counts as Cast', 'counts', ['True', 'False'], 'True')
            .setTooltip('Whether this mechanic running treats the skill as "casted" and will consume mana and start the cooldown. Set to false if it is a mechanic appled when the skill fails such as cleanup or an error message.')
        );
    } else if (this.type == Type.TRIGGER && name != 'Cast' && name != 'Initialize' && name != 'Cleanup') {
        this.data.push(new ListValue('Mana', 'mana', ['True', 'False'], 'False')
            .setTooltip('Whether this trigger requires the mana cost to activate')
        );
        this.data.push(new ListValue('Cooldown', 'cooldown', ['True', 'False'], 'False')
            .setTooltip('Whether this trigger requires to be off cooldown to activate')
        );
    }

    this.dataKey      = 'data';
    this.componentKey = 'children';
}

Component.prototype.dupe = function (parent) {
    let i;
    let ele = new Component(this.name, this.type, this.container, parent);
    for (i = 0; i < this.components.length; i++) {
        ele.components.push(this.components[i].dupe(ele));
    }
    ele.data = ele.data.slice(0, 1);
    for (i = ele.data.length; i < this.data.length; i++) {
        ele.data.push(copyRequirements(this.data[i], this.data[i].dupe()));
    }
    ele.description = this.description;
    return ele;
};

/**
 * Creates the builder HTML element for the component and
 * appends it onto the target HTML element.
 *
 * @param {Element} target - the HTML element to append the result to
 */
Component.prototype.createBuilderHTML = function (target) {
    // Create the wrapping divs with the appropriate classes
    let container  = document.createElement('div');
    container.comp = this;
    if (this.type == Type.TRIGGER) {
        container.className = 'componentWrapper';
    }

    let div       = document.createElement('div');
    div.className = 'component ' + this.type;
    if (this.type != Type.TRIGGER) {
        div.draggable   = true;
        div.ondragstart = this.drag;
    }
    div.ondrop = this.drop;
    if (this.container) {
        div.ondragover = this.allowDrop;
    }

    // Component label
    let label       = document.createElement('h3');
    label.title     = 'Edit ' + this.name + ' options';
    label.className = this.type + 'Label';
    label.innerHTML = this.name;
    label.component = this;
    label.addEventListener('click', function (e) {
        this.component.createFormHTML();
        showSkillPage('skillForm');
    });
    div.appendChild(label);

    // Container components can add children so they get a button
    if (this.container) {
        let add       = document.createElement('div');
        add.className = 'builderButton';
        add.innerHTML = '+ Add Child';
        add.component = this;
        add.addEventListener('click', function (e) {
            activeComponent = this.component;
            showSkillPage('componentChooser');
        });
        div.appendChild(add);

        let vision              = document.createElement('div');
        vision.title            = 'Hide Children';
        vision.className        = 'builderButton smallButton';
        vision.style.background = 'url("editor/img/eye.png") no-repeat center #222';
        vision.component        = this;
        vision.addEventListener('click', function (e) {
            let comp = this.component;
            if (comp.childrenHidden) {
                comp.childDiv.style.display = 'block';
                this.style.backgroundImage  = 'url("editor/img/eye.png")';
            } else {
                comp.childDiv.style.display = 'none';
                this.style.backgroundImage  = 'url("editor/img/eyeShaded.png")';
            }
            comp.childrenHidden = !comp.childrenHidden;
        });
        div.appendChild(vision);
        this.childrenHidden = false;
    }

    // Add the duplicate button
    if (this.type != Type.TRIGGER) {
        let duplicate              = document.createElement('div');
        duplicate.className        = 'builderButton smallButton';
        duplicate.title            = 'Duplicate';
        duplicate.style.background = 'url("editor/img/duplicate.png") no-repeat center #222';
        duplicate.component        = this;
        duplicate.addEventListener('click', function (e) {
            let comp = this.component;
            let copy = comp.dupe(comp.parent);
            comp.parent.components.push(copy);
            copy.createBuilderHTML(comp.parent.html);
        });
        div.appendChild(duplicate);
    }

    // Add the remove button
    let remove              = document.createElement('div');
    remove.title            = 'Remove';
    remove.className        = 'builderButton smallButton cancelButton';
    remove.style.background = 'url("editor/img/delete.png") no-repeat center #f00';
    remove.component        = this;
    remove.addEventListener('click', function (e) {
        let list = this.component.parent.components;
        for (let i = 0; i < list.length; i++) {
            if (list[i] == this.component) {
                list.splice(i, 1);
                break;
            }
        }
        this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode);
    });
    div.appendChild(remove);

    container.appendChild(div);

    // Apply child components
    let childContainer       = document.createElement('div');
    childContainer.className = 'componentChildren';
    if (this.components.length > 0) {
        for (let i = 0; i < this.components.length; i++) {
            this.components[i].createBuilderHTML(childContainer);
        }
    }
    container.appendChild(childContainer);
    this.childDiv = childContainer;

    // Append the content
    target.appendChild(container);

    this.html = childContainer;
};

Component.prototype.allowDrop = function (e) {
    e.preventDefault();
    if (hoverSpace) {
        hoverSpace.style.marginBottom = '0px';
        hoverSpace.onmouseout         = undefined;
    }
    hoverSpace = e.target;
    while (hoverSpace.className.indexOf('component') < 0) {
        hoverSpace = hoverSpace.parentNode;
    }
    let thing = document.getElementById('dragComponent');
    if (hoverSpace.id != 'dragComponent' && hoverSpace.parentNode.comp.container && canDrop(thing, hoverSpace)) {
        hoverSpace.style.marginBottom = '30px';
        hoverSpace.onmouseout         = function () {
            if (!hoverSpace) {
                this.onmouseout = undefined;
                return;
            }
            hoverSpace.style.marginBottom = '0px';
            hoverSpace.onmouseout         = undefined;
            hoverSpace                    = undefined;
        }
    } else hoverSpace = undefined;
};

Component.prototype.drag = function (e) {
    e.dataTransfer.setData('text', 'anything');
    let dragged = document.getElementById('dragComponent');
    if (dragged) {
        dragged.id = '';
    }
    e.target.id = 'dragComponent';
};

Component.prototype.drop = function (e) {
    if (hoverSpace) {
        hoverSpace.style.marginBottom = '0px';
        hoverSpace                    = undefined;
    }

    e.preventDefault();
    let thing  = document.getElementById('dragComponent').parentNode;
    let target = e.target;
    while (target.className.indexOf('component') < 0) {
        target = target.parentNode;
    }
    if (target.id == 'dragComponent' || !target.parentNode.comp.container || !canDrop(thing, target)) {
        return;
    }
    let targetComp = target.parentNode.comp;
    let thingComp  = thing.comp;
    target         = target.parentNode.childNodes[1];
    thing.parentNode.removeChild(thing);
    target.appendChild(thing);

    thingComp.parent.components.splice(thingComp.parent.components.indexOf(thingComp), 1);
    thingComp.parent = targetComp;
    thingComp.parent.components.push(thingComp);
};

/**
 * Creates the form HTML for editing the component data and
 * applies it to the appropriate part of the page.
 */
Component.prototype.createFormHTML = function () {
    let target = document.getElementById('skillForm');

    let form = document.createElement('form');

    let header       = document.createElement('h4');
    header.innerHTML = this.name;
    form.appendChild(header);

    if (this.description) {
        let desc       = document.createElement('p');
        desc.innerHTML = this.description;
        form.appendChild(desc);
    }

    if (this.data.length > 1) {
        let h = document.createElement('hr');
        form.appendChild(h);

        let i = 1;
        for (let j = 1; j < this.data.length; j++) {
            if (this.data[j] instanceof AttributeValue) {
                i = 0;
                break;
            }
        }
        for (; i < this.data.length; i++) {
            this.data[i].hidden = false;
            this.data[i].createHTML(form);
        }
    }

    let hr = document.createElement('hr');
    form.appendChild(hr);

    let done       = document.createElement('h5');
    done.className = 'doneButton';
    done.innerHTML = 'Done';
    done.component = this;
    done.addEventListener('click', function (e) {
        this.component.update();
        document.getElementById('skillForm').removeChild(this.component.form);
        showSkillPage('builder');
    });
    form.appendChild(done);

    this.form = form;

    target.innerHTML = '';
    target.appendChild(form);
    activeComponent = this;

    for (let i = 0; i < this.data.length; i++) {
        this.data[i].applyRequireValues();
    }
}

/**
 * Updates the component using the form data if it exists
 */
Component.prototype.update = function () {
    for (let j = 0; j < this.data.length; j++) {
        this.data[j].update();
    }
};

/**
 * Gets the save string for the component
 *
 * @param {string} spacing - spacing to put before the data
 */
Component.prototype.getSaveString = function (spacing) {
    this.createFormHTML();

    let id    = '';
    let index = saveIndex;
    while (index > 0 || id.length == 0) {
        id += String.fromCharCode((index % 26) + 97);
        index = Math.floor(index / 26);
    }
    let result = spacing + this.name + '-' + id + ":\n";
    saveIndex++;

    result += spacing + "  type: '" + this.type + "'\n";
    if (this.data.length > 0) {
        result += spacing + '  data:\n';
        for (let i = 0; i < this.data.length; i++) {
            if (!this.data[i].hidden)
                result += this.data[i].getSaveString(spacing + '    ');
        }
    }
    if (this.components.length > 0) {
        result += spacing + '  children:\n';
        for (let j = 0; j < this.components.length; j++) {
            result += this.components[j].getSaveString(spacing + '    ');
        }
    }
    return result;
};

/**
 * Loads component data from the config lines stating at the given index
 *
 * @param {YAMLObject} data - the data to load
 *
 * @returns {Number} the index of the last line of data for this component
 */
Component.prototype.load = loadSection;

// -- Custom constructor ------------------------------------------------------- //

extend('CustomComponent', 'Component');

function CustomComponent(data) {
    this.super(data.display, data.type.toLowerCase(), data.container);
    this.description = data.description;

    for (let i = 0; i < data.options.length; i++) {
        let option = data.options[i];
        switch (option.type) {
            case 'NUMBER':
                this.data.push(new AttributeValue(option.display, option.key, option.base, option.scale)
                    .setTooltip(option.description)
                );
                break;
            case 'TEXT':
                this.data.push(new StringValue(option.display, option.key, option.default)
                    .setTooltip(option.description)
                );
                break;
            case 'DROPDOWN':
                this.data.push(new ListValue(option.display, option.key, option.options, option.options[0])
                    .setTooltip(option.description)
                );
                break;
            case 'LIST':
                this.data.push(new MultiListValue(option.display, option.key, option.options, [])
                    .setTooltip(option.description)
                );
                break;
            default:
                throw new Error("Invalid component with key " + data.key);
        }
    }
}

// -- Trigger constructors ----------------------------------------------------- //

extend('TriggerBlockBreak', 'Component');

function TriggerBlockBreak() {
    this.super('Block Break', Type.TRIGGER, true);
    this.description = 'Applies skill effects when a player breaks a block matching  the given details';

    this.data.push(new MultiListValue('Material', 'material', getAnyMaterials, ['Any'])
        .setTooltip('The type of block expected to be broken')
    );
    this.data.push(new IntValue('Data', 'data', -1)
        .setTooltip('The expected data value of the block (-1 for any data value)')
    );
}

extend('TriggerBlockPlace', 'Component');

function TriggerBlockPlace() {
    this.super('Block Place', Type.TRIGGER, true);
    this.description = 'Applies skill effects when a player places a block matching  the given details';

    this.data.push(new MultiListValue('Material', 'material', getAnyMaterials, ['Any'])
        .setTooltip('The type of block expected to be placed')
    );
    this.data.push(new IntValue('Data', 'data', -1)
        .setTooltip('The expected data value of the block (-1 for any data value)')
    );
}

extend('TriggerCast', 'Component');

function TriggerCast() {
    this.super('Cast', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player casts the skill using either the cast command, the skill bar, or click combos.';
}

extend('TriggerCleanup', 'Component');

function TriggerCleanup() {
    this.super('Cleanup', Type.TRIGGER, true);

    this.description = 'Applies skill effects when the player disconnects or unlearns the skill. This is always applied with a skill level of 1 just for the sake of math.';
}

extend('TriggerCrouch', 'Component');

function TriggerCrouch() {
    this.super('Crouch', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player starts or stops crouching using the shift key.';

    this.data.push(new ListValue('Type', 'type', ['Start Crouching', 'Stop Crouching', 'Both'], 'Start Crouching')
        .setTooltip('Whether you want to apply components when crouching or not crouching')
    );
}

extend('TriggerDeath', 'Component');

function TriggerDeath() {
    this.super('Death', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player dies.';
}

extend('TriggerDropItem', 'Component');

function TriggerDropItem() {
    this.super('Drop Item', Type.TRIGGER, true);

    this.description = 'Applies skill effects upon dropping an item';

    this.data.push(new ListValue('Drop multiple', 'drop multiple', ['True', 'False', 'Ignore'], 'Ignore')
        .setTooltip('Whether the player has to drop multiple items or a single item')
    );
}

extend('TriggerEnvironmentDamage', 'Component');

function TriggerEnvironmentDamage() {
    this.super('Environment Damage', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player takes environmental damage.';

    this.data.push(new ListValue('Type', 'type', getDamageTypes, 'FALL')
        .setTooltip('The source of damage to apply for')
    );
}

extend('TriggerFishing', 'Component');

function TriggerFishing() {
    this.super('Fishing', Type.TRIGGER, true);

    this.description = 'Applies skill effects upon right-clicking with a fishing rod';
}

extend('TriggerFishingBite', 'Component');

function TriggerFishingBite() {
    this.super('Fishing Bite', Type.TRIGGER, true);

    this.description = 'Applies skill efects when a fish bites the fishing rod of a player';
}

extend('TriggerFishingFail', 'Component');

function TriggerFishingFail() {
    this.super('Fishing Fail', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player fails to catch a fish due to poor timing.';
}

extend('TriggerFishingGrab', 'Component');

function TriggerFishingGrab() {
    this.super('Fishing Grab', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player catches a fish';
}

extend('TriggerFishingGround', 'Component');

function TriggerFishingGround() {
    this.super('Fishing Ground', Type.TRIGGER, true);

    this.description = 'Applies skill effects when the bobber of a fishing rod hits the ground';
}

extend('TriggerFishingReel', 'Component');

function TriggerFishingReel() {
    this.super('Fishing Reel', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player reels in a fishing rod out of water or air with no fish on the rod.';
}

extend('TriggerInitialize', 'Component');

function TriggerInitialize() {
    this.super('Initialize', Type.TRIGGER, true);

    this.description = 'Applies skill effects immediately. This can be used for passive abilities.';
}

extend('TriggerItemSwap', 'Component');

function TriggerItemSwap() {
    this.super('Item Swap', Type.TRIGGER, true);

    this.description = 'Applies skill effects upon pressing the swap-key on your keyboard.';

    this.data.push(new ListValue('Cancel swap', 'cancel', ['True', 'False'], 'True')
        .setTooltip('True cancels the item swap. False allows the item swap'));
}

extend('TriggerKill', 'Component');

function TriggerKill() {
    this.super('Kill', Type.TRIGGER, true);

    this.description = 'Applies skill effects upon killing something';
}

extend('TriggerLand', 'Component');

function TriggerLand() {
    this.super('Land', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player lands on the ground.';

    this.data.push(new DoubleValue('Min Distance', 'min-distance', 0)
        .setTooltip('The minimum distance the player should fall before effects activating.')
    );
}

extend('TriggerLaunch', 'Component');

function TriggerLaunch() {
    this.super('Launch', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player launches a projectile.';

    this.data.push(new ListValue('Type', 'type', ['Any',
                                                  'Arrow',
                                                  'Egg',
                                                  'Ender Pearl',
                                                  'Fireball',
                                                  'Fishing Hook',
                                                  'Snowball'], 'Any')
        .setTooltip('The type of projectile that should be launched.')
    );
}

extend('TriggerLeftClick', 'Component');

function TriggerLeftClick() {
    this.super('Left Click', Type.TRIGGER, true);

    this.description = 'Applies skill effects upon performing a left-click';

    this.data.push(new ListValue('Crouch', 'crouch', ['Crouch', 'Dont crouch', 'Both'], 'Crouch')
        .setTooltip('If the player has to be crouching in order for this trigger to function')
    );
}

extend('TriggerMove', 'Component');

function TriggerMove() {
    this.super('Move', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player moves around. This triggers every tick the player is moving, so use this sparingly. Use the "api-moved" value to check/use the distance traveled.';
}

extend('TriggerPhysicalDamage', 'Component');

function TriggerPhysicalDamage() {
    this.super('Physical Damage', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player deals physical (or non-skill) damage. This includes melee attacks and firing a bow.';

    this.data.push(new ListValue('Target Caster', 'target', ['True', 'False'], 'True')
        .setTooltip('True makes children target the caster. False makes children target the damaged entity')
    );
    this.data.push(new ListValue('Type', 'type', ['Both', 'Melee', 'Projectile'], 'Both')
        .setTooltip('The type of damage dealt')
    );
    this.data.push(new DoubleValue("Min Damage", "dmg-min", 0)
        .setTooltip('The minimum damage that needs to be dealt')
    );
    this.data.push(new DoubleValue("Max Damage", "dmg-max", 999)
        .setTooltip('The maximum damage that needs to be dealt')
    );
}

extend('TriggerRightClick', 'Component');

function TriggerRightClick() {
    this.super('Right Click', Type.TRIGGER, true);

    this.description = 'Applies skill effects upon performing a right-click (NOTE: When clicking in air, you have to have an item in your hand)';

    this.data.push(new ListValue('Crouch', 'crouch', ['Crouch', 'Dont crouch', 'Both'], 'Crouch')
        .setTooltip('If the player has to be crouching in order for this trigger to function')
    );
}

extend('TriggerSkillDamage', 'Component');

function TriggerSkillDamage() {
    this.super('Skill Damage', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player deals damage with a skill.';

    this.data.push(new ListValue('Target Caster', 'target', ['True', 'False'], 'True')
        .setTooltip('True makes children target the caster. False makes children target the damaged entity')
    );
    this.data.push(new DoubleValue("Min Damage", "dmg-min", 0)
        .setTooltip('The minimum damage that needs to be dealt')
    );
    this.data.push(new DoubleValue("Max Damage", "dmg-max", 999)
        .setTooltip('The maximum damage that needs to be dealt')
    );
    this.data.push(new StringListValue('Category', 'category', ['default'])
        .setTooltip('The type of skill damage to apply for. Leave this empty to apply to all skill damage.')
    );
}

extend('TriggerTookPhysicalDamage', 'Component');

function TriggerTookPhysicalDamage() {
    this.super('Took Physical Damage', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player takes physical (or non-skill) damage. This includes melee attacks and projectiles not fired by a skill.';

    this.data.push(new ListValue('Target Caster', 'target', ['True', 'False'], 'True')
        .setTooltip('True makes children target the caster. False makes children target the attacking entity')
    );
    this.data.push(new ListValue('Type', 'type', ['Both', 'Melee', 'Projectile'], 'Both')
        .setTooltip('The type of damage dealt')
    );
    this.data.push(new DoubleValue("Min Damage", "dmg-min", 0)
        .setTooltip('The minimum damage that needs to be dealt')
    );
    this.data.push(new DoubleValue("Max Damage", "dmg-max", 999)
        .setTooltip('The maximum damage that needs to be dealt')
    );
}

extend('TriggerTookSkillDamage', 'Component');

function TriggerTookSkillDamage() {
    this.super('Took Skill Damage', Type.TRIGGER, true);

    this.description = 'Applies skill effects when a player takes damage from a skill other than their own.';

    this.data.push(new ListValue('Target Caster', 'target', ['True', 'False'], 'True')
        .setTooltip('True makes children target the caster. False makes children target the attacking entity')
    );
    this.data.push(new DoubleValue("Min Damage", "dmg-min", 0)
        .setTooltip('The minimum damage that needs to be dealt')
    );
    this.data.push(new DoubleValue("Max Damage", "dmg-max", 999)
        .setTooltip('The maximum damage that needs to be dealt')
    );
    this.data.push(new StringListValue('Category', 'category', ['default'])
        .setTooltip('The type of skill damage to apply for. Leave this empty to apply to all skill damage.')
    );
}

// -- Target constructors ------------------------------------------------------ //

extend('TargetArea', 'Component');

function TargetArea() {
    this.super('Area', Type.TARGET, true);

    this.description = 'Targets all units in a radius from the current target (the casting player is the default target).';

    this.data.push(new AttributeValue("Radius", "radius", 3, 0)
        .setTooltip('The radius of the area to target in blocks')
    );
	addTargetOptions(this);
    this.data.push(new ListValue("Random", "random", ['True', 'False'], 'False')
        .setTooltip('Whether to randomize the targets selected')
    );
}

extend('TargetCone', 'Component');

function TargetCone() {
    this.super('Cone', Type.TARGET, true);

    this.description = 'Targets all units in a line in front of the current target (the casting player is the default target). If you include the caster, that counts towards the max amount.';

    this.data.push(new AttributeValue("Range", "range", 5, 0)
        .setTooltip('The max distance away any target can be in blocks')
    );
    this.data.push(new AttributeValue("Angle", "angle", 90, 0)
        .setTooltip('The angle of the cone arc in degrees')
    );
	addTargetOptions(this);
}

extend('TargetLinear', 'Component');

function TargetLinear() {
    this.super('Linear', Type.TARGET, true);

    this.description = 'Targets all units in a line in front of the current target (the casting player is the default target).';

    this.data.push(new AttributeValue("Range", "range", 5, 0)
        .setTooltip('The max distance away any target can be in blocks')
    );
    this.data.push(new AttributeValue("Tolerance", "tolerance", 0, 0)
        .setTooltip('How much to expand the potential entity\'s hitbox in all directions, in blocks. This makes it easier to aim')
    );
	addTargetOptions(this);
}

extend('TargetLocation', 'Component');

function TargetLocation() {
    this.super('Location', Type.TARGET, true);

    this.description = 'Targets the location the target or caster is looking at. Combine this with another targeting type for ranged area effects.';

    this.data.push(new AttributeValue('Range', 'range', 5, 0)
        .setTooltip('The max distance the location can be from the target\'s eyes')
    );
    this.data.push(new ListValue('Entities', 'entities', ['True', 'False'], 'True')
        .setTooltip('True to account for entities, or false to pass through them')
    );
    this.data.push(new ListValue('Fluids', 'fluids', ['True', 'False'], 'False')
        .setTooltip('True to account for fluids (water and lava), or false to pass through them')
    );
    this.data.push(new ListValue('Passable blocks', 'passable', ['True', 'False'], 'True')
        .setTooltip('True to account for passable or non-collidable blocks (grass, saplings, etc), or false to pass through them')
    );
    this.data.push(new ListValue('Center', 'center', ['True', 'False'], 'False')
        .setTooltip('Whether to move the hit location to the center of the block')
    );
}

extend('TargetNearest', 'Component');

function TargetNearest() {
    this.super('Nearest', Type.TARGET, true);

    this.description = 'Targets the closest unit(s) in a radius from the current target (the casting player is the default target). If you include the caster, that counts towards the max number.';

    this.data.push(new AttributeValue("Radius", "radius", 3, 0)
        .setTooltip('The radius of the area to target in blocks')
    );
	addTargetOptions(this);
}

extend('TargetOffset', 'Component');

function TargetOffset() {
    this.super('Offset', Type.TARGET, true);

    this.description = 'Targets a location that is the given offset away from each target.';

    this.data.push(new SectionMarker('Offset'));
    this.data.push(new AttributeValue('Forward', 'forward', 0, 0)
        .setTooltip('The offset from the target in the direction they are facing. Negative numbers go backwards.')
    );
    this.data.push(new AttributeValue('Upward', 'upward', 2, 0.5)
        .setTooltip('The offset from the target upwards. Negative numbers go below them.')
    );
    this.data.push(new AttributeValue('Right', 'right', 0, 0)
        .setTooltip('The offset from the target to their right. Negative numbers go to the left.')
    );
}

extend('TargetRemember', 'Component');

function TargetRemember() {
    this.super('Remember', Type.TARGET, true);

    this.description = 'Targets entities stored using the "Remember Targets" mechanic for the matching key. If it was never set, this will fail.';

    this.data.push(new StringValue('Key', 'key', 'target')
        .setTooltip('The unique key for the target group that should match that used by the "Remember Targets" skill')
    );
}

extend('TargetSelf', 'Component');

function TargetSelf() {
    this.super('Self', Type.TARGET, true);

    this.description = 'Returns the current target back to the caster.';
}

extend('TargetSingle', 'Component');

function TargetSingle() {
    this.super('Single', Type.TARGET, true);

    this.description = 'Targets a single unit in front of the current target (the casting player is the default target).';

    this.data.push(new AttributeValue("Range", "range", 5, 0)
        .setTooltip('The max distance away any target can be in blocks')
    );
    this.data.push(new AttributeValue("Tolerance", "tolerance", 0, 0)
        .setTooltip('How much to expand the potential entity\'s hitbox in all directions, in blocks. This makes it easier to aim')
    );
    this.data.push(new ListValue("Group", "group", ["Ally", "Enemy", "Both"], "Enemy")
        .setTooltip('The alignment of targets to get')
    );
    this.data.push(new ListValue("Through Wall", "wall", ['True', 'False'], 'False')
        .setTooltip('Whether to allow targets to be on the other side of a wall')
    );
}

// -- Condition constructors --------------------------------------------------- //

extend('ConditionAltitude', 'Component');

function ConditionAltitude()
{
    this.super('Altitude', Type.CONDITION, true);

    this.description = "Applies child components whenever the player is on a certain height-level";

    this.data.push(new AttributeValue('Min', 'min', 0, 0)
        .setTooltip('The minimum height a player has to be on')
    );

    this.data.push(new AttributeValue('Max', 'max', 0, 0)
        .setTooltip('The maximum height a player can be on')
    );

}

extend('ConditionArmor', 'Component');

function ConditionArmor() {
    this.super('Armor', Type.CONDITION, true);
    this.description = "Applies child components when the target is wearing an armor item matching the given details.";

    this.data.push(new ListValue('Armor', 'armor', ['Helmet', 'Chestplate', 'Leggings', 'Boots', 'Any'], 'Any')
        .setTooltip('The type of armor to check')
    );

    addItemConditionOptions(this);
}

extend('ConditionAttribute', 'Component');

function ConditionAttribute() {
    this.super('Attribute', Type.CONDITION, true);

    this.description = 'Requires the target to have a given number of attributes';

    this.data.push(new StringValue('Attribute', 'attribute', 'Vitality')
        .setTooltip('The name of the attribute you are checking the value of')
    );
    this.data.push(new AttributeValue('Min', 'min', 0)
        .setTooltip('The minimum amount of the attribute the target requires')
    );
    this.data.push(new AttributeValue('Max', 'max', 999, 0)
        .setTooltip('The maximum amount of the attribute the target requires')
    );
}

extend('ConditionBiome', 'Component');

function ConditionBiome() {
    this.super('Biome', Type.CONDITION, true);

    this.description = 'Applies child components when in a specified biome.';

    this.data.push(new ListValue('Type', 'type', ['In Biome', 'Not In Biome'], 'In Biome')
        .setTooltip('Whether the target should be in the biome. If checking for in the biome, they must be in any one of the checked biomes. If checking for the opposite, they must not be in any of the checked biomes.')
    );
    this.data.push(new MultiListValue('Biome', 'biome', getBiomes, ['Forest'])
        .setTooltip('The biomes to check for. The expectation would be any of the selected biomes need to match')
    );
}

extend('ConditionBlock', 'Component');

function ConditionBlock() {
    this.super('Block', Type.CONDITION, true);

    this.description = 'Applies child components if the target is currently standing on a block of the given type.';

    this.data.push(new ListValue('Type', 'standing', ['On Block',
                                                      'Not On Block',
                                                      'In Block',
                                                      'Not In Block'], 'On Block')
        .setTooltip('Specifies which block to check and whether it should match the selected mateiral. "On Block" is directly below the player while "In Block" is the block a player\'s feet are in.')
    );
    this.data.push(new ListValue('Material', 'material', getMaterials, 'Dirt')
        .setTooltip('The type of the block to require the targets to stand on')
    );
}

extend('ConditionBurning', 'Component');

function ConditionBurning() 
{
    this.super('Burning', Type.CONDITION, true);

    this.description = 'Applies child components if the caster burns or not';

    this.data.push(new ListValue('Type', 'burn', ['Burn', 'Dont burn'], 'Burn')
        .setTooltip('Specifies whether the player has to be burning for this skill to be performed')
        );
}

extend('ConditionCeiling', 'Component');

function ConditionCeiling() {
    this.super('Ceiling', Type.CONDITION, true);

    this.description = 'Checks the height of the ceiling above each target';

    this.data.push(new AttributeValue('Distance', 'distance', 5, 0)
        .setTooltip('How high to check for the ceiling')
    );
    this.data.push(new ListValue('At least', 'at-least', ['True', 'False'], 'True')
        .setTooltip('When true, the ceiling must be at least the give number of blocks high. If false, the ceiling must be lower than the given number of blocks')
    );
}

extend('ConditionChance', 'Component');

function ConditionChance() {
    this.super('Chance', Type.CONDITION, true);

    this.description = 'Rolls a chance to apply child components.';

    this.data.push(new AttributeValue('Chance', 'chance', 25, 0)
        .setTooltip('The chance to execute children as a percentage. "25" would be 25%.')
    );
}

extend('ConditionClass', 'Component');

function ConditionClass() {
    this.super('Class', Type.CONDITION, true);

    this.description = 'Applies child components when the target is the given class or optionally a profession of that class. For example, if you check for "Fighter" which professes into "Warrior", a "Warrior" will pass the check if you do not enable "exact".';

    this.data.push(new StringValue('Class', 'class', 'Fighter')
        .setTooltip('The class the player should be')
    );
    this.data.push(new ListValue('Exact', 'exact', ['True', 'False'], 'False')
        .setTooltip('Whether the player must be exactly the given class. If false, they can be a later profession of the class.')
    );
}

extend('ConditionClassLevel', 'Component');

function ConditionClassLevel() {
    this.super('Class Level', Type.CONDITION, true);

    this.description = 'Applies child components when the level of the class with this skill is within the range. This only checks the level of the caster, not the targets.';

    this.data.push(new IntValue('Min Level', 'min-level', 2)
        .setTooltip('The minimum class level the player should be. If the player has multiple classes, this will be of their main class')
    );
    this.data.push(new IntValue('Max Level', 'max-level', 99)
        .setTooltip('The maximum class level the player should be. If the player has multiple classes, this will be of their main class')
    );
}

extend('ConditionCombat', 'Component');

function ConditionCombat() {
    this.super('Combat', Type.CONDITION, true);

    this.description = 'Applies child components to targets that are in/out of combat, depending on the settings.';

    this.data.push(new ListValue('In Combat', 'combat', ['True', 'False'], 'True')
        .setTooltip('Whether the target should be in or out of combat')
    );
    this.data.push(new DoubleValue('Seconds', 'seconds', 10)
        .setTooltip('The time in seconds since the last combat activity before something is considered not in combat')
    );
}

extend('ConditionCrouch', 'Component');

function ConditionCrouch() {
    this.super('Crouch', Type.CONDITION, true);

    this.description = 'Applies child components if the target player(s) are crouching';

    this.data.push(new ListValue('Crouching', 'crouch', ['True', 'False'], 'True')
        .setTooltip('Whether the player should be crouching')
    );
}

extend('ConditionDirection', 'Component');

function ConditionDirection() {
    this.super('Direction', Type.CONDITION, true);

    this.description = 'Applies child components when the target or caster is facing the correct direction relative to the other.';

    this.data.push(new ListValue('Type', 'type', ['Target', 'Caster'], 'Target')
        .setTooltip('The entity to check the direction of')
    );
    this.data.push(new ListValue('Direction', 'direction', ['Away', 'Towards'], 'Away')
        .setTooltip('The direction the chosen entity needs to be looking relative to the other.')
    );
}

extend('ConditionElevation', 'Component');

function ConditionElevation() {
    this.super('Elevation', Type.CONDITION, true);

    this.description = 'Applies child components when the elevation of the target matches the settings.';

    this.data.push(new ListValue('Type', 'type', ['Normal', 'Difference'], 'Normal')
        .setTooltip('The type of comparison to make. Normal is just their Y-coordinate. Difference would be the difference between that the caster\'s Y-coordinate')
    );
    this.data.push(new AttributeValue('Min Value', 'min-value', 0, 0)
        .setTooltip('The minimum value for the elevation required. A positive minimum value with a "Difference" type would be for when the target is higher up than the caster')
    );
    this.data.push(new AttributeValue('Max Value', 'max-value', 255, 0)
        .setTooltip('The maximum value for the elevation required. A negative maximum value with a "Difference" type would be for when the target is below the caster')
    );
}

extend('ConditionElse', 'Component');

function ConditionElse() {
    this.super('Else', Type.CONDITION, true);

    this.description = 'Applies child elements if the previous component failed to execute. This not only applies for conditions not passing, but mechanics failing due to no target or other cases.';
}

extend('ConditionEntityType', 'Component');

function ConditionEntityType() {
    this.super('Entity Type', Type.CONDITION, true);

    this.description = 'Applies child elements if the target matches one of the selected entity types'

    this.data.push(new MultiListValue('Types', 'types', getEntities)
        .setTooltip('The entity types to target')
    );
}

extend('ConditionFire', 'Component');

function ConditionFire() {
    this.super('Fire', Type.CONDITION, true);

    this.description = 'Applies child components when the target is on fire.';

    this.data.push(new ListValue('Type', 'type', ['On Fire', 'Not On Fire'], 'On Fire')
        .setTooltip('Whether the target should be on fire')
    );
}

extend('ConditionFlag', 'Component');

function ConditionFlag() {
    this.super('Flag', Type.CONDITION, true);

    this.description = 'Applies child components when the target is marked by the appropriate flag.';

    this.data.push(new ListValue('Type', 'type', ['Set', 'Not Set'], 'Set')
        .setTooltip('Whether the flag should be set')
    );
    this.data.push(new StringValue('Key', 'key', 'key')
        .setTooltip('The unique key representing the flag. This should match the key for when you set it using the Flag mechanic or the Flat Toggle mechanic')
    );
}

extend('ConditionFood', 'Component');

function ConditionFood() {
    this.super('Food', Type.CONDITION, true);

    this.description = "Applies child components when the target's food level matches the settings.";

    this.data.push(new ListValue('Type', 'type', ['Food', 'Percent', 'Difference', 'Difference Percent'], 'Food')
        .setTooltip('The type of measurement to use for the food. Food level is their flat food left. Percent is the percentage of food they have left. Difference is the difference between the target\'s flat food and the caster\'s. Difference percent is the difference between the target\'s percentage food left and the caster\s')
    );
    this.data.push(new AttributeValue('Min Value', 'min-value', 0, 0)
        .setTooltip('The minimum food required. A positive minimum with one of the "Difference" types would be for when the target has more food')
    );
    this.data.push(new AttributeValue('Max Value', 'max-value', 10, 2)
        .setTooltip('The maximum food required. A negative maximum with one of the "Difference" types would be for when the target has less food')
    );
}

extend('ConditionGround', 'Component');

function ConditionGround() {
    this.super('Ground', Type.CONDITION, true);

    this.description = 'Applies child components when the target is on the ground';

    this.data.push(new ListValue('Type', 'type', ['On Ground', 'Not On Ground'], 'On Ground')
        .setTooltip('Whether the target should be on the ground')
    );
}

extend('ConditionHealth', 'Component');

function ConditionHealth() {
    this.super('Health', Type.CONDITION, true);

    this.description = "Applies child components when the target's health matches the settings.";

    this.data.push(new ListValue('Type', 'type', ['Health', 'Percent', 'Difference', 'Difference Percent'], 'Health')
        .setTooltip('The type of measurement to use for the health. Health is their flat health left. Percent is the percentage of health they have left. Difference is the difference between the target\'s flat health and the caster\'s. Difference percent is the difference between the target\'s percentage health left and the caster\s')
    );
    this.data.push(new AttributeValue('Min Value', 'min-value', 0, 0)
        .setTooltip('The minimum health required. A positive minimum with one of the "Difference" types would be for when the target has more health')
    );
    this.data.push(new AttributeValue('Max Value', 'max-value', 10, 2)
        .setTooltip('The maximum health required. A negative maximum with one of the "Difference" types would be for when the target has less health')
    );
}

extend('ConditionItem', 'Component');

function ConditionItem() {
    this.super('Item', Type.CONDITION, true);
    this.description = "Applies child components when the target is wielding an item matching the given material.";

    addItemConditionOptions(this);
}

extend('ConditionInventory', 'Component');

function ConditionInventory() {
    this.super('Inventory', Type.CONDITION, true);

    this.description = 'Applies child components when the target player contains the given item in their inventory. This does not work on mobs.';

    this.data.push(new AttributeValue('Amount', 'amount', 1, 0)
        .setTooltip('The amount of the item needed in the player\'s inventory')
    );

    addItemConditionOptions(this);
}

extend('ConditionLight', 'Component');

function ConditionLight() {
    this.super('Light', Type.CONDITION, true);

    this.description = "Applies child components when the light level at the target's location matches the settings.";

    this.data.push(new AttributeValue('Min Light', 'min-light', 0, 0)
        .setTooltip('The minimum light level needed. 16 is full brightness while 0 is complete darkness')
    );
    this.data.push(new AttributeValue('Max Light', 'max-light', 16, 16)
        .setTooltip('The maximum light level needed. 16 is full brightness while 0 is complete darkness')
    );
}

extend('ConditionMana', 'Component');

function ConditionMana() {
    this.super('Mana', Type.CONDITION, true);

    this.description = "Applies child components when the target's mana matches the settings.";

    this.data.push(new ListValue('Type', 'type', ['Mana', 'Percent', 'Difference', 'Difference Percent'], 'Mana')
        .setTooltip('The type of measurement to use for the mana. Mana is their flat mana left. Percent is the percentage of mana they have left. Difference is the difference between the target\'s flat mana and the caster\'s. Difference percent is the difference between the target\'s percentage mana left and the caster\s')
    );
    this.data.push(new AttributeValue('Min Value', 'min-value', 0, 0)
        .setTooltip('The minimum amount of mana needed')
    );
    this.data.push(new AttributeValue('Max Value', 'max-value', 10, 2)
        .setTooltip('The maximum amount of mana needed')
    );
}

extend('ConditionMoney', 'Component');

function ConditionMoney() {
    this.super('Money', Type.CONDITION, true);

    this.description = "Applies child components when the target's balance matches the settings (requires Vault and an economy plugin). Always is false for non-player targets.";
	
    this.data.push(new ListValue('Type', 'type', ['Min', 'Max', 'Between'], 'Min')
        .setTooltip('The type of comparison to make')
    );
    this.data.push(new AttributeValue('Min Value', 'min-value', 10, 0)
        .requireValue('type', ['Min', 'Between'])
        .setTooltip('The minimum balance the target must have, inclusive.')
    );
    this.data.push(new AttributeValue('Max Value', 'max-value', 100, 0)
        .requireValue('type', ['Max', 'Between'])
        .setTooltip('The maximum balance the target can have, inclusive.')
    );
}

extend('ConditionMounted', 'Component');

function ConditionMounted() {
    this.super('Mounted', Type.CONDITION, true);

    this.description = 'Applies child elements if the target is being mounted by one of the selected entity types'

    this.data.push(new MultiListValue('Types', 'types', getAnyEntities, ['Any'])
        .setTooltip('The entity types that can be mounting the target')
    );

}

extend('ConditionMounting', 'Component');

function ConditionMounting() {
    this.super('Mounting', Type.CONDITION, true);

    this.description = 'Applies child elements if the target is mounting one of the selected entity types'

    this.data.push(new MultiListValue('Types', 'types', getAnyEntities, ['Any'])
        .setTooltip('The entity types the target can be mounting')
    );

}

extend('ConditionMythicMobType', 'Component');

function ConditionMythicMobType() {
    this.super('MythicMob Type', Type.CONDITION, true);

    this.description = 'Applies child elements if the target corresponds to one of the entered MythicMob types, or is not a MythicMob if left empty'

    this.data.push(new StringListValue('MythicMob Types', 'types', [])
        .setTooltip('The MythicMob types to target')
    );
}

extend('ConditionName', 'Component');

function ConditionName() {
    this.super('Name', Type.CONDITION, true);

    this.description = 'Applies child components when the target has a name matching the settings.';

    this.data.push(new ListValue('Contains Text', 'contains', ['True', 'False'], 'True')
        .setTooltip('Whether the target should have a name containing the text')
    );
    this.data.push(new ListValue('Regex', 'regex', ['True', 'False'], 'False')
        .setTooltip('Whether the text is formatted as regex. If you do not know what regex is, ignore this option')
    );
    this.data.push(new StringValue('Text', 'text', 'text')
        .setTooltip('The text to look for in the target\'s name')
    );
}

extend('ConditionOffhand', 'Component');

function ConditionOffhand() {
    this.super('Offhand', Type.CONDITION, true);
    this.description = "Applies child components when the target is wielding an item matching the given material as an offhand item. This is for v1.9+ servers only.";

    addItemConditionOptions(this);
}

extend('ConditionPermission', 'Component');

function ConditionPermission() {
    this.super('Permission', Type.CONDITION, true);

    this.description = 'Applies child components if the caster has the required permission';

    this.data.push(new StringValue('Permission', 'perm', 'some.permission')
        .setTooltip('The permission the player needs to have')
    );
}

extend('ConditionPotion', 'Component');

function ConditionPotion() {
    this.super('Potion', Type.CONDITION, true);

    this.description = 'Applies child components when the target has the potion effect.';

    this.data.push(new ListValue('Type', 'type', ['Active', 'Not Active'], 'Active')
        .setTooltip('Whether the potion should be active')
    );
    this.data.push(new ListValue('Potion', 'potion', getAnyPotion, 'Any')
        .setTooltip('The type of potion to look for')
    );
    this.data.push(new AttributeValue('Min Rank', 'min-rank', 0, 0)
        .setTooltip('The minimum rank the potion effect can be')
    );
    this.data.push(new AttributeValue('Max Rank', 'max-rank', 999, 0)
        .setTooltip('The maximum rank the potion effect can be')
    );
}

extend('ConditionSkillLevel', 'Component');

function ConditionSkillLevel(skill) {
    this.super('Skill Level', Type.CONDITION, true);

    this.description = 'Applies child components when the skill level is with the range. This checks the skill level of the caster, not the targets.';

    this.data.push(new StringValue('Skill', 'skill', skill)
        .setTooltip('The name of the skill to check the level of. If you want to check the current skill, enter the current skill\'s name anyway')
    );
    this.data.push(new IntValue('Min Level', 'min-level', 2)
        .setTooltip('The minimum level of the skill needed')
    );
    this.data.push(new IntValue('Max Level', 'max-level', 99)
        .setTooltip('The maximum level of the skill needed')
    );
}

extend('ConditionSlot', 'Component');

function ConditionSlot() {
    this.super('Slot', Type.CONDITION, true);
    this.description = "Applies child components when the target player has a matching item in the given slot.";

    this.data.push(new StringListValue('Slots (one per line)', 'slot', [9])
        .setTooltip('The slots to look at. Slots 0-8 are the hot bar, 9-35 are the main inventory, 36-39 are armor, and 40 is the offhand slot. Multiple slots will check if any of the slots match.')
    );

    addItemConditionOptions(this);
}

extend('ConditionStatus', 'Component');

function ConditionStatus() {
    this.super('Status', Type.CONDITION, true);

    this.description = 'Applies child components when the target has the status condition.';

    this.data.push(new ListValue('Type', 'type', ['Active', 'Not Active'], 'Active')
        .setTooltip('Whether the status should be active')
    );
    this.data.push(new ListValue('Status', 'status', ['Any',
                                                      'Absorb',
                                                      'Curse',
                                                      'Disarm',
                                                      'Invincible',
                                                      'Root',
                                                      'Silence',
                                                      'Stun'], 'Any')
        .setTooltip('The status to look for')
    );
}

extend('ConditionTime', 'Component');

function ConditionTime() {
    this.super('Time', Type.CONDITION, true);

    this.description = 'Applies child components when the server time matches the settings.';

    this.data.push(new ListValue('Time', 'time', ['Day', 'Night'], 'Day')
        .setTooltip('The time to check for in the current world')
    );
}

extend('ConditionTool', 'Component');

function ConditionTool() {
    this.super('Tool', Type.CONDITION, true);

    this.description = 'Applies child components when the target is wielding a matching tool.';

    this.data.push(new ListValue('Material', 'material', ['Any',
                                                          'Wood',
                                                          'Stone',
                                                          'Iron',
                                                          'Gold',
                                                          'Diamond',
                                                          'Netherite'], 'Any')
        .setTooltip('The material the held tool needs to be made out of')
    );
    this.data.push(new ListValue('Tool', 'tool', ['Any', 'Axe', 'Hoe', 'Pickaxe', 'Shovel', 'Sword'], 'Any')
        .setTooltip('The type of tool it needs to be')
    );
}

extend('ConditionValue', 'Component');

function ConditionValue() {
    this.super('Value', Type.CONDITION, true);

    this.description = 'Applies child components if a stored value is within the given range.';

    this.data.push(new StringValue('Key', 'key', 'value')
        .setTooltip('The unique string used for the value set by the Value mechanics.')
    );
    this.data.push(new AttributeValue('Min Value', 'min-value', 1, 0)
        .setTooltip('The lower bound of the required value')
    );
    this.data.push(new AttributeValue('Max Value', 'max-value', 999, 0)
        .setTooltip('The upper bound of the required value')
    );
}

extend('ConditionWater', 'Component');

function ConditionWater() {
    this.super('Water', Type.CONDITION, true);

    this.description = 'Applies child components when the target is in or out of water, depending on the settings.';

    this.data.push(new ListValue('State', 'state', ['In Water', 'Out Of Water'], 'In Water')
        .setTooltip('Whether the target needs to be in the water')
    );
}

extend('ConditionWeather', 'Component');

function ConditionWeather() {
    this.super('Weather', Type.CONDITION, true);

    this.description = 'Applies child components when the target\'s location has the given weather condition';

    this.data.push(new ListValue('Type', 'type', ['None', 'Rain', 'Snow', 'Thunder'], 'Rain')
        .setTooltip('Whether the target needs to be in the water')
    );
}

extend('ConditionWorld', 'Component');

function ConditionWorld() 
{
    this.super('World', Type.CONDITION, true);

    this.description = 'Applies child components when the target is in a specific world';

    this.data.push(new ListValue('Blacklist', 'blacklist', ['True', 'False'], 'False')
        .setTooltip('Whether the list should be seen as a blacklist')
    );

    this.data.push(new StringListValue('Worlds', 'worlds', [])
        .setTooltip("Which worlds should be taken into consideration")
    );

}

// -- Mechanic constructors ---------------------------------------------------- //

extend('MechanicAttribute', 'Component');

function MechanicAttribute() {
    this.super('Attribute', Type.MECHANIC, false);

    this.description = 'Gives a player bonus attributes temporarily.';

    this.data.push(new StringValue('Attribute', 'key', 'Intelligence')
        .setTooltip('The name of the attribute to add to')
    );
    this.data.push(new ListValue('Operation', 'operation', ['ADD_NUMBER', 'MULTIPLY_PERCENTAGE'], 'ADD_NUMBER')
        .setTooltip('The operation on the original value by amount, ADD_NUMBER: Scalar adding, MULTIPLY_PERCENTAGE: Multiply the value by amount')
    );
    this.data.push(new AttributeValue('Amount', 'amount', 5, 2)
        .setTooltip('The amount to use with the operation')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 0)
        .setTooltip('How long in seconds to give the attributes to the player')
    );
    this.data.push(new ListValue('Stackable', 'stackable', ['True', 'False'], 'False')
        .setTooltip('Whether applying multiple times stacks the effects')
    );
}

extend('MechanicArmor', 'Component');

function MechanicArmor() {
    this.super('Armor', Type.MECHANIC, false);

    this.description = 'Sets the specified armor slot of the target to the item defined by the settings';

    this.data.push(new ListValue('Slot', 'slot', ['Hand', 'Off Hand', 'Feet', 'Legs', 'Chest', 'Head'], 'Hand')
        .setTooltip('The slot number to set the item to')
    );
    this.data.push(new ListValue('Overwrite', 'overwrite', ['True', 'False'], 'False')
        .setTooltip('USE WITH CAUTION. Whether to overwrite an existing item in the slot. If true, will permanently delete the existing iem')
    );
	addItemOptions(this)
}

extend('MechanicArmorStand', 'Component');

function MechanicArmorStand() {
    this.super('Armor Stand', Type.MECHANIC, true);

    this.description = 'Summons an armor stand that can be used as a marker or for item display (check Armor Mechanic for latter). Applies child components on the armor stand';

    this.data.push(new StringValue('Armor Stand Key', 'key', 'default')
        .setTooltip('The key to refer to the armorstand by. Only one armorstand of each key can be active per target at the time')
    );
    this.data.push(new AttributeValue('Duration', 'duration', 5, 0)
        .setTooltip('How long the armorstand lasts before being deleted')
    );
    this.data.push(new StringValue('Name', 'name', 'Armor Stand')
        .setTooltip('The name the armor stand displays')
    );
    this.data.push(new ListValue('Name visible', 'name-visible', ['True', 'False'], 'False')
        .setTooltip('Whether the armorstand\'s name should be visible from afar')
    );
    this.data.push(new ListValue('Follow target', 'follow', ['True', 'False'], 'False')
        .setTooltip('Whether the armorstand should follow the target')
    );
    this.data.push(new ListValue('Apply gravity', 'gravity', ['True', 'False'], 'True')
        .setTooltip('Whether the armorstand should be affected by gravity')
    );
    this.data.push(new ListValue('Small', 'tiny', ['True', 'False'], 'False')
        .setTooltip('Whether the armorstand should be small')
    );
    this.data.push(new ListValue('Show arms', 'arms', ['True', 'False'], 'False')
        .setTooltip('Whether the armorstand should display its arms')
    );
    this.data.push(new ListValue('Show base plate', 'base', ['True', 'False'], 'False')
        .setTooltip('Whether the armorstand should display its base plate')
    );
    this.data.push(new ListValue('Visible', 'visible', ['True', 'False'], 'True')
        .setTooltip('Whether the armorstand should be visible')
    );
    this.data.push(new ListValue('Marker', 'marker', ['True', 'False'], 'True')
        .setTooltip('Setting this to true will remove the armor stand\'s hitbox')
    );
    this.data.push(new SectionMarker('Offset'));
    this.data.push(new AttributeValue('Forward Offset', 'forward', 0, 0)
        .setTooltip('How far forward in front of the target the armorstand should be in blocks. A negative value will put it behind.')
    );
    this.data.push(new AttributeValue('Upward Offset', 'upward', 0, 0)
        .setTooltip('How far above the target the armorstand should be in blocks. A negative value will put it below.')
    );
    this.data.push(new AttributeValue('Right Offset', 'right', 0, 0)
        .setTooltip('How far to the right the armorstand should be of the target. A negative value will put it to the left.')
    );
}

extend('MechanicArmorStandPose', 'Component');

function MechanicArmorStandPose() {
    this.super('Armor Stand Pose', Type.MECHANIC, false);

    this.description = 'Sets the pose of an armor stand target. Values should be in the format x,y,z where rotations are in degrees. Example: 0.0,0.0,0.0';

    this.data.push(new StringValue('Head', 'head', '').setTooltip('The pose values of the head. Leave empty if should be ignored')
    );
    this.data.push(new StringValue('Body', 'body', '').setTooltip('The pose values of the body. Leave empty if should be ignored')
    );
    this.data.push(new StringValue('Left Arm', 'left-arm', '').setTooltip('The pose values of the left arm. Leave empty if should be ignored')
    );
    this.data.push(new StringValue('Right Arm', 'right-arm', '').setTooltip('The pose values of the right arm. Leave empty if should be ignored')
    );
    this.data.push(new StringValue('Left Leg', 'left-leg', '').setTooltip('The pose values of the left leg. Leave empty if should be ignored')
    );
    this.data.push(new StringValue('Right Leg', 'right-leg', '').setTooltip('The pose values of the right leg. Leave empty if should be ignored')
    );
}

extend('MechanicBlock', 'Component');

function MechanicBlock() {
    this.super('Block', Type.MECHANIC, false);

    this.description = 'Changes blocks to the given type of block for a limited duration.';

    this.data.push(new ListValue('Shape', 'shape', ['Sphere', 'Cuboid'], 'Sphere')
        .setTooltip('The shape of the region to change the blocks for')
    );
    this.data.push(new ListValue('Type', 'type', ['Air', 'Any', 'Solid'], 'Solid')
        .setTooltip('The type of blocks to replace. Air or any would be for making obstacles while solid would change the environment')
    );
    this.data.push(new ListValue('Block', 'block', getMaterials, 'Ice')
        .setTooltip('The type of block to turn the region into')
    );
    this.data.push(new IntValue('Block Data', 'data', 0)
        .setTooltip('The block data to apply, mostly applicable for things like signs, woods, steps, or the similar')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 5, 0)
        .setTooltip('How long the blocks should be replaced for')
    );

    // Sphere options
    this.data.push(new AttributeValue('Radius', 'radius', 3, 0).requireValue('shape', ['Sphere'])
        .setTooltip('The radius of the sphere region in blocks')
    );

    // Cuboid options
    this.data.push(new AttributeValue('Width (X)', 'width', 5, 0).requireValue('shape', ['Cuboid'])
        .setTooltip('The width of the cuboid in blocks')
    );
    this.data.push(new AttributeValue('Height (Y)', 'height', 5, 0).requireValue('shape', ['Cuboid'])
        .setTooltip('The height of the cuboid in blocks')
    );
    this.data.push(new AttributeValue('Depth (Z)', 'depth', 5, 0).requireValue('shape', ['Cuboid'])
        .setTooltip('The depth of the cuboid in blocks')
    );

    this.data.push(new SectionMarker('Offset'));
    this.data.push(new AttributeValue('Forward Offset', 'forward', 0, 0)
        .setTooltip('How far forward in front of the target the region should be in blocks. A negative value will put it behind.')
    );
    this.data.push(new AttributeValue('Upward Offset', 'upward', 0, 0)
        .setTooltip('How far above the target the region should be in blocks. A negative value will put it below.')
    );
    this.data.push(new AttributeValue('Right Offset', 'right', 0, 0)
        .setTooltip('How far to the right the region should be of the target. A negative value will put it to the left.')
    );
}

extend('MechanicBuff', 'Component');

function MechanicBuff() {
    this.super('Buff', Type.MECHANIC, false);

    this.description = 'Buffs combat stats of the target';

    this.data.push(new ListValue('Immediate', 'immediate', ['True', 'False'], 'False')
        .setTooltip('Whether to apply the buff to the current damage trigger.')
    );
    this.data.push(new ListValue('Type', 'type', ['DAMAGE',
                                                  'DEFENSE',
                                                  'SKILL_DAMAGE',
                                                  'SKILL_DEFENSE',
                                                  'HEALING'], 'DAMAGE')
        .requireValue('immediate', ['False'])
        .setTooltip('What type of buff to apply. DAMAGE/DEFENSE is for regular attacks, SKILL_DAMAGE/SKILL_DEFENSE are for damage from abilities, and HEALING is for healing from abilities')
    );
    this.data.push(new ListValue('Modifier', 'modifier', ['Flat', 'Multiplier'], 'Flat')
        .setTooltip('The sort of scaling for the buff. Flat will increase/reduce incoming damage by a fixed amount where Multiplier does it by a percentage of the damage. Multipliers above 1 will increase damage taken while multipliers below 1 reduce damage taken.')
    );
    this.data.push(new StringValue('Category', 'category', '')
        .requireValue('type', ['SKILL_DAMAGE', 'SKILL_DEFENSE'])
        .setTooltip('What kind of skill damage to affect. If left empty, this will affect all skill damage.')
    );
    this.data.push(new AttributeValue('Value', 'value', 1, 0)
        .setTooltip('The amount to increase/decrease incoming damage by')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 0)
        .requireValue('immediate', ['False'])
        .setTooltip('The duration of the buff in seconds')
    );
}

extend('MechanicCancel', 'Component');

function MechanicCancel() {
    this.super('Cancel', Type.MECHANIC, false);

    this.description = 'Cancels the event that caused the trigger this is under to go off. For example, damage based triggers will stop the damage that was dealt while the Launch trigger would stop the projectile from firing.';
}

extend('MechanicCancelEffect', 'Component');

function MechanicCancelEffect() {
    this.super('Cancel Effect', Type.MECHANIC, false);

    this.description = 'Stops a particle effect prematurely.';

    this.data.push(new StringValue('Effect Key', 'effect-key', 'default')
        .setTooltip('The key used when setting up the effect')
    );
}

extend('MechanicChannel', 'Component');

function MechanicChannel() {
    this.super('Channel', Type.MECHANIC, true);

    this.description = 'Applies child effects after a duration which can be interrupted. During the channel, the player cannot move, attack, or use other spells.';

    this.data.push(new ListValue('Still', 'still', ['True', 'False'], 'True')
        .setTooltip('Whether to hold the player in place while channeling')
    );
    this.data.push(new AttributeValue('Time', 'time', 3, 0)
        .setTooltip('The amouont of time, in seconds, to channel for')
    );
}

extend('MechanicCleanse', 'Component');

function MechanicCleanse() {
    this.super('Cleanse', Type.MECHANIC, false);

    this.description = 'Cleanses negative potion or status effects from the targets.';

    this.data.push(new MultiListValue('Potion', 'potion', getBadPotions)
        .setTooltip('The type of potion effect to remove from the target')
    );
    this.data.push(new MultiListValue('Status', 'status', ['All', 'Curse', 'Disarm', 'Root', 'Silence', 'Stun'])
        .setTooltip('The status to remove from the target')
    );
}

extend('MechanicCommand', 'Component');

function MechanicCommand() {
    this.super('Command', Type.MECHANIC, false);

    this.description = 'Executes a command for each of the targets.';

    this.data.push(new StringValue('Command', 'command', '')
        .setTooltip('The command to execute. {player} = caster\'s name, {target} = target\'s name, {targetUUID} = target\'s UUID (useful if targets are non players), &lc: "{", &rc: "}", &sq: "\'"')
    );
    this.data.push(new ListValue('Execute Type', 'type', ['Console', 'OP'], 'OP')
        .setTooltip('Console: executes the command from the console. OP: Only if the target is a player, will have them execute it while given a temporary OP permission (If server closes in the meantime, the permission might stay, not recommended!!)')
    );
}

extend('MechanicCooldown', 'Component');

function MechanicCooldown() {
    this.super('Cooldown', Type.MECHANIC, false);

    this.description = "Lowers the cooldowns of the target's skill(s). If you provide a negative amount, it will increase the cooldown.";

    this.data.push(new StringValue('Skill (or "all")', 'skill', 'all')
        .setTooltip('The skill to modify the cooldown for')
    );
    this.data.push(new ListValue('Type', 'type', ['Seconds', 'Percent'], 'Seconds')
        .setTooltip('The modification unit to use. Seconds will add/subtract seconds from the cooldown while Percent will add/subtract a percentage of its full cooldown')
    );
    this.data.push(new AttributeValue('Value', 'value', -1, 0)
        .setTooltip('The amount to add/subtract from the skill\'s cooldown')
    );
}

extend('MechanicDamage', 'Component');

function MechanicDamage() {
    this.super('Damage', Type.MECHANIC, false);

    this.description = 'Inflicts skill damage to each target. Multiplier type would be a percentage of the target health.';

    this.data.push(new ListValue('Type', 'type', ['Damage', 'Multiplier', 'Percent Left', 'Percent Missing'], 'Damage')
        .setTooltip('The unit to use for the amount of damage. Damage will deal flat damage, Multiplier will deal a percentage of the target\'s max health, Percent Left will deal a percentage of their current health, and Percent Missing will deal a percentage of the difference between their max health and current health')
    );
    this.data.push(new AttributeValue("Value", "value", 3, 1)
        .setTooltip('The amount of damage to deal')
    );
    this.data.push(new ListValue('True Damage', 'true', ['True', 'False'], 'False')
        .setTooltip('Whether to deal true damage. True damage ignores armor and all plugin checks, and doesn not have a damage animation nor knockback')
    );
    this.data.push(new StringValue('Classifier', 'classifier', 'default')
        .setTooltip('The type of damage to deal. Can act as elemental damage or fake physical damage')
    );
    this.data.push(new ListValue('Apply Knockback', 'knockback', ['True', 'False'], 'True')
        .setTooltip('Whether the damage will inflict knockback. Ignored if it is True Damage')
    );
    this.data.push(new ListValue('Damage Cause', 'cause', ['Contact', 'Entity Attack', 'Entity Sweep Attack', 'Projectile', 'Suffocation', 'Fall', 'Fire', 'Fire Tick', 'Melting', 'Lava', 'Drowning', 'Block Explosion', 'Entity Explosion', 'Void', 'Lightning', 'Suicide', 'Starvation', 'Poison', 'Magic', 'Wither', 'Falling Block', 'Thorns', 'Dragon Breath', 'Custom', 'Fly Into Wall', 'Hot Floor', 'Cramming', 'Dryout', 'Freeze', 'Sonic Boom'], 'Entity Attack')
        .setTooltip('Damage Cause considered by the server. This will have influence over the death message and ProRPGItems\' defenses')
        .requireValue('true', ['False'])
    );
}

extend('MechanicDamageBuff', 'Component');

function MechanicDamageBuff() {
    this.super('Damage Buff', Type.MECHANIC, false);

    this.description = 'Modifies the physical damage dealt by each target by a multiplier or a flat amount for a limited duration. Negative flat amounts or multipliers less than one will reduce damage dealt while the opposite will increase damage dealt. (e.g. a 5% damage buff would be a multiplier or 1.05)';

    this.data.push(new ListValue('Type', 'type', ['Flat', 'Multiplier'], 'Flat')
        .setTooltip('The type of buff to apply. Flat increases damage by a fixed amount while multiplier increases it by a percentage.')
    );
    this.data.push(new ListValue('Skill Damage', 'skill', ['True', 'False'], 'False')
        .setTooltip('Whether to buff skill damage. If false, it will affect physical damage.')
    );
    this.data.push(new AttributeValue('Value', 'value', 1, 0)
        .setTooltip('The amount to increase/decrease the damage by. A negative amoutn with the "Flat" type will decrease damage, similar to a number less than 1 for the multiplier.')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 0)
        .setTooltip('The duration of the buff in seconds')
    );
}

extend('MechanicDamageLore', 'Component');

function MechanicDamageLore() {
    this.super('Damage Lore', Type.MECHANIC, false);

    this.description = 'Damages each target based on a value found in the lore of the item held by the caster.';

    this.data.push(new ListValue("Hand", "hand", ['Main', 'Offhand'], 'Main')
        .setTooltip('The hand to check for the item. Offhand items are MC 1.9+ only.')
    );
    this.data.push(new StringValue('Regex', 'regex', 'Damage: {value}')
        .setTooltip('The regex for the text to look for. Use {value} for where the important number should be. If you do not know about regex, consider looking it up on Wikipedia or avoid using major characters such as [ ] { } ( ) . + ? * ^ \\ |')
    );
    this.data.push(new AttributeValue('Multiplier', 'multiplier', 1, 0)
        .setTooltip('The multiplier to use on the value to get the actual damage to deal')
    );
    this.data.push(new ListValue('True Damage', 'true', ['True', 'False'], 'False')
        .setTooltip('Whether to deal true damage. True damage ignores armor and all plugin checks.')
    );
    this.data.push(new StringValue('Classifier', 'classifier', 'default')
        .setTooltip('The type of damage to deal. Can act as elemental damage or fake physical damage')
    );
    this.data.push(new ListValue('Apply Knockback', 'knockback', ['True', 'False'], 'True')
        .setTooltip('Whether the damage will inflict knockback. Ignored if it is True Damage')
    );
    this.data.push(new ListValue('Damage Cause', 'cause', ['Contact', 'Entity Attack', 'Entity Sweep Attack', 'Projectile', 'Suffocation', 'Fall', 'Fire', 'Fire Tick', 'Melting', 'Lava', 'Drowning', 'Block Explosion', 'Entity Explosion', 'Void', 'Lightning', 'Suicide', 'Starvation', 'Poison', 'Magic', 'Wither', 'Falling Block', 'Thorns', 'Dragon Breath', 'Custom', 'Fly Into Wall', 'Hot Floor', 'Cramming', 'Dryout', 'Freeze', 'Sonic Boom'], 'Entity Attack')
        .setTooltip('Damage Cause considered by the server. This will have influence over the death message and ProRPGItems\' defenses')
        .requireValue('true', ['False'])
    );
}

extend('MechanicDefenseBuff', 'Component');

function MechanicDefenseBuff() {
    this.super('Defense Buff', Type.MECHANIC, false);

    this.description = 'Modifies the physical damage taken by each target by a multiplier or a flat amount for a limited duration. Negative flag amounts or multipliers less than one will reduce damage taken while the opposite will increase damage taken. (e.g. a 5% defense buff would be a multiplier or 0.95, since you would be taking 95% damage)';

    this.data.push(new ListValue('Type', 'type', ['Flat', 'Multiplier'], 'Flat')
        .setTooltip('The type of buff to apply. Flat will increase/reduce incoming damage by a fixed amount where Multiplier does it by a percentage of the damage. Multipliers above 1 will increase damage taken while multipliers below 1 reduce damage taken.')
    );
    this.data.push(new ListValue('Skill Defense', 'skill', ['True', 'False'], 'False')
        .setTooltip('Whether to buff skill defense. If false, it will affect physical defense.')
    );
    this.data.push(new AttributeValue('Value', 'value', 1, 0)
        .setTooltip('The amount to increase/decrease incoming damage by')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 0)
        .setTooltip('The duration of the buff in seconds')
    );
}

extend('MechanicDelay', 'Component');

function MechanicDelay() {
    this.super('Delay', Type.MECHANIC, true);

    this.description = 'Applies child components after a delay.';

    this.data.push(new AttributeValue('Delay', 'delay', 2, 0)
        .setTooltip('The amount of time to wait before applying child components in seconds')
    );
}

extend('MechanicDisguise', 'Component');

function MechanicDisguise() {
    this.super('Disguise', Type.MECHANIC, false);

    this.description = 'Disguises each target according to the settings. This mechanic requires the LibsDisguise plugin to be installed on your server.';

    this.data.push(new AttributeValue('Duration', 'duration', -1, 0)
        .setTooltip('How long to apply the disguise for in seconds. Use a negative number to permanently disguise the targets.')
    );
    this.data.push(new ListValue('Type', 'type', ['Mob', 'Player', 'Misc'], 'Mob')
        .setTooltip('The type of disguise to use, as defined by the LibsDisguise plugin.')
    );

    this.data.push(new ListValue('Mob', 'mob', ['Bat',
                                                'Blaze',
                                                'Cave Spider',
                                                'Chicken',
                                                'Cow',
                                                'Creeper',
                                                'Donkey',
                                                'Elder Guardian',
                                                'Ender Dragon',
                                                'Enderman',
                                                'Endermite',
                                                'Ghast',
                                                'Giant',
                                                'Guardian',
                                                'Horse',
                                                'Iron Golem',
                                                'Magma Cube',
                                                'Mule',
                                                'Mushroom Cow',
                                                'Ocelot',
                                                'Pig',
                                                'Pig Zombie',
                                                'Rabbit',
                                                'Sheep',
                                                'Shulker',
                                                'Silverfish',
                                                'Skeleton',
                                                'Slime',
                                                'Snowman',
                                                'Spider',
                                                'Squid',
                                                'Undead Horse',
                                                'Villager',
                                                'Witch',
                                                'Wither',
                                                'Wither Skeleton',
                                                'Wolf',
                                                'Zombie',
                                                'Zombie Villager'], 'Zombie')
        .requireValue('type', ['Mob'])
        .setTooltip('The type of mob to disguise the target as')
    );
    this.data.push(new ListValue('Adult', 'adult', ['True', 'False',], 'True')
        .requireValue('type', ['Mob'])
        .setTooltip('Whether to use the adult variant of the mob')
    );

    this.data.push(new StringValue('Player', 'player', 'Eniripsa96')
        .requireValue('type', ['Player'])
        .setTooltip('The player to disguise the target as')
    );

    this.data.push(new ListValue('Misc', 'misc', ['Area Effect Cloud',
                                                  'Armor Stand',
                                                  'Arrow',
                                                  'Boat',
                                                  'Dragon Fireball',
                                                  'Dropped Item',
                                                  'Egg',
                                                  'Ender Crystal',
                                                  'Ender Pearl',
                                                  'Ender Signal',
                                                  'Experience Orb',
                                                  'Falling Block',
                                                  'Fireball',
                                                  'Firework',
                                                  'Fishing Hook',
                                                  'Item Frame',
                                                  'Leash Hitch',
                                                  'Minecart',
                                                  'Minecart Chest',
                                                  'Minecart Command',
                                                  'Minecart Furnace',
                                                  'Minecart Hopper',
                                                  'Minecart Mob Spawner',
                                                  'Minecart TNT',
                                                  'Painting',
                                                  'Primed TNT',
                                                  'Shulker Bullet',
                                                  'Snowball',
                                                  'Spectral Arrow',
                                                  'Splash Potion',
                                                  'Tipped Arrow',
                                                  'Thrown EXP Bottle',
                                                  'Wither Skull'], 'Painting')
        .requireValue('type', ['Misc'])
        .setTooltip('The object to disguise the target as')
    );
    this.data.push(new IntValue('Data', 'data', 0)
        // .requireValue('type', [ 'Misc' ])
        .requireValue('misc', ['Area Effect Cloud',
                               'Armor Stand',
                               'Arrow',
                               'Boat',
                               'Dragon Fireball',
                               'Egg',
                               'Ender Crystal',
                               'Ender Pearl',
                               'Ender Signal',
                               'Experience Orb',
                               'Fireball',
                               'Firework',
                               'Fishing Hook',
                               'Item Frame',
                               'Leash Hitch',
                               'Minecart',
                               'Minecart Chest',
                               'Minecart Command',
                               'Minecart Furnace',
                               'Minecart Hopper',
                               'Minecart Mob Spawner',
                               'Minecart TNT',
                               'Painting',
                               'Primed TNT',
                               'Shulker Bullet',
                               'Snowball',
                               'Spectral Arrow',
                               'Splash Potion',
                               'Tipped Arrow',
                               'Thrown EXP Bottle',
                               'Wither Skull'])
        .setTooltip('Data value to use for the disguise type. What it does depends on the disguise')
    );

    this.data.push(new ListValue('Material', 'mat', getMaterials, 'Anvil')
        // .requireValue('type', [ 'Misc' ])
        .requireValue('misc', ['Dropped Item', 'Falling Block'])
        .setTooltip('Material to use for the disguise type. Note that items used for falling block will not function.')
    );
}

extend('MechanicDurability', 'Component');

function MechanicDurability() {
    this.super('Durability', Type.MECHANIC, false);

    this.description = 'Lowers the durability of a held item';

    this.data.push(new AttributeValue('Amount', 'amount', 1, 0)
        .setTooltip('Amount to reduce the item\'s durability by')
    );
    this.data.push(new ListValue('Offhand', 'offhand', ['True', 'False'], 'False')
        .setTooltip('Whether to apply to the offhand slot')
    );
}

extend('MechanicExplosion', 'Component');

function MechanicExplosion() {
    this.super('Explosion', Type.MECHANIC, false);

    this.description = 'Causes an explosion at the current target\'s position';

    this.data.push(new AttributeValue('Power', 'power', 3, 0)
        .setTooltip('The strength of the explosion')
    );
    this.data.push(new ListValue('Damage Blocks', 'damage', ['True', 'False'], 'False')
        .setTooltip('Whether to damage blocks with the explosion')
    );
    this.data.push(new ListValue('Fire', 'fire', ['True', 'False'], 'False')
        .setTooltip('Whether to set affected blocks on fire')
    );
}

extend('MechanicFire', 'Component');

function MechanicFire() {
    this.super('Fire', Type.MECHANIC, false);

    this.description = 'Sets the target on fire for a duration.';

    this.data.push(new AttributeValue('Damage', 'damage', 1, 0)
        .setTooltip('The damage dealt by each fire tick')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 1)
        .setTooltip('The duration of the fire in seconds')
    );
}

extend('MechanicFlag', 'Component');

function MechanicFlag() {
    this.super('Flag', Type.MECHANIC, false);

    this.description = 'Marks the target with a flag for a duration. Flags can be checked by other triggers, spells or the related for interesting synergies and effects.';

    this.data.push(new StringValue('Key', 'key', 'key')
        .setTooltip('The unique string for the flag. Use the same key when checking it in a Flag Condition.')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 1)
        .setTooltip('The duration the flag should be set for. To set one indefinitely, use Flag Toggle.')
    );
}

extend('MechanicFlagClear', 'Component');

function MechanicFlagClear() {
    this.super('Flag Clear', Type.MECHANIC, false);

    this.description = 'Clears a flag from the target.';

    this.data.push(new StringValue('Key', 'key', 'key')
        .setTooltip('The unique string for the flag. This should match that of the mechanic that set the flag to begin with.')
    );
}

extend('MechanicFlagToggle', 'Component');

function MechanicFlagToggle() {
    this.super('Flag Toggle', Type.MECHANIC, false);

    this.description = 'Toggles a flag on or off for the target. This can be used to make toggle effects.';

    this.data.push(new StringValue('Key', 'key', 'key')
        .setTooltip('The unique string for the flag. Use the same key when checking it in a Flag Condition')
    );
}

extend('MechanicFood', 'Component');

function MechanicFood() {
    this.super('Food', Type.MECHANIC, false);

    this.description = 'Adds or removes to a player\'s hunger and saturation';

    this.data.push(new AttributeValue('Food', 'food', 1, 1)
        .setTooltip('The amount of food to give. Use a negative number to lower the food meter.')
    );
    this.data.push(new AttributeValue('Saturation', 'saturation', 0, 0)
        .setTooltip('How much saturation to give. Use a negative number to lower saturation. This is the hidden value that determines how long until food starts going down.')
    );
}

extend('MechanicForgetTargets', 'Component');

function MechanicForgetTargets() {
    this.super('Forget Targets', Type.MECHANIC, false);

    this.description = 'Clears targets stored by the "Remember Targets" mechanic';

    this.data.push(new StringValue('Key', 'key', 'key')
        .setTooltip('The unique key the targets were stored under')
    );
}

extend('MechanicHeal', 'Component');

function MechanicHeal() {
    this.super('Heal', Type.MECHANIC, false);

    this.description = 'Restores health to each target.';

    this.data.push(new ListValue("Type", "type", ["Health", "Percent"], "Health")
        .setTooltip('The unit to use for the amount of health to restore. Health restores a flat amount while Percent restores a percentage of their max health.')
    );
    this.data.push(new AttributeValue("Value", "value", 3, 1)
        .setTooltip('The amount of health to restore')
    );
}

extend('MechanicHealthSet', 'Component');

function MechanicHealthSet() {
    this.super('Health Set', Type.MECHANIC, false);

    this.description = 'Sets the target\'s health to the specified amount, ignoring resistances, damage buffs, and so on';

    this.data.push(new AttributeValue("Health", "health", 1, 0)
        .setTooltip('The health to set to')
    );
}

extend('MechanicHeldItem', 'Component');

function MechanicHeldItem() {
    this.super('Held Item', Type.MECHANIC, false);

    this.description = 'Sets the held item slot of the target player. This will do nothing if trying to set it to a skill slot.';

    this.data.push(new AttributeValue("Slot", "slot", 0, 0)
        .setTooltip('The slot to set it to')
    );
}

extend('MechanicImmunity', 'Component');

function MechanicImmunity() {
    this.super('Immunity', Type.MECHANIC, false);

    this.description = 'Provides damage immunity from one source for a duration.'

    this.data.push(new ListValue('Type', 'type', getDamageTypes, 'Poison')
        .setTooltip('The damage type to give an immunity for')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 0)
        .setTooltip('How long to give an immunity for')
    );
    this.data.push(new AttributeValue('Multiplier', 'multiplier', 0, 0)
        .setTooltip('The multiplier for the incoming damage. Use 0 if you want full immunity.')
    );
}

extend('MechanicInterrupt', 'Component');

function MechanicInterrupt() {
    this.super('Interrupt', Type.MECHANIC, false);

    this.description = 'Interrupts any channeling being done by each target if applicable.';
}

extend('MechanicItem', 'Component');

function MechanicItem() {
    this.super('Item', Type.MECHANIC, false);

    this.description = 'Gives each player target the item defined by the settings.';

    addItemOptions(this);
}

extend('MechanicItemDrop', 'Component');

function MechanicItemDrop() {
    this.super('Item Drop', Type.MECHANIC, false);

    this.description = 'Spawns a dropped item defined by the settings at the specified location.';
	
    this.data.push(new AttributeValue('Pickup Delay', 'pickup_delay', 10, 0)
        .setTooltip('How many ticks must pass before the item can be picked up, in ticks.')
    );
    this.data.push(new AttributeValue('Duration', 'duration', 6000, 0)
        .setTooltip('The time after which the item will despawn if not picked up, in ticks. Caps at 6000.')
    );

    addItemOptions(this);
	
	this.data.push(new SectionMarker("Offset"));
    this.data.push(new AttributeValue('Forward offset', 'forward', 0, 0)
        .setTooltip('How far forward in blocks to teleport. A negative value teleports backwards.')
    );
    this.data.push(new AttributeValue('Upward offset', 'upward', 0, 0)
        .setTooltip('How far upward in blocks to teleport. A negative value teleports downward.')
    );
    this.data.push(new AttributeValue('Right offset', 'right', 0, 0)
        .setTooltip('How far to the right in blocks to teleport. A negative value teleports to the left.')
    );
}

extend('MechanicItemProjectile', 'Component');

function MechanicItemProjectile() {
    this.super('Item Projectile', Type.MECHANIC, true);

    this.description = 'Launches a projectile using an item as its visual that applies child components upon landing. The target passed on will be the collided target or the location where it landed if it missed.';
    
    this.data.push(new ListValue("Group", "group", ["Ally", "Enemy"], "Enemy")
        .setTooltip('The alignment of targets to hit')
    );

	addItemOptions(this);
    addProjectileOptions(this);
    addEffectOptions(this, true);
}

extend('MechanicItemRemove', 'Component');

function MechanicItemRemove() {
    this.super('Item Remove', Type.MECHANIC, false);

    this.description = 'Removes an item from a player inventory. This does nothing to mobs.';

    this.data.push(new AttributeValue('Amount', 'amount', 1, 0)
        .setTooltip('The amount of the item needed in the player\'s inventory')
    );

    addItemConditionOptions(this);
}

extend('MechanicLaunch', 'Component');

function MechanicLaunch() {
    this.super('Launch', Type.MECHANIC, false);

    this.description = 'Launches the target relative to their forward direction. Use negative values to go in the opposite direction (e.g. negative forward makes the target go backwards)';

    this.data.push(new ListValue('Relative', 'relative', ['Target', 'Caster', 'Between'], 'Target')
        .setTooltip('Determines what is considered "forward". Target uses the direction the target is facing, Caster uses the direction the caster is facing, and Between uses the direction from the caster to the target.')
    );
    this.data.push(new AttributeValue('Forward Speed', 'forward', 0, 0)
        .setTooltip('The speed to give the target in the direction they are facing')
    );
    this.data.push(new AttributeValue('Upward Speed', 'upward', 2, 0.5)
        .setTooltip('The speed to give the target upwards')
    );
    this.data.push(new AttributeValue('Right Speed', 'right', 0, 0)
        .setTooltip('The speed to give the target to their right')
    );
}

extend('MechanicLightning', 'Component');

function MechanicLightning() {
    this.super('Lightning', Type.MECHANIC, true);

    this.description = 'Strikes lightning on or near the target, applying child components to the struck targets. Negative offsets will offset it in the opposite direction (e.g. negative forward offset puts it behind the target).';

    this.data.push(new AttributeValue('Damage', 'damage', 5, 0)
        .setTooltip('The damage dealt by the lightning bolt')
    );
    this.data.push(new ListValue("Group", "group", ["Ally", "Enemy", "Both"], "Enemy")
        .setTooltip('The alignment of targets to hit')
    );
    this.data.push(new ListValue("Include Caster", "caster", ['True', 'False'], 'False')
        .setTooltip('Whether the lightning strike can hit the caster')
    );
	
	this.data.push(new SectionMarker("Offset"));
    this.data.push(new AttributeValue('Forward Offset', 'forward', 0, 0)
        .setTooltip('How far in front of the target in blocks to place the lightning')
    );
    this.data.push(new AttributeValue('Right Offset', 'right', 0, 0)
        .setTooltip('How far to the right of the target in blocks to place the lightning')
    );
}

extend('MechanicMana', 'Component');

function MechanicMana() {
    this.super('Mana', Type.MECHANIC, false);

    this.description = 'Restores or deducts mana from the target.';

    this.data.push(new ListValue('Type', 'type', ['Mana', 'Percent'], 'Mana')
        .setTooltip('The unit to use for the amount of mana to restore/drain. Mana does a flat amount while Percent does a percentage of their max mana')
    );
    this.data.push(new AttributeValue('Value', 'value', 1, 0)
        .setTooltip('The amount of mana to restore/drain')
    );
}

extend('MechanicMessage', 'Component');

function MechanicMessage() {
    this.super('Message', Type.MECHANIC, false);

    this.description = 'Sends a message to each player target. To include numbers from Value mechanics, use the filters {<key>} where <key> is the key the value is stored under.'

    this.data.push(new StringValue('Message', 'message', 'text')
        .setTooltip('The message to display. {player} = caster\'s name, {target} = target\'s name, {targetUUID} = target\'s UUID (useful if targets are non players), &lc: "{", &rc: "}", &sq: "\'"')
    );
}

extend('MechanicMine', 'Component');

function MechanicMine() {
    this.super('Mine', Type.MECHANIC, false);

    this.description = 'Destroys a selection of blocks at the location of the target.'
	
	this.data.push(new MultiListValue('Material', 'materials', ['Origin', ...getAnyMaterials()], ['Origin'])
        .setTooltip('The types of blocks allowed to be broken. \'Origin\' refers to the material at the targeted location')
    );
    this.data.push(new ListValue('Drop', 'drop', ['True', 'False'], 'True')
        .setTooltip('Whether to create drops for the destroyed blocks')
    );
    this.data.push(new ListValue('Tool', 'tool', ['Caster', 'Target', ...getMaterials()], 'Diamond pickaxe').requireValue('drop', ['True'])
        .setTooltip('What tool to use when breaking the blocks. This allows to take into account the fact that, for example, Diamond Ore does not drop when mined with a Stone Pickaxe, as well as to consider enchantments like Looting and Silk Touch. \'Caster\' an \'Target\' refers to the items in their respective main hands')
    );
	
    this.data.push(new ListValue('Shape', 'shape', ['Sphere', 'Cuboid'], 'Sphere')
        .setTooltip('The shape of the region to mine')
    );

    // Sphere options
    this.data.push(new AttributeValue('Radius', 'radius', 2, 0).requireValue('shape', ['Sphere'])
        .setTooltip('The radius of the sphere, in blocks')
    );

    // Cuboid options
    this.data.push(new AttributeValue('Width (X)', 'width', 3, 0).requireValue('shape', ['Cuboid'])
        .setTooltip('The width of the cuboid, in blocks')
    );
    this.data.push(new AttributeValue('Height (Y)', 'height', 3, 0).requireValue('shape', ['Cuboid'])
        .setTooltip('The height of the cuboid, in blocks')
    );
    this.data.push(new AttributeValue('Depth (Z)', 'depth', 3, 0).requireValue('shape', ['Cuboid'])
        .setTooltip('The depth of the cuboid, in blocks')
    );
	
	this.data.push(new SectionMarker("Offset"));
    this.data.push(new AttributeValue('Forward Offset', 'forward', 0, 0)
        .setTooltip('How far forward in front of the target the region should be in blocks. A negative value will put it behind.')
    );
    this.data.push(new AttributeValue('Upward Offset', 'upward', 0, 0)
        .setTooltip('How far above the target the region should be in blocks. A negative value will put it below.')
    );
    this.data.push(new AttributeValue('Right Offset', 'right', 0, 0)
        .setTooltip('How far to the right the region should be of the target. A negative value will put it to the left.')
    );
}

extend('MechanicMoney', 'Component');

function MechanicMoney() {
    this.super('Money', Type.MECHANIC, false);

    this.description = 'Adds or multiplies the target\'s balance by some amount (requires Vault and an economy plugin). Fails if the resulting balance is not within the range allowed by the economy plugin.'
	
    this.data.push(new ListValue('Type', 'type', ['Add', 'Multiply'], 'Add')
	.setTooltip('Whether the target\'s balance will be added or multiplied by the set amount.')
    );
    this.data.push(new AttributeValue('Amount', 'amount', 5, 0)
        .setTooltip('The amount that the target\'s balance will be added or multiplied by. Can be negative.')
    );
    this.data.push(new ListValue('Allows negative', 'allows_negative', ['True', 'False'], 'False')
		.setTooltip('Whether the mechanic will be executed even if it will result in the target having a negative balance.')
    );
}

extend('MechanicParticle', 'Component');

function MechanicParticle() {
    this.super('Particle', Type.MECHANIC, false);

    this.description = 'Plays a particle effect about the target.';

    addParticleOptions(this);

    this.data.push(new SectionMarker('Offset'));
    this.data.push(new DoubleValue('Forward Offset', 'forward', 0)
        .setTooltip('How far forward in front of the target in blocks to play the particles. A negative value will go behind.')
    );
    this.data.push(new DoubleValue('Upward Offset', 'upward', 0)
        .setTooltip('How far above the target in blocks to play the particles. A negative value will go below.')
    );
    this.data.push(new DoubleValue('Right Offset', 'right', 0)
        .setTooltip('How far to the right of the target to play the particles. A negative value will go to the left.')
    );
}

extend('MechanicParticleAnimation', 'Component');

function MechanicParticleAnimation() {
    this.super('Particle Animation', Type.MECHANIC, false);

    this.description = 'Plays an animated particle effect at the location of each target over time by applying various transformations.';

    this.data.push(new IntValue('Steps', 'steps', 1, 0)
        .setTooltip('The number of times to play particles and apply translations each application.')
    );
    this.data.push(new DoubleValue('Frequency', 'frequency', 0.05, 0)
        .setTooltip('How often to apply the animation in seconds. 0.05 is the fastest (1 tick). Lower than that will act the same.')
    );
    this.data.push(new IntValue('Angle', 'angle', 0)
        .setTooltip('How far the animation should rotate over the duration in degrees')
    );
    this.data.push(new IntValue('Start Angle', 'start', 0)
        .setTooltip('The starting orientation of the animation. Horizontal translations and the forward/right offsets will be based off of this.')
    );
    this.data.push(new AttributeValue('Duration', 'duration', 5, 0)
        .setTooltip('How long the animation should last for in seconds')
    );
    this.data.push(new AttributeValue('H-Translation', 'h-translation', 0, 0)
        .setTooltip('How far the animation moves horizontally relative to the center over a cycle. Positive values make it expand from the center while negative values make it contract.')
    );
    this.data.push(new AttributeValue('V-Translation', 'v-translation', 0, 0)
        .setTooltip('How far the animation moves vertically over a cycle. Positive values make it rise while negative values make it sink.')
    );
    this.data.push(new IntValue('H-Cycles', 'h-cycles', 1)
        .setTooltip('How many times to move the animation position throughout the animation. Every other cycle moves it back to where it started. For example, two cycles would move it out and then back in.')
    );
    this.data.push(new IntValue('V-Cycles', 'v-cycles', 1)
        .setTooltip('How many times to move the animation position throughout the animation. Every other cycle moves it back to where it started. For example, two cycles would move it up and then back down.')
    );

    addParticleOptions(this);

    this.data.push(new SectionMarker('Offset'));
    this.data.push(new DoubleValue('Forward Offset', 'forward', 0)
        .setTooltip('How far forward in front of the target in blocks to play the particles. A negative value will go behind.')
    );
    this.data.push(new DoubleValue('Upward Offset', 'upward', 0)
        .setTooltip('How far above the target in blocks to play the particles. A negative value will go below.')
    );
    this.data.push(new DoubleValue('Right Offset', 'right', 0)
        .setTooltip('How far to the right of the target to play the particles. A negative value will go to the left.')
    );

    this.data.push(new ListValue('Rotate w/ Player', '-with-rotation', ['True', 'False'], 'False')
        .setTooltip('Whether to follow the rotation of the player for the effect.')
    );
}

extend('MechanicParticleEffect', 'Component');

function MechanicParticleEffect() {
    this.super('Particle Effect', Type.MECHANIC, false);

    this.description = 'Plays a particle effect that follows the current target, using formulas to determine shape, size, and motion';

    addEffectOptions(this, false);
}

extend('MechanicParticleProjectile', 'Component');

function MechanicParticleProjectile() {
    this.super('Particle Projectile', Type.MECHANIC, true);

    this.description = 'Launches a projectile using particles as its visual that applies child components upon landing. The target passed on will be the collided target or the location where it landed if it missed.';

    this.data.push(new DoubleValue('Gravity', 'gravity', 0)
        .setTooltip('How much gravity to apply each tick. Negative values make it fall while positive values make it rise')
    );
    this.data.push(new ListValue('Pierce', 'pierce', ['True', 'False'], 'False')
        .setTooltip('Whether this projectile should pierce through initial targets and continue hitting those behind them')
    );
    this.data.push(new ListValue("Group", "group", ["Ally", "Enemy"], "Enemy")
        .setTooltip('The alignment of targets to hit')
    );
	
    addProjectileOptions(this);

    addParticleOptions(this);

    this.data.push(new DoubleValue('Frequency', 'frequency', 0.05)
        .setTooltip('How often to play a particle effect where the projectile is. It is recommended not to change this value unless there are too many particles playing')
    );

    addEffectOptions(this, true);
}

extend('MechanicPassive', 'Component');

function MechanicPassive() {
    this.super('Passive', Type.MECHANIC, true);

    this.description = 'Applies child components continuously every period. The seconds value below is the period or how often it applies.';

    this.data.push(new AttributeValue('Seconds', 'seconds', 1, 0)
        .setTooltip('The delay in seconds between each application')
    );
}

extend('MechanicPermission', 'Component');

function MechanicPermission() {
    this.super('Permission', Type.MECHANIC, true);

    this.description = 'Grants each player target a permission for a limited duration. This mechanic requires Vault with an accompanying permissions plugin in order to work.';

    this.data.push(new StringValue('Permission', 'perm', 'plugin.perm.key')
        .setTooltip('The permission to give to the player')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 0)
        .setTooltip('How long in seconds to give the permission to the player')
    );
}

extend('MechanicPotion', 'Component');

function MechanicPotion() {
    this.super('Potion', Type.MECHANIC, false);

    this.description = 'Applies a potion effect to the target for a duration.';

    this.data.push(new ListValue('Potion', 'potion', getPotionTypes, 'Absorption')
        .setTooltip('The type of potion effect to apply')
    );
    this.data.push(new ListValue('Ambient Particles', 'ambient', ['True', 'False'], 'True')
        .setTooltip('Whether to show ambient particles')
    );
    this.data.push(new AttributeValue('Tier', 'tier', 1, 0)
        .setTooltip('The strength of the potion')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 1)
        .setTooltip('How long to apply the effect for')
    );
}

extend('MechanicPotionProjectile', 'Component');

function MechanicPotionProjectile() {
    this.super('Potion Projectile', Type.MECHANIC, true);

    this.description = 'Drops a splash potion from each target that does not apply potion effects by default. This will apply child elements when the potion lands. The targets supplied will be everything hit by the potion. If nothing is hit by the potion, the target will be the location it landed.';

    this.data.push(new ListValue('Type', 'type', getPotionTypes, 'Fire Resistance')
        .setTooltip('The type of the potion to use for the visuals')
    );
    this.data.push(new ListValue("Group", "group", ["Ally", "Enemy", "Both"], "Enemy")
        .setTooltip('The alignment of entities to hit')
    );
    this.data.push(new ListValue('Linger', 'linger', ['True', 'False'], 'False')
        .setTooltip('Whether the potion should be a lingering potion (for 1.9+ only)')
    );
}

extend('MechanicProjectile', 'Component');

function MechanicProjectile() {
    this.super('Projectile', Type.MECHANIC, true);

    this.description = 'Launches a projectile that applies child components on hit. The target supplied will be the struck target.';

    this.data.push(new ListValue('Projectile', 'projectile', ['Arrow',
                                                              'Egg',
                                                              'Snowball',
                                                              'Fireball',
                                                              'Large Fireball',
                                                              'Small fireball'], 'Arrow')
        .setTooltip('The type of projectile to fire')
    );
    this.data.push(new ListValue('Flaming', 'flaming', ['True', 'False'], 'False')
        .setTooltip('Whether to make the launched projectiles on fire.')
    );
    this.data.push(new ListValue('Cost', 'cost', ['None', 'All', 'One'], 'None')
        .setTooltip('The item cost of the skill. "One" will only charge the player 1 item of it\'s type, whereas "All" will charge 1 for each fired projectile.')
    );

    addProjectileOptions(this);
    addEffectOptions(this, true);
}

extend('MechanicPurge', 'Component');

function MechanicPurge() {
    this.super('Purge', Type.MECHANIC, false);

    this.description = 'Purges the target of positive potion effects or statuses';

    this.data.push(new MultiListValue('Potion', 'potion', getGoodPotions)
        .setTooltip('The potion effect to remove from the target, if any')
    );
    this.data.push(new MultiListValue('Status', 'status', ['All', 'Absorb', 'Invincible'])
        .setTooltip('The status to remove from the target, if any')
    );
}

extend('MechanicPush', 'Component');

function MechanicPush() {
    this.super('Push', Type.MECHANIC, false);

    this.description = 'Pushes the target relative to the caster. This will do nothing if used with the caster as the target. Positive numbers apply knockback while negative numbers pull them in.';

    this.data.push(new ListValue('Type', 'type', ['Fixed', 'Inverse', 'Scaled'], 'Fixed')
        .setTooltip('How to scale the speed based on relative position. Fixed does the same speed to all targets. Inverse pushes enemies farther away faster. Scaled pushes enemies closer faster.')
    );
    this.data.push(new AttributeValue('Speed', 'speed', 3, 1)
        .setTooltip('How fast to push the target away. Use a negative value to pull them closer.')
    );
    this.data.push(new StringValue('Source', 'source', 'none')
        .setTooltip('The source to push/pull from. This should be a key used in a Remember Targets mechanic. If no targets are remembered, this will default to the caster.')
    );
}

extend('MechanicRememberTargets', 'Component');

function MechanicRememberTargets() {
    this.super('Remember Targets', Type.MECHANIC, false);

    this.description = 'Stores the current targets for later use under a specified key';

    this.data.push(new StringValue('Key', 'key', 'target')
        .setTooltip('The unique key to store the targets under. The "Remember" target will use this key to apply effects to the targets later on.')
    );
}

extend('MechanicRepeat', 'Component');

function MechanicRepeat() {
    this.super('Repeat', Type.MECHANIC, true);

    this.description = 'Applies child components multiple times. When it applies them is determined by the delay (seconds before the first application) and period (seconds between successive applications).';

    this.data.push(new AttributeValue('Repetitions', 'repetitions', 3, 0)
        .setTooltip('How many times to activate child components')
    );
    this.data.push(new DoubleValue('Period', 'period', 1)
        .setTooltip('The time in seconds between each time applying child components')
    );
    this.data.push(new DoubleValue('Delay', 'delay', 0)
        .setTooltip('The initial delay before starting to apply child components')
    );
    this.data.push(new ListValue('Stop on Fail', 'stop-on-fail', ['True', 'False'], 'False')
        .setTooltip('Whether to stop the repeat task early if the effects fail')
    );
}

extend('MechanicSound', 'Component');

function MechanicSound() {
    this.super('Sound', Type.MECHANIC, false);

    this.description = "Plays a sound at the target's location.";

    this.data.push(new ListValue('Sound', 'sound', [ 'Custom', ...getSounds() ], 'Ambient Cave')
        .setTooltip('The sound clip to play. Select \'Custom\' to enter custom sounds from your resource pack.')
    );
    this.data.push(new StringValue('Custom sound name', 'custom', 'myrp:some_sound')
		.requireValue('sound', ['Custom'])
        .setTooltip('Namespaced key of your custom sound.')
    );
    this.data.push(new AttributeValue('Volume', 'volume', 100, 0)
        .setTooltip('The volume of the sound as a percentage. Numbers above 100 will not get any louder, but will be heard from a farther distance')
    );
    this.data.push(new AttributeValue('Pitch', 'pitch', 1, 0)
        .setTooltip('The pitch of the sound as a numeric speed multiplier between 0.5 and 2.')
    );
}

extend('MechanicStat', 'Component');

function MechanicStat() {
    this.super('Stat', Type.MECHANIC, false);

    this.description = 'Gives a player bonus stat temporarily.';

    // this.data.push(new ListValue('Stat', 'key', ['health',
    //                                              'mana',
    //                                              'mana-regen',
    //                                              'physical-damage',
    //                                              'melee-damage',
    //                                              'projectile-damage',
    //                                              'physical-damage',
    //                                              'melee-defense',
    //                                              'projectile-defense',
    //                                              'skill-damage',
    //                                              'skill-defense',
    //                                              'move-speed',
    //                                              'attack-speed',
    //                                              'armor',
    //                                              'luck',
    //                                              'armor-toughness',
    //                                              'exp',
    //                                              'hunger',
    //                                              'hunger-heal',
    //                                              'cooldown',
    //                                              'knockback-resist'], 'health')
    //     .setTooltip('The name of the stat to add to')
    // );
    this.data.push(new StringValue('Stat', 'key', 'health')
        .setTooltip('The name of the stat to add to')
    );
    this.data.push(new ListValue('Operation', 'operation', ['ADD_NUMBER', 'MULTIPLY_PERCENTAGE'], 'ADD_NUMBER')
        .setTooltip('The operation on the original value by amount, ADD_NUMBER: Scalar adding, MULTIPLY_PERCENTAGE: Multiply the value by amount')
    );
    this.data.push(new AttributeValue('Amount', 'amount', 5, 2)
        .setTooltip('The amount to use with the operation')
    );
    this.data.push(new AttributeValue('Seconds', 'seconds', 3, 0)
        .setTooltip('How long in seconds to give the stat to the player')
    );
    this.data.push(new ListValue('Stackable', 'stackable', ['True', 'False'], 'False')
        .setTooltip('Whether applying multiple times stacks the effects')
    );
}

extend('MechanicStatus', 'Component');

function MechanicStatus() {
    this.super('Status', Type.MECHANIC, false);

    this.description = 'Applies a status effect to the target for a duration.';

    this.data.push(new ListValue('Status', 'status', ['Absorb',
                                                      'Curse',
                                                      'Disarm',
                                                      'Invincible',
                                                      'Root',
                                                      'Silence',
                                                      'Stun'], 'Stun')
        .setTooltip('The status to apply')
    );
    this.data.push(new AttributeValue('Duration', 'duration', 3, 1)
        .setTooltip('How long in seconds to apply the status')
    );
}

extend('MechanicTaunt', 'Component');

function MechanicTaunt() {
    this.super('Taunt', Type.MECHANIC, false);

    this.description = 'Draws aggro of targeted creatures. Regular mobs are set to attack the caster. The Spigot/Bukkit API for this was not functional on older versions, so it may not work on older servers. For MythicMobs, this uses their aggro system using the amount chosen below.';

    this.data.push(new AttributeValue('Amount', 'amount', 1, 0)
        .setTooltip('The amount of aggro to apply if MythicMobs is active. Use negative amounts to reduce aggro')
    );
}

extend('MechanicTrigger', 'Component');

function MechanicTrigger() {
    this.super('Trigger', Type.MECHANIC, true);

    this.description = 'Listens for a trigger on the current targets for a duration.';

    this.data.push(new ListValue('Trigger', 'trigger', ['Block Break',
                                                        'Block Place', 
                                                        'Crouch',
                                                        'Death',
                                                        'Drop Item',
                                                        'Environment Damage',
                                                        'Fishing',
                                                        'Fishing Bite',
                                                        'Fishing Fail',
                                                        'Fishing Grab',
                                                        'Fishing Ground',
                                                        'Fishing Reel',
                                                        'Item Swap',
                                                        'Kill',
                                                        'Land',
                                                        'Launch',
                                                        'Left Click',
                                                        'Move',
                                                        'Physical Damage',
                                                        'Right Click',
                                                        'Skill Damage',
                                                        'Took Physical Damage',
                                                        'Took Skill Damage'], 'Death')
        .setTooltip('The trigger to listen for')
    );
    this.data.push(new AttributeValue('Duration', 'duration', 5, 0)
        .setTooltip('How long to listen to the trigger for')
    );
    this.data.push(new ListValue('Stackable', 'stackable', ['True', 'False',], 'True')
        .setTooltip('Whether different players (or the same player) can listen to the same target at the same time')
    );
    this.data.push(new ListValue('Once', 'once', ['True', 'False'], 'True')
        .setTooltip('Whether the trigger should only be used once each cast. When false, the trigger can execute as many times as it happens for the duration.')
    );

    //BLOCK
    var blockTriggers = ['Block Break', 'Block Place'];
    this.data.push(new MultiListValue('Material', 'material', getAnyMaterials, ['Any'])
        .requireValue('trigger', blockTriggers)
        .setTooltip('The type of block expected to be handled')
    );

    this.data.push(new IntValue('Data', 'data', -1)
        .requireValue('trigger', blockTriggers)
        .setTooltip('The expected data value of the block (-1 for any data value)')
    );

    //CLICK
    var clickTriggers = ['Left Click', 'Right Click'];
    this.data.push(new ListValue('Crouch', 'crouch', ['Crouch', 'Dont crouch', 'Both'], 'Crouch')
        .requireValue('trigger', clickTriggers)
        .setTooltip('If the player has to be crouching in order for this trigger to function')
    );

    // CROUCH
    this.data.push(new ListValue('Type', 'type', ['Start Crouching', 'Stop Crouching', 'Both'], 'Start Crouching')
        .requireValue('trigger', ['Crouch'])
        .setTooltip('Whether you want to apply components when crouching or not crouching')
    );

    //DROP_ITEM
    this.data.push(new ListValue('Drop multiple', 'drop multiple', ['True', 'False', 'Ignore'], 'Ignore')
        .requireValue('trigger', ['Drop Item'])
        .setTooltip('Whether the player has to drop multiple items or a single item')
    );

    // ENVIRONMENT_DAMAGE
    this.data.push(new ListValue('Type', 'type', getDamageTypes, 'FALL')
        .requireValue('trigger', ['Environment Damage'])
        .setTooltip('The source of damage to apply for')
    );

    //ITEM_SWAP
    this.data.push(new ListValue('Cancel swap', 'cancel', ['True', 'False'], 'True')
        .requireValue('trigger', ['Item Swap'])
        .setTooltip('True cancels the item swap. False allows the item swap'));

    // LAND
    this.data.push(new DoubleValue('Min Distance', 'min-distance', 0)
        .requireValue('trigger', ['Land'])
        .setTooltip('The minimum distance the player should fall before effects activating.')
    );

    // LAUNCH
    this.data.push(new ListValue('Type', 'type', ['Any',
                                                  'Arrow',
                                                  'Egg',
                                                  'Ender Pearl',
                                                  'Fireball',
                                                  'Fishing Hook',
                                                  'Snowball'], 'Any')
        .requireValue('trigger', ['Launch'])
        .setTooltip('The type of projectile that should be launched.')
    );

    // PHYSICAL
    this.data.push(new ListValue('Type', 'type', ['Both', 'Melee', 'Projectile'], 'Both')
        .requireValue('trigger', ['Physical Damage', 'Took Physical Damage'])
        .setTooltip('The type of damage dealt')
    );

    // SKILL
    this.data.push(new StringValue('Category', 'category', '')
        .requireValue('trigger', ['Skill Damage', 'Took Skill Damage'])
        .setTooltip('The type of skill damage to apply for. Leave this empty to apply to all skill damage.')
    );

    // DAMAGE
    var damageTriggers = ['Physical Damage', 'Skill Damage', 'Took Physical Damage', 'Took Skill Damage'];
    this.data.push(new ListValue('Target Listen Target', 'target', ['True', 'False'], 'True')
        .requireValue('trigger', damageTriggers)
        .setTooltip('True makes children target the target that has been listened to. False makes children target the entity fighting the target entity.')
    );
    this.data.push(new DoubleValue("Min Damage", "dmg-min", 0)
        .requireValue('trigger', damageTriggers)
        .setTooltip('The minimum damage that needs to be dealt')
    );
    this.data.push(new DoubleValue("Max Damage", "dmg-max", 999)
        .requireValue('trigger', damageTriggers)
        .setTooltip('The maximum damage that needs to be dealt')
    );
}

extend('MechanicValueAdd', 'Component');

function MechanicValueAdd() {
    this.super('Value Add', Type.MECHANIC, false);

    this.description = 'Adds to a stored value under a unique key for the caster. If the value wasn\'t set before, this will set the value to the given amount.';

    this.data.push(new StringValue('Key', 'key', 'value')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new AttributeValue('Amount', 'amount', 1, 0)
        .setTooltip('The amount to add to the value')
    );
}

extend('MechanicValueAttribute', 'Component');

function MechanicValueAttribute() {
    this.super('Value Attribute', Type.MECHANIC, false);

    this.description = 'Loads a player\'s attribute count for a specific attribute as a stored value to be used in other mechanics.';

    this.data.push(new StringValue('Key', 'key', 'attribute')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new StringValue('Attribute', 'attribute', 'Vitality')
        .setTooltip('The name of the attribute you are loading the value of')
    );
}

extend('MechanicValueCopy', 'Component');

function MechanicValueCopy() {
    this.super('Value Copy', Type.MECHANIC, false);

    this.description = 'Copies a stored value from the caster to the target or vice versa';

    this.data.push(new StringValue('Key', 'key', 'value')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new StringValue('Destination', 'destination', 'value')
        .setTooltip('The key to copy the original value to')
    );
    this.data.push(new ListValue('To target', 'to-target', ['True', 'False'], 'True')
        .setTooltip('The amount to add to the value')
    );
}

extend('MechanicValueDistance', 'Component');

function MechanicValueDistance() {
    this.super('Value Distance', Type.MECHANIC, false);

    this.description = 'Stores the distance between the target and the caster into a value';

    this.data.push(new StringValue('Key', 'key', 'attribute')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
}

extend('MechanicValueHealth', 'Component');

function MechanicValueHealth() {
    this.super('Value Health', Type.MECHANIC, false);

    this.description = 'Stores the target\'s current health as a value under a given key for the caster';

    this.data.push(new StringValue('Key', 'key', 'value')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new ListValue('Type', 'type', ['Current', 'Max', 'Missing', 'Percent'], 'Current')
        .setTooltip('Current provides the health the target has, max provides their total health, missing provides how much health they have lost, and percent is the ratio of health to total health.')
    );
}

extend('MechanicValueLocation', 'Component');

function MechanicValueLocation() {
    this.super('Value Location', Type.MECHANIC, false);

    this.description = 'Loads the first target\'s current location into a stored value for use at a later time.';

    this.data.push(new StringValue('Key', 'key', 'location')
        .setTooltip('The unique key to store the location under. This key can be used in place of attribute values to use the stored value.')
    );
}

extend('MechanicValueLore', 'Component');

function MechanicValueLore() {
    this.super('Value Lore', Type.MECHANIC, false);

    this.description = 'Loads a value from a held item\'s lore into a stored value under the given unique key for the caster.';

    this.data.push(new StringValue('Key', 'key', 'lore')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new ListValue("Hand", "hand", ['Main', 'Offhand'], 'Main')
        .setTooltip('The hand to check for the item. Offhand items are MC 1.9+ only.')
    );
    this.data.push(new StringValue('Regex', 'regex', 'Damage: {value}')
        .setTooltip('The regex string to look for, using {value} as the number to store. If you do not know about regex, consider looking it up on Wikipedia or avoid using major characters such as [ ] { } ( ) . + ? * ^ \\ |')
    );
    this.data.push(new AttributeValue('Multiplier', 'multiplier', 1, 0)
        .setTooltip('The multiplier for the acquired value. If you want the value to remain unchanged, leave this value at 1.')
    );
}

extend('MechanicValueLoreSlot', 'Component');

function MechanicValueLoreSlot() {
    this.super('Value Lore Slot', Type.MECHANIC, false);

    this.description = 'Loads a value from an item\'s lore into a stored value under the given unique key for the caster.';

    this.data.push(new StringValue('Key', 'key', 'lore')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new IntValue("Slot", "slot", 9)
        .setTooltip('The slot of the inventory to fetch the item from. Slots 0-8 are the hotbar, 9-35 are the main inventory, 36-39 are armor, and 40 is the offhand slot.')
    );
    this.data.push(new StringValue('Regex', 'regex', 'Damage: {value}')
        .setTooltip('The regex string to look for, using {value} as the number to store. If you do not know about regex, consider looking it up on Wikipedia or avoid using major characters such as [ ] { } ( ) . + ? * ^ \\ |')
    );
    this.data.push(new AttributeValue('Multiplier', 'multiplier', 1, 0)
        .setTooltip('The multiplier for the acquired value. If you want the value to remain unchanged, leave this value at 1.')
    );
}

extend('MechanicValueMana', 'Component');

function MechanicValueMana() {
    this.super('Value Mana', Type.MECHANIC, false);

    this.description = 'Stores the target player\'s current mana as a value under a given key for the caster';

    this.data.push(new StringValue('Key', 'key', 'value')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new ListValue('Type', 'type', ['Current', 'Max', 'Missing', 'Percent'], 'Current')
        .setTooltip('Current provides the mana the target has, max provides their total mana, missing provides how much mana they have lost, and percent is the ratio of health to total mana.')
    );
}

extend('MechanicValueMultiply', 'Component');

function MechanicValueMultiply() {
    this.super('Value Multiply', Type.MECHANIC, false);

    this.description = 'Multiplies a stored value under a unique key for the caster. If the value wasn\'t set before, this will not do anything.';

    this.data.push(new StringValue('Key', 'key', 'value')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new AttributeValue('Multiplier', 'multiplier', 1, 0)
        .setTooltip('The amount to multiply the value by')
    );
}

extend('MechanicValuePlaceholder', 'Component');

function MechanicValuePlaceholder() {
    this.super('Value Placeholder', Type.MECHANIC, false);

    this.description = 'Uses a placeholder string and stores it as a value for the caster';

    this.data.push(new StringValue('Key', 'key', 'value')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new ListValue("Type", "type", ['Number', 'String'], 'Number')
        .setTooltip('The type of value to store. Number values require numeric placeholders. String values can be used in messages or commands.')
    );
    this.data.push(new StringValue('Placeholder', 'placeholder', '%player_food_level%')
        .setTooltip('The placeholder string to use. Can contain multiple placeholders if using the String type.')
    );
}

extend('MechanicValueRandom', 'Component')

function MechanicValueRandom() {
    this.super('Value Random', Type.MECHANIC, false);

    this.description = 'Stores a specified value under a given key for the caster.';

    this.data.push(new StringValue('Key', 'key', 'value')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new ListValue('Type', 'type', ['Normal', 'Triangular'], 'Normal')
        .setTooltip('The type of random to use. Triangular favors numbers in the middle, similar to rolling two dice.')
    );
    this.data.push(new AttributeValue('Min', 'min', 0, 0)
        .setTooltip('The minimum value it can be')
    );
    this.data.push(new AttributeValue('Max', 'max', 0, 0)
        .setTooltip('The maximum value it can be')
    );
}

extend('MechanicValueSet', 'Component');

function MechanicValueSet() {
    this.super('Value Set', Type.MECHANIC, false);

    this.description = 'Stores a specified value under a given key for the caster.';

    this.data.push(new StringValue('Key', 'key', 'value')
        .setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.')
    );
    this.data.push(new AttributeValue('Value', 'value', 1, 0)
        .setTooltip('The value to store under the key')
    );
}

extend('MechanicWarp', 'Component');

function MechanicWarp() {
    this.super('Warp', Type.MECHANIC, false);

    this.description = 'Warps the target relative to their forward direction. Use negative numbers to go in the opposite direction (e.g. negative forward will cause the target to warp backwards).';

    this.data.push(new ListValue('Through Walls', 'walls', ['True', 'False'], 'False')
        .setTooltip('Whether to allow the target to teleport through walls')
    );
    this.data.push(new SectionMarker('Position'));
    this.data.push(new AttributeValue('Forward', 'forward', 3, 1)
        .setTooltip('How far forward in blocks to teleport. A negative value teleports backwards.')
    );
    this.data.push(new AttributeValue('Upward', 'upward', 0, 0)
        .setTooltip('How far upward in blocks to teleport. A negative value teleports downward.')
    );
    this.data.push(new AttributeValue('Right', 'right', 0, 0)
        .setTooltip('How far to the right in blocks to teleport. A negative value teleports to the left.')
    );
}

extend('MechanicWarpLoc', 'Component');

function MechanicWarpLoc() {
    this.super('Warp Location', Type.MECHANIC, false);

    this.description = 'Warps the target to a specified location.';

    this.data.push(new StringValue('World (or "current")', 'world', 'current')
        .setTooltip('The name of the world that the location is in')
    );
    this.data.push(new DoubleValue('X', 'x', 0)
        .setTooltip('The X-coordinate of the desired position')
    );
    this.data.push(new DoubleValue('Y', 'y', 0)
        .setTooltip('The Y-coordinate of the desired position')
    );
    this.data.push(new DoubleValue('Z', 'z', 0)
        .setTooltip('The Z-coordinate of the desired position')
    );
    this.data.push(new DoubleValue('Yaw', 'yaw', 0)
        .setTooltip('The Yaw of the desired position (left/right orientation)')
    );
    this.data.push(new DoubleValue('Pitch', 'pitch', 0)
        .setTooltip('The Pitch of the desired position (up/down orientation)')
    );
}

extend('MechanicWarpRandom', 'Component');

function MechanicWarpRandom() {
    this.super('Warp Random', Type.MECHANIC, false);

    this.description = 'Warps the target in a random direction the given distance.';

    this.data.push(new ListValue('Only Horizontal', 'horizontal', ['True', 'False'], 'True')
        .setTooltip('Whether to limit the random position to the horizontal plane')
    );
    this.data.push(new ListValue('Through Walls', 'walls', ['True', 'False'], 'False')
        .setTooltip('Whether to allow the target to teleport through walls')
    );
    this.data.push(new AttributeValue('Distance', 'distance', 3, 1)
        .setTooltip('The max distance in blocks to teleport')
    );
}

extend('MechanicWarpSwap', 'Component');

function MechanicWarpSwap() {
    this.super('Warp Swap', Type.MECHANIC, false);

    this.description = 'Switches the location of the caster and the target. If multiple targets are provided, this takes the first one.';
}

extend('MechanicWarpTarget', 'Component');

function MechanicWarpTarget() {
    this.super('Warp Target', Type.MECHANIC, false);

    this.description = 'Warps either the target or the caster to the other. This does nothing when the target is the caster.';

    this.data.push(new ListValue('Type', 'type', ['Caster to Target', 'Target to Caster'], 'Caster to Target')
        .setTooltip('The direction to warp the involved targets')
    );
}

extend('MechanicWarpValue', 'Component');

function MechanicWarpValue() {
    this.super('Warp Value', Type.MECHANIC, false);

    this.description = 'Warps all targets to a location remembered using the Value Location mechanic.';

    this.data.push(new StringValue('Key', 'key', 'location')
        .setTooltip('The unique key the location is stored under. This should be the same key used in the Value Location mechanic.')
    );
}

extend('MechanicWolf', 'Component');

function MechanicWolf() {
    this.super('Wolf', Type.MECHANIC, true);

    this.description = 'Summons a wolf on each target for a duration. Child components will start off targeting the wolf so you can add effects to it. You can also give it its own skillset, though Cast triggers will not occur.';

    this.data.push(new ListValue('Collar Color', 'color', getDyes(), 'Black')
        .setTooltip('The color of the collar that the wolf should wear')
    );
    this.data.push(new StringValue('Wolf Name', 'name', "{player}'s Wolf")
        .setTooltip('The displayed name of the wolf. Use {player} to embed the caster\'s name.')
    );
    this.data.push(new AttributeValue('Health', 'health', 10, 0)
        .setTooltip('The starting health of the wolf')
    );
    this.data.push(new AttributeValue('Damage', 'damage', 3, 0)
        .setTooltip('The damage dealt by the wolf each attack')
    );
    this.data.push(new ListValue('Sitting', 'sitting', ['True', 'False'], 'False')
        .setTooltip('Whether the wolf starts off sitting')
    );
    this.data.push(new AttributeValue('Duration', 'seconds', 10, 0)
        .setTooltip('How long to summon the wolf for')
    );
    this.data.push(new AttributeValue('Amount', 'amount', 1, 0)
        .setTooltip('How many wolves to summon')
    );
    this.data.push(new StringListValue('Skills (one per line)', 'skills', [])
        .setTooltip('The skills to give the wolf. Skills are executed at the level of the skill summoning the wolf. Skills needing a Cast trigger will not work.')
    );
}

// The active component being edited or added to
var activeComponent = undefined;

/**
 * Adds the options for item related effects to the component
 *
 * @param {Component} component - the component to add to
 */
function addItemOptions(component) {

    component.data.push(new SectionMarker('Item Options'));
    component.data.push(new ListValue('Material', 'material', getMaterials, 'Arrow')
        .setTooltip('The type of item to give to the player')
    );
    component.data.push(new IntValue('Amount', 'amount', 1)
        .setTooltip('The quantity of the item to give to the player')
    );
    component.data.push(new IntValue('Durability', 'data', 0).requireValue('material', getDamageableMaterials())
        .setTooltip('The durability to reduce from the item')
    );
    component.data.push(new ListValue('Unbreakable', 'unbreakable', ['True', 'False'], 'False').requireValue('material', getDamageableMaterials())
        .setTooltip('Whether to make the item unbreakable')
    );
    component.data.push(new IntValue('CustomModelData', 'byte', 0)
        .setTooltip('The CustomModelData of the item')
    );
	component.data.push(new MultiListValue('Hide Flags', 'hide-flags', ['Enchants', 'Attributes', 'Unbreakable', 'Destroys', 'Placed on', 'Potion effects', 'Dye'], [])
        .setTooltip('Flags to hide from the item')
    );
    component.data.push(new ListValue('Custom Name/Lore', 'custom', ['True', 'False'], 'False')
        .setTooltip('Whether to apply a custom name/lore to the item')
    );

    component.data.push(new StringValue('Name', 'name', 'Name').requireValue('custom', ['True'])
        .setTooltip('The name of the item')
    );
    component.data.push(new StringListValue('Lore', 'lore', []).requireValue('custom', ['True'])
        .setTooltip('The lore text for the item (the text below the name)')
    );
    component.data.push(new StringValue('Potion Color', 'potion_color', '#385dc6').requireValue('material', ['Potion',
                                                                                                        'Splash potion', 'Lingering potion'])
        .setTooltip('The potion color in hex RGB')
    );
    component.data.push(new ListValue('Potion Type', 'potion_type', getPotionTypes, 'Speed').requireValue('material', ['Potion',
                                                                                                                  'Splash potion', 'Lingering potion'])
        .setTooltip('The type of potion')
    );
    component.data.push(new IntValue('Potion Level', 'potion_level', 0).requireValue('material', ['Potion', 'Splash potion', 'Lingering potion'])
        .setTooltip('The potion level')
    );
    component.data.push(new IntValue('Potion Duration', 'potion_duration', 30).requireValue('material', ['Potion',
                                                                                                    'Splash potion', 'Lingering potion'])
        .setTooltip('The potion duration (seconds)')
    );
    component.data.push(new StringValue('Armor Color', 'armor_color', '#a06540').requireValue('material', ['Leather helmet', 'Leather chestplate', 'Leather leggings', 'Leather boots'])
        .setTooltip('The armor color in hex RGB')
    );
}

/**
 * Adds the options for item-check related effects to the component
 *
 * @param {Component} component - the component to add to
 */
function addItemConditionOptions(component) {

    component.data.push(new ListValue('Check Material', 'check-mat', ['True', 'False'], 'True')
        .setTooltip('Whether the item needs to be a certain type')
    );
    component.data.push(new ListValue('Material', 'material', getMaterials, 'Arrow')
        .requireValue('check-mat', ['True'])
        .setTooltip('The type the item needs to be')
    );

    component.data.push(new ListValue('Check Data', 'check-data', ['True', 'False'], 'False')
        .setTooltip('Whether the item needs to have a certain data value')
    );
    component.data.push(new IntValue('Data', 'data', 0)
        .requireValue('check-data', ['True'])
        .setTooltip('The data value the item must have')
    );

    component.data.push(new ListValue('Check Lore', 'check-lore', ['True', 'False'], 'False')
        .setTooltip('Whether the item requires a bit of text in its lore')
    );
    component.data.push(new StringValue('Lore', 'lore', 'text')
        .requireValue('check-lore', ['True'])
        .setTooltip('The text the item requires in its lore')
    );

    component.data.push(new ListValue('Check Name', 'check-name', ['True', 'False'], 'False')
        .setTooltip('Whether the item needs to have a bit of text in its display name')
    );
    component.data.push(new StringValue('Name', 'name', 'name')
        .requireValue('check-name', ['True'])
        .setTooltip('The text the item requires in its display name')
    );

    component.data.push(new ListValue('Regex', 'regex', ['True', 'False'], 'False')
        .setTooltip('Whether the name and lore checks are regex strings. If you do not know what regex is, leave this option alone.')
    );
}

function addProjectileOptions(component) {
    component.data.push(new SectionMarker('Projectile Options'));

    // General data
    component.data.push(new AttributeValue('Velocity', 'velocity', 3, 0)
        .setTooltip('How fast the projectile is launched. A negative value fires it in the opposite direction.')
    );
    component.data.push(new AttributeValue('Lifespan', 'lifespan', 5, 0)
        .setTooltip('How long in secods before the projectile will expire in case it doesn\'t hit anything.')
    );
    component.data.push(new ListValue('Spread', 'spread', ['Cone', 'Horizontal Cone', 'Rain'], 'Cone')
        .setTooltip('The orientation for firing projectiles. Cone will fire arrows in a cone centered on your reticle. Horizontal cone does the same as cone, just locked to the XZ axis (parallel to the ground). Rain drops the projectiles from above the target. For firing one arrow straight, use "Cone"')
    );
    component.data.push(new AttributeValue('Amount', 'amount', 1, 0)
        .setTooltip('The number of projectiles to fire')
    );

    // Cone values
    component.data.push(new AttributeValue('Angle', 'angle', 30, 0)
        .requireValue('spread', ['Cone', 'Horizontal Cone'])
        .setTooltip('The angle in degrees of the cone arc to spread projectiles over. If you are only firing one projectile, this does not matter.')
    );

    // Rain values
    component.data.push(new AttributeValue('Height', 'height', 8, 0)
        .requireValue('spread', ['Rain'])
        .setTooltip('The distance in blocks over the target to rain the projectiles from')
    );
    component.data.push(new AttributeValue('Radius', 'rain-radius', 2, 0)
        .requireValue('spread', ['Rain'])
        .setTooltip('The radius of the rain emission area in blocks')
    );

    // Offsets
    component.data.push(new SectionMarker('Offset'));
    component.data.push(new AttributeValue('Forward Offset', 'forward', 0, 0)
        .setTooltip('How far forward in front of the target the projectile should fire from in blocks. A negative value will put it behind.')
    );
    component.data.push(new AttributeValue('Upward Offset', 'upward', 0, 0)
        .setTooltip('How far above the target the projectile should fire from in blocks. A negative value will put it below.')
    );
    component.data.push(new AttributeValue('Right Offset', 'right', 0, 0)
        .setTooltip('How far to the right of the target the projectile should fire from. A negative value will put it to the left.')
    );
}

/**
 * Adds the options for particle effects to the components
 *
 * @param {Component} component - the component to add to
 */
function addParticleOptions(component) {
    component.data.push(new SectionMarker('Particle Options'));
    component.data.push(new ListValue('Particle', 'particle', getParticles, 'Villager happy')
        .setTooltip('The type of particle to display')
    );

    component.data.push(new ListValue('Material', 'material', getMaterials, 'Dirt').requireValue('particle', ['Item crack',
                                                                                                              'Block crack',
                                                                                                              'Block dust',
                                                                                                              'Falling dust',
                                                                                                              'Block marker'])
        .setTooltip('The material to use for the particles')
    );
    component.data.push(new IntValue('Durability', 'durability', 0).requireValue('particle', ['Item crack'])
        .setTooltip('The durability to be reduced from the item used to make the particles')
    );
    component.data.push(new IntValue('CustomModelData', 'type', 0).requireValue('particle', ['Item crack'])
        .setTooltip('The CustomModelData of the item used to make the particles')
    );
    component.data.push(new StringValue('Dust Color', 'dust-color', '#FF0000').requireValue('particle', ['Redstone', 'Dust color transition'])
        .setTooltip('The color of the dust particles in hex RGB')
    );
    component.data.push(new StringValue('Final Dust Color', 'final-dust-color', '#FF0000').requireValue('particle', ['Dust color transition'])
        .setTooltip('The color to transition to, in hex RGB')
    );
    component.data.push(new DoubleValue('Dust Size', 'dust-size', 1).requireValue('particle', ['Redstone', 'Dust color transition'])
        .setTooltip('The size of the dust particles')
    );

    component.data.push(new ListValue('Arrangement', 'arrangement', ['Sphere', 'Circle', 'Hemisphere'], 'Sphere')
        .setTooltip('The arrangement to use for the particles. Circle is a 2D circle, Hemisphere is half a 3D sphere, and Sphere is a 3D sphere')
    );
    // Circle arrangement direction
    component.data.push(new ListValue('Circle Direction', 'direction', ['XY',
                                                                        'XZ',
                                                                        'YZ'], 'XZ').requireValue('arrangement', ['Circle'])
        .setTooltip('The orientation of the circle. XY and YZ are vertical circles while XZ is a horizontal circle.')
    );
    component.data.push(new AttributeValue('Radius', 'radius', 1, 0)
        .setTooltip('The radius of the arrangement in blocks')
    );
    component.data.push(new AttributeValue('Points', 'particles', 20, 0)
        .setTooltip('The amount of points that conform the chosen arrangement')
    );

    // Bukkit particle data value
    component.data.push(new IntValue('Effect Data', 'data', 0).requireValue('particle', ['Smoke',
                                                                                         'Ender Signal',
                                                                                         'Mobspawner Flames',
                                                                                         'Potion Break'])
        .setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break')
    );
    component.data.push(new IntValue('Visible Radius', 'visible-radius', 25).setTooltip('How far away players can see the particles from in blocks')
    );
    component.data.push(new DoubleValue('DX', 'dx', 0).setTooltip('Offset in the X direction, used as the Red value for some particles.')
    );
    component.data.push(new DoubleValue('DY', 'dy', 0).setTooltip('Offset in the Y direction, used as the Green value for some particles.')
    );
    component.data.push(new DoubleValue('DZ', 'dz', 0).setTooltip('Offset in the Z direction, used as the Blue value for some particles.')
    );
    component.data.push(new DoubleValue('Amount', 'amount', 1).setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color.')
    );
    component.data.push(new DoubleValue('Speed', 'speed', 0.1).setTooltip('Speed of the particle. For some particles controls other parameters, such as size.')
    );
}

function addEffectOptions(component, optional) {
    component.data.push(new SectionMarker('Particle Effect Options'));
    var opt = appendNone;
    if (optional) {
        opt = appendOptional;

        component.data.push(new ListValue('Use Effect', 'use-effect', ['True', 'False'], 'False')
            .setTooltip('Whether to use a particle effect.')
        );
    }

    component.data.push(opt(new StringValue('Effect Key', 'effect-key', 'default')
        .setTooltip('The key to refer to the effect by. Only one effect of each key can be active at a time.')
    ));
    component.data.push(opt(new AttributeValue('Duration', 'duration', 1, 0)
        .setTooltip('The time to play the effect for in seconds')
    ));

    component.data.push(opt(new StringValue('Shape', '-shape', 'hexagon')
        .setTooltip('Key of a formula for deciding where particles are played each iteration. View "effects.yml" for a list of defined formulas and their keys.')
    ));
    component.data.push(opt(new ListValue('Shape Direction', '-shape-dir', ['XY', 'YZ', 'XZ'], 'XY')
        .setTooltip('The plane the shape formula applies to. XZ would be flat, the other two are vertical.')
    ));
    component.data.push(opt(new StringValue('Shape Size', '-shape-size', '1')
        .setTooltip('Formula for deciding the size of the shape. This can be any sort of formula using the operations defined in the wiki.')
    ));
    component.data.push(opt(new StringValue('Animation', '-animation', 'one-circle')
        .setTooltip('Key of a formula for deciding where the particle effect moves relative to the target. View "effects.yml" for a list of defined formulas and their keys.')
    ));
    component.data.push(opt(new ListValue('Animation Direction', '-anim-dir', ['XY', 'YZ', 'XZ'], 'XZ')
        .setTooltip('The plane the animation motion moves through. XZ wold be flat, the other two are vertical.')
    ));
    component.data.push(opt(new StringValue('Animation Size', '-anim-size', '1')
        .setTooltip('Formula for deciding the multiplier of the animation distance. This can be any sort of formula using the operations defined in the wiki.')
    ));
    component.data.push(opt(new IntValue('Interval', '-interval', 1)
        .setTooltip('Number of ticks between playing particles.')
    ));
    component.data.push(opt(new IntValue('View Range', '-view-range', 25)
        .setTooltip('How far away the effect can be seen from.')
    ));

    component.data.push(opt(new ListValue('Particle', '-particle-type', getParticles, 'Villager happy')
        .setTooltip('The type of particle to use.')
    ));
    component.data.push(opt(new ListValue('Material', '-particle-material', getMaterials, 'Dirt')
        .requireValue('-particle-type', ['Item crack', 'Block crack', 'Block dust', 'Falling dust', 'Block marker'])
        .setTooltip('The material to use for the particle.')
    ));
    component.data.push(opt(new IntValue('Durability', '-particle-durability', 0)
		.requireValue('particle', ['Item crack'])
        .setTooltip('The durability to be reduced from the item used to make the particles')
    ));
    component.data.push(opt(new IntValue('CustomModelData', '-particle-data', 0)
        .requireValue('-particle-type', ['Item crack'])
        .setTooltip('The data value for the material used by the particle. For 1.14+ determines the CustomModelData of the item.')
    ));
    component.data.push(new StringValue('Dust Color', '-particle-dust-color', '#FF0000').requireValue('-particle-type', ['Redstone', 'Dust color transition'])
        .setTooltip('The color of the dust particles in hex RGB')
    );
    component.data.push(new StringValue('Final Dust Color', '-particle-final-dust-color', '#FF0000').requireValue('-particle-type', ['Dust color transition'])
        .setTooltip('The color to transition to, in hex RGB')
    );
    component.data.push(new DoubleValue('Dust Size', '-particle-dust-size', 1).requireValue('-particle-type', ['Redstone', 'Dust color transition'])
        .setTooltip('The size of the dust particles')
    );
    component.data.push(opt(new IntValue('Amount', '-particle-amount', 1)
        .setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color.')
    ));
    component.data.push(opt(new DoubleValue('DX', '-particle-dx', 0)
        .setTooltip('Offset in the X direction, used as the Red value for some particles.')
    ));
    component.data.push(opt(new DoubleValue('DY', '-particle-dy', 0)
        .setTooltip('Offset in the Y direction, used as the Green value for some particles.')
    ));
    component.data.push(opt(new DoubleValue('DZ', '-particle-dz', 0)
        .setTooltip('Offset in the Z direction, used as the Blue value for some particles.')
    ));
    component.data.push(opt(new DoubleValue('Speed', '-particle-speed', 0.1)
        .setTooltip('Speed of the particle. For some particles controls other parameters, such as size.')
    ));
    component.data.push(opt(new DoubleValue('Initial Rotation', '-initial-rotation', 0)
        .setTooltip('The amount to rotate the effect (useful for effects like the square).')
    ));
    component.data.push(opt(new ListValue('Rotate w/ Player', '-with-rotation', ['True', 'False'], 'True')
        .setTooltip('Whether to follow the rotation of the player for the effect.')
    ));
}

function addTargetOptions(component) {
    component.data.push(new ListValue("Group", "group", ["Ally", "Enemy", "Both"], "Enemy")
        .setTooltip('The alignment of targets to get')
    );
    component.data.push(new ListValue("Through Wall", "wall", ['True', 'False'], 'False')
        .setTooltip('Whether to allow targets to be on the other side of a wall')
    );
    component.data.push(new ListValue("Include Caster", "caster", ['True', 'False', 'In area'], 'False')
        .setTooltip('Whether to include the caster in the target list. "True" will always include them, "False" will never, and "In area" will only if they are within the targeted area')
    );
    component.data.push(new AttributeValue("Max Targets", "max", 99, 0)
        .setTooltip('The max amount of targets to apply children to')
    );
}

function appendOptional(value) {
    value.requireValue('use-effect', ['True']);
    return value;
}

function appendNone(value) {
    return value;
}
