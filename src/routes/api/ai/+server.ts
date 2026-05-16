import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';
import { SILICONFLOW_API_KEY } from '$env/static/private';
import { readFileSync } from 'fs';
import { resolve } from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';

// ── Schema loading (once at startup) ──────────────────────────────────────────
const __dirname = dirname(fileURLToPath(import.meta.url));
const SCHEMA = JSON.parse(
  readFileSync(resolve(__dirname, 'schema.json'), 'utf-8')
) as Record<string, Record<string, { description: string; fields: Record<string, any> }>>;

// ── Keyword → component name mapping ─────────────────────────────────────────
type KWEntry = [string[], 'Mechanic' | 'Target' | 'Trigger' | 'Condition', string];

const KEYWORD_MAP: KWEntry[] = [
  // ---------------- Logic & Control ----------------
  [['延迟', 'delay', 'after', '等待'], 'Mechanic', 'Delay'],
  [['重复', 'repeat', 'loop', '循环', '多次'], 'Mechanic', 'Repeat'],
  [['被动', 'passive', '持续', 'aura', '光环'], 'Mechanic', 'Passive'],
  [['触发', 'trigger', '监听'], 'Mechanic', 'Trigger'],
  [['蓄力', 'channel', '引导', 'channeling'], 'Mechanic', 'Channel'],

  // ---------------- Movement & Physics ----------------
  [['冲锋', '冲', 'dash', 'charge', 'launch', '弹射', '弹飞', '位移', '飞出', '向前冲', '突进'], 'Mechanic', 'Launch'],
  [['击退', 'knockback', '推开', '推飞', 'push', '弹开', '打飞'], 'Mechanic', 'Push'],
  [['传送', '瞬移', 'warp', 'teleport', '跳到', '闪现'], 'Mechanic', 'Warp Location'],
  [['传送到目标', 'warp target'], 'Mechanic', 'Warp Target'],
  [['传送到变量', 'warp value'], 'Mechanic', 'Warp Value'],
  [['重力', 'gravity'], 'Mechanic', 'Armor Stand'], // Armor stands are often used for gravity/physics markers

  // ---------------- Visual & Audio ----------------
  [['粒子', 'particle', '特效', '光效', '特效显示'], 'Mechanic', 'Particle'],
  [['音效', 'sound', '播放声音', '声音', '听见'], 'Mechanic', 'Sound'],
  [['投射粒子', '笔直粒子', '粒子弹', '飞行粒子', '发射粒子', '对首个', '投射', 'particle projectile', 'shoot particle', '激光',
    'arrow', 'bolt', 'shot', 'projectile', 'shoot', 'launch projectile', 'throw', 'shuriken', 'fireball', 'snowball',
    'ice arrow', 'fire arrow', 'magic bolt', 'beam', '箭', '弹'], 'Mechanic', 'Particle Projectile'],
  [['粒子动画', '持续粒子', 'particle animation'], 'Mechanic', 'Particle Animation'],
  [['变身', '伪装', 'disguise'], 'Mechanic', 'Disguise'],
  [['隐身', 'invisible', '隐形', 'invisibility'], 'Mechanic', 'Invisibility'],

  // ---------------- Combat & Damage ----------------
  [['伤害', 'damage', '攻击', '造成伤害', 'deal damage', 'hurt', '扣血'], 'Mechanic', 'Damage'],
  [['护盾', '防御', 'defense buff', '减伤', '抵抗'], 'Mechanic', 'Defense Buff'],
  [['加成', 'damage buff', '伤害增益', '增加伤害'], 'Mechanic', 'Damage Buff'],
  [['爆炸', 'explosion', '爆', 'explode', 'blast'], 'Mechanic', 'Explosion'],
  [['闪电', 'lightning', '雷击', '雷', 'thunder', 'storm', 'electric'], 'Mechanic', 'Lightning'],
  [['火焰', '燃烧', 'burn', 'fire', '着火', '点火', 'ignite', 'flame', 'blaze', 'inferno'], 'Mechanic', 'Fire'],
  [['嘲讽', 'taunt', '吸引仇恨'], 'Mechanic', 'Taunt'],

  // ---------------- Recovery & Stats ----------------
  [['治疗', 'heal', '回血', '回复', 'healing', '恢复'], 'Mechanic', 'Heal'],
  [['法法量', 'mana', '魔力', '蓝量', '回蓝'], 'Mechanic', 'Mana'],
  [['属性', 'attribute', '增加属性', '临时属性'], 'Mechanic', 'Attribute'],
  [['数值', 'stat', '固定属性'], 'Mechanic', 'Stat'],
  [['药水', 'potion', '速度', '减速', '缓慢', '力量', '虚弱', '中毒', '失明',
    'slow', 'slowness', 'freeze', 'ice', 'frozen', 'chill', 'frost', 'cold',
    'poison', 'wither', 'weakness', 'blindness', 'nausea', 'confusion', 'haste', 'strength'], 'Mechanic', 'Potion'],
  [['净化', 'cleanse', '清除debuff', '驱散'], 'Mechanic', 'Cleanse'],

  // ---------------- Utility & Variable ----------------
  [['提示', 'message', '告诉', '说话', '显示文字'], 'Mechanic', 'Message'],
  [['金币', 'money', '扣钱', '给钱', '钱', 'economy'], 'Mechanic', 'Money'],
  [['给物品', 'item', '获得物品'], 'Mechanic', 'Item'],
  [['移除物品', 'item remove'], 'Mechanic', 'Item Remove'],
  [['命令', 'command', '控制台', '指令'], 'Mechanic', 'Command'],
  [['设置变量', 'value set', '变量赋值'], 'Mechanic', 'Value Set'],
  [['增加变量', 'value add'], 'Mechanic', 'Value Add'],
  [['标记', 'flag', '状态标记'], 'Mechanic', 'Flag'],

  // ---------------- Summon ----------------
  [['召唤', 'summon', '生成', '生物', '小弟'], 'Mechanic', 'Summon'],
  [['召唤狼', 'wolf'], 'Mechanic', 'Wolf'],
  [['盔甲架', 'armor stand', '标记点'], 'Mechanic', 'Armor Stand'],

  // ---------------- Targets ----------------
  [['自身', 'self', 'caster', '施法者', '自己'], 'Target', 'Self'],
  [['单体', 'single target', '最近一个', '目标'], 'Target', 'Single'],
  [['范围', 'area', 'aoe', '球形范围', '周围'], 'Target', 'Area'],
  [['直线', 'linear', '穿刺', '线形', '前方'], 'Target', 'Linear'],
  [['锥形', 'cone', '扇形'], 'Target', 'Cone'],
  [['最近', 'nearest', 'closest'], 'Target', 'Nearest'],

  // ---------------- Triggers ----------------
  [['施法', 'cast', '主动技能', '右键'], 'Trigger', 'Cast'],
  [['被动触发', 'initialize', '初始化', '学习技能'], 'Trigger', 'Initialize'],
  [['物理攻击', 'physical damage', '普攻触发', '攻击触发'], 'Trigger', 'Physical Damage'],
  [['受击', '被打', '被攻击', '受伤'], 'Trigger', 'Took Physical Damage'],
];

// ── Format a component's schema ───────────────────────────────────────────────
function formatComponent(type: string, name: string): string {
  const comp = SCHEMA[type]?.[name];
  if (!comp) return '';

  const lines: string[] = [`### ${type}: ${name}`];
  if (comp.description) lines.push(`Description: ${comp.description}`);

  for (const [, info] of Object.entries(comp.fields ?? {})) {
    const f = info as any;
    if (f.type === 'AttributeSelect') {
      const def = f.default !== undefined ? ` (default: ${f.default})` : '';
      lines.push(`- ${f.yamlKeys[0]} / ${f.yamlKeys[1]}: number${def}`);
    } else if (f.type === 'DropdownSelect' && f.options?.length) {
      const def = f.default !== undefined && !String(f.default).startsWith('[') ? ` (default: ${f.default})` : '';
      lines.push(`- ${f.yamlKey}: [${f.options.slice(0, 8).join(' | ')}]${def}`);
    } else if (f.type === 'BooleanSelect') {
      const def = f.default !== undefined ? ` (default: ${f.default})` : '';
      lines.push(`- ${f.yamlKey}: true | false${def}`);
    } else if (['IntSelect', 'DoubleSelect'].includes(f.type)) {
      const def = f.default !== undefined ? ` (default: ${f.default})` : '';
      lines.push(`- ${f.yamlKey}: number${def}`);
    } else if (f.type === 'StringSelect') {
      const def = f.default ? ` (default: "${f.default}")` : '';
      lines.push(`- ${f.yamlKey}: string${def}`);
    }
  }
  return lines.join('\n');
}

// ── RAG retrieval ─────────────────────────────────────────────────────────────
function retrieveComponents(prompt: string): string {
  const lower = prompt.toLowerCase();
  const seen = new Set<string>();
  const blocks: string[] = [];

  for (const [keywords, type, name] of KEYWORD_MAP) {
    if (keywords.some(kw => lower.includes(kw))) {
      const key = `${type}:${name}`;
      if (seen.has(key)) continue;
      seen.add(key);
      const block = formatComponent(type, name);
      if (block) blocks.push(block);
    }
  }

  if (!blocks.length) return '';
  return `\n## RAG: Relevant Component Schemas\nUse EXACT field names. AttributeSelect → TWO keys: {key}-base and {key}-scale.\n\n${blocks.join('\n\n')}\n`;
}

// ── System prompt ─────────────────────────────────────────────────────────────
const BASE_SYSTEM_PROMPT = `You are an expert at creating Minecraft RPG skills for the ProSkillAPI (Fabled) plugin.
The user will describe a skill in natural language. You MUST respond with ONLY valid YAML, no markdown fences, no explanation.

== CORE RULES ==
1. Root key = skill name (PascalCase, no spaces).
2. Hierarchy: Trigger → Target → Mechanic (each nested under "children:").
3. Every component has a "data:" block with "icon-key: ''" plus its own fields.
4. AttributeSelect fields (radius, range, speed, etc.) MUST use "{key}-base" and "{key}-scale".
   - ⚠️ NEVER use just "{key}". Use "{key}-base: X" and "{key}-scale: 0".

== MECHANIC BEHAVIORAL BIBLE (Expert Knowledge) ==

### 1. Movement & Physics
- Launch (冲锋): Sets velocity directly. "forward-base: 2.0" is a dash. "reset-y: true" prevents unintended altitude gain.
- Push (击退): Moves target AWAY from caster. "speed-base: 3.0" is strong.
- Warp Location (传送): world: "current", x: value, y: value, z: value. Teleports instantly.
- Warp Target (交换/传送到目标): Useful for "Blink to target" or "Swap locations".
- ⚠️ PROJECTILE / RANGED SKILLS (arrow, bolt, shot, beam, ice arrow, fireball, etc.):
  Use "Particle Projectile" as CORE mechanic. Structure: Cast → Self → Particle Projectile → [damage + effects on hit].
  Example potion inside: status: "Slow", level-base: 2, duration-base: 3.
  velocity-base: 1.5 (arrow speed). gravity-base: 0.03 (light drop). particle: "SNOWBALL" for ice, "Flame" for fire.

### 2. Visual & Particles
- Particle:
  - forward, upward, right: POSITION OFFSETS from target. eye level = upward: 1.0.
  - dx, dy, dz: RANDOM SPREAD around the point (cloud effect).
  - arrangement: "None" (point), "Circle", "Sphere". Sphere needs radius-base and particles-base (point count).
  - amount: Particles per fire. Use 15-30 for good effects.
  - ⚠️ particle field MUST be one of these EXACT values (case-sensitive):
    Flame, CRIT, CRIT_MAGIC, SPELL, SPELL_MOB, SPELL_WITCH, TOWN_AURA, PORTAL,
    ENCHANTMENT_TABLE, EXPLOSION_LARGE, EXPLOSION_HUGE, FIREWORKS_SPARK, SMOKE_LARGE,
    SMOKE_NORMAL, CLOUD, SNOWBALL, HEART, VILLAGER_HAPPY, DRIP_WATER, DRIP_LAVA,
    REDSTONE, NOTE, SUSPENDED, DEPTH_SUSPEND, SLIME, WATER_SPLASH, SNOW_SHOVEL,
    END_ROD, DRAGON_BREATH, DAMAGE_INDICATOR, SWEEP_ATTACK, FALLING_DUST, TOTEM,
    SPIT, SQUID_INK, BUBBLE_POP, CURRENT_DOWN, BUBBLE_COLUMN_UP, NAUTILUS,
    DOLPHIN, SNEEZE, CAMPFIRE_COSY_SMOKE, CAMPFIRE_SIGNAL_SMOKE, COMPOSTER,
    FALLING_LAVA, LANDING_LAVA, FALLING_WATER, SOUL, SOUL_FIRE_FLAME, ASH,
    WHITE_ASH, CRIMSON_SPORE, WARPED_SPORE, SPORE_BLOSSOM_AIR, GLOW, ELECTRIC_SPARK.
    ✅ For a dash skill use: Flame or CRIT or SPELL.
    ❌ NEVER use: "speed", "trail", "magic", or any other made-up name.
- Particle Projectile:
  - Fires a physics object. Child mechanics run on HIT entities.
  - velocity-base: Speed. gravity-base: -0.05 for drop, 0 for straight laser.
  - ⚠️ Use this for "Projectiles", "Beams", or "Shots".
- Disguise: Turns player into a Mob or Player. Needs duration-base.

### 3. Combat
- Damage: value-base. type: "Damage" (flat) or "Multiplier" (%).
- Buff/Damage Buff: type: "Multiplier" (e.g. 1.5 = +50% damage).
- Status (控制): status: "Stun" (stunned), "Root" (stuck), "Silence" (no skills).
- Taunt: Mobs target the caster.

### 4. Logic & Setup
- Repeat: Executes children multiple times. period: time between reps (e.g. 0.5s).
- Delay: Pauses before children.
- Passive: Used with "Initialize" trigger. Runs every X seconds.
- Trigger Mechanic: Allows a skill to start LISTENING for a new trigger on a target (e.g. "Next time target takes damage...").
- Armor Stand: Invisible marker. marker: true, visible: false. Used to anchor effects.

### 5. Common Constraints
- ⚠️ Target Selectors (Area, Cone, Linear): max-targets-base MUST be >= 1. Default: 999.
- ⚠️ Particle: radius-base and particles-base are ONLY for Sphere/Circle arrangement.

== YAML STRUCTURE TEMPLATE ==
SkillName:
  name: "SkillName"
  type: "Damage"
  max-level: 5
  icon: "NETHER_STAR"
  attributes:
    level-base: 1
    level-scale: 0
    cooldown-base: 10
    cooldown-scale: 0
    mana-base: 20
    mana-scale: 0
  components:
    Cast:
      type: "trigger"
      data:
        icon-key: ""
      children:
        Self-a:
          type: "target"
          data:
            icon-key: ""
          children:
            Particle-a:
              type: "mechanic"
              data:
                icon-key: ""
                particle: "Flame"
                arrangement: ""
                amount: 15
                dx: 0
                dy: 0
                dz: 0
                speed: 0.05
                forward: 0
                upward: 1
                right: 0
              children: {}
            Sound-a:
              type: "mechanic"
              data:
                icon-key: ""
                sound: "ENTITY_ENDERMAN_TELEPORT"
                volume-base: 1.0
                volume-scale: 0
                pitch-base: 1.0
                pitch-scale: 0
              children: {}
`;

// ── Handler (SSE streaming) ───────────────────────────────────────────────────
export const POST: RequestHandler = async ({ request }) => {
  const { prompt } = await request.json();
  if (!prompt) {
    return json({ error: 'Prompt is required' }, { status: 400 });
  }

  const ragContext = retrieveComponents(prompt);
  const systemPrompt = BASE_SYSTEM_PROMPT + ragContext;

  // Use SSE streaming for live progress
  const stream = new ReadableStream({
    async start(controller) {
      const enc = new TextEncoder();
      const send = (data: string) =>
        controller.enqueue(enc.encode(`data: ${JSON.stringify({ chunk: data })}\n\n`));

      try {
        // You can change the model source here. This is a Chinese model platform.
        const response = await fetch('https://api.siliconflow.cn/v1/chat/completions', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${SILICONFLOW_API_KEY}`
          },
          body: JSON.stringify({
            // You can change the model here
            model: 'Qwen/Qwen2.5-72B-Instruct',
            stream: true,
            messages: [
              { role: 'system', content: systemPrompt.trim() },
              { role: 'user', content: prompt }
            ],
            temperature: 0.15,
            max_tokens: 4000
          })
        });

        if (!response.ok) {
          const err = await response.text();
          controller.enqueue(enc.encode(`data: ${JSON.stringify({ error: err })}\n\n`));
          controller.close();
          return;
        }

        const reader = response.body!.getReader();
        const decoder = new TextDecoder();
        let buffer = '';

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          buffer += decoder.decode(value, { stream: true });
          const lines = buffer.split('\n');
          buffer = lines.pop() ?? '';

          for (const line of lines) {
            if (!line.startsWith('data: ')) continue;
            const raw = line.slice(6).trim();
            if (raw === '[DONE]') continue;
            try {
              const parsed = JSON.parse(raw);
              const delta = parsed.choices?.[0]?.delta?.content;
              if (delta) send(delta);
            } catch { /* skip malformed */ }
          }
        }

        controller.enqueue(enc.encode(`data: ${JSON.stringify({ done: true })}\n\n`));
      } catch (e) {
        controller.enqueue(enc.encode(`data: ${JSON.stringify({ error: String(e) })}\n\n`));
      }

      controller.close();
    }
  });

  return new Response(stream, {
    headers: {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive'
    }
  });
};
