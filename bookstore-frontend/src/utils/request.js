import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/store/user'
import router from '@/router'

NProgress.configure({ showSpinner: false })

const service = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    NProgress.start()
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = 'Bearer ' + userStore.token
    }
    return config
  },
  (error) => {
    NProgress.done()
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    NProgress.done()
    const res = response.data
    // 文件下载直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    if (res.code === 200) {
      return res
    }
    // 401 未登录
    if (res.code === 401) {
      ElMessage.error(res.message || '登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      return Promise.reject(new Error(res.message))
    }
    // 403 无权限
    if (res.code === 403) {
      ElMessage.error('无权限访问')
      return Promise.reject(new Error(res.message))
    }
    // silent 请求不弹错误提示
    if (!response.config.silent) {
      ElMessage.error(res.message || '请求失败')
    }
    return Promise.reject(new Error(res.message))
  },
  (error) => {
    NProgress.done()
    // silent 请求失败时不弹错误提示(用于首页等公共接口)
    if (!error.config?.silent) {
      // 网络连接失败时, 合并提示避免刷屏
      const msg = error.code === 'ERR_NETWORK' ? '后端服务未启动，请先启动后端' : (error.message || '网络异常')
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default service
