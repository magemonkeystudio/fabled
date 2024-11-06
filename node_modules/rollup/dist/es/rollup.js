/*
  @license
	Rollup.js v4.21.1
	Mon, 26 Aug 2024 15:53:42 GMT - commit c33c6ceb7da712c3d14b67b81febf9303fbbd96c

	https://github.com/rollup/rollup

	Released under the MIT License.
*/
export { version as VERSION, defineConfig, rollup, watch } from './shared/node-entry.js';
import './shared/parseAst.js';
import '../native.js';
import 'node:path';
import 'path';
import 'node:process';
import 'node:perf_hooks';
import 'node:fs/promises';
import 'tty';
