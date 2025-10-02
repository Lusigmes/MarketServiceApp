<script setup lang="ts">
import { useAuth } from '@/composables/useAuth';
import { useRouter } from 'vue-router';
import { computed, ref, onMounted, onUnmounted } from 'vue';
import { useNotificacaoStore } from '@/store/notificacaoStore';

const { usuario, logout } = useAuth();
const router = useRouter();
const menu = ref(false);
const menuNotificacoes = ref(false);
const notificacaoStore = useNotificacaoStore();

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
});

const tabAtual = computed(() => {
  const currentRoute = router.currentRoute.value.path;
  if (currentRoute === '/dashboard') return 'telaInicial';
  if (currentRoute === '/dashboard/demandas') return 'demandas';
  if (currentRoute === '/dashboard/prestadores') return 'prestadores';
  if (currentRoute === '/dashboard/propostas') return 'propostas';
});

const estaLogado = computed(() => usuario.value);

// Computed para notificações não lidas
const notificacoesNaoLidas = computed(() => {
  return notificacaoStore.notificacoes.filter(n => !n.lida);
});

const quantidadeNotificacoes = computed(() => {
  return notificacoesNaoLidas.value.length;
});

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

async function marcarComoLida(id: number) {
  await notificacaoStore.marcarComoLida(id);
}

async function marcarTodasComoLidas() {
  const promises = notificacoesNaoLidas.value.map(notificacao => 
    notificacaoStore.marcarComoLida(notificacao.id)
  );
  await Promise.all(promises);
}

async function carregarNotificacoes() {
  if (usuario.value?.id) {
    try {
      await notificacaoStore.carregar(usuario.value.id);
    } catch (err) {
      console.error("Erro ao carregar notificações:", err);
    }
  }
}

let intervalo: number | null = null;

onMounted(() => {
  carregarNotificacoes();
  
  intervalo = window.setInterval(() => {
    carregarNotificacoes();
  }, 30000);
});

onUnmounted(() => {
  if (intervalo) {
    clearInterval(intervalo);
  }
});
</script>

<template>
  <v-sheet elevation="4" rounded="lg" class="pa-4 mb-4" color="grey lighten-4">
    <v-row class="align-center justify-space-between">
      <v-col>
        <h2 class="text-h5 mb-0">
          Bem-vindo<span v-if="usuario">, {{ usuario?.nome }}</span>
        </h2>
      </v-col>

      <v-col cols="auto" class="d-flex align-center gap-2">
        <!-- Botão de Notificações -->
        <v-menu 
          v-if="estaLogado"
          v-model="menuNotificacoes" 
          location="bottom end" 
          :close-on-content-click="false"
          content-class="notificacoes-menu"
        >
          <template #activator="{ props }">
            <v-badge
              :content="quantidadeNotificacoes"
              :model-value="quantidadeNotificacoes > 0"
              color="error"
              class="mr-2"
            >
              <v-btn
                v-bind="props"
                icon="mdi-bell"
                color="primary"
                variant="text"
                size="large"
              />
            </v-badge>
          </template>

          <v-card min-width="400" max-width="400" max-height="400" class="overflow-hidden">
            <v-toolbar color="primary" density="compact">
              <v-toolbar-title class="text-white">
                Notificações
                <span v-if="quantidadeNotificacoes > 0" class="text-caption">
                  ({{ quantidadeNotificacoes }} não lida{{ quantidadeNotificacoes !== 1 ? 's' : '' }})
                </span>
              </v-toolbar-title>
              
              <v-spacer></v-spacer>
              
              <v-btn
                v-if="quantidadeNotificacoes > 0"
                icon="mdi-check-all"
                variant="text"
                color="white"
                size="small"
                @click="marcarTodasComoLidas"
                title="Marcar todas como lidas"
              />
            </v-toolbar>

            <v-divider></v-divider>

            <v-list class="pa-0" v-if="notificacaoStore.notificacoes.length > 0">
              <v-list-item
                v-for="notificacao in notificacaoStore.notificacoes"
                :key="notificacao.id"
                :class="{ 'bg-grey-lighten-4': !notificacao.lida }"
                class="border-b"
              >
                <v-list-item-title class="text-body-2">
                  {{ notificacao.mensagem }}
                </v-list-item-title>
                
                <v-list-item-subtitle class="text-caption text-medium-emphasis mt-1">
                  {{ new Date(notificacao.dataCriacaoNotificacao).toLocaleString('pt-BR') }}
                </v-list-item-subtitle>

                <template #append>
                  <v-btn
                    v-if="!notificacao.lida"
                    icon="mdi-check"
                    size="x-small"
                    variant="text"
                    color="success"
                    @click="marcarComoLida(notificacao.id)"
                    title="Marcar como lida"
                  />
                </template>
              </v-list-item>
            </v-list>

            <v-card-text v-else class="text-center text-medium-emphasis py-8">
              <v-icon size="48" color="grey-lighten-1" class="mb-2">mdi-bell-off</v-icon>
              <div>Nenhuma notificação</div>
            </v-card-text>
          </v-card>
        </v-menu>

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
              size="large"
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

.notificacoes-menu {
  max-width: 400px;
}

.v-btn--active {
  color: var(--v-theme-primary) !important;
  background-color: transparent !important; 
}

.gap-2 {
  gap: 8px;
}

.border-b {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}
</style>