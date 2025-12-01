<script setup lang="ts">
import { itemListaLogin } from '@/api/itemService';
import { useAuth } from '@/composables/useAuth';
import { useNotification } from '@/composables/useNotification';
import type { LoginUsuarioInterface } from '@/types';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

    const itemBreadCrumb = reactive(itemListaLogin());
    const router = useRouter();
    const {login, loading, error} = useAuth();

    const dadosLogin = reactive<LoginUsuarioInterface>({
        email:"",
        senha:""
    });
    const { showNotification } = useNotification();

    const mostrarSenha = ref(false);

    async function fazerLogin(){
        const sucesso = await login(dadosLogin);
        if(sucesso){
            router.push("/dashboard")
        }else{
          showNotification("Falha no Login.", "error");
        }
    }
function irRegistro(){
  router.push("/registro")
}
</script>


<template>
  <v-app>
    <v-main>
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

      <v-container class="fill-height d-flex justify-center align-center" fluid>
        <v-card class="pa-6" width="400" elevation="6">
          <h2 class="text-center mb-4" style="color:#1a1a1a;">Login</h2>
          
          <v-form @submit.prevent="fazerLogin">
            <v-text-field 
              v-model="dadosLogin.email" 
              label="E-mail" 
              type="email" 
              outlined 
              dense 
              class="mb-3" 
              prepend-inner-icon="mdi-email"
            />
            
            <v-text-field 
              v-model="dadosLogin.senha" 
              label="Senha" 
              :type="mostrarSenha ? 'text' : 'password'" 
              outlined 
              dense 
              class="mb-3"
              prepend-inner-icon="mdi-lock"
              :append-inner-icon="mostrarSenha ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="mostrarSenha = !mostrarSenha"
            />

            <v-btn 
              type="submit" 
              color="primary" 
              class="mt-4" 
              block
              :loading="loading"
              :disabled="loading"
            >
              <template v-if="loading">
                <v-progress-circular
                  indeterminate
                  size="20"
                  width="2"
                  class="mr-2"
                />
                Entrando...
              </template>
              <template v-else>
                <v-icon icon="mdi-login" class="mr-2" />
                Entrar
              </template>
            </v-btn>

            <v-alert
              v-if="error"
              type="error"
              variant="tonal"
              class="mt-4"
              closable
            >
              {{ error }}
            </v-alert>
          </v-form>

          <v-divider class="my-4" />

          <div class="text-center">
            <span class="text-body-2 text-medium-emphasis mr-2">
              Não tem uma conta?
            </span>
            <v-btn
              @click="irRegistro"
              variant="text"
              color="primary"
              size="small"
            >
              <v-icon icon="mdi-account-plus" class="mr-1" />
              Cadastre-se
            </v-btn>
          </div>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<style scoped>
.v-card {
  border-radius: 12px;
}
</style>