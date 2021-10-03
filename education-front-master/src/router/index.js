import Vue from 'vue'
import Router from 'vue-router'
import index from '@/views/index'
import home from '@/views/home'
import login from '@/views/login'

Vue.use(Router)

const router =  new Router({
  routes: [
    {
      path: '/login',
      component: login,
      name: 'login',
    },
    { path: '/',
      component: index,
      name: 'index',
      redirect: {name: 'home'},
      meta: { title: '主入口整体布局' },
      children: [
        { path: '/home', component: home, name: 'home', meta: { title: '首页' } }
      ]
    }
  ]
})

// vue  路由全局拦截器
router.beforeEach(function (to, from, next) {
  let token = localStorage.getItem('front-token')
  if (to.path === '/login') {
    if (token) {
      location.href = '/'
    } else {
      next()
    }
  } else {
    if (!token) {
      next({ name: 'login' }) // 没有token 跳转登录页面
    }
    next()
  }
})
export default router
