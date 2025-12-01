<script setup lang="ts">
import { atualizarProposta } from '@/api/PropostaService';
import { useNotification } from '@/composables/useNotification';
import type { PropostaResponseInterface } from '@/types';
import { reactive } from 'vue';
import * as yup from 'yup';

const { showNotification } = useNotification();

interface Props {
    proposta: PropostaResponseInterface,
    prestadorId: number | null
}

const props = defineProps<Props>();

const emit = defineEmits<{
    (e: 'salvar', payload: Partial<PropostaResponseInterface>): void
    (e: 'cancelar'): void
}>();

const form = reactive({ ...props.proposta });

const errors = reactive({
    titulo: '',
    comentario: '',
    preco: '',
});

const schema = yup.object({
    titulo: yup.string()
        .required('Título é obrigatório')
        .min(3, 'Título deve ter pelo menos 3 caracteres')
        .max(100, 'Título deve ter no máximo 100 caracteres'),
    comentario: yup.string()
        .required('Comentário é obrigatório')
        .min(10, 'Comentário deve ter pelo menos 10 caracteres')
        .max(500, 'Comentário deve ter no máximo 500 caracteres'),
    preco: yup.number()
        .min(0, 'Preço deve ser positivo')
        .max(1000000, 'Preço máximo é R$ 1.000.000')
        .required('Preço é obrigatório'),
});

const validarForm = async () => {
    try {
        await schema.validate(form, { abortEarly: false });
        Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');
        return true;
    } catch (erro: any) {
        Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');
        if (erro.inner) {
            erro.inner.forEach((e: any) => {
                errors[e.path as keyof typeof errors] = e.message;
            });
        }
        return false;
    }
};

const salvar = async () => {
    const valido = await validarForm();
    if (!valido) return;
    
    try {
        const propostaAtualizada = await atualizarProposta(props.proposta.id, form);
        emit('salvar', { ...propostaAtualizada });
        showNotification("Proposta atualizada com sucesso!", "success");
    } catch (error) {
        showNotification("Erro ao atualizar proposta.", "error");
    }
};
</script>

<template>
    <v-card class="edit-form-card">
        <v-toolbar color="white" density="compact">
            <v-toolbar-title class="text-black">
                Editar Proposta
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon @click="$emit('cancelar')" color="black" size="small">
                <v-icon>mdi-close</v-icon>
            </v-btn>
        </v-toolbar>

        <v-card-text class="pa-4">
            <v-form @submit.prevent="salvar">
                <v-text-field
                    v-model="form.titulo"
                    label="Título da Proposta"
                    :error-messages="errors.titulo"
                    variant="outlined"
                    density="comfortable"
                    class="mb-4"
                    :maxlength="100"
                    counter
                    required
                />

                <v-textarea
                    v-model="form.comentario"
                    label="Detalhes da Proposta"
                    :error-messages="errors.comentario"
                    variant="outlined"
                    density="comfortable"
                    class="mb-4"
                    rows="4"
                    :maxlength="500"
                    counter
                    auto-grow
                    required
                />

                <v-text-field
                    v-model="form.preco"
                    label="Valor (R$)"
                    :error-messages="errors.preco"
                    variant="outlined"
                    density="comfortable"
                    class="mb-6"
                    type="number"
                    min="0"
                    step="0.01"
                    required
                >
                    <template #append>
                        <span class="text-caption text-medium-emphasis">R$</span>
                    </template>
                </v-text-field>
            </v-form>
        </v-card-text>

        <v-card-actions class="pa-4 pt-0">
            <v-spacer></v-spacer>
            <v-btn
                color="secondary"
                variant="text"
                @click="$emit('cancelar')"
                class="mr-2"
            >
                Cancelar
            </v-btn>
            <v-btn
                color="primary"
                variant="flat"
                @click="salvar"
            >
                Salvar 
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<style scoped>
.edit-form-card {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

:deep(.v-field__prepend-inner) {
    padding-top: 0 !important;
}

:deep(.v-field__append-inner) {
    padding-top: 0 !important;
}
</style>