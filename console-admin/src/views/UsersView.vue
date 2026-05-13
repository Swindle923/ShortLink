<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { pageUsers, freezeUser, unfreezeUser, updateUserRole } from '@/api';

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const query = reactive({ keyword: '', role: '', status: null, current: 1, size: 10 });

const load = async () => {
  loading.value = true;
  try {
    const resp = await pageUsers({ ...query });
    rows.value = resp.data.records || [];
    total.value = resp.data.total || 0;
  } finally {
    loading.value = false;
  }
};

const onSearch = () => { query.current = 1; load(); };

const handleFreeze = async (row) => {
  await ElMessageBox.confirm(`确认冻结用户 ${row.username} 吗？同时会强制下线。`, '确认', { type: 'warning' });
  await freezeUser(row.username);
  ElMessage.success('已冻结');
  load();
};
const handleUnfreeze = async (row) => {
  await unfreezeUser(row.username);
  ElMessage.success('已解冻');
  load();
};
const handleRole = async (row) => {
  const target = row.role === 'ADMIN' ? 'USER' : 'ADMIN';
  await ElMessageBox.confirm(`将 ${row.username} 改为 ${target}？`, '确认', { type: 'warning' });
  await updateUserRole(row.username, target);
  ElMessage.success('角色已更新');
  load();
};

onMounted(load);
</script>

<template>
  <div class="app-page" v-loading="loading">
    <div class="page-header">
      <h2>用户管理</h2>
    </div>
    <div class="card">
      <el-form :model="query" inline>
        <el-form-item label="搜索">
          <el-input v-model="query.keyword" placeholder="用户名/姓名/手机号" clearable @keydown.enter="onSearch" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" clearable placeholder="全部" style="width: 120px">
            <el-option label="ADMIN" value="ADMIN" />
            <el-option label="USER" value="USER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
            <el-option label="正常" :value="0" />
            <el-option label="冻结" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="rows" stripe size="small">
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="mail" label="邮箱" min-width="160" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'warning' : 'info'">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'danger' : 'success'">
              {{ row.status === 1 ? '冻结' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="linkCount" label="短链接数" width="100" />
        <el-table-column prop="createTime" label="注册时间" width="170" />
        <el-table-column label="操作" fixed="right" width="230">
          <template #default="{ row }">
            <el-button v-if="row.status !== 1" size="small" type="warning" @click="handleFreeze(row)">冻结</el-button>
            <el-button v-else size="small" type="success" @click="handleUnfreeze(row)">解冻</el-button>
            <el-button size="small" @click="handleRole(row)">
              切换为 {{ row.role === 'ADMIN' ? 'USER' : 'ADMIN' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.current"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="load"
        @size-change="load"
        style="margin-top: 12px; justify-content: flex-end"
      />
    </div>
  </div>
</template>
