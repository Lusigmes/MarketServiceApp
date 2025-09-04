<script setup lang="ts">
import { findPrestadorIdByUsuarioId } from '@/api/PrestadorService';
import type { PropostaResponseInterface } from '@/types';
import { StatusProposta } from '@/types/enums';
import { onMounted, ref, reactive, watch} from 'vue';
import * as yup from 'yup';
import { criarProposta } from '@/api/PropostaService';

    interface Props {
        usuarioId: number,
        demandaId: number
    };

    const props = defineProps<Props>();
    const emit = defineEmits<{
        (e: 'criar-proposta', payload: Partial<PropostaResponseInterface>): void;
        (e: 'cancelar'): void;

    }>();
    
    const prestadorId = ref<number | null> (null);

    const form = reactive<Partial<PropostaResponseInterface>>({
        titulo: '',
        preco: 0,
        comentario: '',
        statusProposta: StatusProposta.PENDENTE,
        demandaId: props.demandaId,
        prestadorId: prestadorId.value
    });

    const errors = reactive({
        titulo: '',
        preco: '',
        comentario: '',
    });
    const schema = yup.object({
        titulo: yup.string().required('Título é obrigatório'),
        preco: yup.string().required('Preço é obrigatório'),
        comentario: yup.string().required('Comentário é obrigatório'),
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

    const salvarProposta = async () => {
        const valido = await validarForm();
        if(!valido) return;
        try {
            const propostaSalva = await criarProposta(form);
            emit('criar-proposta', propostaSalva as PropostaResponseInterface);
        } catch (error) {
            console.error("ERRO: ", error)
        }
    };

    watch(prestadorId, (idAtualizado) => { form.prestadorId = idAtualizado ?? null; console.log("ATT",idAtualizado) });

    onMounted(async ()=>{
        prestadorId.value = await findPrestadorIdByUsuarioId(props.usuarioId)
    });


</script>


<template>
     <v-card>
        <v-card-title>Criar Proposta</v-card-title>
        <v-card-text style="max-height: 400px; overflow-y:auto;">
          <v-text-field v-model="form.titulo" label="Título" :error-messages="errors.titulo" />
          <v-textarea v-model="form.comentario" label="Comentário sobre proposta" :error-messages="errors.comentario" />
          <v-text-field v-model="form.preco" label="Preço" type="number" :error-messages="errors.preco" />
        </v-card-text>
        <v-card-actions class="d-flex justify-end">
          <v-btn color="primary" @click="salvarProposta">Salvar</v-btn>
          <v-btn text color="red" @click="$emit('cancelar')">Cancelar</v-btn>
        </v-card-actions>
      </v-card>
</template>