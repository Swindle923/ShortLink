<script setup>
import { onMounted, ref } from 'vue';
import { opsOverview, opsHighRisk, opsLifecycleAlerts } from '@/api';

const loading = ref(false);
const overview = ref({});
const highRiskRows = ref([]);
const alertRows = ref([]);

const levelTag = (score) => {
  if (score == null) return '';
  if (score >= 70) return 'danger';
  if (score >= 40) return 'warning';
  return 'success';
};

const load = async () => {
  loading.value = true;
  try {
    const ov = await opsOverview({});
    overview.value = ov.data || {};
    const hr = await opsHighRisk({ current: 1, size: 10 });
    highRiskRows.value = hr.data?.records || [];
    const al = await opsLifecycleAlerts({ current: 1, size: 10 });
    alertRows.value = al.data?.records || [];
  } finally {
    loading.value = false;
  }
};

onMounted(load);
</script>

<template>
  <div class="app-page" v-loading="loading">
    <div class="page-header">
      <h2>运营大盘</h2>
      <el-button type="primary" @click="load">刷新</el-button>
    </div>

    <el-row :gutter="16" class="stats-grid">
      <el-col :span="6"><div class="stat">
        <span class="label">链接总数</span>
        <span class="value">{{ overview.linkCount ?? 0 }}</span>
      </div></el-col>
      <el-col :span="6"><div class="stat">
        <span class="label">活跃链接</span>
        <span class="value">{{ overview.activeCount ?? 0 }}</span>
      </div></el-col>
      <el-col :span="6"><div class="stat warn">
        <span class="label">即将过期</span>
        <span class="value">{{ overview.expiringSoonCount ?? 0 }}</span>
      </div></el-col>
      <el-col :span="6"><div class="stat danger">
        <span class="label">高风险链接</span>
        <span class="value">{{ overview.highRiskCount ?? 0 }}</span>
      </div></el-col>
    </el-row>

    <el-row :gutter="16" class="stats-grid">
      <el-col :span="6"><div class="stat">
        <span class="label">健康率</span>
        <span class="value">{{ ((overview.healthyRate ?? 0)).toFixed(1) }}%</span>
      </div></el-col>
      <el-col :span="6"><div class="stat">
        <span class="label">今日 PV</span>
        <span class="value">{{ overview.todayPv ?? 0 }}</span>
      </div></el-col>
      <el-col :span="6"><div class="stat">
        <span class="label">累计 PV</span>
        <span class="value">{{ overview.totalPv ?? 0 }}</span>
      </div></el-col>
      <el-col :span="6"><div class="stat">
        <span class="label">配额告急</span>
        <span class="value">{{ overview.quotaRiskCount ?? 0 }}</span>
      </div></el-col>
    </el-row>

    <div class="card" style="margin-top:16px">
      <h3>风险 Top 10</h3>
      <el-table :data="highRiskRows" size="small" stripe>
        <el-table-column prop="fullShortUrl" label="短链接" min-width="180" />
        <el-table-column prop="describe" label="描述" min-width="120" />
        <el-table-column label="风险分值" width="120">
          <template #default="{ row }">
            <el-tag :type="levelTag(row.riskScore)" effect="plain">
              {{ row.riskScore }} · {{ row.riskLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskReason" label="风险原因" min-width="220" />
        <el-table-column prop="todayPv" label="今日 PV" width="90" />
        <el-table-column prop="totalPv" label="累计 PV" width="90" />
      </el-table>
    </div>

    <div class="card" style="margin-top:16px">
      <h3>生命周期告警</h3>
      <el-table :data="alertRows" size="small" stripe>
        <el-table-column prop="fullShortUrl" label="短链接" min-width="180" />
        <el-table-column prop="alertType" label="类型" width="140" />
        <el-table-column prop="alertMessage" label="告警内容" min-width="260" />
        <el-table-column prop="daysToExpire" label="剩余天数" width="100" />
        <el-table-column label="配额使用" width="120">
          <template #default="{ row }">
            <span v-if="row.quotaUsageRate != null">{{ (row.quotaUsageRate * 100).toFixed(1) }}%</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.stats-grid { margin-bottom: 12px; }
.stat {
  background: #fff;
  border-radius: 6px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}
.stat .label { color: #909399; font-size: 13px; }
.stat .value { font-size: 28px; font-weight: 600; color: #1f2937; }
.stat.warn .value { color: #e6a23c; }
.stat.danger .value { color: #f56c6c; }
.card h3 { margin: 0 0 12px; font-weight: 600; color: #1f2937; }
</style>
