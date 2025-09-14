<template>
  <v-text-field
    :model-value="modelValue"
    @update:model-value="emit('update:modelValue', $event)"
    :label="label"
    :error-messages="errorMsg"
    v-mask="'##/##/####'"
    placeholder="dd/MM/aaaa"
    @blur="validarData"
    @click="mostrarPicker"
    ref="inputRef"
  />
  <input 
    type="date" 
    ref="datePickerRef" 
    :value="valorISO" 
    @change="onDatePickerChange"
    style="position: absolute; opacity: 0; width: 0; height: 0;"
  />
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { 
  formatarDataBr, 
  formatarDataISO, 
  validarDataBr, 
  extrairPartesDataBr,
  isValidISOFormat
} from '@/utils/dateUtils';

const props = defineProps<{
    modelValue: string | undefined;
    label: string;
    errorMsg: string;
}>();

const emit = defineEmits<{
    (e: 'update:modelValue', payload: string): void;
}>();

const inputRef = ref<HTMLInputElement>();
const datePickerRef = ref<HTMLInputElement>();
const valorISO = ref('');

// Converter valor ISO para BR quando o componente receber um valor do pai
watch(() => props.modelValue, (newValue) => {
    if (newValue && isValidISOFormat(newValue)) {
        // Se veio do backend em ISO, converter para BR
        valorISO.value = newValue;
        emit('update:modelValue', formatarDataBr(newValue));
    } else if (newValue && newValue.includes('/')) {
        // Se já está em BR, manter
        valorISO.value = formatarDataISO(newValue);
    } else {
        valorISO.value = '';
    }
}, { immediate: true });

const validarData = () => {
    if (!props.modelValue) return;
    
    if (props.modelValue.includes('/')) {
        if (!validarDataBr(props.modelValue)) {
            emit('update:modelValue', '');
        }
    }
};

const mostrarPicker = () => {
    if (datePickerRef.value) {
        datePickerRef.value.showPicker();
    }
};

const onDatePickerChange = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (target.value) {
        emit('update:modelValue', formatarDataBr(target.value));
    }
};
</script>