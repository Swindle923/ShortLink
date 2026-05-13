<script setup>
import { computed, ref } from 'vue';

const GRAFANA_BASE = 'http://127.0.0.1:3000';

const tabs = [
  {
    label: 'Service Overview',
    uid: 'shortlink-service-overview',
    slug: 'shortlink-service-overview'
  },
  {
    label: 'Business Chain',
    uid: 'shortlink-business-chain',
    slug: 'shortlink-business-chain'
  },
  {
    label: 'JVM Runtime',
    uid: 'shortlink-jvm-runtime',
    slug: 'shortlink-jvm-runtime'
  }
];

const activeKey = ref(tabs[0].uid);
const activeTab = computed(() => tabs.find((t) => t.uid === activeKey.value) || tabs[0]);
const iframeUrl = computed(() => {
  const t = activeTab.value;
  return `${GRAFANA_BASE}/d/${t.uid}/${t.slug}?orgId=1&kiosk=tv&refresh=30s&theme=light`;
});
const openInGrafana = () => window.open(iframeUrl.value, '_blank');
</script>

<template>
  <div class="app-page">
    <div class="page-header">
      <h2>监控中心</h2>
      <el-button type="primary" @click="openInGrafana">在 Grafana 中打开</el-button>
    </div>
    <div class="card">
      <el-tabs v-model="activeKey">
        <el-tab-pane v-for="t in tabs" :key="t.uid" :name="t.uid" :label="t.label" />
      </el-tabs>
      <iframe :src="iframeUrl" class="grafana-frame" />
    </div>
  </div>
</template>

<style scoped>
.grafana-frame {
  width: 100%;
  height: calc(100vh - 260px);
  min-height: 500px;
  border: 0;
  border-radius: 4px;
  background: #f4f6f8;
}
</style>
