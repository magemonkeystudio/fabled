/*
 * This file was pulled from the original SkillAPI editor and updated to use TypeScript
 * and modern JavaScript
 */

/**
 * RegEx patterns used by the YAML parser
 */
const Regex = {
  INT: /^-?[0-9]+$/,
  FLOAT: /^-?[0-9]+\.[0-9]+$/,
  SPACE: /^( +)/
};

/**
 * Parses the YAML data string
 *
 * @param {string} text - the YAML data string
 *
 * @returns {YAMLObject} the parsed data
 */
export const parseYAML = (text: string): YAMLObject => {
  text = text.replace(/\r\n/g, "\n").replace(/\n *\n/g, "\n").replace(/ +\n/g, "\n");
  const data = new YAMLObject();
  const index = 0;
  const lines = text.split("\n");
  data.parse(lines, index, 0);
  return data;
};

/**
 * Counts the number of leading spaces in a string
 *
 * @param {string} string - the string to check
 *
 * @returns {Number} the number of leading spaces
 */
export const countSpaces = (string: string): number => {
  const m = string.match(Regex.SPACE);
  return !m ? 0 : m[1].length;
};

/**
 * Represents a collection of YAML data (ConfigurationSection in Bukkit)
 */
export class YAMLObject {
  public key?: string;
  public data: any = {};

  constructor(key?: string) {
    this.key = key;
  }

  public put = (key: string, value: any) => {
    this.data[key] = value;
  }

  /**
   * Checks whether the YAML data contains a value under the given key
   *
   * @param {string} key - key of the value to check for
   *
   * @returns {boolean} true if contains the value, false otherwise
   */
  public has = (key: string): boolean => {
    return this.data[key] !== undefined;
  };

  /**
   * Retrieves a value from the data
   *
   * @param {string} key   - key of the value to retrieve
   * @param {Object} value - default value to return if one isn't found
   *
   * @returns {string} the obtained value
   */
  public get = (key: string, value: any): any => {
    return this.has(key) ? this.data[key] : value;
  };

  /**
   * Parses YAML data using the provided parameters
   *
   * @param {Array}  lines  - the lines of the YAML data
   * @param {Number} index  - the starting index of the data to parse
   * @param {Number} indent - the number of spaces preceeding the keys of the data
   *
   * @returns {Number} the ending index of the parsed data
   */
  public parse = (lines: any[], index: number, indent: number): number => {
    while (index < lines.length && countSpaces(lines[index]) >= indent) {
      while (index < lines.length && (countSpaces(lines[index]) != indent || lines[index].replace(/ /g, "").charAt(0) == "#" || lines[index].indexOf(":") == -1)) index++;
      if (index == lines.length) return index;

      const key = lines[index].substring(indent, lines[index].indexOf(":"));

      // New empty section
      if (lines[index].indexOf(": {}") == lines[index].length - 4 && lines[index].length >= 4) {
        this.data[key] = {};
      }

      // String list
      else if (index < lines.length - 1
        && lines[index + 1].charAt(indent) == "-"
        && lines[index + 1].charAt(indent + 1) == " "
        && countSpaces(lines[index + 1]) == indent) {
        const stringList = [];
        while (++index < lines.length && lines[index].charAt(indent) == "-") {
          let str = lines[index].substring(indent + 2);
          if (str.charAt(0) == "'") str = str.substring(1, str.length - 1);
          else if (str.charAt(0) == "\"") str = str.substring(1, str.length - 1);
          stringList.push(str);
        }
        this.data[key] = stringList;
        index--;
      }

      // New section with content
      else if (index < lines.length - 1 && countSpaces(lines[index + 1]) > indent) {
        index++;
        const newIndent = countSpaces(lines[index]);
        const newData = new YAMLObject();
        index = newData.parse(lines, index, newIndent) - 1;
        this.data[key] = newData;
      }

      // Regular value
      else {
        let value = lines[index].substring(lines[index].indexOf(":") + 2);
        if (value.charAt(0) == "'") value = value.substring(1, value.length - 1);
        else if (!isNaN(value)) {
          if (Regex.INT.test(value)) value = parseInt(value);
          else value = parseFloat(value);
        }
        this.data[key] = value;
      }

      do {
        index++;
      }
      while (index < lines.length && lines[index].replace(/ /g, "").charAt(0) == "#");
    }
    return index;
  };
}