<template>
  <v-app>
    <Toolbar />

    <v-main>
      <router-view />
    </v-main>

    <Footer />
  </v-app>
</template>

<script setup lang="ts">
import Footer from '@/layouts/Footer.vue';
import Toolbar from '@/layouts/Toolbar.vue';
</script>