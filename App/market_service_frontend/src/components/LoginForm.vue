<script setup lang="ts">
import { useAuth } from '@/composables/useAuth';
import type { LoginUsuarioInterface } from '@/types/usuario';
import { reactive } from 'vue';
import { useRouter } from 'vue-router';

    
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
  <form @submit.prevent="fazerLogin" class="form">
    <input v-model="dadosLogin.email" type="email" placeholder="E-mail" />
    <input v-model="dadosLogin.senha" type="password" placeholder="Senha" />
    <button type="submit">Entrar</button>
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