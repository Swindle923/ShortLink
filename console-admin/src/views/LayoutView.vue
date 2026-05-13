<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { logout } from '@/api';

const route = useRoute();
const router = useRouter();
const username = computed(() => localStorage.getItem('admin_username') || 'admin');
const realName = computed(() => localStorage.getItem('admin_real_name') || '');
const activeMenu = computed(() => route.path);

const menus = [
  { path: '/dashboard', title: '运营大盘', icon: 'DataAnalysis' },
  { path: '/users', title: '用户管理', icon: 'User' },
  { path: '/links', title: '短链接管理', icon: 'Link' },
  { path: '/monitoring', title: '监控中心', icon: 'Odometer' }
];

const navigate = (path) => router.push(path);

const handleLogout = async () => {
  try {
    await logout();
  } catch (e) {}
  localStorage.clear();
  ElMessage.success('已退出登录');
  router.push('/login');
};
</script>

<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" class="side">
      <div class="logo">短链接管理员中台</div>
      <el-menu :default-active="activeMenu" @select="navigate" class="nav-menu">
        <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">
          <el-icon><component :is="m.icon" /></el-icon>
          <span>{{ m.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="top-bar">
        <div class="bread">管理员控制台</div>
        <el-dropdown>
          <span class="user-chip">
            <el-icon><Avatar /></el-icon>
            {{ realName || username }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.side {
  background: #1f2937;
  color: #fff;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
  font-size: 15px;
  border-bottom: 1px solid #2a3546;
}
.nav-menu {
  background: #1f2937;
  border: none;
}
:deep(.el-menu) { border-right: 0; }
:deep(.el-menu-item) { color: #cbd5e1; background: transparent; }
:deep(.el-menu-item:hover) { background: #2a3546; color: #fff; }
:deep(.el-menu-item.is-active) { background: #3b82f6; color: #fff; }
.top-bar {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #edf0f5;
}
.bread { font-weight: 600; color: #1f2937; }
.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #1f2937;
}
</style>
