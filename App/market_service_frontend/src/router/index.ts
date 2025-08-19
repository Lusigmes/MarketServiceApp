import LoginForm from '@/components/LoginForm.vue';
import RegistroForm from '@/components/RegistroForm.vue'
import { useAuth } from '@/composables/useAuth';
import Dashboard from '@/views/Dashboard.vue';
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {path: "/login", component: LoginForm},
  {path: "/registro", component: RegistroForm},
  {path: "/dashboard",
    component: Dashboard,
    meta: {requiresAuth:true}
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach(async (to) => {
  const { token, fetchUsuario, usuario } = useAuth();
  
  if (token.value && !usuario.value) {
      await fetchUsuario();
    }

  if (to.meta.requiresAuth && !token.value) {
    return "/login";
  }
});


export default router
