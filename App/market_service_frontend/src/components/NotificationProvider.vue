<script setup lang="ts">
    import { useNotification } from '@/composables/useNotification';
    const {notificationBar} = useNotification();
</script>
<template>
  <v-snackbar
    v-model="notificationBar.show"
    :color="notificationBar.color"
    timeout="3000"
    location="top right"
    rounded
  >
    {{ notificationBar.message }}
    <template #actions>
      <v-btn icon @click="notificationBar.show = false">
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </template>
  </v-snackbar>
</template>