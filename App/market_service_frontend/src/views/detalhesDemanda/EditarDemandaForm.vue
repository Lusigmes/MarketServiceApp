<script setup lang="ts">
import { atualizarDemanda } from '@/api/DemandaService';
import DataInput from '@/components/DataInput.vue';
import type { DemandaResponseInterface } from '@/types';
import { PrioridadeDemanda } from '@/types/enums';
import { formatarDataBr } from '@/utils/dateUtils';
import { labelPrioridade } from '@/utils/labelsUtils';
import { onMounted, reactive } from 'vue';
import * as yup from 'yup';

  interface Props {
      demanda: DemandaResponseInterface,
      clienteId: number | null
  }

  const props = defineProps<Props>();

  const emit = defineEmits<{
      (e: 'salvar', payload: Partial<DemandaResponseInterface>): void
      (e: 'cancelar'): void
  }>();

  const form = reactive({...props.demanda});

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
    if(!valido) return;

    const payload = {
      ...form,
      prazo: form.prazo ? formatarDataBr(form.prazo) : ''
    };
    try{
      const demandaAtualizada = await atualizarDemanda(props.demanda.id, payload, props.clienteId);
      emit('salvar', { ...demandaAtualizada }); // ...props.demanda  / id: props.demanda.id 
    } catch (error) {
      console.error("Erro ao atualizar:", error);
    }
 
  };

    
  onMounted(async () => {
    console.log("NO EDIT", props.clienteId);
  });
</script>

<template>
  <v-card>
    <v-card-title>Editar Demanda</v-card-title>
    <v-card-text style="max-height: 400px; overflow-y: auto;">
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
