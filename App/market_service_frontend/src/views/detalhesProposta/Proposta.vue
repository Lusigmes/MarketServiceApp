<script setup lang="ts">
import type { PropostaResponseInterface } from '@/types';
import { corStatusProposta } from '@/utils/labelsUtils';
import { ref, computed } from 'vue';
import EditarPropostaForm from './EditarPropostaForm.vue';
import { atualizarProposta } from '@/api/PropostaService';
import { StatusDemanda, StatusProposta } from '@/types/enums';
import { getDemandaById } from '@/api/DemandaService';
import { useNotification } from '@/composables/useNotification';

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
        (e: 'proposta-aceita-cancelada'): void;//8

    }>();

    const props = defineProps<Props>();

    const editando = ref(false); 
    const abrirFormEdicao = () => { editando.value = true; };
    const fecharFormEdicao = () => { editando.value = false; };
    
    const propostaAtualizadaRef = ref<PropostaResponseInterface | null>(null);

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
            console.log("Proposta status anterior:", props.proposta.statusProposta);
            if (props.proposta.statusProposta === StatusProposta.ACEITA) {
                console.log("Emitindo proposta-aceita-cancelada");
                emit('proposta-aceita-cancelada');
            }
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
//8
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

</script>

<template>
    <v-card 
        :class="corStatusProposta(props.proposta.statusProposta)"
        class="pa-4 rounded-lg elevation-0"
    >        
        <template v-if="!editando">
            <v-card-title class="text-h6 text-truncate" style="max-width: 70%;">{{ props.proposta?.titulo }}</v-card-title>
            <v-card-text>
                <p><b>Descrição:</b> {{ props.proposta?.comentario }}</p>
                <p><b>Valor:</b> R$ {{ props.proposta?.preco.toFixed(2) }}</p>
                <p><b>Status:</b> {{ props.proposta?.statusProposta }}</p>
                <p><b>Data:</b> {{ new Date(props.proposta?.dataCriacao ?? "").toLocaleDateString() }}</p>
            </v-card-text>

            <v-card-actions v-if="mostrarBotoesPrestador">
                <v-btn
                    v-if="podeConcluir"
                    color="success"
                    rounded
                    @click="concluirProposta"
                >
                    Concluir
                </v-btn>
                
                <v-btn
                    v-if="podeDesfazer"
                    color="success"
                    rounded
                    @click="desfazerAcao"
                >
                    Reabrir
                </v-btn>
                
                <v-btn
                    v-if="podeCancelar"
                    icon
                    color="red"
                    class="position-absolute"
                    style="top: 8px; right: 8px;"
                    @click="cancelarProposta"
                >
                    <v-icon>mdi-delete</v-icon>
                </v-btn>
                
                <v-spacer></v-spacer>

                <v-btn
                    v-if="permissaoPrestadorResponsavel"
                    color="primary"
                    rounded
                    @click="abrirFormEdicao"
                >
                    Editar
                </v-btn>
                
                <v-btn color="red" rounded variant="text" @click="emit('fechar')">
                    Fechar
                </v-btn>
            </v-card-actions>
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