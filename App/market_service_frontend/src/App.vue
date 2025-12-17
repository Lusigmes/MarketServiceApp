<template>
  <v-app>
    <Toolbar />

    <v-main>
      <router-view />
    </v-main>
    
    <Footer />

    <NotificationProvider/>
  </v-app>
</template>

<script setup lang="ts">
import Footer from '@/layouts/Footer.vue';
import Toolbar from '@/layouts/Toolbar.vue';
import NotificationProvider from './components/NotificationProvider.vue';

</script>