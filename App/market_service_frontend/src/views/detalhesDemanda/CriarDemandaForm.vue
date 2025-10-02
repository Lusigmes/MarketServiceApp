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
  <v-card>
    <v-card-title>Criar Demanda</v-card-title>
    <v-card-text style="max-height: 400px; overflow-y:auto;">
      <v-text-field v-model="form.titulo" label="Título" :error-messages="errors.titulo" />
      <v-textarea v-model="form.descricao" label="Descrição" :error-messages="errors.descricao" />
      <v-text-field v-model="form.categoria" label="Categoria" :error-messages="errors.categoria" />
      <v-text-field v-model="form.localizacao" label="Localização" :error-messages="errors.localizacao" />
      <DataInput v-model="form.prazo" label="Prazo" :error-msg="errors.prazo" />
      <v-select v-model="form.prioridade" :items="Object.values(PrioridadeDemanda).map(p => ({ text: labelPrioridade(p), value: p }))" item-title="text" item-value="value" label="Urgência" :error-messages="errors.prioridade" />
      <v-text-field v-model="form.orcamentoEstimado" label="Orçamento Estimado" type="number" :error-messages="errors.orcamentoEstimado" />
    </v-card-text>
    <v-card-actions class="d-flex justify-end">
      <v-btn color="primary" @click="salvar">Salvar</v-btn>
      <v-btn text color="red" @click="$emit('cancelar')">Cancelar</v-btn>
    </v-card-actions>
  </v-card>
</template>