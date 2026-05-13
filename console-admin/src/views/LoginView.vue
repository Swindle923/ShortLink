<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { login } from '@/api';

const router = useRouter();
const loading = ref(false);
const form = ref({ username: 'admin', password: 'admin123456' });

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码');
    return;
  }
  loading.value = true;
  try {
    const resp = await login(form.value);
    const { username, token, role, realName } = resp.data;
    localStorage.setItem('admin_username', username);
    localStorage.setItem('admin_token', token);
    localStorage.setItem('admin_role', role);
    localStorage.setItem('admin_real_name', realName || '');
    ElMessage.success('登录成功');
    router.push('/dashboard');
  } catch (e) {
    // message already shown by interceptor
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h2>短链接管理员中台</h2>
      <p class="subtitle">SaaS 短链接系统 · 管理员控制台</p>
      <el-form :model="form" label-position="top" @keydown.enter="handleLogin">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入管理员账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" :loading="loading" size="large" style="width:100%" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
      <p class="hint">默认管理员：admin / admin123456</p>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  height: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2c3e50 0%, #4a90e2 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
}
.login-card h2 {
  margin: 0 0 8px;
  font-weight: 600;
  color: #2c3e50;
}
.subtitle {
  margin: 0 0 24px;
  color: #909399;
  font-size: 13px;
}
.hint {
  margin-top: 16px;
  font-size: 12px;
  color: #c0c4cc;
  text-align: center;
}
</style>
