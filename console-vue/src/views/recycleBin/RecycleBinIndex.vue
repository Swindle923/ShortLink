<template>
  <div class="recycle-page">
    <div class="toolbar">
      <span class="title">回收站</span>
      <span class="desc">共 {{ totalNums }} 条短链接</span>
    </div>
    <el-table
      :data="tableData"
      height="calc(100vh - 220px)"
      style="width: 100%"
      :header-cell-style="{ background: '#f7f8fa', color: '#606266' }"
    >
      <template #empty>
        <div class="empty-box">暂无回收数据</div>
      </template>
      <el-table-column label="短链接信息" min-width="280">
        <template #default="scope">
          <div class="table-link-box">
            <img :src="getImgUrl(scope.row.favicon)" width="20" height="20" alt="" />
            <div class="name-date">
              <el-tooltip show-after="500" :content="scope.row.describe">
                <span>{{ scope.row.describe }}</span>
              </el-tooltip>
              <span class="time">{{ scope.row.createTime }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="短链接网址" min-width="300">
        <template #default="scope">
          <div class="table-url-box">
            <span>{{ scope.row.domain + '/' + scope.row.shortUri }}</span>
            <el-tooltip show-after="500" :content="scope.row.originUrl">
              <span>{{ scope.row.originUrl }}</span>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="访问配额" width="170">
        <template #default="scope">
          <div class="quota-box">
            <div>
              <span>已用</span>
              <span>{{ scope.row.currentAccessCount || 0 }}</span>
            </div>
            <div>
              <span>剩余</span>
              <span :class="{ danger: remainAccessCount(scope.row) === 0 }">{{ formatRemainAccess(scope.row) }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="scope">
          <div class="operate-box">
            <el-tooltip show-after="500" content="恢复" placement="bottom-end">
              <el-icon class="table-edit" @click="recoverLink(scope.row)">
                <RefreshRight />
              </el-icon>
            </el-tooltip>
            <el-tooltip show-after="500" content="删除" placement="bottom-end">
              <el-popconfirm
                width="300"
                title="删除后短链跳转会失效，同时停止数据统计，这是一个不可逆的操作，是否删除?"
                @confirm="removeLink(scope.row)"
              >
                <template #reference>
                  <el-icon class="table-edit">
                    <Delete />
                  </el-icon>
                </template>
              </el-popconfirm>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-block">
      <el-pagination
        v-model:current-page="pageParams.current"
        v-model:page-size="pageParams.size"
        :page-sizes="[10, 15, 20, 30]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalNums"
        @size-change="queryRecycleBinPage"
        @current-change="queryRecycleBinPage"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import defaultImg from '@/assets/png/短链默认图标.png'

const { proxy } = getCurrentInstance()
const API = proxy.$API

const tableData = ref([])
const totalNums = ref(0)
const pageParams = reactive({
  current: 1,
  size: 15
})

const getImgUrl = (url) => {
  return url ?? defaultImg
}

const formatRemainAccess = (row) => {
  if (row?.maxAccessCount === null || row?.maxAccessCount === undefined) {
    return '不限'
  }
  const remainCount = row.maxAccessCount - (row.currentAccessCount || 0)
  return Math.max(remainCount, 0)
}

const remainAccessCount = (row) => {
  if (row?.maxAccessCount === null || row?.maxAccessCount === undefined) {
    return -1
  }
  return Math.max(row.maxAccessCount - (row.currentAccessCount || 0), 0)
}

const queryRecycleBinPage = async () => {
  const res = await API.smallLinkPage.queryRecycleBin(pageParams)
  tableData.value = res?.data?.data?.records || []
  totalNums.value = +res?.data?.data?.total || 0
}

const recoverLink = async (data) => {
  const { gid, fullShortUrl } = data
  const res = await API.smallLinkPage.recoverLink({ gid, fullShortUrl })
  if (res?.data?.code === '0') {
    ElMessage.success('恢复成功')
    queryRecycleBinPage()
  } else {
    ElMessage.error(res?.data?.message || '恢复失败')
  }
}

const removeLink = async (data) => {
  const { gid, fullShortUrl } = data
  const res = await API.smallLinkPage.removeLink({ gid, fullShortUrl })
  if (res?.data?.code === '0') {
    ElMessage.success('删除成功')
    queryRecycleBinPage()
  } else {
    ElMessage.error(res?.data?.message || '删除失败')
  }
}

onMounted(() => {
  queryRecycleBinPage()
})
</script>

<style lang="scss" scoped>
.recycle-page {
  padding: 16px;
  height: calc(100vh - 54px);
  background-color: #ffffff;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.desc {
  color: rgba(0, 0, 0, 0.45);
}

.empty-box {
  height: 55vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.table-link-box {
  display: flex;
  align-items: center;
}

.name-date {
  margin-left: 8px;
  display: flex;
  flex-direction: column;
}

.time {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}

.table-url-box {
  display: flex;
  flex-direction: column;

  span {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
  }
}

.quota-box {
  display: flex;
  flex-direction: column;
  gap: 3px;

  div {
    display: flex;
    justify-content: space-between;
  }

  span:first-child {
    color: rgba(0, 0, 0, 0.45);
    margin-right: 8px;
  }
}

.danger {
  color: #f56c6c;
  font-weight: 600;
}

.operate-box {
  display: flex;
  align-items: center;
}

.table-edit {
  cursor: pointer;
  color: #606266;
  margin-right: 10px;
  font-size: 18px;
}

.table-edit:hover {
  color: #409eff;
}

.pagination-block {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
