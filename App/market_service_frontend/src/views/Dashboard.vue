<script setup lang="ts">
    import { useAuth } from '@/composables/useAuth';
    import { useRouter } from 'vue-router';

    const {usuario, logout} = useAuth();
    const router = useRouter();
    function sair(){
        logout();
        router.push("/login")
    }
</script>

<template>
  <div>
    <h1>Bem-vindo, {{ usuario?.nome }}</h1>
    <p>Email: {{ usuario?.email }}</p>
    <p>Tipo: {{ usuario?.tipoUsuario }}</p>

    <button @click="sair">Sair</button>
  </div>
</template>

<style scoped></style>