<script setup lang="ts">
import { ref, computed } from 'vue';
import { useNotificacaoStore } from '@/store/notificacaoStore';
import { useAuth } from '@/composables/useAuth';
import { onMounted, onUnmounted } from 'vue';

const notificacaoStore = useNotificacaoStore();
const { usuario } = useAuth();

const menuNotificacoes = ref(false);
const dialogDetalhe = ref(false);
const notificacaoSelecionada = ref<any>(null);

const notificacoesNaoLidas = computed(() => {
  return notificacaoStore.notificacoes.filter(n => !n.lida);
});

const quantidadeNotificacoes = computed(() => {
  return notificacoesNaoLidas.value.length;
});

async function marcarComoLida(id: number) {
  await notificacaoStore.marcarComoLida(id);
}

async function marcarTodasComoLidas() {
  const promises = notificacoesNaoLidas.value.map(notificacao => 
    notificacaoStore.marcarComoLida(notificacao.id)
  );
  await Promise.all(promises);
}

function abrirDetalheNotificacao(notificacao: any) {
  notificacaoSelecionada.value = notificacao;
  dialogDetalhe.value = true;
  
  if (!notificacao.lida) {
    marcarComoLida(notificacao.id);
  }
}

function fecharDetalheNotificacao() {
  dialogDetalhe.value = false;
  notificacaoSelecionada.value = null;
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

defineExpose({
  carregarNotificacoes,
  marcarComoLida,
  marcarTodasComoLidas
});
</script>

<template>
  <v-menu 
    v-model="menuNotificacoes" 
    location="bottom end" 
    :close-on-content-click="false"
    content-class="notificacoes-menu"
    max-height="500"
    scrollable
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
          color="white"
          variant="text"
          size="large"
          :title="`${quantidadeNotificacoes} notificação${quantidadeNotificacoes !== 1 ? 'es' : ''}`"
        />
      </v-badge>
    </template>

    <v-card min-width="500" max-width="500" class="overflow-hidden">
      <v-toolbar color="primary" density="compact" class="px-3">
        <v-toolbar-title class="text-white d-flex align-center">
          Notificações
          <span v-if="quantidadeNotificacoes > 0" class="text-caption ml-2">
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
        
        <v-btn
          icon="mdi-close"
          variant="text"
          color="white"
          size="small"
          @click="menuNotificacoes = false"
          title="Fechar"
        />
      </v-toolbar>

      <v-divider></v-divider>

      <div class="notificacoes-container" style="max-height: 450px; overflow-y: auto;">
        <v-list class="pa-0" v-if="notificacaoStore.notificacoes.length > 0">
          <v-list-item
            v-for="notificacao in notificacaoStore.notificacoes"
            :key="notificacao.id"
            :class="{ 
              'bg-blue-lighten-5': !notificacao.lida, 
              'cursor-pointer': true,
              'border-bottom': true 
            }"
            @click="abrirDetalheNotificacao(notificacao)"
            class="py-3 px-4"
          >
            <template #prepend>
              <div class="mr-3">
                <v-icon
                  :color="notificacao.lida ? 'grey' : 'primary'"
                  :icon="notificacao.lida ? 'mdi-check-circle' : 'mdi-bell-ring'"
                  size="20"
                />
              </div>
            </template>

            <div class="d-flex flex-column w-100">
              <div class="d-flex justify-space-between align-start">
                <v-list-item-title class="text-body-1 font-weight-medium mb-1">
                  {{ notificacao.mensagem }}
                </v-list-item-title>
                
                <v-chip
                  v-if="!notificacao.lida"
                  size="x-small"
                  color="orange"
                  text-color="white"
                  class="ml-2"
                >
                  Nova
                </v-chip>
              </div>
              
              <v-list-item-subtitle class="text-caption text-medium-emphasis">
                <v-icon size="12" class="mr-1">mdi-clock</v-icon>
                {{ notificacao.dataCriacaoNotificacao }}
              </v-list-item-subtitle>
            </div>

            <template #append>
              <v-btn
                v-if="!notificacao.lida"
                icon="mdi-check"
                size="x-small"
                variant="text"
                color="success"
                @click.stop="marcarComoLida(notificacao.id)"
                title="Marcar como lida"
                class="ml-2"
              />
            </template>
          </v-list-item>
        </v-list>

        <div v-else class="text-center text-medium-emphasis py-8">
          <v-icon size="64" color="grey-lighten-2" class="mb-4">mdi-bell-off</v-icon>
          <h3 class="text-h6 text-medium-emphasis mb-2">Nenhuma notificação</h3>
          <p class="text-body-2 text-medium-emphasis">Você não tem notificações no momento.</p>
        </div>
      </div>
    </v-card>
  </v-menu>

  <!--detalhe-->
  <v-dialog 
    v-model="dialogDetalhe" 
    max-width="500px"
    persistent
  >
    <v-card v-if="notificacaoSelecionada">
      <v-toolbar color="primary" density="compact" class="px-3">
        <v-toolbar-title class="text-white d-flex align-center">
          <v-icon class="mr-2">mdi-bell</v-icon>
          Notificação
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn
          icon
          color="white"
          @click="fecharDetalheNotificacao"
          size="small"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-toolbar>

      <v-card-text class="pa-5">
        <div class="d-flex align-center mb-4">
          <v-chip
            v-if="!notificacaoSelecionada.lida"
            color="orange"
            text-color="white"
            class="mr-2"
          >
            Nova
          </v-chip>
          <v-chip
            v-else
            color="green"
            text-color="white"
            class="mr-2"
          >
            Lida
          </v-chip>
          <span class="text-caption text-medium-emphasis ml-auto">
            <v-icon size="14" class="mr-1">mdi-clock</v-icon>
            {{ notificacaoSelecionada.dataCriacaoNotificacao }}
          </span>
        </div>

        <div class="notificacao-conteudo">
          <div class="text-body-1 mb-4 pa-3 bg-grey-lighten-4 rounded-lg">
            {{ notificacaoSelecionada.mensagem }}
          </div>
        </div>
      </v-card-text>

      <v-card-actions class="pa-4">
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          variant="text"
          @click="fecharDetalheNotificacao"
          class="mr-2"
        >
          Fechar
        </v-btn>
        <v-btn
          v-if="!notificacaoSelecionada.lida"
          color="success"
          variant="flat"
          @click="marcarComoLida(notificacaoSelecionada.id); fecharDetalheNotificacao()"
          prepend-icon="mdi-check"
        >
          Marcar como Lida
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.notificacoes-menu {
  max-width: 500px;
}

.notificacoes-container::-webkit-scrollbar {
  width: 8px;
}

.notificacoes-container::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 4px;
}

.notificacoes-container::-webkit-scrollbar-thumb {
  background: #bdbdbd;
  border-radius: 4px;
}

.notificacoes-container::-webkit-scrollbar-thumb:hover {
  background: #9e9e9e;
}

.border-bottom {
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.bg-blue-lighten-5 {
  background-color: #e3f2fd !important;
}

.v-list-item:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

.cursor-pointer {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.notificacao-conteudo {
  min-height: 100px;
}
</style>