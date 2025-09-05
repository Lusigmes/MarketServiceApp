import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import '@/api/connect/connect';
import 'vuetify/styles';
import '@mdi/font/css/materialdesignicons.css';
import VueTheMask from 'vue-the-mask';


const vuetify = createVuetify({
  components,
  directives,
})

createApp(App)
  .use(vuetify)
  .use(router)
  .use(VueTheMask)
  .mount('#app')
