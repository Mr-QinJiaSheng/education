import Vue from 'vue'
import App from '@/App'
import ElementUI from 'element-ui'
import router from '@/router'                 // api: https://github.com/vuejs/vue-router
import store from '@/store'                   // api: https://github.com/vuejs/vuex
import '@/icons'                              // api: http://www.iconfont.cn/
import '@/assets/scss/index.scss'
import 'element-ui/lib/theme-chalk/index.css'
import httpRequest from '@/utils/httpRequest' // api: https://github.com/axios/axios
import axios from 'axios'
import {hasPermission} from './utils'
// import { isAuth } from '@/utils'
import cloneDeep from 'lodash/cloneDeep'
import moment from 'moment'// 导入moment
Vue.prototype.hasPermission = hasPermission
Vue.prototype.moment = moment
Vue.use(ElementUI)
Vue.prototype.axios = axios
Vue.config.productionTip = false

// 挂载全局
Vue.prototype.$http = httpRequest // ajax请求方法

// 保存整站vuex本地储存初始状态
window.SITE_CONFIG['storeState'] = cloneDeep(store.state)
// 全局请求拦截器
axios.interceptors.request.use(config => {
  let token = localStorage.getItem('token')
  if (token) {
    config.headers.token = token
  }
  return config
}, error => {
  // 响应异常处理
  return Promise.reject(error)
})

// 全局响应拦截器
axios.interceptors.response.use(response => {
  let code = response.data.code
  if (code !== 1) {
    if (code === 1001) { // 1001 表示用户未登录或者用户身份已过期
      localStorage.clear()
      router.push('/login')
    } else {
      if (code !== 0) {
        ElementUI.Message({
          message: response.data.message,
          type: 'error'
        })
      }
    }
  }
  return response
}, error => {
  // 响应异常处理
  return Promise.reject(error)
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
