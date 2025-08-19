import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const routes = [
  { path: "/", name: "Home", component: () => import('@/components/Home.vue') },
  { path: "/login", name: "Login", component: () => import('@/components/LoginForm.vue') },
  { path: "/registro", name: "Registro", component: () => import('@/components/RegistroForm.vue') },
  { path: "/dashboard", name: "Dashboard", component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } }
]

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

  return true; 
})

export default router
