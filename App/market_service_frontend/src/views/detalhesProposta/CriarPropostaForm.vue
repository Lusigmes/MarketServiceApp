
<script setup lang="ts">
import { findPrestadorIdByUsuarioId } from '@/api/PrestadorService';
import type { PropostaResponseInterface } from '@/types';
import { StatusProposta } from '@/types/enums';
import { onMounted, ref, reactive, watch} from 'vue';
import * as yup from 'yup';
import { criarProposta } from '@/api/PropostaService';
import { useNotification } from '@/composables/useNotification';

    const {showNotification} = useNotification();
    
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
            showNotification("Proposta criada com sucesso.", "success");
        } catch (error) {
            showNotification("Erro ao criar proposta.", "error");
        }
    };

    watch(prestadorId, (prestadorIdAtualizado) => { form.prestadorId = prestadorIdAtualizado ?? null; });

    onMounted(async ()=>{
        prestadorId.value = await findPrestadorIdByUsuarioId(props.usuarioId)
    });
</script>

<template>
    <v-card class="criar-form-card">
        <v-toolbar color="white" density="compact">
            <v-toolbar-title class="text-black">
                Criar Proposta
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon @click="$emit('cancelar')" color="black" size="small">
                <v-icon>mdi-close</v-icon>
            </v-btn>
        </v-toolbar>

        <v-card-text class="pa-4">
            <v-form @submit.prevent="salvarProposta">
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
                @click="salvarProposta"
            >
                Criar Proposta
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<style scoped>
.criar-form-card {
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