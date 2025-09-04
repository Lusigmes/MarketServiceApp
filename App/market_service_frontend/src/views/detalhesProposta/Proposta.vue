<script setup lang="ts">
import type { PropostaResponseInterface } from '@/types';
import { corStatusProposta } from '@/utils/labelsUtils';

    interface Props{
        proposta: PropostaResponseInterface;
    }
    const props = defineProps<Props>();
</script>
<template>
    <v-card :class="corStatusProposta(props.proposta.statusProposta)"
        class="pa-4 rounded-lg elevation-0"
    >
        <h1>{{ props.proposta.titulo }}</h1>
        <h1>{{ props.proposta.comentario }}</h1>
        <h1>{{ props.proposta.prestadorId }}</h1>

    </v-card>
</template>