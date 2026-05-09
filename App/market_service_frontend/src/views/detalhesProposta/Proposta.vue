<script setup lang="ts">
import type { DemandaResponseInterface, PropostaResponseInterface } from '@/types';
import { corPrioridade, corStatusProposta, labelPrioridade } from '@/utils/labelsUtils';
import { ref, computed, onMounted, watch } from 'vue';
import EditarPropostaForm from './EditarPropostaForm.vue';
import { atualizarProposta } from '@/api/PropostaService';
import { PrioridadeDemanda, StatusDemanda, StatusProposta } from '@/types/enums';
import { getDemandaById } from '@/api/DemandaService';
import { useNotification } from '@/composables/useNotification';
import { formatarDataParaExibicao } from '@/utils/dateUtils';

    const{showNotification} = useNotification();

    interface Props{
        proposta: PropostaResponseInterface; 
        prestadorId?: number | null;
        tipoUsuario?: 'CLIENTE' | 'PRESTADOR';
        statusDemanda?: StatusDemanda;
    };

    const emit = defineEmits<{
        (e: 'fechar'): void;
        (e: 'atualizar-proposta', payload: Partial<PropostaResponseInterface>): void;

    }>();

    const props = defineProps<Props>();

    const editando = ref(false); 
    const abrirFormEdicao = () => { editando.value = true; };
    const fecharFormEdicao = () => { editando.value = false; };
    
    const propostaAtualizadaRef = ref<PropostaResponseInterface | null>(null);

    const demandaVinculada = ref<DemandaResponseInterface | null>(null);
    const carregandoDemanda = ref(false);
    
    const carregarDemandaVinculada = async () => {
        if(!props.proposta?.demandaId) return;
        carregandoDemanda.value = true;
        try {
            const demanda = await getDemandaById(props.proposta.demandaId);
            demandaVinculada.value = demanda.data;
            console.log(demanda.data);
        } catch (error) {
            console.error('Erro ao carregar demanda vinculada:', error);
            showNotification('Erro ao carregar dados da demanda', 'error');
        }finally{
            carregandoDemanda.value = false;
        }
    };

    const mostrarDetalhesDemanda = computed(()=> {
        return props.tipoUsuario === 'PRESTADOR' && demandaVinculada.value;
    });

    const getCorTooltipPrioridade = (prioridade: PrioridadeDemanda): string => {
        switch(prioridade){
            case PrioridadeDemanda.ALTA: return 'red-darken-3';
            case PrioridadeDemanda.MEDIA: return 'orange-darken-3';
            case PrioridadeDemanda.BAIXA: return 'green-darken-3';
            default: return 'grey-darken-2';
        }
    };
    const getDescricaoPrioridade = (prioridade: PrioridadeDemanda): string => {
        switch(prioridade){
            case PrioridadeDemanda.ALTA: 
                return 'Demanda de ALTA prioridade - Requer atenção imediata';
            case PrioridadeDemanda.MEDIA: 
                return 'Demanda de MÉDIA prioridade - Pode ser agendada';
            case PrioridadeDemanda.BAIXA: 
                return 'Demanda de BAIXA prioridade - Sem urgência';
            default: 
                return `Prioridade ${labelPrioridade(prioridade)}`;
        }
    };
    const prazoFormatado = computed(() =>{
        if(!demandaVinculada.value?.prazo) return '';
        return formatarDataParaExibicao(demandaVinculada.value.prazo);
    });

    
    const concluirProposta = async () => {
        if (props.proposta.statusProposta !== StatusProposta.ACEITA) return;       
        try {
            const propostaAtualizada = {
                statusProposta: StatusProposta.CONCLUIDA
            }
            const propostaAtt = await atualizarProposta(props.proposta.id, propostaAtualizada);
            
            propostaAtualizadaRef.value = propostaAtt;
                
            emit("atualizar-proposta", {...props.proposta, ...propostaAtualizada});      
            showNotification("Proposta concluída com sucesso.","success");
        } catch (error) {
            showNotification("Erro ao concluir proposta.","error");
        }
    };
    
    const cancelarProposta = async () => { 
        if (![StatusProposta.PENDENTE, StatusProposta.ACEITA].includes(props.proposta.statusProposta)) return;
        try {
            const propostaCancelada = {
                statusProposta: StatusProposta.CANCELADA
            };
            const propostaAtt = await atualizarProposta(props.proposta.id, propostaCancelada);
            
            propostaAtualizadaRef.value = propostaAtt;   
            emit("atualizar-proposta",{...props.proposta, ...propostaCancelada})
            // console.log("Proposta status anterior:", props.proposta.statusProposta);
            showNotification("Proposta cancelada com sucesso.", "success");
        } catch (err) { showNotification("Erro ao cancelar proposta", "error"); }
    };
    
    
    const desfazerAcao = async () => { 
        if (![StatusProposta.CANCELADA].includes(props.proposta.statusProposta)) return;       
        try {
            const demandaResponse = await getDemandaById(props.proposta.demandaId);
            const demanda = demandaResponse.data;
            if(demanda.statusDemanda === 'CANCELADA'){
                showNotification("Não pode desfazer ação quando a demanda vinculada está Cancelada", "warning");
                return;
            }
            
            const propostaReativada = {
                statusProposta: StatusProposta.PENDENTE
            };
            const propostaAtt = await atualizarProposta(props.proposta.id, propostaReativada);
            
            propostaAtualizadaRef.value = propostaAtt;   
            emit("atualizar-proposta",{...props.proposta, ...propostaReativada})
            showNotification("Ação desfeita", "success");
        } catch (err) { showNotification("Erro ao desfazer ação.", "error");  }
    };


    const mostrarBotoesPrestador = computed(() => props.tipoUsuario === 'PRESTADOR');

    const permissaoPrestadorResponsavel = computed(() => 
        props.tipoUsuario === 'PRESTADOR' && props.prestadorId === props.proposta.prestadorId
    );

    const podeConcluir = computed(() => 
        permissaoPrestadorResponsavel.value && props.proposta.statusProposta === StatusProposta.ACEITA
    );

    const podeCancelar = computed(() => {
        if(!permissaoPrestadorResponsavel.value) return false;
        
        const demandaPermiteCancelar = 
        props.statusDemanda !== StatusDemanda.CONCLUIDA &&
        props.statusDemanda !== StatusDemanda.CANCELADA;
        
        if(!demandaPermiteCancelar) return false;

        return props.proposta.statusProposta === StatusProposta.PENDENTE || 
            props.proposta.statusProposta === StatusProposta.ACEITA;
    });


    const podeDesfazer = computed(() => 
        permissaoPrestadorResponsavel.value &&
        (props.proposta.statusProposta === "CANCELADA" )
    );

    const salvarEdicao = (propostaAtualizada: Partial<PropostaResponseInterface>) => {
        emit('atualizar-proposta', propostaAtualizada);
        fecharFormEdicao();
    };

    const dataFormatada = computed(() => {
        return formatarDataParaExibicao(props.proposta?.dataCriacao);
    });

    onMounted(async () => {
        await carregarDemandaVinculada();
    });
    watch(() => props.proposta, async () => {
        await carregarDemandaVinculada();
    }, {deep:true});
</script>

<template>
    <v-card 
        :class="corStatusProposta(props.proposta.statusProposta)"
        class="pa-4 rounded-lg elevation-4 proposta-card"
    >        
        <div class="dark-overlay"></div>
        
        <template v-if="!editando">
            <div class="d-flex justify-space-between align-start mb-4">
                <div class="flex-grow-1 mr-3">
                    <h3 class="text-h5 font-weight-bold text-white mb-1 text-truncate" style="text-shadow: 0 1px 2px rgba(0,0,0,0.3);">
                        {{ props.proposta?.titulo }}
                    </h3>
                </div>
                
                <div class="status-badge-large">
                    <v-icon size="22" color="white">
                        {{ 
                            props.proposta?.statusProposta === 'PENDENTE' ? 'mdi-clock-outline' :
                            props.proposta?.statusProposta === 'ACEITA' ? 'mdi-check-circle' :
                            props.proposta?.statusProposta === 'RECUSADA' ? 'mdi-close-circle' :
                            props.proposta?.statusProposta === 'CONCLUIDA' ? 'mdi-check-all' :
                            props.proposta?.statusProposta === 'CANCELADA' ? 'mdi-cancel' : 'mdi-file-document'
                        }}
                    </v-icon>
                    <span class="status-text">
                        {{ props.proposta?.statusProposta }}
                    </span>
                </div>
            </div>

            <div class="descricao-container mb-4">
                <div class="descricao-label d-flex align-center mb-1">
                    <v-icon color="white" size="16" class="mr-1">mdi-text</v-icon>
                    <span class="text-body-2 text-white font-weight-medium">Descrição da Proposta</span>
                </div>
                <p class="descricao-text text-white mb-0">
                    {{ props.proposta?.comentario }}
                </p>
            </div>
            
            <div class="d-flex flex-wrap gap-2 mb-4">
                <v-chip
                    size="small"
                    style="background: rgba(255, 255, 255, 0.15);"
                >
                    <v-icon size="14" color="white" class="mr-1">mdi-cash</v-icon>
                    <span class="text-white font-weight-medium">
                        R$ {{ props.proposta?.preco?.toFixed(2) }}
                    </span>
                </v-chip>
                
                <v-chip
                    size="small"
                    style="background: rgba(255, 255, 255, 0.15);"
                >
                    <v-icon size="14" color="white" class="mr-1">mdi-calendar</v-icon>
                    <span class="text-white font-weight-medium">
                        {{ dataFormatada }}
                    </span>
                </v-chip>
            </div>

            <div 
                v-if="mostrarDetalhesDemanda && demandaVinculada"
                class="demanda-alert mb-4"
                :style="{
                    background: demandaVinculada.statusDemanda === 'CONCLUIDA' ? 'gray' : 'gray',
                    borderLeft: demandaVinculada.statusDemanda === 'CONCLUIDA' ? '4px solid #4CAF50' : '4px solid #2196F3'
                }"
            >
                <div class="d-flex align-center mb-2">
                    <v-icon 
                        :color="demandaVinculada.statusDemanda === 'CONCLUIDA' ? 'green' : 'blue'"
                        class="mr-2"
                    >
                        {{ demandaVinculada.statusDemanda === 'CONCLUIDA' ? 'mdi-check-circle' : 'mdi-file-document-outline' }}
                    </v-icon>
                    <span class="text-body-1 font-weight-bold text-white">
                        Demanda Vinculada
                    </span>
                </div>
                
                <div class="demanda-details" v-if="!carregandoDemanda">
                    <div class="d-flex justify-space-between mb-1">
                        <span class="text-caption text-white" style="opacity: 0.9;">Título da Demanda:</span>
                        <span class="text-caption text-white font-weight-medium">{{ demandaVinculada.titulo }}</span>
                    </div>
                    
                    <div class="d-flex justify-space-between mb-1">
                        <span class="text-caption text-white" style="opacity: 0.9;">Prioridade:</span>
                        <v-tooltip
                            location="top"
                            :text="getDescricaoPrioridade(demandaVinculada.prioridade)"
                            :color="getCorTooltipPrioridade(demandaVinculada.prioridade)"
                        >
                            <template v-slot:activator="{ props: tooltipProps }">
                                <span 
                                    v-bind="tooltipProps"
                                    class="text-caption text-white font-weight-medium"
                                    style="cursor: help;"
                                >
                                    <v-icon 
                                        :color="corPrioridade(demandaVinculada.prioridade)" 
                                        size="14" 
                                        class="mr-1"
                                    >
                                        mdi-flag
                                    </v-icon>
                                    {{ labelPrioridade(demandaVinculada.prioridade) }}
                                </span>
                            </template>
                        </v-tooltip>
                    </div>
                    
                    <div class="d-flex justify-space-between mb-1">
                        <span class="text-caption text-white" style="opacity: 0.9;">Prazo:</span>
                        <span class="text-caption text-white font-weight-medium">
                            {{ prazoFormatado }}
                        </span>
                    </div>
                    
                    <div class="d-flex justify-space-between mb-1">
                        <span class="text-caption text-white" style="opacity: 0.9;">Orçamento Estimado:</span>
                        <span class="text-caption text-white font-weight-medium">
                           R$ {{ demandaVinculada.orcamentoEstimado }}
                        </span>
                    </div>
                    
                    <div class="d-flex justify-space-between">
                        <span class="text-caption text-white" style="opacity: 0.9;">Localização:</span>
                        <span class="text-caption text-white font-weight-medium">
                            <v-icon size="12" color="white" class="mr-1">mdi-map-marker</v-icon>
                            {{ demandaVinculada.localizacao }}
                        </span>
                    </div>
                </div>
                
                <div v-else class="text-center py-2">
                    <v-progress-circular
                        indeterminate
                        size="24"
                        width="2"
                        color="white"
                    />
                    <span class="text-caption text-white ml-2">Carregando demanda...</span>
                </div>
            </div>

            <div v-if="mostrarBotoesPrestador" class="actions-container">
                <div class="left-actions">
                    <v-btn
                        v-if="podeConcluir"
                        color="green"
                        rounded="lg"
                        @click="concluirProposta"
                        class="mr-2"
                        size="small"
                    >
                        Concluir
                    </v-btn>
                    
                    <v-btn
                        v-if="podeDesfazer"
                        color="warning"
                        rounded="lg"
                        @click="desfazerAcao"
                        class="mr-2"
                        size="small"
                    >
                        Reabrir
                    </v-btn>
                </div>
                
                <div class="right-actions">
                    <v-btn
                        v-if="podeCancelar"
                        color="red"
                        rounded="lg"
                        @click="cancelarProposta"
                        class="mr-2"
                        size="small"
                    >
                        Cancelar
                    </v-btn>
                    
                    <v-btn
                        v-if="permissaoPrestadorResponsavel"
                        color="white"
                        rounded="lg"
                        @click="abrirFormEdicao"
                        class="mr-2"
                        size="small"
                    >
                        Editar
                    </v-btn>
                    
                    <v-btn 
                        text 
                        color="white" 
                        rounded="lg" 
                        @click="emit('fechar')"
                        size="small"
                    >
                        Fechar
                    </v-btn>
                </div>
            </div>
        </template>

        <template v-else>
            <EditarPropostaForm
                :prestador-id="prestadorId!"
                :proposta="props.proposta"
                @cancelar="fecharFormEdicao"
                @salvar="salvarEdicao"
            />
        </template>
    </v-card>
</template>

<style scoped>
.proposta-card {
    position: relative;
    overflow: hidden;
    min-height: 300px;
}

.dark-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0.3));
    pointer-events: none;
    z-index: 1;
}

.proposta-card > *:not(.dark-overlay) {
    position: relative;
    z-index: 2;
}

.status-badge-large {
    display: flex;
    flex-direction: column;
    align-items: center;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    padding: 8px 12px;
    min-width: 100px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(4px);
}

.status-text {
    font-size: 0.7rem;
    font-weight: 700;
    color: white;
    margin-top: 4px;
    text-align: center;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.descricao-container {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 12px;
}

.descricao-label {
    opacity: 0.9;
}

.descricao-text {
    line-height: 1.5;
    opacity: 0.95;
}

.demanda-alert {
    border-radius: 8px;
    padding: 12px;
    backdrop-filter: blur(4px);
}

.demanda-details {
    background: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    padding: 10px;
}

.actions-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    padding-top: 16px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.left-actions, .right-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.text-white {
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.gap-2 {
    gap: 8px;
}

@media (max-width: 768px) {
    .actions-container {
        flex-direction: column;
        align-items: stretch;
    }
    
    .left-actions, .right-actions {
        justify-content: center;
        width: 100%;
    }
    
    .status-badge-large {
        min-width: 80px;
        padding: 6px 8px;
    }
}

.proposta-card .flex-grow-1 {
    min-width: 0;
}

.text-truncate {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
</style>