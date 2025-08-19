<script setup lang="ts">
import { itemListaHome } from '@/api/itemService';
import { reactive } from 'vue';
import { useRouter } from 'vue-router';

  const router = useRouter();
  const itemBreadCrumb = reactive(itemListaHome());

  function irLogin() {
    router.push("/login");
  }

  function irRegistro() {
    router.push("/registro");
  }
  
</script>

<template>
  <v-card elevation="0">
    <v-breadcrumbs class="ml-4 mt-0" :items="itemBreadCrumb">
      <template v-slot:prepend>
        <v-icon icon="mdi-home"></v-icon>
      </template>
      <template v-slot:divider>
        <v-icon icon="mdi-chevron-right"></v-icon>
      </template>
    </v-breadcrumbs>
  </v-card>

  <v-card class="pa-6 text-center">
    <h1>Bem-vindo ao Marketplace</h1>
    <h2>serviços variados de maneira rápida</h2>
    <div class="mt-6 flex justify-center">
      <v-btn color="primary" class="mx-2" @click="irLogin">Login</v-btn>
      <v-btn color="secondary" class="mx-2" @click="irRegistro">Registrar</v-btn>
    </div>
  </v-card>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
  background-color: #f2f2f2;
}

header {
  display: flex;
  gap: 16px;
  margin-top: 2rem;
}

header button {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  background-color: #007bff;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
}

header button:hover {
  background-color: #0056b3;
}

.welcome {
  margin-top: 5rem;
  text-align: center;
}

.welcome h1 {
  color: #1a1a1a;
}

.welcome p {
  color: #555;
  margin-top: 1rem;
}
</style>
