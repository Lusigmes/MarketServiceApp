<script setup lang="ts">
import { atualizarDemanda } from '@/api/DemandaService';
import DataInput from '@/components/DataInput.vue';
import { useNotification } from '@/composables/useNotification';
import type { DemandaResponseInterface } from '@/types';
import { PrioridadeDemanda } from '@/types/enums';
import { labelPrioridade } from '@/utils/labelsUtils';
import { reactive } from 'vue';
import * as yup from 'yup';
import { formatarDataBr, formatarDataParaISO, isValidISOFormat } from '../../utils/dateUtils';

const { showNotification } = useNotification();

interface Props {
    demanda: DemandaResponseInterface,
    clienteId: number | null
}

const props = defineProps<Props>();

const emit = defineEmits<{
    (e: 'salvar', payload: Partial<DemandaResponseInterface>): void
    (e: 'cancelar'): void
}>();

const form = reactive({
    ...props.demanda,
    prazo: props.demanda.prazo ? formatarDataParaVisualizacao(props.demanda.prazo) : ''
});

const errors = reactive({
    titulo: '',
    descricao: '',
    categoria: '',
    localizacao: '',
    prazo: '',
    prioridade: '',
    orcamentoEstimado: ''
});

const schema = yup.object({
    titulo: yup.string()
        .required('Título é obrigatório')
        .min(3, 'Título deve ter pelo menos 3 caracteres')
        .max(100, 'Título deve ter no máximo 100 caracteres'),
    descricao: yup.string()
        .required('Descrição é obrigatória')
        .min(10, 'Descrição deve ter pelo menos 10 caracteres')
        .max(1000, 'Descrição deve ter no máximo 1000 caracteres'),
    categoria: yup.string()
        .required('Categoria é obrigatória')
        .min(2, 'Categoria deve ter pelo menos 2 caracteres')
        .max(50, 'Categoria deve ter no máximo 50 caracteres'),
    localizacao: yup.string()
        .required('Localização é obrigatória')
        .min(3, 'Localização deve ter pelo menos 3 caracteres')
        .max(200, 'Localização deve ter no máximo 200 caracteres'),
    prazo: yup.string()
        .required('Prazo é obrigatório')
        .test('data-valida', 'Data inválida', (value) => {
            if (!value) return false;
            return isValidISOFormat(formatarDataParaISO(value)) || value.includes('/');
        }),
    prioridade: yup
        .mixed<PrioridadeDemanda>()
        .oneOf(Object.values(PrioridadeDemanda) as PrioridadeDemanda[])
        .required('Prioridade é obrigatória'),
    orcamentoEstimado: yup.number()
        .min(0, 'Orçamento deve ser positivo')
        .max(1000000, 'Orçamento máximo é R$ 1.000.000')
        .required('Orçamento é obrigatório'),
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

function formatarDataParaVisualizacao(data: string): string {
    if (!data) return '';
    
    if (data.includes('/')) {
        return data;
    }
    
    if (isValidISOFormat(data)) {
        return formatarDataBr(data);
    }
    
    return '';
}

const salvar = async () => {
    const valido = await validarForm();
    if (!valido) return;

    const payload = {
        ...form,
    };
    
    try {
        const demandaAtualizada = await atualizarDemanda(props.demanda.id, payload, props.clienteId);
        emit('salvar', { ...demandaAtualizada });
        showNotification("Demanda atualizada com sucesso!", "success");
    } catch (error) {
        showNotification("Não foi possível atualizar a demanda.", "error");
    }
};
</script>


<template>
    <v-card class="edit-demand-card">
        <v-toolbar color="white" density="compact">
            <v-toolbar-title class="text-black">
                Editar Demanda
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon @click="$emit('cancelar')" color="black" size="small">
                <v-icon>mdi-close</v-icon>
            </v-btn>
        </v-toolbar>

        <v-card-text class="pa-4 demand-form-content" style="max-height: 450px; overflow-y: auto;">
            <v-form @submit.prevent="salvar">
                <v-text-field
                    v-model="form.titulo"
                    label="Título da Demanda"
                    :error-messages="errors.titulo"
                    variant="outlined"
                    density="comfortable"
                    class="mb-3"
                    :maxlength="100"
                    counter
                    required
                />

                <v-textarea
                    v-model="form.descricao"
                    label="Descrição Detalhada"
                    :error-messages="errors.descricao"
                    variant="outlined"
                    density="comfortable"
                    class="mb-3"
                    rows="3"
                    :maxlength="1000"
                    counter
                    auto-grow
                    required
                />

                <v-text-field
                    v-model="form.categoria"
                    label="Categoria do Serviço"
                    :error-messages="errors.categoria"
                    variant="outlined"
                    density="comfortable"
                    class="mb-3"
                    :maxlength="50"
                    counter
                    required
                />

                <v-text-field
                    v-model="form.localizacao"
                    label="Localização"
                    :error-messages="errors.localizacao"
                    variant="outlined"
                    density="comfortable"
                    class="mb-3"
                    :maxlength="200"
                    counter
                    required
                />

                <DataInput
                    v-model="form.prazo"
                    label="Prazo de Conclusão"
                    :error-msg="errors.prazo"
                    class="mb-3"
                />

                <v-select
                    v-model="form.prioridade"
                    :items="Object.values(PrioridadeDemanda).map(p => ({ 
                        title: labelPrioridade(p), 
                        value: p 
                    }))"
                    item-title="title"
                    item-value="value"
                    label="Nível de Urgência"
                    :error-messages="errors.prioridade"
                    variant="outlined"
                    density="comfortable"
                    class="mb-3"
                    required
                />

                <v-text-field
                    v-model="form.orcamentoEstimado"
                    label="Orçamento Estimado (R$)"
                    :error-messages="errors.orcamentoEstimado"
                    variant="outlined"
                    density="comfortable"
                    class="mb-3"
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

        <v-card-actions class="pa-4 border-top">
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
.edit-demand-card {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
    max-height: 80vh; 
    display: flex;
    flex-direction: column;
}

.demand-form-content {
    flex: 1;
    overflow-y: auto;
    min-height: 200px; 
}

.border-top {
    border-top: 1px solid rgba(0, 0, 0, 0.12);
    flex-shrink: 0; 
}

.demand-form-content::-webkit-scrollbar {
    width: 8px;
}

.demand-form-content::-webkit-scrollbar-track {
    background: #f5f5f5;
    border-radius: 4px;
}

.demand-form-content::-webkit-scrollbar-thumb {
    background: #bdbdbd;
    border-radius: 4px;
}

.demand-form-content::-webkit-scrollbar-thumb:hover {
    background: #9e9e9e;
}

:deep(.v-toolbar__content) {
    padding-right: 8px !important;
}
</style>