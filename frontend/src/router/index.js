import { createRouter, createWebHistory } from 'vue-router'
import BookList from '@/views/BookList.vue'

const routes = [
  { path: '/', redirect: '/books' },
  {
    path: '/books',
    name: 'BookList',
    component: BookList,
    meta: { title: '图书管理' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  document.title = to.meta?.title ? `${to.meta.title} - 图书管理系统` : '图书管理系统'
})

export default router
