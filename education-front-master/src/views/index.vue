<template>
  <div>
    <header-common/>
    <main-common/>
    <footerCommon/>
  </div>
</template>

<script>
  import headerCommon from './common/header-common'
  import mainCommon from './common/main-common'
  import footerCommon from './common/footer-common'
  export default {
    name: 'index',
    components: {headerCommon, mainCommon, footerCommon},
    data () {
      return {
         webSocket: null,
      }
    },

    mounted () {
      this.initWebSocket()
    },



    methods: {
      initWebSocket() {
        let that = this
        if ('WebSocket' in window) {
          let isHttps = 'https:' == document.location.protocol ? true : false;
          let hostName = location.hostname
          let port = location.port
          if (port && parseInt(port) != 80) {
            hostName += ":" + port
          }
          if (isHttps) { // https 需要使用 wss:// 方式 连接
            that.webSocket = new WebSocket("wss://" + hostName + "/webSocket")
          } else {
            that.webSocket = new WebSocket("ws://" + hostName + "/webSocket")
          }
          that.webSocket.onopen = that.onOpen
          that.webSocket.onmessage = that.onMessage
          that.webSocket.onclose = that.onClose
        }
      },

      onOpen() {
        console.log("webSocket连接成功");
      },

      //接收消息
      onMessage(event) { //数据接收
        let that = this
        let data = JSON.parse(event.data);
        this.$message({
          type: 'error',
          message: data.message
        })
        setTimeout(function () {
          that.$router.push('login')
        }, 5000)
      },

      onClose(event) {  //关闭
        console.log("webSocket关闭成功");
      },
    }
  }
</script>

<style scoped>

</style>
