<script setup lang="ts">
import type { PropostaResponseInterface } from '@/types';
import { corStatusProposta } from '@/utils/labelsUtils';

    interface Props{
        proposta: PropostaResponseInterface; 
        prestadorId?: number | null;
    }

    const emit = defineEmits<{
        (e: 'fechar'): void;
    }>();

    const props = defineProps<Props>();
</script>
<template>


    <v-card :class="corStatusProposta(props.proposta.statusProposta)"
            class="pa-4 rounded-lg elevation-0"
        >        
        <v-card-title>{{ props.proposta?.titulo }}</v-card-title>
        <v-card-text>
            <p><b>Descrição:</b> {{ props.proposta?.comentario }}</p>
            <p><b>Valor:</b> R$ {{ props.proposta?.preco.toFixed(2) }}</p>
            <p><b>Status:</b> {{ props.proposta?.statusProposta }}</p>
            <p><b>Data:</b> {{ new Date(props.proposta?.dataCriacao ?? "").toLocaleDateString() }}</p>
        </v-card-text>


        <v-card-actions v-if="props.prestadorId">
            <v-btn color="primary" variant="text" @click="emit('fechar')">Fechar</v-btn>
        </v-card-actions>
    </v-card>

</template>