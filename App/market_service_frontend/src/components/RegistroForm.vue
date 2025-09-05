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
            <v-text-field 
              v-model="usuario.nome" 
              label="Nome" 
              outlined 
              dense 
              :error-messages="errors.nome"
            />
          </v-col>
          <v-col cols="6">
            <v-select
              v-model="usuario.tipoUsuario"
              :items="['CLIENTE', 'PRESTADOR']"
              label="Tipo"
              outlined
              dense
              :error-messages="errors.tipoUsuario"
            />
          </v-col>
        </v-row>

        <v-text-field 
          v-model="usuario.cpf" 
          label="CPF" 
          outlined 
          dense 
          class="mb-3" 
          v-mask="'###.###.###-##'"
          :error-messages="errors.cpf"
        />
        
        <v-text-field 
          v-model="usuario.email" 
          label="E-mail" 
          type="email" 
          outlined 
          dense 
          class="mb-3" 
          :error-messages="errors.email"
        />
        
        <v-text-field 
          v-model="usuario.senha" 
          label="Senha" 
          type="password" 
          outlined 
          dense 
          class="mb-3" 
          :error-messages="errors.senha"
        />
        
        <v-text-field 
          v-model="usuario.cep" 
          label="CEP" 
          outlined 
          dense 
          class="mb-3" 
          v-mask="'#####-###'"
          :error-messages="errors.cep"
        />

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
import type { RegistroUsuarioInterface } from '@/types';
import { itemListaRegistro } from '@/api/itemService';
import * as yup from 'yup'
import { validarCPF } from '@/utils/validarCPF';

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
  const errors = reactive({
      nome: "",
      cpf: "",
      email: "",
      senha: "",
      cep: "",
      tipoUsuario: ""
  });
  
  const schema = yup.object({
    nome: yup.string()
      .required('Nome é obrigatório')
      .min(2, 'Nome deve ter pelo menos 2 caracteres')
      .max(100, 'Nome deve ter no máximo 100 caracteres'),
    cpf: yup.string()
      .required('CPF é obrigatório')
      .transform(value => value ? value.replace(/\D/g, '') : '') 
      .length(11, 'CPF deve conter exatamente 11 números')
      .test('cpf-valido', 'CPF inválido', (value) => {
        const resultado = validarCPF(value);
        return resultado;
      }),
    email: yup.string()
      .required('E-mail é obrigatório')
      .email('E-mail deve ser válido'),
    senha: yup.string()
    .required('Senha é obrigatória')
    .min(6, 'Senha deve ter pelo menos 6 caracteres'),
    cep: yup.string()
      .transform(value => value.replace(/\D/g, ''))
      .required('CEP é obrigatório')
      .length(8, 'CEP deve conter exatamente 8 números'),
    tipoUsuario: yup.string()
     .required('Tipo de usuário é obrigatório')
      .oneOf(['CLIENTE', 'PRESTADOR'], 'Tipo de usuário deve ser CLIENTE ou PRESTADOR')
  });

  async function registrarUsuario(){
    
    try {
      const usuarioParaEnviar = {
        ...usuario,
        cpf: usuario.cpf.replace(/\D/g, ''),
        cep: usuario.cep.replace(/\D/g, ''),
      };
      const valido = await validarForm();
      if(!valido) return;

      await registro(usuarioParaEnviar);
      alert("Registro realizado com sucesso!");
      router.push("/dashboard")
    } catch (error) {
      console.error(error);
      alert("Erro ao registrar usuário");
    }
  };

  const validarForm = async(): Promise<boolean> => {
    try {
      await schema.validate(usuario, { abortEarly: false });
    
      Object.keys(errors).forEach(key => { errors[key as keyof typeof errors] = ''; });
      return true;
    } catch (erro: any) {
      Object.keys(errors).forEach(key => { errors[key as keyof typeof errors] = ''; });
      
      if (erro.inner) {
        erro.inner.forEach((e: yup.ValidationError) => {
          const field = e.path as keyof typeof errors;
          if (field in errors) {
            errors[field] = e.message;
          }
        });
      }
      
      return false;
    }
  }


</script>