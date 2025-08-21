<template>
  <div>
    <h2 class="text-h4 mb-6">Dashboard</h2>
    
    <v-row>
      <v-col cols="12" md="6">
        <v-card class="pa-4" elevation="2">
          <v-card-title class="text-h6">Bem-vindo ao Sistema</v-card-title>
          <v-card-text>
            <p>Esta é a tela inicial do seu dashboard. Aqui você pode adicionar:</p>
            <ul>
              <li>Estatísticas resumidas</li>
              <li>Gráficos de desempenho</li>
              <li>Últimas atividades</li>
              <li>Notificações importantes</li>
            </ul>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6">
        <v-card class="pa-4" elevation="2">
          <v-card-title class="text-h6">Acesso Rápido</v-card-title>
          <v-card-text>
            <p>Use as abas acima para acessar:</p>
            <ul>
              <li><strong>Demandas</strong>: Visualize e gerencie todas as demandas</li>
              <li><strong>Prestadores</strong>: Gerencie prestadores de serviço</li>
            </ul>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>