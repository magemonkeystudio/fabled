import type Modal from '$components/Modal.svelte';
import {get, writable} from 'svelte/store';
import type ComponentModal from '$components/modal/ComponentModal.svelte';
import type ComponentSelectModal from '$components/modal/ComponentSelectModal.svelte';
import type ProComponent from "$api/components/procomponent";

export const activeModal = writable<Modal | ComponentModal | undefined>(undefined);
export const modalData = writable<any>(undefined);

export const openModal = (modal: Modal | ComponentModal | ComponentSelectModal,
                          data: ProComponent) => {
    activeModal.set(modal);
    modalData.set(data);
};

export const closeModal = () => {
    activeModal.set(undefined);
    modalData.set(undefined);
};

export const isModalOpen = () => {
    return !!get(activeModal);
};