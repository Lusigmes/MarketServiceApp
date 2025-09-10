<script setup lang="ts">
import { atualizarProposta } from '@/api/PropostaService';
import type { PropostaResponseInterface } from '@/types';
import { onMounted, reactive } from 'vue';
import * as yup from 'yup';

  interface Props {
      proposta: PropostaResponseInterface,
      prestadorId: number | null
  }

  const props = defineProps<Props>();

  const emit = defineEmits<{
      (e: 'salvar', payload: Partial<PropostaResponseInterface>): void
      (e: 'cancelar'): void
  }>();

  const form = reactive({...props.proposta});

  const errors = reactive({
    titulo: '',
    comentario: '',
    preco: '',
  });

  const schema = yup.object({
    titulo: yup.string().required('Título é obrigatório'),
    comentario: yup.string().required('Comentário é obrigatória'),
    preco: yup.number().min(0, 'Preço deve ser positivo').required(),
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
    try{
      const propostaAtualizada = await atualizarProposta(props.proposta.id, form);
      emit('salvar', { ...propostaAtualizada }); 
    } catch (error) {
      console.error("Erro ao atualizar:", error);
    }
 
  };

    
  onMounted(async () => {
    console.log("NO EDIT", props.prestadorId);
  });
</script>

<template>
  <v-card>
    <v-card-title>Editar Demanda</v-card-title>
    <v-card-text style="max-height: 400px; overflow-y: auto;">
      <v-text-field v-model="form.titulo" label="Título" :error-messages="errors.titulo" />
      <v-textarea v-model="form.comentario" label="Descrição" :error-messages="errors.comentario" />
      <v-text-field v-model="form.preco" label="Orçamento Estimado" type="number" :error-messages="errors.preco" />
    </v-card-text>
    <v-card-actions class="d-flex justify-end">
      <v-btn color="primary" @click="salvar">Salvar</v-btn>
      <v-btn text color="red" @click="$emit('cancelar')">Cancelar</v-btn>
    </v-card-actions>
  </v-card>
</template>
