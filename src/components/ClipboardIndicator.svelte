<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import { hasClipboardData, getClipboardPreview, clearClipboard } from '$api/blockly/blockly.svelte';

	let clipboardData: string | null = null;
	let showTooltip = false;
	let interval: ReturnType<typeof setInterval>;

	async function updateClipboardStatus() {
		try {
			const hasData = await hasClipboardData();
			clipboardData = hasData ? await getClipboardPreview() : null;
		} catch (error) {
			console.debug('Failed to check clipboard status:', error);
			clipboardData = null;
		}
	}

	async function handleClearClipboard() {
		await clearClipboard();
		await updateClipboardStatus();
	}

	onMount(() => {
		updateClipboardStatus();
		// Update clipboard status every 5 seconds
		interval = setInterval(updateClipboardStatus, 5000);
		
		// Listen for storage changes (for cross-tab updates)
		const handleStorageChange = (e: StorageEvent) => {
			if (e.key === 'fabled_blockly_clipboard') {
				updateClipboardStatus();
			}
		};
		
		window.addEventListener('storage', handleStorageChange);
		
		return () => {
			window.removeEventListener('storage', handleStorageChange);
		};
	});

	onDestroy(() => {
		if (interval) {
			clearInterval(interval);
		}
	});
</script>

{#if clipboardData}
	<div class="clipboard-indicator"
		 role="button"
		 tabindex="0"
		 on:mouseenter={() => showTooltip = true}
		 on:mouseleave={() => showTooltip = false}
		 on:keydown={(e) => e.key === 'Enter' && (showTooltip = !showTooltip)}>
		<div class="clipboard-icon">📋</div>
		
		{#if showTooltip}
			<div class="clipboard-tooltip">
				<div class="tooltip-content">
					<div class="tooltip-header">Clipboard Content</div>
					<div class="tooltip-preview">{clipboardData}</div>
					<div class="tooltip-actions">
						<div class="shortcut-hint">Ctrl+V to paste</div>
						<button class="clear-btn" on:click={handleClearClipboard}>Clear</button>
					</div>
				</div>
			</div>
		{/if}
	</div>
{/if}

<style>
	.clipboard-indicator {
		position: fixed;
		top: 180px;
		right: 20px;
		z-index: 1000;
		background: #333;
		border-radius: 50%;
		width: 48px;
		height: 48px;
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
		box-shadow: 0 2px 8px rgba(0,0,0,0.3);
		transition: transform 0.2s ease;
	}

	.clipboard-indicator:hover {
		transform: scale(1.1);
	}

	.clipboard-icon {
		font-size: 20px;
	}

	.clipboard-tooltip {
		position: absolute;
		bottom: 60px;
		right: 0;
		background: #444;
		border-radius: 8px;
		padding: 12px;
		min-width: 250px;
		max-width: 300px;
		box-shadow: 0 4px 12px rgba(0,0,0,0.4);
		color: white;
		font-size: 12px;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.tooltip-content {
		display: flex;
		flex-direction: column;
		gap: 8px;
	}

	.tooltip-header {
		font-weight: bold;
		color: #fff;
		border-bottom: 1px solid #666;
		padding-bottom: 4px;
	}

	.tooltip-preview {
		color: #ccc;
		font-family: monospace;
		background: #222;
		padding: 4px 8px;
		border-radius: 4px;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.tooltip-actions {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-top: 4px;
		border-top: 1px solid #666;
	}

	.shortcut-hint {
		color: #999;
		font-style: italic;
	}

	.clear-btn {
		background: #666;
		color: white;
		border: none;
		padding: 4px 8px;
		border-radius: 4px;
		cursor: pointer;
		font-size: 11px;
		transition: background-color 0.2s ease;
	}

	.clear-btn:hover {
		background: #777;
	}

	.clear-btn:active {
		background: #555;
	}
</style>
