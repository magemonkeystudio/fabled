import { readFileSync, writeFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, resolve } from 'path';

const __dirname = dirname(fileURLToPath(import.meta.url));
const src = readFileSync(resolve(__dirname, '../src/api/components/components.svelte.ts'), 'utf-8');

// Match each class that extends FabledMechanic/FabledTarget/FabledTrigger/FabledCondition
const classRegex = /class\s+(\w+)\s+extends\s+Fabled(Mechanic|Target|Trigger|Condition)\s*\{([\s\S]*?)public static override/g;
// Match name field
const nameRegex = /name:\s+['"]([^'"]+)['"]/;
// Match description - handles both simple and multi-line/concatenated
const descRegex = /description:\s+((?:'[^']*'(?:\s*\+\s*'[^']*')*(?:\s*\+\s*`[^`]*`)*)|(?:`[^`]*`))/;
// Match data fields
const fieldRegex = /new\s+(AttributeSelect|DropdownSelect|BooleanSelect|StringSelect|IntSelect|DoubleSelect|ColorSelect)\s*\(([^)]+)\)/g;

const schema = {
    Trigger: {},
    Target: {},
    Mechanic: {},
    Condition: {}
};

let m;
while ((m = classRegex.exec(src)) !== null) {
    const [, , type, body] = m;
    const nameMatch = nameRegex.exec(body);
    if (!nameMatch) continue;
    const name = nameMatch[1];

    // Extract description
    let description = '';
    const descMatch = descRegex.exec(body);
    if (descMatch) {
        // Flatten concatenated strings: 'a' + 'b' + `c` → 'abc'
        description = descMatch[1]
            .replace(/`([^`]*)`/g, '$1')
            .replace(/'([^']*)'/g, '$1')
            .replace(/\s*\+\s*/g, '')
            .replace(/<[^>]+>/g, '')  // strip HTML tags like <code>
            .trim();
    }

    const fields = {};
    let fm;
    const fieldSrc = body;
    fieldRegex.lastIndex = 0;
    while ((fm = fieldRegex.exec(fieldSrc)) !== null) {
        const [, selectType, rawArgs] = fm;
        // Parse args - split by comma but respect string quotes and brackets
        const args = parseArgs(rawArgs);
        if (args.length < 2) continue;

        const label = stripQuotes(args[0]);
        const key = stripQuotes(args[1]);
        if (!key || key.startsWith('//')) continue;

        let defaultVal = args[2] ? stripQuotes(args[2]) : undefined;

        let fieldInfo = { type: selectType };
        if (defaultVal !== undefined && defaultVal !== '') {
            fieldInfo.default = isNaN(Number(defaultVal)) ? defaultVal : Number(defaultVal);
        }

        // For AttributeSelect: YAML key = key-base / key-scale
        if (selectType === 'AttributeSelect') {
            fieldInfo.yamlKeys = [`${key}-base`, `${key}-scale`];
        } else {
            fieldInfo.yamlKey = key;
        }

        // For DropdownSelect: try to extract options array from raw args
        if (selectType === 'DropdownSelect') {
            const optMatch = rawArgs.match(/\[([^\]]+)\]/);
            if (optMatch) {
                fieldInfo.options = optMatch[1]
                    .split(',')
                    .map(s => s.trim().replace(/^['"]|['"]$/g, ''))
                    .filter(Boolean);
            }
        }

        fieldInfo.label = label;
        fields[key] = fieldInfo;
    }

    if (!schema[type]) schema[type] = {};
    schema[type][name] = { description, fields };
}

function stripQuotes(s) {
    if (!s) return '';
    return s.trim().replace(/^['"`]|['"`]$/g, '');
}

function parseArgs(raw) {
    const args = [];
    let depth = 0;
    let curr = '';
    let inStr = false;
    let strChar = '';
    for (const ch of raw) {
        if (inStr) {
            curr += ch;
            if (ch === strChar) inStr = false;
        } else if (ch === "'" || ch === '"' || ch === '`') {
            inStr = true;
            strChar = ch;
            curr += ch;
        } else if (ch === '(' || ch === '[') {
            depth++;
            curr += ch;
        } else if (ch === ')' || ch === ']') {
            depth--;
            curr += ch;
        } else if (ch === ',' && depth === 0) {
            args.push(curr.trim());
            curr = '';
        } else {
            curr += ch;
        }
    }
    if (curr.trim()) args.push(curr.trim());
    return args;
}

const out = JSON.stringify(schema, null, 2);
writeFileSync(resolve(__dirname, '../src/routes/api/ai/schema.json'), out, 'utf-8');
console.log('Schema extraction complete!');

// Print summary
for (const [type, comps] of Object.entries(schema)) {
    console.log(`${type}: ${Object.keys(comps).join(', ')}`);
}
