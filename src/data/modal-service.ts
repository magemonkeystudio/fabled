import type Modal                from '$components/Modal.svelte';
import { get, writable }         from 'svelte/store';
import type ComponentModal       from '$components/modal/ComponentModal.svelte';
import type ComponentSelectModal from '$components/modal/ComponentSelectModal.svelte';
import type SettingsModal   from '$components/modal/SettingsModal.svelte';
import type FabledComponent from '$api/components/fabled-component';

export const activeModal = writable<typeof Modal | typeof ComponentModal
	| typeof ComponentSelectModal | typeof SettingsModal | undefined>(undefined);
export const modalData   = writable<unknown>(undefined);

export const openModal = (modal: typeof Modal | typeof ComponentModal | typeof ComponentSelectModal | typeof SettingsModal,
													data?: FabledComponent) => {
	activeModal.set(modal);
	if (data) modalData.set(data);
};

export const closeModal = () => {
	activeModal.set(undefined);
	modalData.set(undefined);
};

export const isModalOpen = () => {
	return !!get(activeModal);
};