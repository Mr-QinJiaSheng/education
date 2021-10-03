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
          </el-form-item>

          <el-form-item  prop="code">
            <el-row :gutter="20">
              <el-col :span="14">
                <el-input v-model="form.imageCode" placeholder="验证码">
                </el-input>
              </el-col>
              <el-col :span="10" class="login-captcha">
                <img :src="imgUrl" @click="changeCode()" title="换一张" alt="换一张">
              </el-col>
            </el-row>
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
    data () {
      return {
        key: null,
        imgUrl: '',
        form: {
          userName: '',
          password: '',
          imageCode: '',
          checked: false
        },
        rules: {
          userName: [
            { required: true, message: '帐号不能为空', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '密码不能为空', trigger: 'blur' }
          ],

          imageCode: [
            { required: true, message: '请输入验证码', trigger: 'blur' }
          ]
        }
      }
    },

    mounted () {
      this.changeCode()
    },

    methods: {

      changeCode () {
        this.key = new Date().getTime()
        this.imgUrl = this.$http.httpUrl('/image?key=' + this.key)
      },

      // 提交表单
      doLogin () {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            this.form.key = this.key
            this.axios.post(this.$http.httpUrl('/login'), this.form)
              .then((response) => {
                 // 登录成功
                if (response.data.code === 1) {
                  this.$message.success(response.data.message)
                  localStorage.setItem('token', response.data.data.token)
                  localStorage.setItem('userInfo', JSON.stringify(response.data.data.userInfo))
                  setTimeout(() => {
                    this.$router.push('home')
                  }, 1500)
                } else {
                  this.$message.error(response.data.message)
                  this.changeCode()
                }
              })
           // alert('submit!');
          } else {
          //  console.log('error submit!!');
            return false
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
    top: 12%;
    color: #409eff;
    font-weight: 700;
    font-size: 24px;
    margin-bottom: 20px;
    text-align: center;
  }

  .box-card {
    height: 410px;
    width: 420px;
    position: relative;
    top: 20%;
    left: 36%;
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
    background: url("/static/img/login-bg.jpg");
    background-size:100% 100%; //   background: url('/static/image/loginng.gif') no-repeat;
    background-position: center;
    position: fixed;
  }
</style>
