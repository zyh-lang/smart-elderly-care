<template>
  <el-row :gutter="16">
    <el-col :span="7">
      <el-card shadow="never">
        <template #header>老人列表</template>
        <el-menu :default-active="String(currentElder?.id)" @select="selectElder">
          <el-menu-item v-for="elder in elders" :key="elder.id" :index="String(elder.id)">
            <span>{{ elder.name }}（{{ elder.age }}岁 / {{ elder.community }}）</span>
          </el-menu-item>
        </el-menu>
      </el-card>
    </el-col>
    <el-col :span="17">
      <el-card shadow="never" v-if="currentElder">
        <template #header>{{ currentElder.name }} 的健康档案</template>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本档案" name="record">
            <el-form :model="recordForm" label-width="90px" style="max-width: 600px">
              <el-form-item label="身高(cm)"><el-input-number v-model="recordForm.height" :min="0" /></el-form-item>
              <el-form-item label="体重(kg)"><el-input-number v-model="recordForm.weight" :min="0" /></el-form-item>
              <el-form-item label="血型">
                <el-select v-model="recordForm.bloodType" clearable style="width: 100%">
                  <el-option v-for="t in ['A', 'B', 'AB', 'O']" :key="t" :label="t + '型'" :value="t" />
                </el-select>
              </el-form-item>
              <el-form-item label="过敏史"><el-input v-model="recordForm.allergyHistory" /></el-form-item>
              <el-form-item label="既往病史"><el-input v-model="recordForm.medicalHistory" type="textarea" :rows="2" /></el-form-item>
              <el-form-item label="家族病史"><el-input v-model="recordForm.familyHistory" type="textarea" :rows="2" /></el-form-item>
              <el-form-item label="生活习惯"><el-input v-model="recordForm.livingHabits" /></el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveRecord">保存档案</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="健康指标" name="metric">
            <div class="toolbar">
              <el-select v-model="metricType" style="width: 180px">
                <el-option v-for="t in metricTypes" :key="t.value" :label="t.label" :value="t.value" />
              </el-select>
              <el-input v-model="metricValue" placeholder="指标值，如 120/80" style="width: 160px" />
              <el-button type="primary" @click="addMetricData">录入</el-button>
            </div>
            <el-table :data="metrics" border stripe>
              <el-table-column prop="metricType" label="类型" width="150">
                <template #default="{ row }">{{ metricLabel(row.metricType) }}</template>
              </el-table-column>
              <el-table-column prop="metricValue" label="数值" width="120" />
              <el-table-column prop="measureTime" label="测量时间" min-width="170" />
              <el-table-column prop="remark" label="备注" min-width="140" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="用药记录" name="medication">
            <div class="toolbar">
              <el-button type="primary" @click="openMedDialog">添加用药</el-button>
            </div>
            <el-table :data="medications" border stripe>
              <el-table-column prop="drugName" label="药品名称" width="150" />
              <el-table-column prop="dosage" label="剂量" width="100" />
              <el-table-column prop="frequency" label="频次" width="120" />
              <el-table-column prop="remindTime" label="提醒时间" width="130" />
              <el-table-column prop="remark" label="备注" min-width="140" />
              <el-table-column label="操作" width="90">
                <template #default="{ row }">
                  <el-button size="small" type="danger" @click="removeMedication(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-card>
      <el-empty v-else description="请选择左侧老人" />
    </el-col>
  </el-row>

  <el-dialog v-model="medDialogVisible" title="添加用药" width="460px">
    <el-form :model="medForm" label-width="90px">
      <el-form-item label="药品名称"><el-input v-model="medForm.drugName" /></el-form-item>
      <el-form-item label="剂量"><el-input v-model="medForm.dosage" placeholder="如：1片" /></el-form-item>
      <el-form-item label="频次"><el-input v-model="medForm.frequency" placeholder="如：每日两次" /></el-form-item>
      <el-form-item label="提醒时间"><el-input v-model="medForm.remindTime" placeholder="如：08:00,20:00" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="medForm.remark" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="medDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveMedication">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getElderList } from '../api/user'
import {
  getHealthRecord, saveHealthRecord, getMetrics, addMetric,
  getMedications, addMedication, deleteMedication
} from '../api/health'

const elders = ref([])
const currentElder = ref(null)
const activeTab = ref('record')

const recordForm = reactive({ height: null, weight: null, bloodType: '', allergyHistory: '', medicalHistory: '', familyHistory: '', livingHabits: '' })

const metricTypes = [
  { label: '血压', value: 'BLOOD_PRESSURE' },
  { label: '血糖', value: 'BLOOD_SUGAR' },
  { label: '心率', value: 'HEART_RATE' },
  { label: '体温', value: 'TEMPERATURE' },
  { label: '体重', value: 'WEIGHT' }
]
const metricLabel = (v) => metricTypes.find(t => t.value === v)?.label || v
const metricType = ref('BLOOD_PRESSURE')
const metricValue = ref('')
const metrics = ref([])

const medications = ref([])
const medDialogVisible = ref(false)
const medForm = reactive({ drugName: '', dosage: '', frequency: '', remindTime: '', remark: '' })

const selectElder = async (id) => {
  currentElder.value = elders.value.find(e => String(e.id) === String(id))
  await loadHealthData()
}

const loadHealthData = async () => {
  if (!currentElder.value) return
  const elderId = currentElder.value.id
  try {
    const record = (await getHealthRecord(elderId)).data
    if (record) {
      Object.assign(recordForm, {
        height: record.height, weight: record.weight, bloodType: record.bloodType || '',
        allergyHistory: record.allergyHistory || '', medicalHistory: record.medicalHistory || '',
        familyHistory: record.familyHistory || '', livingHabits: record.livingHabits || ''
      })
    }
  } catch (e) { /* ignore */ }
  try {
    metrics.value = (await getMetrics({ elderId })).data
  } catch (e) { /* ignore */ }
  try {
    medications.value = (await getMedications(elderId)).data
  } catch (e) { /* ignore */ }
}

const saveRecord = async () => {
  await saveHealthRecord({ ...recordForm, elderId: currentElder.value.id })
  ElMessage.success('档案已保存')
}

const addMetricData = async () => {
  if (!metricValue.value) {
    ElMessage.warning('请输入指标值')
    return
  }
  await addMetric({ elderId: currentElder.value.id, metricType: metricType.value, metricValue: metricValue.value })
  ElMessage.success('指标已录入')
  metricValue.value = ''
  await loadHealthData()
}

const openMedDialog = () => {
  Object.assign(medForm, { drugName: '', dosage: '', frequency: '', remindTime: '', remark: '' })
  medDialogVisible.value = true
}

const saveMedication = async () => {
  if (!medForm.drugName) {
    ElMessage.warning('请输入药品名称')
    return
  }
  await addMedication({ ...medForm, elderId: currentElder.value.id, status: 1 })
  ElMessage.success('用药已添加')
  medDialogVisible.value = false
  await loadHealthData()
}

const removeMedication = async (row) => {
  await deleteMedication(row.id)
  ElMessage.success('已删除')
  await loadHealthData()
}

onMounted(async () => {
  try {
    elders.value = (await getElderList()).data
    if (elders.value.length > 0) {
      await selectElder(elders.value[0].id)
    }
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
</style>
