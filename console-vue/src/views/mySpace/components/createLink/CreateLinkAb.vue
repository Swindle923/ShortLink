<template>
  <div>
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px">
      A/B 测试：同一短链配置 2~4 个目标 URL，系统按权重随机分流。
      可在访问统计中分别查看各变体的 PV/UV，判断哪个落地页效果更好。
    </el-alert>
    <el-form ref="ruleFormRef" :model="formData" :rules="formRule" label-width="90px">
      <el-form-item label="短链分组" prop="gid">
        <el-select v-model="formData.gid" placeholder="请选择">
          <el-option
            v-for="item in groupInfo"
            :key="item.gid"
            :label="item.name"
            :value="item.gid"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="描述信息" prop="describe">
        <el-input
          v-model="formData.describe"
          maxlength="100"
          show-word-limit
          placeholder="本次 A/B 测试描述，例如：首页改版对照实验"
        />
      </el-form-item>

      <el-form-item label="变体列表">
        <div class="variants-wrap">
          <div v-for="(v, idx) in formData.abVariants" :key="idx" class="variant-row">
            <div class="variant-key">{{ v.variantKey }}</div>
            <el-input
              v-model="v.targetUrl"
              placeholder="https:// 开头的变体落地页"
              class="variant-url"
            />
            <el-input-number
              v-model="v.weight"
              :min="0"
              :max="100"
              :step="5"
              controls-position="right"
              class="variant-weight"
            />
            <span class="weight-suffix">%</span>
            <el-button
              v-if="formData.abVariants.length > 2"
              type="danger"
              link
              @click="removeVariant(idx)"
            >删除</el-button>
          </div>
          <div class="variants-footer">
            <el-button
              v-if="formData.abVariants.length < 4"
              link
              type="primary"
              @click="addVariant"
            >+ 新增变体</el-button>
            <span class="weight-sum" :class="{ invalid: totalWeight !== 100 }">
              权重合计：{{ totalWeight }}%（需等于 100%）
            </span>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="有效期" prop="validDateType">
        <el-radio-group v-model="formData.validDateType">
          <el-radio :label="0">永久</el-radio>
          <el-radio :label="1">自定义</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="formData.validDateType === 1" label="选择时间">
        <el-date-picker
          :disabled-date="disabledDate"
          v-model="formData.validDate"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="datetime"
          placeholder="选择失效时间"
        />
      </el-form-item>
      <el-form-item label="访问上限">
        <el-input-number
          v-model="formData.maxAccessCount"
          :min="1"
          :max="2147483647"
          controls-position="right"
          placeholder="为空表示不限制"
        />
        <span class="alert">留空表示不限访问次数</span>
      </el-form-item>

      <el-form-item>
        <div style="width: 100%; display: flex; justify-content: flex-end">
          <el-button
            class="buttons"
            type="primary"
            :disabled="submitDisable"
            @click="onSubmit(ruleFormRef)"
          >确认</el-button>
          <el-button class="buttons" @click="cancel">取消</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch, onBeforeUnmount, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  groupInfo: Array,
  defaultGid: String,
})
const { proxy } = getCurrentInstance()
const API = proxy.$API

const urlRegex = /^(https?:\/\/(([a-zA-Z0-9]+-?)+[a-zA-Z0-9]+\.)+(([a-zA-Z0-9]+-?)+[a-zA-Z0-9]+))(:\d+)?(\/.*)?(\?.*)?(#.*)?$/

const initialVariants = () => [
  { variantKey: 'A', targetUrl: '', weight: 50 },
  { variantKey: 'B', targetUrl: '', weight: 50 }
]

const formData = reactive({
  gid: null,
  describe: '',
  validDateType: 0,
  validDate: null,
  maxAccessCount: null,
  abVariants: initialVariants(),
})

const totalWeight = computed(() =>
  formData.abVariants.reduce((sum, v) => sum + (Number(v.weight) || 0), 0)
)

const groupInfoRef = ref([])
watch(
  () => props.groupInfo,
  (nV) => {
    groupInfoRef.value = nV || []
    if (props.defaultGid) formData.gid = props.defaultGid
    else if (groupInfoRef.value.length) formData.gid = groupInfoRef.value[0].gid
  },
  { immediate: true }
)
watch(
  () => props.defaultGid,
  (nV) => {
    if (nV) formData.gid = nV
  }
)

const variantKeys = ['A', 'B', 'C', 'D']
const addVariant = () => {
  if (formData.abVariants.length >= 4) return
  const nextKey = variantKeys[formData.abVariants.length]
  formData.abVariants.push({ variantKey: nextKey, targetUrl: '', weight: 0 })
}
const removeVariant = (idx) => {
  if (formData.abVariants.length <= 2) return
  formData.abVariants.splice(idx, 1)
  formData.abVariants.forEach((v, i) => { v.variantKey = variantKeys[i] })
}

const disabledDate = (time) => new Date(time).getTime() < Date.now()

const formRule = reactive({
  gid: [{ required: true, message: '请选择分组', trigger: 'blur' }],
  describe: [{ required: true, message: '请输入描述信息', trigger: 'blur' }]
})

const validateVariants = () => {
  for (const v of formData.abVariants) {
    if (!v.targetUrl) {
      ElMessage.error(`变体 ${v.variantKey} 的跳转链接不能为空`)
      return false
    }
    if (!urlRegex.test(v.targetUrl)) {
      ElMessage.error(`变体 ${v.variantKey} 的链接格式不合法`)
      return false
    }
  }
  if (totalWeight.value !== 100) {
    ElMessage.error(`权重合计必须为 100%，当前为 ${totalWeight.value}%`)
    return false
  }
  return true
}

const emits = defineEmits(['onSubmit', 'cancel'])

const ruleFormRef = ref()
const submitDisable = ref(false)
const onSubmit = async (formEl) => {
  submitDisable.value = true
  if (!formEl || !validateVariants()) {
    submitDisable.value = false
    return
  }
  await formEl.validate(async (valid) => {
    if (!valid) {
      submitDisable.value = false
      return
    }
    const payload = {
      gid: formData.gid,
      originUrl: formData.abVariants[0].targetUrl,
      describe: formData.describe,
      createdType: 1,
      validDateType: formData.validDateType,
      validDate: formData.validDate,
      maxAccessCount: formData.maxAccessCount,
      redirectMode: 1,
      abVariants: formData.abVariants.map(v => ({
        variantKey: v.variantKey,
        targetUrl: v.targetUrl,
        weight: v.weight
      }))
    }
    const res = await API.smallLinkPage.addSmallLink(payload)
    if (!res?.data?.success) {
      ElMessage.error(res?.data?.message || '创建失败')
    } else {
      ElMessage.success('创建成功！')
      emits('onSubmit', false)
      resetForm()
    }
    submitDisable.value = false
  })
}
const resetForm = () => {
  formData.gid = props.defaultGid || (groupInfoRef.value[0]?.gid ?? null)
  formData.describe = ''
  formData.validDate = null
  formData.validDateType = 0
  formData.maxAccessCount = null
  formData.abVariants = initialVariants()
}
const cancel = () => {
  emits('cancel', false)
  resetForm()
}

onBeforeUnmount(resetForm)

defineExpose({ initFormData: resetForm })
</script>

<style lang="less" scoped>
.variants-wrap {
  width: 100%;
}
.variant-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.variant-key {
  width: 28px;
  height: 28px;
  border-radius: 4px;
  background: #409eff;
  color: #fff;
  text-align: center;
  line-height: 28px;
  font-weight: 600;
  flex-shrink: 0;
}
.variant-url { flex: 1; }
.variant-weight { width: 100px; flex-shrink: 0; }
.weight-suffix { color: #999; }
.variants-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}
.weight-sum { font-size: 12px; color: #67c23a; }
.weight-sum.invalid { color: #f56c6c; font-weight: 600; }
.alert {
  margin-left: 8px;
  color: #e6a23c;
  font-size: 12px;
}
</style>
