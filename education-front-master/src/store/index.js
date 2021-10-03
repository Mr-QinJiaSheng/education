import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    userInfo: {},
    host: 'http://localhost:8004', // 线上api接口地址
    fileHost: 'http://localhost:8004/uploads',
    proxyHttp: true // 是否开启http代理
  },

  mutations: {

    updateUserInfo (state, data) {
      state.userInfo = data
      localStorage.setItem('userInfo', JSON.stringify(data))
    }

  },

  getters: {

    getUserInfo (state) {
      if (state.userInfo.name) {
        return state.userInfo
      }
      return JSON.parse(localStorage.getItem('userInfo'))
    },


  }
})
