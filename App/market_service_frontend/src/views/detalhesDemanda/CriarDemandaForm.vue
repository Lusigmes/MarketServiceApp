<script setup lang="ts">
import { reactive } from 'vue';
import type { DemandaResponseInterface } from '@/types';
import { PrioridadeDemanda, } from '@/types/enums';
import { criarDemanda } from '@/api/DemandaService';
import * as yup from 'yup';
import { labelPrioridade } from '@/utils/labelsUtils';
import DataInput from '@/components/DataInput.vue';
import { useNotification } from "@/composables/useNotification";
import  {formatarDataParaISO} from "@/utils/dateUtils";
  const { showNotification } = useNotification();

  interface Props {
    clienteId: number | null;
  }

  const props = defineProps<Props>();

  const emit = defineEmits<{
    (e: 'salvar', payload: DemandaResponseInterface): void;
    (e: 'cancelar'): void;
  }>();

  const form = reactive<Partial<DemandaResponseInterface>>({
    titulo: '',
    descricao: '',
    categoria: '',
    localizacao: '',
    prazo: '',
    prioridade: PrioridadeDemanda.BAIXA,
    orcamentoEstimado: 0,
    clienteId: props.clienteId,
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
    titulo: yup.string().required('Título é obrigatório'),
    descricao: yup.string().required('Descrição é obrigatória'),
    categoria: yup.string().required('Categoria é obrigatória'),
    localizacao: yup.string().required('Localização é obrigatória'),
    prazo: yup.string().required('Prazo é obrigatório'),
    prioridade: yup
      .mixed<PrioridadeDemanda>()
      .oneOf(Object.values(PrioridadeDemanda) as PrioridadeDemanda[])
      .required(),
    orcamentoEstimado: yup.number().min(0, 'Orçamento deve ser positivo').required(),
  });

  const validarForm = async () => {
    try {
      await schema.validate(form, {abortEarly:false});
      Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');
      return true;
      
    } catch (erro:any) {
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
      const payload = {
        ...form,
        prazo: formatarDataParaISO(form.prazo || '')
      };

      const salvarDemanda = await criarDemanda(payload);
      emit('salvar', salvarDemanda as DemandaResponseInterface);
      showNotification("Demanda criada com sucesso!", "success");
    } catch (error) {
      showNotification("Erro ao criar demanda!", "error");
      throw error;
    }
  };
</script>

<template>
    <v-card class="criar-demand-card">
        <v-toolbar color="white" density="compact">
            <v-toolbar-title class="text-black">
                Criar Demanda
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
                    required
                />

                <v-text-field
                    v-model="form.localizacao"
                    label="Localização"
                    :error-messages="errors.localizacao"
                    variant="outlined"
                    density="comfortable"
                    class="mb-3"
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
                Criar Demanda
            </v-btn>
        </v-card-actions>
    </v-card>
</template>
<style scoped>
.criar-demand-card {
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