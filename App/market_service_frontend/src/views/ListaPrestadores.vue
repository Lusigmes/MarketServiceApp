<script setup lang="ts">

  interface Props {
    usuario: any;
    usuarioId: number;
    tipoUsuario: 'CLIENTE' | 'PRESTADOR';
  }


  
</script>

<template>
    <v-container>
        <v-row justify="end" class="mb-4">
            <v-col cols="auto">
                <v-select
                    label="Ordenar por"
                    :items="[
                        { text: 'Nome (A-Z)', value: 'usuario.nome,asc' },
                        { text: 'Nome (Z-A)', value: 'usuario.nome,desc' },
                        { text: 'Email (A-Z)', value: 'usuario.email,asc' },
                    ]"
                    @update:model-value="mudarOrdenacao"
                    density="compact"
                    variant="outlined"
                />
            </v-col>
        </v-row>

        <v-row justify="center" v-if="loading">
            <v-progress-circular indeterminate color="primary" />
        </v-row>

        <v-row v-else dense>
            <v-col
                v-for="prestador in prestadores"
                :key="prestador.id"
                cols="12" sm="6" md="4"
            >
                <v-card 
                    class="pa-3"
                    rounded="lg"
                    elevation="4"
                    @click="abrirModalDetalhe(prestador)"
                    style="cursor: pointer;"
                >
                    <div class="d-flex justify-space-between align-center mb-2">
                        <h3 class="text-h6 text-truncate">{{ prestador.usuario.nome }}</h3>
                        <v-chip size="small" color="primary" text-color="white">
                            Prestador
                        </v-chip>
                    </div>

                    <p class="text-body-2 text-blue-darken-4 mb-2">
                        {{ prestador.usuario.email }}
                    </p>

                    <div class="d-flex flex-wrap gap-2">
                        <v-chip size="small" variant="outlined" color="indigo-darken-4">
                            {{ prestador.usuario.tipoUsuario }}
                        </v-chip>
                        <v-chip size="small" variant="outlined" color="grey-darken-4">
                            CEP: {{ prestador.usuario.cep }}
                        </v-chip>
                    </div>
                </v-card>
            </v-col>
        </v-row>

        <v-row justify="center" class="mt-4">
            <v-pagination
                :length="totalPages"
                :model-value="page + 1"
                @update:model-value="mudarPagina"
                color="primary"
                size="small"
                rounded
            />
        </v-row>

        <v-dialog v-model="dialogDetalhe" max-width="500">
            <v-card v-if="prestadorAtual">
                <v-card-title class="d-flex justify-space-between align-center">
                    <span>Detalhes do Prestador</span>
                    <v-btn icon @click="fecharModalDetalhe">
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                </v-card-title>

                <v-card-text>
                    <v-list>
                        <v-list-item>
                            <v-list-item-title>Nome</v-list-item-title>
                            <v-list-item-subtitle>{{ prestadorAtual.usuario.nome }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                            <v-list-item-title>Email</v-list-item-title>
                            <v-list-item-subtitle>{{ prestadorAtual.usuario.email }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                            <v-list-item-title>CEP</v-list-item-title>
                            <v-list-item-subtitle>{{ prestadorAtual.usuario.cep }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                            <v-list-item-title>Tipo de Usuário</v-list-item-title>
                            <v-list-item-subtitle>{{ prestadorAtual.usuario.tipoUsuario }}</v-list-item-subtitle>
                        </v-list-item>
                    </v-list>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" @click="fecharModalDetalhe">Fechar</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>