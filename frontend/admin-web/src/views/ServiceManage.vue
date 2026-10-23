<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-button type="primary" @click="openAdd">新增服务</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="serviceName" label="服务名称" width="160" />
      <el-table-column prop="serviceType" label="服务类型" width="120" />
      <el-table-column prop="price" label="价格（元）" width="120" />
      <el-table-column prop="description" label="服务说明" min-width="240" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑服务' : '新增服务'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="服务名称"><el-input v-model="form.serviceName" /></el-form-item>
        <el-form-item label="服务类型"><el-input v-model="form.serviceType" placeholder="如：护理/康复/保洁/做饭/陪护" /></el-form-item>
        <el-form-item label="价格（元）"><el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" /></el-form-item>
        <el-form-item label="服务说明"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getServiceList, addService, updateService, deleteService } from '../api/service'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive({ id: null, serviceName: '', serviceType: '', price: 0, description: '', status: 1 })

const load = async () => {
  loading.value = true
  try {
    const res = await getServiceList()
    list.value = res.data
  } catch (e) { /* ignore */ } finally {
    loading.value = false
  }
}

const openAdd = () => {
  Object.assign(form, { id: null, serviceName: '', serviceType: '', price: 0, description: '', status: 1 })
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.serviceName || !form.serviceType) {
    ElMessage.warning('请填写服务名称和服务类型')
    return
  }
  if (form.id) {
    await updateService(form)
  } else {
    await addService(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除服务「${row.serviceName}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteService(row.id)
      ElMessage.success('删除成功')
      load()
    })
    .catch(() => {})
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
</style>
