<script setup lang="ts">
import { useAuth } from '@/composables/useAuth';
import { useRouter } from 'vue-router';
import { itemListaDashboard } from '@/api/itemService';
import { reactive } from 'vue';

const { usuario } = useAuth();
const router = useRouter();

const breadcrumbItems = reactive(itemListaDashboard());
</script>

<template>
  <v-container fluid class="pa-4">

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
h3 {
  font-weight: 500;
}
.v-divider {
  background-color: rgba(0, 0, 0, 0.2);
}
</style>
