<script setup lang="ts">
import { useAuth } from '@/composables/useAuth';
import { useRouter } from 'vue-router';
import { ref } from 'vue';

const { usuario, logout } = useAuth();
const router = useRouter();
const menu = ref(false);

function sair() {
  logout();
  router.push("/login");
}

function irLogin() {
  router.push("/login");
}
</script>

<template>
  <v-sheet elevation="4" rounded="lg" class="pa-4 mb-4" color="grey lighten-4">
    <v-row class="align-center justify-space-between">
      <v-col>
        <h2 class="text-h5 mb-0">
          Bem-vindo<span v-if="usuario">, {{ usuario?.nome }}</span>
        </h2>
      </v-col>

      <v-col cols="auto">
        <v-menu v-model="menu" location="bottom end" content-class="menu-custom">
          <template #activator="{ props }">
            <v-btn
              v-bind="props"
              icon="mdi-account-circle"
              color="primary"
              variant="text"
            />
          </template>

          <v-list>
            <template v-if="usuario">
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
            </template>

            <template v-else>
                <v-list-item @click="irLogin">
                    <v-btn block color="primary" variant="elevated" class="rounded-lg text-white">
                        Entrar
                    </v-btn>
                </v-list-item>
            </template>
          </v-list>
        </v-menu>
      </v-col>
    </v-row>
  </v-sheet>
</template>

<style lang="css" scoped>
.menu-custom {
  min-width: 250px; 
  padding: 12px; 
}

</style>