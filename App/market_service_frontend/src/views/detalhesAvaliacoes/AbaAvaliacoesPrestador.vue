<script setup lang="ts">
import { carregarAvaliacoesPorPrestadorPaginado, carregarEstatisticasPrestador } from '@/api/AvaliacaoService';
import { useAvaliacaoPagination } from '@/composables/usePagination';
import type { AvaliacaoResponseInterface } from '@/types';
import { formatarDataBr } from '@/utils/dateUtils';
import { ref, onMounted } from 'vue';

interface Props {
    prestadorId: number;
}
const props = defineProps<Props>();

const {
    items: avaliacoes,
    page,
    totalPages,
    totalElements,
    loading,
    atualizarPagina,
} = useAvaliacaoPagination<AvaliacaoResponseInterface>(
    carregarAvaliacoesPorPrestadorPaginado, 
    props.prestadorId, 
    5, 
    "dataAvaliacao,desc"
);

const estatisticas = ref<{media: number; totalAvaliacoes: number}>({media: 0,totalAvaliacoes: 0});

const carregandoEstatisticas = ref(false);

const carregarEstatisticas = async () => {
    carregandoEstatisticas.value = true;
    try {
        estatisticas.value = await carregarEstatisticasPrestador(props.prestadorId);
    } catch (error) {
        console.error('Erro ao carregar estatísticas:', error);
    } finally {
        carregandoEstatisticas.value = false;
    }
};


const formatarData = (dataAvaliacao: string) => {
    if (dataAvaliacao.includes('/')) {
        return dataAvaliacao.split(' ')[0]; 
    }
    return formatarDataBr(dataAvaliacao);
};
const proximaPagina = async () => {
    if (page.value < totalPages.value - 1) {
        await atualizarPagina(page.value + 1);
    }
};

const paginaAnterior = async () => {
    if (page.value > 0) {
        await atualizarPagina(page.value - 1);
    }
};
const formatarNomeCliente = (avaliacao: AvaliacaoResponseInterface) => {
    return avaliacao.nomeCliente || `Cliente #${avaliacao.clienteId}`;
};

onMounted(async () =>{
    await Promise.all([atualizarPagina(), carregarEstatisticas()]);
} ) ;
</script>

<template>
    <div class="avaliacoes-minimalista">
        <div class="estatisticas-minimalistas mb-4">
            <div class="d-flex justify-center align-center gap-6 text-center">
                <div>
                    <div class="text-h5 font-weight-bold text-primary mr-10">{{ estatisticas.media.toFixed(1) }}</div>
                    <div class="text-caption text-medium-emphasis mr-10">Média</div>
                </div>
                <v-divider vertical></v-divider>
                <div>
                    <div class="text-h5 font-weight-bold text-primary ml-10">{{ estatisticas.totalAvaliacoes }}</div>
                    <div class="text-caption text-medium-emphasis ml-10">Avaliações</div>
                </div>
            </div>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="text-center py-4">
            <v-progress-circular indeterminate color="primary" size="24" />
            <p class="text-caption text-medium-emphasis mt-2">Carregando...</p>
        </div>

        <!-- Lista -->
        <div v-else-if="avaliacoes.length > 0">
            <v-card variant="outlined" class="mb-3">
                <v-list density="compact" class="py-0">
                    <v-list-item
                        v-for="avaliacao in avaliacoes"
                        :key="avaliacao.id"
                        class="border-b avaliacao-item"
                    >
                        <template #prepend>
                            <div class="estrelas-mini mr-3">
                                <v-icon
                                    v-for="n in 5"
                                    :key="n"
                                    :color="n <= avaliacao.nota ? 'yellow-darken-3' : 'grey-lighten-2'"
                                    size="14"
                                    class="mr-1"
                                >
                                    mdi-star
                                </v-icon>
                            </div>
                        </template>

                        <v-list-item-title class="text-body-2">
                            <div class="d-flex justify-space-between align-start">
                                <div class="flex-grow-1">
                                    <span class="font-weight-medium">{{ formatarNomeCliente(avaliacao) }}</span>
                                    <span class="text-medium-emphasis ml-2">•</span>
                                    <span class="text-caption text-medium-emphasis ml-2">
                                        {{ avaliacao.comentario }}
                                    </span>
                                </div>
                                <span class="text-caption text-medium-emphasis ml-2 shrink-0">
                                    {{ formatarData(avaliacao.dataAvaliacao) }}
                                </span>
                            </div>
                        </v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-card>

            <div v-if="totalPages > 1" class="paginacao-minimalista">
                <div class="d-flex justify-center align-center gap-3">
                    <v-btn
                        icon
                        size="x-small"
                        :disabled="page === 0"
                        @click="paginaAnterior"
                        variant="text"
                        color="primary"
                    >
                        <v-icon>mdi-chevron-left</v-icon>
                    </v-btn>
                    

                    
                    <v-btn
                        icon
                        size="x-small"
                        :disabled="page >= totalPages - 1"
                        @click="proximaPagina"
                        variant="text"
                        color="primary"
                    >
                        <v-icon>mdi-chevron-right</v-icon>
                    </v-btn>
                </div>
            </div>
        </div>

        <!-- Sem avaliações -->
        <div v-else class="text-center py-6">
            <v-icon size="40" color="grey-lighten-2" class="mb-2">mdi-comment-outline</v-icon>
            <p class="text-caption text-medium-emphasis">Nenhuma avaliação</p>
        </div>
    </div>
</template>

<style scoped>
.avaliacoes-minimalista {
    max-height: 400px;
    overflow-y: auto;
}

.estatisticas-minimalistas {
    padding: 12px;
    background-color: rgba(0, 0, 0, 0.02);
    border-radius: 8px;
    border: 1px solid rgba(0, 0, 0, 0.08);
}

.avaliacao-item {
    min-height: 48px;
    padding: 8px 0;
}

.avaliacao-item:last-child {
    border-bottom: none !important;
}

.estrelas-mini {
    min-width: 80px;
}

.border-b {
    border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.paginacao-minimalista {
    padding: 8px;
    background-color: rgba(0, 0, 0, 0.02);
    border-radius: 6px;
}

.avaliacoes-minimalista::-webkit-scrollbar {
    width: 4px;
}

.avaliacoes-minimalista::-webkit-scrollbar-track {
    background: #f8f8f8;
}

.avaliacoes-minimalista::-webkit-scrollbar-thumb {
    background: #e0e0e0;
    border-radius: 2px;
}

.avaliacoes-minimalista::-webkit-scrollbar-thumb:hover {
    background: #d0d0d0;
}

.text-caption {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 200px;
}

.shrink-0 {
    flex-shrink: 0;
}
</style>