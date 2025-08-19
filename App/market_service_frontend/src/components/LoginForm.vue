<script setup lang="ts">
import { itemListaLogin } from '@/api/itemService';
import { useAuth } from '@/composables/useAuth';
import type { LoginUsuarioInterface } from '@/types/usuario';
import { reactive } from 'vue';
import { useRouter } from 'vue-router';

    const itemBreadCrumb = reactive(itemListaLogin());
    const router = useRouter();
    const {login} = useAuth();

    const dadosLogin = reactive<LoginUsuarioInterface>({
        email:"",
        senha:""
    });

    async function fazerLogin(){
        const sucesso = await login(dadosLogin);
        if(sucesso){
            router.push("/dashboard")
        }else{
            alert("Falha no login")
        }
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

  <v-container class="fill-height d-flex justify-center align-center" fluid>
    <v-card class="pa-6" width="360" elevation="6">
      <h2 class="text-center mb-4" style="color:#1a1a1a;">Login</h2>
      <v-form @submit.prevent="fazerLogin">
        <v-text-field v-model="dadosLogin.email" label="E-mail" type="email" outlined dense class="mb-3" />
        <v-text-field v-model="dadosLogin.senha" label="Senha" type="password" outlined dense class="mb-3" />

        <v-btn type="submit" color="primary" class="mt-4" block>Entrar</v-btn>
      </v-form>
    </v-card>
  </v-container>
</template>

<style scoped>
.v-card {
  border-radius: 12px;
}
</style>