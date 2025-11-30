    <script setup lang="ts">
    import type { AvaliacaoInterface, PrestadorResponseInterface } from '@/types';
    import { ref, onMounted } from 'vue';
    import { useNotification } from '@/composables/useNotification';
    import { carregarPrestadorPorId } from '@/api/PrestadorService';
    import { criarAvaliacao } from '@/api/AvaliacaoService';

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
    const nota = ref(5);
    const comentario = ref('');
    const prestador = ref<PrestadorResponseInterface | null>(null);

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

    const avaliar = async () => {
        if (!comentario.value.trim()) {
            showNotification('Por favor, escreva um comentário', 'warning');
            return;
        }

        if (nota.value < 1 || nota.value > 5) {
            showNotification('Por favor, selecione uma nota entre 1 e 5', 'warning');
            return;
        }

        loading.value = true;
        try {
            const avaliacaoDTO: AvaliacaoInterface = {
                nota: nota.value,
                comentario: comentario.value.trim(),
                clienteId: props.clienteId,
                prestadorId: props.prestadorId,
                demandaId: props.demandaId
            };

            await criarAvaliacao(avaliacaoDTO);
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
        carregarPrestador();
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
                <!-- Informações do prestador -->
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
                            <h3 class="text-h6 font-weight-bold">{{ prestador.usuario.nome }}</h3>
                            <p class="text-caption text-medium-emphasis">{{ prestador.usuario.email }}</p>
                        </div>
                    </div>
                    <v-divider />
                </div>

                <div v-else class="mb-4">
                    <v-alert type="warning" variant="tonal" density="compact">
                        Não foi possível carregar informações do prestador
                    </v-alert>
                </div>

                <!-- Formulário de avaliação -->
                <div class="mb-4">
                    <p class="text-body-1 font-weight-medium mb-3">Como foi o serviço prestado?</p>
                    
                    <!-- Avaliação por estrelas -->
                    <div class="text-center mb-4">
                        <div class="d-flex justify-center gap-1 mb-2">
                            <v-btn
                                v-for="n in 5"
                                :key="n"
                                icon
                                :color="n <= nota ? 'yellow-darken-3' : 'grey-lighten-1'"
                                @click="nota = n"
                                size="large"
                                variant="text"
                                :disabled="loading"
                            >
                                <v-icon size="32">mdi-star</v-icon>
                            </v-btn>
                        </div>
                        <p class="text-body-2 text-medium-emphasis">
                            {{ nota }} estrela{{ nota !== 1 ? 's' : '' }}
                            <span v-if="nota === 1"> - Ruim</span>
                            <span v-else-if="nota === 2"> - Regular</span>
                            <span v-else-if="nota === 3"> - Bom</span>
                            <span v-else-if="nota === 4"> - Muito Bom</span>
                            <span v-else-if="nota === 5"> - Excelente</span>
                        </p>
                    </div>

                    <!-- Comentário -->
                    <v-textarea
                        v-model="comentario"
                        label="Comentário (obrigatório)"
                        placeholder="Conte como foi sua experiência com este prestador..."
                        outlined
                        rows="3"
                        counter="500"
                        maxlength="500"
                        :disabled="loading"
                        :rules="[
                            (v: string) => !!v.trim() || 'Comentário é obrigatório',
                            (v: string) => v.length <= 500 || 'Máximo 500 caracteres'
                        ]"
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
                    color="grey"
                    variant="outlined"
                    @click="$emit('cancelar')"
                    :disabled="loading"
                    block
                    class="mb-2"
                >
                    Cancelar
                </v-btn>
                <v-btn
                    color="primary"
                    variant="flat"
                    @click="avaliar"
                    :loading="loading"
                    :disabled="loading || !comentario.trim()"
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
                        <v-icon class="mr-2">mdi-send</v-icon>
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