<script setup lang="ts">
import { useAuth } from '@/composables/useAuth';
import { useRouter } from 'vue-router';
import { computed, ref } from 'vue';

const { usuario, logout } = useAuth();
const router = useRouter();
const menu = ref(false);

const tabs = computed(() => {
  if (!usuario.value) return;

  if (usuario.value?.tipoUsuario === "CLIENTE"){
    return [
      {
        id: "telaInicial",
        title: "Dashboard",
        icon: 'mdi-view-dashboard',
        route: '/dashboard'
      },
      {
        id: "demandas", 
        title: "Demandas",
        icon: 'mdi-clipboard-list',
        route: '/dashboard/demandas'
      },
      {
        id: "prestadores",
        title: "Prestadores", 
        icon: 'mdi-account-group',
        route: '/dashboard/prestadores'
      }
    ]
  }

  if (usuario.value?.tipoUsuario === "PRESTADOR"){
    return [
      {
        id: "telaInicial",
        title: "Dashboard",
        icon: 'mdi-view-dashboard',
        route: '/dashboard'
      },
      {
        id: "demandas", 
        title: "Demandas",
        icon: 'mdi-clipboard-list',
        route: '/dashboard/demandas'
      },
      {
        id: "Propostas",
        title: "Propostas", 
        icon: 'mdi-account-group',
        route: '/dashboard/propostas'
      }
    ]

  }


})

const tabAtual = computed(() => {
  const currentRoute = router.currentRoute.value.path;
  if (currentRoute === '/dashboard') return 'telaInicial';
  if (currentRoute === '/dashboard/demandas') return 'demandas';
  if (currentRoute === '/dashboard/prestadores') return 'prestadores';
  if (currentRoute === '/dashboard/propostas') return 'propostas';
  
});

const estaLogado = computed(() => usuario.value);

function sair() {
  logout();
  router.push("/login");
}

function irLogin() {
  router.push("/login");
}

function navegarPara(route: string) {
  router.push(route);
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

      <v-col cols="auto" class="d-flex align-center">
        <v-btn-toggle
          v-if="estaLogado"
          v-model="tabAtual"
          mandatory
          color="primary"
          class="mr-4"
        >
          <v-btn
            v-for="tab in tabs"
            :key="tab.id"
            :value="tab.id"
            @click="navegarPara(tab.route)"
            variant="text"
            class="mx-3"
          >
            {{ tab.title }}
          </v-btn>
        </v-btn-toggle>

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

.v-btn--active {
  color: var(--v-theme-primary) !important;
  background-color: transparent !important; 
}
</style>