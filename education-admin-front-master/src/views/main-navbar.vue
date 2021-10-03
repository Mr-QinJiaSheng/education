<template>
  <nav class="site-navbar" :class="'site-navbar--' + navbarLayoutType">
    <div class="site-navbar__header">
      <h5 class="site-navbar__brand" @click="$router.push({ name: 'home' })">
        <a class="site-navbar__brand-lg" href="javascript:;">智慧云智能教育平台</a>
      </h5>
    </div>
    <div class="site-navbar__body clearfix">
      <el-menu
        class="site-navbar__menu"
        mode="horizontal">
        <el-menu-item class="site-navbar__switch" index="0" @click="sidebarFold = !sidebarFold">
          <icon-svg name="zhedie"></icon-svg>
        </el-menu-item>
      </el-menu>
      <el-menu
        class="site-navbar__menu site-navbar__menu--right"
        mode="horizontal">
        <el-menu-item index="1" @click="$router.push({ name: 'theme' })">
          <template slot="title">
           <el-badge style="font-size: 25px;" value="new">
              <icon-svg name="indexSetting" ></icon-svg>
            </el-badge>
          </template>
        </el-menu-item>

        <el-menu-item index="2">
          <template v-if="hasMessage" slot="title">
            <el-badge style="font-size: 25px">
              <icon-svg name="message"></icon-svg>
            </el-badge>
          </template>

          <template v-else="hasMessage" slot="title">
            <el-badge style="font-size: 25px">
              <icon-svg name="message"></icon-svg>
            </el-badge>
          </template>
        </el-menu-item>

        <el-menu-item class="site-navbar__avatar" index="3">
          <el-dropdown :show-timeout="0" placement="bottom">
            <span class="el-dropdown-link">
              <img src="~@/assets/img/avatar.png" :alt="userName"> {{userName}}
            </span>
            <el-dropdown-menu slot="dropdown">
             <!-- <el-dropdown-item>个人资料</el-dropdown-item>-->
              <el-dropdown-item>修改密码</el-dropdown-item>
              <el-dropdown-item @click.native="logout()">退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-menu-item>
      </el-menu>
    </div>
  </nav>
</template>

<script>
  export default {
    data () {
      return {
        hasMessage: false,
        userName: '',
        webSocket: null
      }
    },

    mounted () {
      let userInfo = JSON.parse(localStorage.getItem('userInfo'))
      this.userName = userInfo.login_name
      this.initWebSocket()
    },

    computed: {

      navbarLayoutType: {
        get () { return this.$store.state.common.navbarLayoutType }
      },
      sidebarFold: {
        get () { return this.$store.state.common.sidebarFold },
        set (val) { this.$store.commit('common/updateSidebarFold', val) }
      },
      mainTabs: {
        get () { return this.$store.state.common.mainTabs },
        set (val) { this.$store.commit('common/updateMainTabs', val) }
      }
     /* userName: {
        get () { return this.$store.state.user.name }
      } */
    },
    methods: {

      initWebSocket () {
        // 判断浏览器是否支持webSocket
        if ('WebSocket' in window) {
          this.webSocket = new WebSocket('ws://localhost/webSocket') // 域名不需要加http或https （只支持http方式）
         // new WebSocket('wss://localhost/webSocket') //表示支持https的方式
          this.webSocket.onopen = this.onOpen
          this.webSocket.onmessage = this.onMessage
          this.webSocket.onerror = this.onError
          this.webSocket.onclose = this.onClose
        }
      },

      // 会在webSocket 连接成功时调用
      onOpen () {
        console.log('webSocket 连接成功')
      },

      // 发生连接错误时调用
      onError () {
        console.log('webSocket 连接异常')
      },

      // 收到服务器消息时调用
      onMessage (event) {
        let that = this
        let data = JSON.parse(event.data) // 获取服务的发送的消息数据
        that.$notify({
          title: '警告',
          message: data.message,
          type: 'warning'
        })

        setTimeout(() => {
          sessionStorage.clear() // 清空缓存
          localStorage.clear()
          this.postLogout() // 将账号注销
          // that.$router.push('login')
        }, 5000) // 5秒后跳转到登录页面
      },

      onClose () {
        console.log('webSocket 关闭成功')
      },

      postLogout () {
        this.axios.post(this.$http.httpUrl('/logout')).then(response => {
          if (response.data.code === 1) {
            this.$message.success(response.data.message)
            localStorage.clear()
            setTimeout(() => {
              this.$router.push('login')
            }, 1000)
          }
        })
      },

      logout () {
        this.$confirm('确定退出系统吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.postLogout()
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消'
          })
        })
      }
    }
  }
</script>
<style scoped>
  .site-navbar__brand {
    font-size: 16px !important;
  }
  .site-navbar {
    background: #0e90d2;
  }
</style>
