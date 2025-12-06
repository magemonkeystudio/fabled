import Database from 'better-sqlite3';
import BetterSqlite3 from 'better-sqlite3';

const DB_PATH = process.env.SQLITE_DB_PATH || './active_channels.db';
export const activeChannels: Set<string> = new Set(); // Export the set

let db: BetterSqlite3.Database;

export async function initializeDatabase() {
	return new Promise<void>((resolve, reject) => {
		try {
			db = new Database(DB_PATH, { verbose: console.log }); // verbose for logging
			console.log('✅ Connected to SQLite database:', DB_PATH);

			// better-sqlite3's run is synchronous, wrap in try/catch for errors
			db.exec(`CREATE TABLE IF NOT EXISTS active_channels (
                channel_id TEXT PRIMARY KEY
            )`);
			console.log('✅ Active channels table ensured.');
			resolve();
		} catch (err: any) {
			console.error('❌ ERROR: Could not connect to SQLite database or create table:', err.message);
			reject(err);
		}
	});
}

export async function loadActiveChannels() {
	return new Promise<void>((resolve, reject) => {
		try {
			// better-sqlite3's all is synchronous
			const rows: { channel_id: string }[] = db
				.prepare(`SELECT channel_id FROM active_channels`)
				.all() as { channel_id: string }[];
			rows.forEach((row) => activeChannels.add(row.channel_id));
			console.log(`✅ Loaded ${activeChannels.size} active channels.`);
			resolve();
		} catch (err: any) {
			console.error('❌ ERROR: Could not load active channels:', err.message);
			reject(err);
		}
	});
}

export async function saveActiveChannel(channelId: string): Promise<Error | null> {
	return new Promise((resolve) => {
		try {
			// better-sqlite3's run is synchronous
			db.prepare(`INSERT OR IGNORE INTO active_channels (channel_id) VALUES (?)`).run(channelId);
			resolve(null); // No error
		} catch (err: any) {
			console.error('❌ ERROR: Failed to save active channel to database:', err.message);
			resolve(err);
		}
	});
}

export async function deleteActiveChannel(channelId: string): Promise<Error | null> {
	return new Promise((resolve) => {
		try {
			// better-sqlite3's run is synchronous
			db.prepare(`DELETE FROM active_channels WHERE channel_id = ?`).run(channelId);
			resolve(null); // No error
		} catch (err: any) {
			console.error('❌ ERROR: Failed to remove active channel from database:', err.message);
			resolve(err);
		}
	});
}
