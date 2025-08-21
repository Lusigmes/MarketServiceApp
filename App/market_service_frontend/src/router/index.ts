import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const routes = [
  { path: "/", name: "Home", component: () => import('@/components/Home.vue') },
  { path: "/login", name: "Login", component: () => import('@/components/LoginForm.vue') },
  { path: "/registro", name: "Registro", component: () => import('@/components/RegistroForm.vue') },
  { 
      path: "/dashboard", 
      name: "Dashboard", 
      component: () => import('@/views/Dashboard.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'TelaInicial',
          component: () => import('@/views/TelaInicial.vue')
        },
        {
          path: 'demandas',
          name: 'TodasDemandas',
          component: () => import('@/views/ListaDemandas.vue')
        },
        {
          path: 'prestadores',
          name: 'TodosPrestadores',
          component: () => import('@/views/ListaPrestadores.vue')
        }
      ]
    }
]
/*
// No router/index.ts
{
  path: '/demanda/:id',
  name: 'DemandaDetalhe',
  component: () => import('@/components/DemandaDetalhe.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/prestador/:id', 
  name: 'PrestadorDetalhe',
  component: () => import('@/components/PrestadorDetalhe.vue'),
  meta: { requiresAuth: true }
}
*/
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
  if(token.value && to.path === '/') {
    return "/dashboard";
  }
  if(token.value && (to.path === '/login' || to.path === '/registro')){
    return "/dashboard";
  }

  return true; 
})

export default router
