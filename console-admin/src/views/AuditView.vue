<script setup>
import { onMounted, reactive, ref } from 'vue';
import { pageAuditLogs } from '@/api';

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const query = reactive({ adminUsername: '', actionType: '', targetType: '', success: null, current: 1, size: 20 });

const actionLabel = {
  FREEZE_USER: '冻结用户',
  UNFREEZE_USER: '解冻用户',
  UPDATE_USER_ROLE: '更新角色',
  DISABLE_LINK: '禁用链接',
  ENABLE_LINK: '启用链接'
};

const load = async () => {
  loading.value = true;
  try {
    const resp = await pageAuditLogs({ ...query });
    rows.value = resp.data.records || [];
    total.value = resp.data.total || 0;
  } finally {
    loading.value = false;
  }
};

const onSearch = () => { query.current = 1; load(); };

onMounted(load);
</script>

<template>
  <div class="app-page" v-loading="loading">
    <div class="page-header"><h2>审计日志</h2></div>
    <div class="card">
      <el-form :model="query" inline>
        <el-form-item label="管理员">
          <el-input v-model="query.adminUsername" placeholder="用户名" clearable @keydown.enter="onSearch" />
        </el-form-item>
        <el-form-item label="动作">
          <el-select v-model="query.actionType" clearable placeholder="全部" style="width: 160px">
            <el-option v-for="(v, k) in actionLabel" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="对象类型">
          <el-select v-model="query.targetType" clearable placeholder="全部" style="width: 120px">
            <el-option label="USER" value="USER" />
            <el-option label="LINK" value="LINK" />
          </el-select>
        </el-form-item>
        <el-form-item label="结果">
          <el-select v-model="query.success" clearable placeholder="全部" style="width: 100px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="rows" stripe size="small">
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column prop="adminUsername" label="管理员" width="120" />
        <el-table-column label="动作" width="140">
          <template #default="{ row }">
            <el-tag>{{ actionLabel[row.actionType] || row.actionType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetType" label="对象类型" width="100" />
        <el-table-column prop="requestParams" label="参数" min-width="260" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.success === 1 ? 'success' : 'danger'">
              {{ row.success === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误信息" min-width="180" show-overflow-tooltip />
      </el-table>

      <el-pagination
        v-model:current-page="query.current"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @current-change="load"
        @size-change="load"
        style="margin-top: 12px; justify-content: flex-end"
      />
    </div>
  </div>
</template>
