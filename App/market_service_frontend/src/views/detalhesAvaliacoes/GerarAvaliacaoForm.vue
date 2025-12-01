<script setup lang="ts">
import type { AvaliacaoInterface, PrestadorResponseInterface } from '@/types';
import { ref, onMounted, reactive } from 'vue';
import { useNotification } from '@/composables/useNotification';
import { carregarPrestadorPorId } from '@/api/PrestadorService';
import { criarAvaliacao } from '@/api/AvaliacaoService';
import * as yup from 'yup';

const { showNotification } = useNotification();

interface Props {
    prestadorId: number;
    clienteId: number;
    demandaId: number;
}

const props = defineProps<Props>();

const emit = defineEmits<{
    (e: 'cancelar'): void;
    (e: 'avaliacao-criada'): void;
}>();

const loading = ref(false);
const carregandoPrestador = ref(false);
const prestador = ref<PrestadorResponseInterface | null>(null);

const schema = yup.object({
    nota: yup
      .number()
      .required('Nota é obrigatória')
      .min(1, 'A nota mínima é 1')
      .max(5, 'A nota máxima é 5'),
    comentario: yup
      .string()
      .trim()
      .required('Comentário é obrigatório')
      .max(500, 'Máximo 500 caracteres')
});

type FormType = yup.InferType<typeof schema>;

const form = reactive<FormType>({
    nota: 5, comentario: ''
});

const errors = reactive<{nota: string; comentario: string}>({
    nota: '', comentario: ''
});


const carregarPrestador = async () => {
    carregandoPrestador.value = true;
    try {
        prestador.value = await carregarPrestadorPorId(props.prestadorId);
    } catch (error) {
        console.error('Erro ao carregar prestador:', error);
        showNotification('Erro ao carregar informações do prestador', 'error');
    } finally {
        carregandoPrestador.value = false;
    }
};

const validarForm = async (): Promise<boolean> => {
    errors.nota = '';
    errors.comentario = '';

    try {
        await schema.validate(form, { abortEarly: false });
        return true;
    } catch (err: any) {
        if (err.inner && Array.isArray(err.inner)) {
            err.inner.forEach((e: any) => {
                if (e.path && (e.message !== undefined)) {
                    (errors as any)[e.path] = e.message;
                }
            });
        } else if (err.path) {
            (errors as any)[err.path] = err.message;
        }
        return false;
    }
};

const validarCampo = async (campo: keyof typeof errors) => {
    try {
        await schema.validateAt(campo, form);
        errors[campo] = '';
    } catch (err: any) {
        errors[campo] = err.message;
    }
};

const avaliar = async () => {
    const valido = await validarForm();
    if (!valido) {
        showNotification('Corrija os erros e renvie', 'warning');
        return;
    }

    loading.value = true;
    try {
        const avaliacao: AvaliacaoInterface = {
            nota: form.nota,
            comentario: form.comentario.trim(),
            clienteId: props.clienteId,
            prestadorId: props.prestadorId,
            demandaId: props.demandaId
        };

        await criarAvaliacao(avaliacao);

        showNotification('Avaliação enviada com sucesso!', 'success');
        emit('avaliacao-criada');

    } catch (error: any) {
        console.error('Erro ao criar avaliação:', error);
        
        if (error.response?.status === 400) {
            showNotification('Dados inválidos para a avaliação', 'error');
        } else if (error.response?.status === 409) {
            showNotification('Você já avaliou este prestador para esta demanda', 'warning');
        } else {
            showNotification('Erro ao enviar avaliação. Tente novamente.', 'error');
        }
    } finally {
        loading.value = false;
    }
};

onMounted( async () => {
    await carregarPrestador();
});
</script>

<template>
    <v-card>
        <v-card-title class="d-flex justify-space-between align-center bg-primary">
            <span class="text-white">Avaliar Prestador</span>
            <v-btn 
                icon 
                @click="$emit('cancelar')" 
                variant="text" 
                color="white"
                :disabled="loading"
            >
                <v-icon>mdi-close</v-icon>
            </v-btn>
        </v-card-title>

        <v-card-text class="pa-4">
            <div v-if="carregandoPrestador" class="text-center mb-4">
                <v-progress-circular indeterminate color="primary" size="24" />
                <p class="text-caption text-medium-emphasis mt-2">Carregando informações...</p>
            </div>

            <div v-else-if="prestador" class="mb-4">
                <div class="d-flex align-center gap-3 mb-3">
                    <v-avatar color="primary" size="48">
                        <span class="text-white text-subtitle-2">
                            {{ prestador.usuario.nome.charAt(0).toUpperCase() }}
                        </span>
                    </v-avatar>
                    <div>
                        <h3 class="text-h6 font-weight-bold ml-8">{{ prestador.usuario.nome }}</h3>
                    </div>
                </div>
                <v-divider />
            </div>

            <div v-else class="mb-4">
                <v-alert type="warning" variant="tonal" density="compact">
                    Não foi possível carregar informações do prestador
                </v-alert>
            </div>

            <div class="mb-4">
                <p class="text-body-1 font-weight-medium mb-3">Como foi o serviço prestado?</p>
                
                <!-- estrelas -->
                <div class="text-center mb-4">
                    <div class="d-flex justify-center gap-1 mb-2">
                        <v-btn
                            v-for="n in 5"
                            :key="n"
                            icon
                            :color="n <= form.nota ? 'yellow-darken-3' : 'grey-lighten-1'"
                            @click="form.nota = n; validarCampo('nota')"
                            size="large"
                            variant="text"
                            :disabled="loading"
                        >
                            <v-icon size="32">mdi-star</v-icon>
                        </v-btn>
                    </div>
                    <p class="text-body-2 text-medium-emphasis">
                        {{ form.nota }} estrela{{ form.nota !== 1 ? 's' : '' }}
                        <span v-if="form.nota === 1"> - Ruim</span>
                        <span v-else-if="form.nota === 2"> - Regular</span>
                        <span v-else-if="form.nota === 3"> - Bom</span>
                        <span v-else-if="form.nota === 4"> - Muito Bom</span>
                        <span v-else-if="form.nota === 5"> - Excelente</span>
                    </p>

                    <div class="text-caption error--text" v-if="errors.nota" style="margin-top:4px;">
                        {{ errors.nota }}
                    </div>
                </div>

                <v-textarea
                    v-model="form.comentario"
                    label="Comentário (obrigatório)"
                    placeholder="Conte como foi sua experiência com este prestador..."
                    outlined
                    rows="3"
                    counter="500"
                    maxlength="500"
                    :disabled="loading"
                    :error-messages="errors.comentario ? [errors.comentario] : []"
                    @blur="validarCampo('comentario')"
                    class="mb-2"
                />

                <v-alert
                    type="info"
                    variant="tonal"
                    density="compact"
                    class="mb-0"
                >
                    <span class="text-caption">
                        ⓘ Sua avaliação será pública e visível para outros clientes
                    </span>
                </v-alert>
            </div>
        </v-card-text>

        <v-card-actions class="pa-4">

            <v-btn
                color="primary"
                variant="flat"
                @click="avaliar"
                :loading="loading"
                :disabled="loading"
                block
            >
                <template v-if="loading">
                    <v-progress-circular
                        indeterminate
                        size="20"
                        width="2"
                        class="mr-2"
                    />
                    Enviando...
                </template>
                <template v-else>
                    Enviar Avaliação
                </template>
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<style scoped>
.v-btn--icon.size-large {
    width: 48px;
    height: 48px;
}
</style>