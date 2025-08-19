
<template>
  <form @submit.prevent="registrarUsuario" class="form">
    <input v-model="usuario.nome" placeholder="Nome" />
    <input v-model="usuario.cpf" placeholder="CPF" />
    <input v-model="usuario.email" type="email" placeholder="E-mail" />
    <input v-model="usuario.senha" type="password" placeholder="Senha" />
    <input v-model="usuario.cep" placeholder="CEP" />
    <select v-model="usuario.tipoUsuario">
      <option value="CLIENTE">Cliente</option>
      <option value="PRESTADOR">Prestador</option>
    </select>
    <button type="submit">Registrar</button>
  </form>
</template>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 300px;
}
</style>

<script setup lang="ts">
    import { reactive } from 'vue';
    import { useRouter } from 'vue-router';
    import { useAuth } from '@/composables/useAuth';
    import type { RegistroUsuarioInterface } from '@/types/usuario';

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