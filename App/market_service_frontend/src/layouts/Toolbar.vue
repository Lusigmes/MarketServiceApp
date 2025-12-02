<script setup lang="ts">
import { useAuth } from '@/composables/useAuth';
import { useRouter } from 'vue-router';
import { computed, ref } from 'vue';
import type { UsuarioResponseInterface } from '@/types';
import EditarUsuarioForm from '@/components/EditarUsuarioForm.vue';
import MinhasAvaliacoesModal from '@/views/detalhesAvaliacoes/MinhasAvaliacoesModal.vue';
import Notificacao from './Notificacao.vue';
import ToolbarInfo from './ToolbarInfo.vue';

const { usuario, logout } = useAuth();
const router = useRouter();
const menu = ref(false);
const dialogEditarUsuario = ref(false);
const minhasAvaliacoesModal = ref();

const tabs = computed(() => {
  if (!usuario.value) return;

  if (usuario.value?.tipoUsuario === "CLIENTE"){
    return [
      {
        id: "demandas", 
        title: "Demandas",
        icon: 'mdi-clipboard-list',
        route: '/dashboard'
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
        id: "demandas", 
 
        title: "Demandas",
        icon: 'mdi-clipboard-list',
        route: '/dashboard'
      },
      {
        id: "propostas",
        title: "Propostas", 
        icon: 'mdi-clipboard-list',
        route: '/dashboard/propostas'
      }
    ]
  }
});

const tabAtual = computed(() => {
  const currentRoute = router.currentRoute.value.path;
  if (currentRoute === '/dashboard' || 
    currentRoute.startsWith('/dashboard/demandas')) return 'demandas';
  if (currentRoute === '/dashboard/prestadores') return 'prestadores';
  if (currentRoute === '/dashboard/propostas') return 'propostas';
});

const estaLogado = computed(() => usuario.value);
const ehPrestador = computed(() => usuario.value?.tipoUsuario === 'PRESTADOR');

function abrirMinhasAvaliacoes(){
  minhasAvaliacoesModal.value?.abrir();
  menu.value = false;
}

function sair() {
  logout();
  router.push("/");
}

function irLogin() {
  router.push("/");
}

function navegarPara(route: string) {
  router.push(route);
}

function abrirFormEdicaoUsuario(){
  dialogEditarUsuario.value = true;
  menu.value = false;
}

async function handleUsuarioEditado(usuarioAtt: Partial<UsuarioResponseInterface>){
  dialogEditarUsuario.value = false;
  if(usuario.value){
    usuario.value = {...usuario.value, ...usuarioAtt};
  }
}

function fecharFormEdicaoUsuario(){
  dialogEditarUsuario.value = false;
}
</script>

<template>
  <v-sheet 
    elevation="4" 
    rounded="lg" 
    class="pa-4 mb-4 toolbar-container" 
    color="blue-darken-3"
  >
    <v-row class="align-center justify-space-between">
      <v-col>
        <h2 class="text-h5 mb-0 text-white font-weight-bold">
          Bem-vindo<span v-if="usuario">, {{ usuario?.nome }}</span>
        </h2>
        <p class="text-caption text-blue-lighten-4 mt-1 mb-0">
          Sistema de Gestão de Serviços
        </p>
      </v-col>

      <v-col cols="auto" class="d-flex align-center gap-2">
        <v-btn-toggle
          v-if="estaLogado"
          v-model="tabAtual"
          mandatory
          color="white"
          class="mr-4 tab-navigation"
        >
          <v-btn
            v-for="tab in tabs"
            :key="tab.id"
            :value="tab.id"
            @click="navegarPara(tab.route)"
            variant="text"
            class="mx-2 text-white"
            :class="{ 'tab-active': tabAtual === tab.id }"
          >
            <v-icon :icon="tab.icon" class="mr-2" />
            {{ tab.title }}
          </v-btn>
        </v-btn-toggle>

        <ToolbarInfo 
          v-if="estaLogado && usuario?.tipoUsuario" 
        />

        <div class="d-flex align-center gap-2">
          <Notificacao v-if="estaLogado" />

          <v-menu 
            v-model="menu" 
            location="bottom end" 
            content-class="user-menu"
            :close-on-content-click="true"
          >
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-account-circle"
                color="white"
                variant="text"
                size="large"
                class="user-avatar-btn"
              />
            </template>

            <v-card width="300" class="user-menu-card">
              <v-card-text class="pa-4 user-info">
                <div class="d-flex align-center mb-3">
                  <v-avatar color="blue-darken-3" size="56" class="mr-3">
                    <span class="text-white text-h6">
                      {{ usuario?.nome?.charAt(0).toUpperCase() }}
                    </span>
                  </v-avatar>
                  <div>
                    <h3 class="text-h6 font-weight-bold text-blue-darken-4 mb-1">
                      {{ usuario?.nome }}
                    </h3>
                    <p class="text-body-2 text-medium-emphasis mb-0">
                      {{ usuario?.email }}
                    </p>
                    <v-chip 
                      size="small" 
                      :color="usuario?.tipoUsuario === 'PRESTADOR' ? 'green' : 'blue'" 
                      text-color="white"
                      class="mt-1"
                    >
                      {{ usuario?.tipoUsuario }}
                    </v-chip>
                  </div>
                </div>
              </v-card-text>

              <v-divider class="mx-4"></v-divider>

              <v-list density="comfortable" class="py-2">
                <v-list-item 
                  v-if="ehPrestador" 
                  @click="abrirMinhasAvaliacoes"
                  class="menu-item"
                >
                  <template #prepend>
                    <v-icon color="blue-darken-3">mdi-star</v-icon>
                  </template>
                  <v-list-item-title class="text-body-1 font-weight-medium">
                    Minhas Avaliações
                  </v-list-item-title>
                </v-list-item>

                <v-list-item 
                  @click="abrirFormEdicaoUsuario"
                  class="menu-item"
                >
                  <template #prepend>
                    <v-icon color="blue-darken-3">mdi-account-edit</v-icon>
                  </template>
                  <v-list-item-title class="text-body-1 font-weight-medium">
                    Editar Perfil
                  </v-list-item-title>
                </v-list-item>

                <v-divider class="my-2 mx-4"></v-divider>

                <v-list-item 
                  @click="sair"
                  class="menu-item logout-item"
                >
                  <template #prepend>
                    <v-icon color="red">mdi-logout</v-icon>
                  </template>
                  <v-list-item-title class="text-body-1 font-weight-medium text-red">
                    Sair
                  </v-list-item-title>
                </v-list-item>
              </v-list>
            </v-card>
          </v-menu>
        </div>
      </v-col>
    </v-row>
  </v-sheet>
 
  <v-dialog 
    v-model="dialogEditarUsuario" 
    max-width="600px"
    persistent
    :scrollable="false"
  >
    <v-card>
      <EditarUsuarioForm
        v-if="dialogEditarUsuario && usuario"
        :usuario="usuario"
        @salvar="handleUsuarioEditado"
        @cancelar="fecharFormEdicaoUsuario"
      />
    </v-card>
  </v-dialog>

  <MinhasAvaliacoesModal ref="minhasAvaliacoesModal" />
</template>

<style scoped>
.toolbar-container {
  background: linear-gradient(135deg, #1976d2 0%, #0d47a1 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(13, 71, 161, 0.3);
}

.tab-navigation {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 4px;
}

.tab-navigation .v-btn {
  border-radius: 8px;
  transition: all 0.3s ease;
  opacity: 0.8;
}

.tab-navigation .v-btn:hover {
  background-color: rgba(255, 255, 255, 0.15);
  opacity: 1;
  transform: translateY(-1px);
}

.tab-active {
  background-color: rgba(255, 255, 255, 0.2) !important;
  opacity: 1 !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.user-avatar-btn {
  transition: transform 0.3s ease;
}

.user-avatar-btn:hover {
  transform: scale(1.1);
}

.d-flex.align-center.gap-2 {
  gap: 12px;
}

:deep(.user-menu) {
  border-radius: 12px !important;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15) !important;
}

:deep(.user-menu-card) {
  border-radius: 12px !important;
  overflow: hidden;
  border: 1px solid #e0e0e0;
}

:deep(.user-info) {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
}

:deep(.menu-item) {
  border-radius: 8px;
  margin: 4px 12px;
  transition: all 0.2s ease;
}

:deep(.menu-item:hover) {
  background-color: #e3f2fd !important;
  transform: translateX(4px);
}

:deep(.logout-item:hover) {
  background-color: #ffebee !important;
}

:deep(.v-list-item--density-comfortable:not(.v-list-item--nav).v-list-item--one-line) {
  min-height: 48px;
  padding: 8px 12px;
}

:deep(.v-list-item__prepend) {
  margin-right: 12px;
}

:global(.v-btn--active) {
  color: white !important;
  font-weight: 600 !important;
}
</style>