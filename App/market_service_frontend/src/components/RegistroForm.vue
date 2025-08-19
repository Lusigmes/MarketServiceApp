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
    <v-card class="pa-6" width="400" elevation="6">
      <h2 class="text-center mb-4" style="color:#1a1a1a;">Registrar Usuário</h2>
      <v-form @submit.prevent="registrarUsuario">
        <v-row class="mb-4" dense>
          <v-col cols="6">
            <v-text-field v-model="usuario.nome" label="Nome" outlined dense />
          </v-col>
          <v-col cols="6">
            <v-select
              v-model="usuario.tipoUsuario"
              :items="['CLIENTE', 'PRESTADOR']"
              label="Tipo"
              outlined
              dense
            />
          </v-col>
        </v-row>

        <v-text-field v-model="usuario.cpf" label="CPF" outlined dense class="mb-3" />
        <v-text-field v-model="usuario.email" label="E-mail" type="email" outlined dense class="mb-3" />
        <v-text-field v-model="usuario.senha" label="Senha" type="password" outlined dense class="mb-3" />
        <v-text-field v-model="usuario.cep" label="CEP" outlined dense class="mb-3" />

        <v-btn type="submit" color="primary" class="mt-4" block>Registrar</v-btn>
      </v-form>
    </v-card>
  </v-container>
</template>

<style scoped>
.v-card {
  border-radius: 12px;
}
</style>

<script setup lang="ts">
    import { reactive } from 'vue';
    import { useRouter } from 'vue-router';
    import { useAuth } from '@/composables/useAuth';
    import type { RegistroUsuarioInterface } from '@/types/usuario';
    import { itemListaRegistro } from '@/api/itemService';
  
    const itemBreadCrumb = reactive(itemListaRegistro());
    const {registro, error} = useAuth();
    const router = useRouter();
    
    const usuario = reactive<RegistroUsuarioInterface>({
        nome: "",
        cpf: "",
        email: "",
        senha: "",
        cep: "",
        tipoUsuario: "CLIENTE"
    });
    
    async function registrarUsuario(){
        try {
            await registro(usuario);
            alert("Registro realizado com sucesso!");
            router.push("/dashboard")
        } catch (error) {
            console.error(error);
            alert("Erro ao registrar usuário");
        }
    }



</script>