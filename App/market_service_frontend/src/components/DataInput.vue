<template>
  <v-text-field
    :model-value="dataFormatada"
    @update:model-value="atualizarData"
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
import { computed, ref, watch } from 'vue';
import { 
  formatarDataBr, 
  formatarDataISO, 
  validarDataBr, 
  extrairPartesDataBr 
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

    const isValidISOFormat = (data: string): boolean => {
        return /^\d{4}-\d{2}-\d{2}$/.test(data);
    };

    const atualizarData = (valor: string) => {
        if (!valor) {
            emit('update:modelValue', '');
            return;
    }

    const partes = extrairPartesDataBr(valor);
        if (partes) {
            const { dia, mes, ano } = partes;
            const dataBr = `${dia}/${mes}/${ano}`;
            
            if (validarDataBr(dataBr)) {
            const isoDate = formatarDataISO(dataBr);
            emit('update:modelValue', isoDate);
            } else {
            emit('update:modelValue', valor);
            }
        } else {
            emit('update:modelValue', valor);
        }
    };

    const validarData = () => {
        if (!props.modelValue) return;
        
        if (!isValidISOFormat(props.modelValue)) {
            const partes = extrairPartesDataBr(props.modelValue);
            if (partes) {
            const { dia, mes, ano } = partes;
            const dataBr = `${dia}/${mes}/${ano}`;
            
            if (validarDataBr(dataBr)) {
                const isoDate = formatarDataISO(dataBr);
                emit('update:modelValue', isoDate);
            }
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
            emit('update:modelValue', target.value);
        }
    };
    
    watch(() => props.modelValue, (newValue) => {
        if (newValue && isValidISOFormat(newValue)) {
            valorISO.value = newValue;
        } else {
            valorISO.value = '';
        }
        }, { immediate: true });

        const dataFormatada = computed(() => {
        if (!props.modelValue) return '';

        if (isValidISOFormat(props.modelValue)) {
            return formatarDataBr(props.modelValue);
        }
        
        return props.modelValue;
    });
</script>