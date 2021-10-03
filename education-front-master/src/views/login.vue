<template>
  <div class="loginpage">
    <el-card class="box-card">
      <div class="title">智慧云智能教育平台</div>

      <div class="login-form">
        <el-form ref="form" :model="form" :rules="rules" @keyup.enter.native="doLogin()" label-width="80px">
          <el-form-item prop="userName">
            <el-input v-model="form.userName" placeholder="用户名" prefix-icon="el-icon-user-solid"></el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="el-icon-key"></el-input>
            <el-checkbox v-model="form.checked" style="margin-top: 7px">一周内自动登录</el-checkbox>
            <span class="forget">忘记密码?</span>
          </el-form-item>

          <el-form-item>
            <el-button @click="doLogin" type="primary">登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'login.vue',
    data () {
      return {
        rules: {
          userName: [
            {required: true, message: '请输入用户名', trigger: 'blur'},
          ],
          password: [
            {required: true, message: '请输入密码', trigger: 'blur'},
          ],
        },
        imgUrl: this.$httpApi.httpUrl('/image'),
        form: {
          userName: '',
          password: '',
          checked: false
        }
      }
    },

    methods: {

      loginSuccess (response, that) {
        let userInfo = response.data.data.userInfo
        localStorage.setItem("userInfo", JSON.stringify(userInfo))
        localStorage.setItem("front-token", response.data.data.token)
        that.$store.commit('updateUserInfo', userInfo)
        that.$message({
          type: 'success',
          message: response.data.message
        })
        setTimeout(function () {
          that.$router.push('/home')
        }, 1000)
      },

      doLogin () {
        let that = this
        this.$refs['form'].validate((valid) => {
          if (valid) {
            that.axios.post(that.$httpApi.httpUrl('/student/login'), that.form)
              .then(function (response) {
                if (response.data.code === 1) {
                  that.loginSuccess(response, that)
                } else {
                  that.$message({
                    type: 'error',
                    message: response.data.message
                  })
                }
              })
          } else {
            console.log('error submit!!');
            return false;
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>

  .forget {
    float: right;
    margin-top: 7px;
    cursor: pointer;
  }
  .forget:hover {
    color: #409eff;
  }
  .title {
    position: absolute;
    left: 25%;
    top: 10%;
    color: #409eff;
    font-weight: 700;
    font-size: 24px;
    margin-bottom: 20px;
    text-align: center;
  }

  .box-card {
    height: 380px;
    width: 420px;
    position: relative;
    top: 20%;
    left: 60%;
  }

  .el-button {
    width: 100%;
  }
  .login-form {
    width: 420px;
    position: absolute;
    top: 30%;
    right: 10%;
  }
  .loginpage{
    height: 100%;
    width: 100%;
    box-sizing: border-box;
    background: url("/static/image/timg.jpg");
    background-size:100% 100%; //   background: url('/static/image/loginng.gif') no-repeat;
    background-position: center;
    position: fixed;
  }
</style>
