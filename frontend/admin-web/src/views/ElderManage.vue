<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-button type="primary" @click="openAdd">新增老人</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="community" label="社区" width="120" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="address" label="住址" min-width="160" />
      <el-table-column prop="emergencyContact" label="紧急联系人" width="110" />
      <el-table-column prop="emergencyPhone" label="紧急电话" width="130" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑老人' : '新增老人'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名"><el-input v-model="form.name" placeholder="必填" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" :max="150" /></el-form-item>
        <el-form-item label="社区"><el-input v-model="form.community" placeholder="如：朝阳社区" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="住址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="紧急联系人"><el-input v-model="form.emergencyContact" /></el-form-item>
        <el-form-item label="紧急电话"><el-input v-model="form.emergencyPhone" /></el-form-item>
        <el-form-item label="绑定家属">
          <el-select v-model="form.familyUserId" clearable placeholder="选择家属账号（不选则不绑定）" style="width: 100%">
            <el-option v-for="u in familyUsers" :key="u.id" :label="`${u.realName || u.username}（${u.username}）`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="与家属关系">
          <el-input v-model="form.relation" placeholder="如：子女" />
        </el-form-item>
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
import { getAllElders, addElder, updateElder, deleteElder, getUserList } from '../api/user'

const list = ref([])
const familyUsers = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive({
  id: null, name: '', gender: 1, age: null, community: '',
  phone: '', address: '', emergencyContact: '', emergencyPhone: '',
  familyUserId: null, relation: '子女'
})

const loadFamilyUsers = async () => {
  try {
    const res = await getUserList({ current: 1, size: 100, role: 'FAMILY' })
    familyUsers.value = res.data.records
  } catch (e) { /* ignore */ }
}

const load = async () => {
  loading.value = true
  try {
    const res = await getAllElders()
    list.value = res.data
  } catch (e) { /* ignore */ } finally {
    loading.value = false
  }
}

const openAdd = () => {
  Object.assign(form, {
    id: null, name: '', gender: 1, age: null, community: '',
    phone: '', address: '', emergencyContact: '', emergencyPhone: '',
    familyUserId: null, relation: '子女'
  })
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, { ...row, relation: '子女' })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.name) {
    ElMessage.warning('请输入老人姓名')
    return
  }
  if (form.id) {
    await updateElder(form)
  } else {
    await addElder(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除老人「${row.name}」吗？删除后其健康档案将不再展示`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteElder(row.id)
      ElMessage.success('删除成功')
      load()
    })
    .catch(() => {})
}

onMounted(() => {
  load()
  loadFamilyUsers()
})
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
</style>
