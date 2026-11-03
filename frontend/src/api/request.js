import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  // 开发环境走 vite 代理（见 vite.config.js），生产可改由 VITE_API_BASE_URL 指定
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端统一返回 { code, message, data }
    if (res && res.code === 200) {
      return res
    }
    const message = res?.message || '请求失败'
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
