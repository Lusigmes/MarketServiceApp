<script setup lang="ts">
import type { PrestadorResponseInterface } from '@/types';
import AbaAvaliacoesPrestador from '../detalhesAvaliacoes/AbaAvaliacoesPrestador.vue';
import { ref } from 'vue';

interface Props {
  prestador: PrestadorResponseInterface;
}

const props = defineProps<Props>();
const abaAtiva = ref('detalhes');

const emit = defineEmits<{
  (e: 'fechar'): void;
  (e: 'abrirChat', prestador: PrestadorResponseInterface): void;
}>();

const abrirChat = () => {
  emit('abrirChat', props.prestador);
};
</script>

<template>
    <v-card rounded="lg">
        <v-card-title class="d-flex justify-space-between align-center bg-primary">
            <span class="text-white">Perfil do {{ props.prestador.usuario.nome }}</span>
            <v-btn icon @click="$emit('fechar')" variant="text" color="white">
                <v-icon>mdi-close</v-icon>
            </v-btn>
        </v-card-title>

        <v-tabs v-model="abaAtiva" background-color="transparent" class="px-4">
            <v-tab value="detalhes">Detalhes</v-tab>
            <v-tab value="avaliacoes">Avaliações</v-tab>
        </v-tabs>

        <v-divider />

        <v-window v-model="abaAtiva">
            <v-window-item value="detalhes">
                <v-card-text class="pa-4">
                    <v-row align="center" class="mb-4">
                        <v-col cols="auto">
                            <v-avatar color="primary" size="64">
                                <span class="text-white text-h5">
                                    {{ props.prestador.usuario.nome.charAt(0).toUpperCase() }}
                                </span>
                            </v-avatar>
                        </v-col>
                        <v-col>
                            <h3 class="text-h6 font-weight-bold">{{ props.prestador.usuario.nome }}</h3>
                            <h2 class="text-body-2 text-medium-emphasis font-weight-bold">{{ props.prestador.especializacao }}</h2>
                        </v-col>
                    </v-row>

                    <v-divider class="my-4" />

                    <v-list density="comfortable" rounded>
                        <v-list-item class="mb-2" rounded="lg">
                            <template #prepend>
                                <v-icon color="primary">mdi-email</v-icon>
                            </template>
                            <v-list-item-title>Email</v-list-item-title>
                            <v-list-item-subtitle>{{ props.prestador.usuario.email }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item class="mb-2" rounded="lg">
                            <template #prepend>
                                <v-icon color="primary">mdi-phone</v-icon>
                            </template>
                            <v-list-item-title>Telefone</v-list-item-title>
                            <v-list-item-subtitle>
                                {{ props.prestador.telefone || 'Não informado' }}
                            </v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item class="mb-2" rounded="lg">
                            <template #prepend>
                                <v-icon color="primary">mdi-map-marker</v-icon>
                            </template>
                            <v-list-item-title>CEP</v-list-item-title>
                            <v-list-item-subtitle>{{ props.prestador.usuario.cep }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item class="mb-2" rounded="lg">
                            <template #prepend>
                                <v-icon color="primary">mdi-account-badge</v-icon>
                            </template>
                            <v-list-item-subtitle>
                                <v-chip size="small" color="primary" variant="flat" rounded="lg">
                                    {{ props.prestador.usuario.tipoUsuario }}
                                </v-chip>
                            </v-list-item-subtitle>
                        </v-list-item>
                    </v-list>
                </v-card-text>

                <v-card-actions class="pa-4">
                    <v-btn
                        color="secondary"
                        variant="flat"
                        @click="abrirChat"
                        block
                        rounded="lg"
                    >
                        <v-icon class="mr-2">mdi-chat</v-icon>
                        Iniciar Chat
                    </v-btn>
                </v-card-actions>
            </v-window-item>

            <v-window-item value="avaliacoes">
                <v-card-text class="pa-4">
                    <AbaAvaliacoesPrestador :prestador-id="props.prestador.id" />
                </v-card-text>
            </v-window-item>
        </v-window>
    </v-card>
</template>

<style scoped>
:deep(.v-card) {
  border-radius: 12px !important;
  overflow: hidden;
}

:deep(.v-list-item) {
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

:deep(.v-list-item:hover) {
  background-color: rgba(0, 0, 0, 0.04);
}

:deep(.v-tab) {
  border-radius: 8px;
  margin: 0 4px;
}

:deep(.v-tab:hover) {
  background-color: rgba(25, 118, 210, 0.08);
}
</style>