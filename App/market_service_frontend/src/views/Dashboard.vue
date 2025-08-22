<script setup lang="ts">
  import { computed } from 'vue';
  import { itemListaDashboard, itemListaDashboardDemandas, itemListaDashboardPrestadores } from '@/api/itemService';
  import { useRoute } from 'vue-router';
  import { useAuth } from '@/composables/useAuth';

  const { usuario } = useAuth();
  const route = useRoute();

  const breadcrumbMap: Record<string, () => any[]> = {
    '/dashboard': itemListaDashboard,
    '/dashboard/demandas': itemListaDashboardDemandas,
    '/dashboard/prestadores': itemListaDashboardPrestadores
  };

  const breadcrumbItems = computed(() =>
    breadcrumbMap[route.path] ? breadcrumbMap[route.path]() : []
  );

</script>

<template>
  <v-container fluid class="pa-4">
    <v-row>
      <v-col>
        <v-breadcrumbs :items="breadcrumbItems" class="py-2 px-0">
          <template #prepend>
            <v-icon disabled>mdi-home</v-icon>
          </template>
          <template #divider>
            <v-icon disabled>mdi-chevron-right</v-icon>
          </template>
        </v-breadcrumbs>
      </v-col>
    </v-row>

    <v-divider class="my-1" style="height: 3px; background-color: rgba(0,0,0,0.3);"></v-divider>

    <v-row class="mt-4" dense>
      <v-col cols="12">
        <v-card class="pa-6" outlined elevation="3" style="min-height: 400px;">
          <router-view
           :usuario="usuario"
           :usuario-id="usuario?.id" 
           :tipo-usuario="(usuario?.tipoUsuario ?? 'CLIENTE') as 'CLIENTE' | 'PRESTADOR'" />
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>