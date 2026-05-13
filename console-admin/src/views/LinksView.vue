<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { pageLinks, disableLink, enableLink } from '@/api';

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const query = reactive({ keyword: '', username: '', enableStatus: null, current: 1, size: 10 });

const load = async () => {
  loading.value = true;
  try {
    const resp = await pageLinks({ ...query });
    rows.value = resp.data.records || [];
    total.value = resp.data.total || 0;
  } finally {
    loading.value = false;
  }
};

const onSearch = () => { query.current = 1; load(); };

const handleDisable = async (row) => {
  await ElMessageBox.confirm('禁用后该短链接将无法访问，确定吗？', '确认', { type: 'warning' });
  await disableLink(row.fullShortUrl, row.gid);
  ElMessage.success('已禁用');
  load();
};
const handleEnable = async (row) => {
  await enableLink(row.fullShortUrl, row.gid);
  ElMessage.success('已启用');
  load();
};

onMounted(load);
</script>

<template>
  <div class="app-page" v-loading="loading">
    <div class="page-header">
      <h2>短链接管理</h2>
    </div>
    <div class="card">
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="短链接或原链接" clearable @keydown.enter="onSearch" />
        </el-form-item>
        <el-form-item label="所属用户">
          <el-input v-model="query.username" placeholder="username" clearable @keydown.enter="onSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.enableStatus" clearable placeholder="全部" style="width: 120px">
            <el-option label="启用" :value="0" />
            <el-option label="已禁用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="rows" stripe size="small">
        <el-table-column prop="fullShortUrl" label="短链接" min-width="200" />
        <el-table-column prop="originUrl" label="原链接" min-width="220" show-overflow-tooltip />
        <el-table-column prop="ownerUsername" label="所属用户" width="120" />
        <el-table-column prop="describe" label="描述" width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.enableStatus === 0 ? 'success' : 'danger'">
              {{ row.enableStatus === 0 ? '启用' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalPv" label="总 PV" width="90" />
        <el-table-column prop="totalUv" label="总 UV" width="90" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-button v-if="row.enableStatus === 0" size="small" type="danger" @click="handleDisable(row)">
              禁用
            </el-button>
            <el-button v-else size="small" type="success" @click="handleEnable(row)">启用</el-button>
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
