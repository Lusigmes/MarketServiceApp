import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const routes = [
  { path: "/", name: "Login", component: () => import('@/components/LoginForm.vue') },
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
          component: () => import('@/views/ListaDemandas.vue')
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
        },
        {
          path: 'propostas',
          name: 'TodasPropostasDoPrestador',
          component: () => import('@/views/ListaPropostas.vue')
        },
        {
          path: 'perfil-prestador',
          name: 'PerfilPrestador',
          component: () => import('@/views/detallhesPrestadores/PerfilPrestador.vue')
        },
        {
          path: 'aba-avaliacoes-prestador',
          name: 'AbaAvaliacoesPrestador',
          component: () => import('@/views/detalhesAvaliacoes/AbaAvaliacoesPrestador.vue')
        },
        {
          path: 'minhas-avaliacoes-modal',
          name: 'MinhasAvaliacoesModal',
          component: () => import('@/views/detalhesAvaliacoes/MinhasAvaliacoesModal.vue')
        },
        {
          path: 'demanda',
          name: 'Demanda',
          component: () => import('@/views/detalhesDemanda/Demanda.vue')
        },
        {
          path: 'proposta',
          name: 'Proposta',
          component: () => import('@/views/detalhesProposta/Proposta.vue')
        },

      ]
    }
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
    return "/";
  }
  if(token.value && to.path === '/') {
    return "/dashboard";
  }
  if(token.value && (to.path === '/' || to.path === '/registro')){
    return "/dashboard";
  }

  return true; 
})

export default router
