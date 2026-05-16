<script lang="ts">
	import Modal from '../Modal.svelte';
	import { skillStore } from '../../data/skill-store.svelte';
	import { goto } from '$app/navigation';
	import { base } from '$app/paths';
	import YAML from 'yaml';

	interface Props {
		onclose: () => void;
	}

	let { onclose }: Props = $props();

	let prompt    = $state('');
	let loading   = $state(false);
	let error     = $state('');
	let yamlChars = $state(0);   // live character count for progress bar
	let streamText = $state(''); // streamed YAML preview (last few lines)

	// Expected YAML size ≈ 800 chars
	const EXPECTED_LEN = 800;
	let progress = $derived(Math.min((yamlChars / EXPECTED_LEN) * 100, 95));

	const generateAndImport = async () => {
		if (!prompt.trim() || loading) return;

		loading    = true;
		error      = '';
		yamlChars  = 0;
		streamText = '';

		let fullYaml = '';

		try {
			const res = await fetch('/api/ai', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ prompt })
			});

			if (!res.ok || !res.body) {
				const data = await res.json().catch(() => ({}));
				throw new Error((data as any).error || 'Failed to generate skill');
			}

			const reader  = res.body.getReader();
			const decoder = new TextDecoder();
			let   buf     = '';

			while (true) {
				const { done, value } = await reader.read();
				if (done) break;

				buf += decoder.decode(value, { stream: true });
				const lines = buf.split('\n');
				buf = lines.pop() ?? '';

				for (const line of lines) {
					if (!line.startsWith('data: ')) continue;
					const raw = line.slice(6).trim();
					try {
						const msg = JSON.parse(raw);
						if (msg.error) throw new Error(msg.error);
						if (msg.chunk) {
							fullYaml  += msg.chunk;
							yamlChars  = fullYaml.length;
							streamText = fullYaml.slice(-400);
						}
					} catch { /* skip invalid lines */ }
				}
			}

			fullYaml = fullYaml.trim()
				.replace(/^```yaml\n/, '').replace(/^```\n/, '')
				.replace(/\n```$/, '').replace(/```$/, '');

			if (!fullYaml) throw new Error('Empty response from AI');

			// Parse name from YAML to navigate correctly
			const parsed = YAML.parse(fullYaml);
			const skillName = Object.keys(parsed)[0];

			await skillStore.loadSkillText(fullYaml);
			
			if (skillName) {
				onclose();
				goto(`${base}/skill/${skillName}`);
			} else {
				error = 'Skill generated but could not determine name from YAML.';
			}
		} catch (e: any) {
			error = e.message || 'An error occurred';
		} finally {
			loading   = false;
			yamlChars = 0;
			streamText = '';
		}
	};
</script>

<Modal onclose={onclose} width="500px">
	<div class="content">
		<h2>Skill AI Generator ✨</h2>
		<p>Describe the skill you want, the more detailed the better (damage, range, effects, cooldown, etc.).</p>

		<textarea
			bind:value={prompt}
			placeholder="Example: A dash skill that leaves white particles, then deals damage to nearby enemies upon arrival."
			rows="4"
			disabled={loading}
		></textarea>

		{#if loading}
			<div class="progress-wrap">
				<div class="progress-bar" style="width: {progress}%"></div>
			</div>
			<div class="stream-preview">
				<span class="blink">▍</span>
				{#if streamText}
					<pre>{streamText}</pre>
				{:else}
					<span class="hint">Generating skill YAML...</span>
				{/if}
			</div>
		{/if}

		{#if error}
			<div class="error">{error}</div>
		{/if}

		<div class="actions">
			<button class="cancel" class:disabled={loading} onclick={onclose}>Cancel</button>
			<button class="generate" class:disabled={loading || !prompt.trim()} onclick={generateAndImport}>
				{#if loading}
					✨ Generating ({yamlChars} chars)...
				{:else}
					Generate Skill
				{/if}
			</button>
		</div>
	</div>
</Modal>

<style>
	.content {
		display: flex;
		flex-direction: column;
		gap: 1rem;
		min-width: 420px;
	}

	h2 {
		margin: 0;
		text-align: center;
		color: white;
	}

	p {
		margin: 0;
		font-size: 0.95rem;
		color: #ddd;
	}

	textarea {
		width: 100%;
		background: rgba(0, 0, 0, 0.4);
		border: 1px solid var(--color-border, #444);
		color: white;
		padding: 0.75rem;
		border-radius: 4px;
		resize: vertical;
		font-family: inherit;
		outline: none;
		box-sizing: border-box;
	}

	textarea:focus  { border-color: #007bff; }
	textarea:disabled { opacity: 0.6; cursor: not-allowed; }

	/* Progress bar */
	.progress-wrap {
		height: 4px;
		background: rgba(255,255,255,0.1);
		border-radius: 2px;
		overflow: hidden;
	}
	.progress-bar {
		height: 100%;
		background: linear-gradient(90deg, #007bff, #00c6ff);
		border-radius: 2px;
		transition: width 0.15s ease;
	}

	/* Stream preview */
	.stream-preview {
		background: rgba(0,0,0,0.5);
		border: 1px solid rgba(255,255,255,0.08);
		border-radius: 4px;
		padding: 0.5rem 0.75rem;
		font-size: 0.72rem;
		color: #aef;
		max-height: 120px;
		overflow: hidden;
		display: flex;
		flex-direction: column;
		gap: 0.25rem;
	}
	.stream-preview pre {
		margin: 0;
		white-space: pre-wrap;
		word-break: break-all;
		font-family: 'Fira Code', monospace;
		font-size: 0.72rem;
		color: #bdf;
		line-height: 1.4;
	}
	.hint { color: #888; font-style: italic; }
	@keyframes blink { 0%,100%{opacity:1} 50%{opacity:0} }
	.blink { animation: blink 1s step-start infinite; color: #007bff; }

	.error {
		color: #ff4d4d;
		font-size: 0.9rem;
		background: rgba(255, 77, 77, 0.1);
		padding: 0.5rem;
		border-radius: 4px;
		border-left: 3px solid #ff4d4d;
	}

	.actions {
		display: flex;
		justify-content: flex-end;
		gap: 1rem;
		margin-top: 0.5rem;
	}

	button {
		padding: 0.5rem 1rem;
		border-radius: 4px;
		border: none;
		font-weight: bold;
		cursor: pointer;
		transition: all 0.2s;
	}

	.cancel { background: transparent; color: #ddd; }
	.cancel:hover:not(.disabled) { background: rgba(255,255,255,0.1); }
	.generate { background: #004f8f; color: white; }
	.generate:hover:not(.disabled) { background: #0066c0; }
	.disabled { opacity: 0.5; cursor: not-allowed; }
</style>
