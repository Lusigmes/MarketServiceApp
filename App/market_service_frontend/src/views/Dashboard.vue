<script setup lang="ts">
import { useAuth } from '@/composables/useAuth';
import { useRouter } from 'vue-router';
import { itemListaDashboard } from '@/api/itemService';
import { reactive, ref } from 'vue';

const { usuario, logout } = useAuth();
const router = useRouter();

function sair() {
  logout();
  router.push("/login");
}

const breadcrumbItems = reactive(itemListaDashboard());
const menu = ref(false); 
</script>


<template>
  <v-container fluid class="pa-4">

    <!-- Header  -->
    <v-sheet elevation="4" rounded="lg" class="pa-4 mb-4" color="grey lighten-4">
      <v-row class="align-center justify-space-between">
        <v-col>
          <h2 class="text-h5 mb-0">Bem-vindo, {{ usuario?.nome }}</h2>
        </v-col>

        <v-col cols="auto">
          <v-menu v-model="menu" location="bottom end">
            <template #activator="{ props }">
              <v-btn v-bind="props" icon="mdi-account-circle" color="primary" variant="text"></v-btn>
            </template>

            <v-list>
              <v-list-item>
                <v-list-item-title>{{ usuario?.nome }}</v-list-item-title>
                <v-list-item-subtitle>{{ usuario?.email }}</v-list-item-subtitle>
              </v-list-item>

              <v-divider></v-divider>

              <v-list-item @click="router.push('/configuracoes')">
                <v-list-item-title>Editar / Configurações</v-list-item-title>
              </v-list-item>

              <v-list-item @click="sair">
                <v-list-item-title class="text-error">Logout</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </v-col>
      </v-row>
    </v-sheet>

    <v-divider class="my-1" style="height: 3px; background-color: rgba(0,0,0,0.3);"></v-divider>

    <!-- Breadcrumb -->
    <v-row>
      <v-col>
        <v-breadcrumbs :items="breadcrumbItems" class="py-2 px-0">
          <template #prepend>
            <v-icon>mdi-home</v-icon>
          </template>
          <template #divider>
            <v-icon>mdi-chevron-right</v-icon>
          </template>
        </v-breadcrumbs>
      </v-col>
    </v-row>

    <v-divider class="my-1" style="height: 3px; background-color: rgba(0,0,0,0.3);"></v-divider>

    <!-- Conteúdo -->
    <v-row class="mt-4" dense>
      <v-col cols="12" md="6">
        <v-card class="pa-6" outlined elevation="3" style="min-height: 300px;">
          <h3 class="text-h6 mb-2">Demandas</h3>
          <p>Aqui você verá as demandas em aberto.</p>
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card class="pa-6" outlined elevation="3" style="min-height: 300px;">
          <h3 class="text-h6 mb-2">Prestadores Disponíveis</h3>
          <p>Aqui você verá os prestadores cadastrados e ativos.</p>
        </v-card>
      </v-col>
    </v-row>

  </v-container>
</template>

<style scoped>
h2 {
  font-weight: 500;
}

.v-divider {
  background-color: rgba(0, 0, 0, 0.2);
}
</style>
