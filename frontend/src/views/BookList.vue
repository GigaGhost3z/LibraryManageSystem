<template>
  <div class="book-page">
    <el-card shadow="never">
      <!-- 查询条件 -->
      <el-form :inline="true" :model="query" class="search-form" @submit.prevent>
        <el-form-item label="关键字">
          <el-input
            v-model="query.keyword"
            placeholder="书名 / 作者 / ISBN"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.category" placeholder="全部分类" clearable style="width: 160px">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="在架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="openCreate">新增图书</el-button>
        <el-button
          type="danger"
          :icon="Delete"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除<span v-if="selectedIds.length">（{{ selectedIds.length }}）</span>
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="rows"
        border
        stripe
        height="500"
        row-key="id"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="46" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="title" label="书名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="150" show-overflow-tooltip />
        <el-table-column prop="publisher" label="出版社" width="180" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="110" />
        <el-table-column prop="publishDate" label="出版日期" width="115" />
        <el-table-column prop="price" label="定价" width="95" align="right">
          <template #default="{ row }">
            {{ row.price != null ? '¥' + Number(row.price).toFixed(2) : '—' }}
          </template>
        </el-table-column>
        <el-table-column label="可借 / 馆藏" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.availableCopies > 0 ? 'success' : 'danger'" size="small">
              {{ row.availableCopies }} / {{ row.totalCopies }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="馆藏位置" width="120" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'primary' : 'info'" size="small">
              {{ row.status === 1 ? '在架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="pager"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :current-page="query.pageNum"
        :page-size="query.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑图书' : '新增图书'"
      width="640px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="ISBN" prop="isbn">
              <el-input v-model.trim="form.isbn" placeholder="如 9787536692930" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="书名" prop="title">
              <el-input v-model.trim="form.title" placeholder="请输入书名" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model.trim="form.author" placeholder="请输入作者" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版社">
              <el-input v-model.trim="form.publisher" placeholder="请输入出版社" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select
                v-model="form.category"
                placeholder="请选择或输入"
                filterable
                allow-create
                default-first-option
                clearable
                style="width: 100%"
              >
                <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版日期">
              <el-date-picker
                v-model="form.publishDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="定价(元)">
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="馆藏位置">
              <el-input v-model.trim="form.location" placeholder="如 A区-01-01" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="馆藏总数" prop="totalCopies">
              <el-input-number v-model="form.totalCopies" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="可借数量" prop="availableCopies">
              <el-input-number v-model="form.availableCopies" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">在架</el-radio>
                <el-radio :value="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model.trim="form.remark"
                type="textarea"
                :rows="2"
                maxlength="255"
                show-word-limit
                placeholder="选填"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Plus, Refresh, Search } from '@element-plus/icons-vue'
import {
  createBook,
  deleteBook,
  deleteBooks,
  listCategories,
  pageBooks,
  updateBook
} from '@/api/book'

const loading = ref(false)
const submitting = ref(false)
const rows = ref([])
const total = ref(0)
const categories = ref([])
const selectedIds = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  category: '',
  status: null
})

const dialogVisible = ref(false)
const formRef = ref()

const emptyForm = () => ({
  id: null,
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  category: '',
  publishDate: '',
  price: 0,
  totalCopies: 1,
  availableCopies: 1,
  location: '',
  status: 1,
  remark: ''
})

const form = reactive(emptyForm())

const rules = {
  isbn: [{ required: true, message: '请输入 ISBN', trigger: 'blur' }],
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  totalCopies: [{ required: true, message: '请输入馆藏总数', trigger: 'change' }],
  availableCopies: [
    { required: true, message: '请输入可借数量', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (value != null && form.totalCopies != null && value > form.totalCopies) {
          callback(new Error('可借数量不能大于馆藏总数'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await pageBooks({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      keyword: query.keyword || undefined,
      category: query.category || undefined,
      status: query.status === null || query.status === '' ? undefined : query.status
    })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    // 错误提示已在 axios 拦截器中统一处理
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await listCategories()
    categories.value = res.data || []
  } catch (e) {
    categories.value = []
  }
}

const handleSearch = () => {
  query.pageNum = 1
  fetchList()
}

const handleReset = () => {
  query.keyword = ''
  query.category = ''
  query.status = null
  query.pageNum = 1
  fetchList()
}

const handlePageChange = (page) => {
  query.pageNum = page
  fetchList()
}

const handleSizeChange = (size) => {
  query.pageSize = size
  query.pageNum = 1
  fetchList()
}

const onSelectionChange = (selection) => {
  selectedIds.value = selection.map((item) => item.id)
}

const openCreate = () => {
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, emptyForm(), {
    ...row,
    price: row.price != null ? Number(row.price) : 0
  })
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(form, emptyForm())
  formRef.value?.clearValidate()
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload = { ...form }
      if (!payload.id) {
        delete payload.id
        await createBook(payload)
        ElMessage.success('新增成功')
      } else {
        await updateBook(payload)
        ElMessage.success('修改成功')
      }
      dialogVisible.value = false
      await Promise.all([fetchList(), fetchCategories()])
    } catch (e) {
      // 错误提示已在拦截器处理
    } finally {
      submitting.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除《${row.title}》吗？此操作不可恢复。`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await deleteBook(row.id)
      ElMessage.success('删除成功')
      // 删掉当前页最后一条时回退一页
      if (rows.value.length === 1 && query.pageNum > 1) {
        query.pageNum -= 1
      }
      await Promise.all([fetchList(), fetchCategories()])
    })
    .catch(() => {})
}

const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 本图书吗？`, '批量删除确认', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await deleteBooks(selectedIds.value)
      ElMessage.success('批量删除成功')
      query.pageNum = 1
      await Promise.all([fetchList(), fetchCategories()])
    })
    .catch(() => {})
}

onMounted(() => {
  fetchList()
  fetchCategories()
})
</script>

<style scoped>
.book-page {
  max-width: 1500px;
  margin: 0 auto;
}

.search-form {
  margin-bottom: 4px;
}

.toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
}

.pager {
  margin-top: 14px;
  justify-content: flex-end;
}
</style>
