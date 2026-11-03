<template>
  <el-container class="layout">
    <el-header class="layout-header">
      <div class="brand">
        <el-icon :size="22"><Reading /></el-icon>
        <span>图书管理系统</span>
      </div>
      <div class="subtitle">Spring Boot + MyBatis-Plus + MySQL / Vue 3 + Element Plus</div>
    </el-header>

    <el-main class="layout-main">
      <router-view />
    </el-main>

    <el-footer class="layout-footer">
      图书管理系统 · 演示项目
    </el-footer>
  </el-container>
</template>

<script setup>
</script>

<style scoped>
.layout {
  min-height: 100%;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(90deg, #1f6feb 0%, #3b82f6 100%);
  color: #fff;
  height: 60px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 19px;
  font-weight: 600;
}

.subtitle {
  font-size: 12px;
  opacity: 0.85;
}

.layout-main {
  padding: 18px;
}

.layout-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 12px;
}
</style>
