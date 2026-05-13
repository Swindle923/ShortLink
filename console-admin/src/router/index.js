import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/login', name: 'Login', component: () => import('@/views/LoginView.vue') },
    {
      path: '/',
      component: () => import('@/views/LayoutView.vue'),
      children: [
        { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/DashboardView.vue') },
        { path: 'users', name: 'Users', component: () => import('@/views/UsersView.vue') },
        { path: 'links', name: 'Links', component: () => import('@/views/LinksView.vue') },
        { path: 'monitoring', name: 'Monitoring', component: () => import('@/views/MonitoringView.vue') },
        { path: 'audit', name: 'Audit', component: () => import('@/views/AuditView.vue') }
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
  ]
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token');
  if (to.name === 'Login') {
    return next();
  }
  if (!token) {
    return next({ name: 'Login' });
  }
  next();
});

export default router;
