/**
 * 全站路由配置
 *
 * 建议:
 * 1. 代码中路由统一使用name属性跳转(不使用path属性)
 */
import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)

// 开发环境不使用懒加载, 因为懒加载页面太多的话会造成webpack热更新太慢, 所以只有生产环境使用懒加载
const _import = require('./import-' + process.env.NODE_ENV)

const router = new Router({
  routes: [
    { path: '/404', component: _import('common/404'), name: '404', meta: { title: '404未找到' } },
    { path: '/login', component: _import('common/login'), name: 'login', meta: { title: '登录' } },

    { path: '/',
      component: _import('main'),
      name: 'main',
      redirect: {name: 'home'},
      meta: { title: '主入口整体布局' },
      children: [
        { path: '/home', component: _import('common/home'), name: 'home', meta: { title: '首页', isTab: false } },
        { path: '/role', component: _import('system/role'), name: 'role', meta: { title: '角色管理', isTab: false } },
        { path: '/menu', component: _import('system/menu'), name: 'menu', meta: { title: '菜单管理', isTab: false } },
        { path: '/admin', component: _import('system/admin'), name: 'admin', meta: { title: '管理员管理', isTab: false } },
        { path: '/course', component: _import('education/course'), name: 'course', meta: { title: '课程管理', isTab: false } },
        { path: '/baiduMap', component: _import('common/baidu-map'), name: 'baidu-map', meta: { title: '百度地图' } },
        { path: '/ueditor', component: _import('education/ueditor'), name: 'ueditor', meta: { title: '百度富文本' } },
        { path: '/gradeInfo', component: _import('education/gradeInfo'), name: 'gradeInfo', meta: { title: '年级管理', isTab: false } },
        { path: '/subject', component: _import('education/subject'), name: 'subject', meta: { title: '科目管理', isTab: false } },
        { path: '/languagePoints', component: _import('education/languagePoints'), name: 'languagePoints', meta: { title: '知识点管理', isTab: false } }
      ]
    }
  ]
})

/**
 * 全局路由拦截器
 */
router.beforeEach((to, from, next) => {
  let token = localStorage.getItem('token')
  if (to.path === '/login') {
    if (token) {
      location.href = '/'
    } else {
      next()
    }
  } else {
    if (!token) {
      next({name: 'login'}) // 没有token,跳转到登陆页面
    } else {
      next()
    }
  }
})
export default router
