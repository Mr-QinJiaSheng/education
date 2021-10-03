<template>
  <div>
    <keep-alive>
      <div class="header">
        <div class="systemtitle">
          <img src="/static/image/logo.png" alt="">
        </div>
        <ul class="head-nav">
          <router-link
            :to="items.path"
            tag="li"
            :class="defaultSelectPage === items.path ? 'activeitem active' : 'activeitem'"
            @click.native="selectMenu(items)"
            v-for="(items, index) in menuList"
            :key="index" >
            {{items.label}}
           <!-- <ul class="itemli" v-if="items.isT">
              <router-link :to="item.path" tag="li" @click.native="selectMneu()" v-for="(item,inx) in items.children" :key="inx">{{item.label}}</router-link>
            </ul>-->
          </router-link>
        </ul>
        <el-dropdown>
            <span class="el-dropdown-link">
              <img v-if="!userInfo.head_img" src="@/assets/img/userImg.png">
              <img v-else :src="fileUrl + userInfo.head_img">
              {{userInfo.name}}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
          <el-dropdown-menu slot="dropdown">
            <!--<el-dropdown-item>个人资料</el-dropdown-item>-->
            <el-dropdown-item>
              <el-badge :value="12" class="item">
                <span>消息通知</span>
              </el-badge>
            </el-dropdown-item>
            <el-dropdown-item @click.native="beforeUpdatePassword()">修改密码</el-dropdown-item>
            <el-dropdown-item @click.native="logout()">退出</el-dropdown-item>
          </el-dropdown-menu>

          <el-dialog append-to-body title="修改密码" :visible.sync="dialogFormVisible">
            <el-form :model="form">
              <el-form-item label="用户名" :label-width="formLabelWidth">
                <el-input v-model="userInfo.name" :disabled="true" autocomplete="off"></el-input>
              </el-form-item>

              <el-form-item label="密码" :label-width="formLabelWidth" >
                <el-input v-model="form.newPassword" autocomplete="off" show-password></el-input>
              </el-form-item>
              <el-form-item label="确认密码" :label-width="formLabelWidth">
                <el-input v-model="form.confirmPassword" autocomplete="off" show-password></el-input>
              </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
              <el-button @click="dialogFormVisible = false">取 消</el-button>
              <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
            </div>
          </el-dialog>
        </el-dropdown>
      </div>
    </keep-alive>
  </div>
</template>

<script>
  import {clearLoginInfo} from '@/api/common'
  export default {
    name: 'header-common',
    data() {
      return {
        userInfo: {},
        fileUrl: this.$store.state.fileHost,
        dialogTableVisible: false,
        dialogFormVisible: false,
        fileHost: this.$store.state.fileHost,
        form: {
          newPassword: '',
          confirmPassword: ''
        },
        formLabelWidth: '120px',
        defaultSelectPage: null,
        menuList:[
          {label:'首页', active:'', isT: false, path: '/home'},
          {label:'课程强化训练', active:'', isT: false, path: '/course'},
          {label:'考试中心', active:'', isT: false, path: '/questionMode'},
          {label:'考试记录', active:'', isT: false, path: '/examHistory'},
          {label:'我的错题本', active:'', isT: false, path: '/wrongBookList'},
        ]
      }
    },

    watch: {
      "$route"() {

      }
    },

    mounted () {
      this.userInfo = this.$store.getters.getUserInfo
      let select = sessionStorage.getItem('select_menu_index')
      if (select) {
          this.defaultSelectPage = select
      } else {
          this.defaultSelectPage = '/home'
      }
    },

    methods: {

        selectMenu (menu) {
            this.defaultSelectPage = menu.path
            sessionStorage.setItem('select_menu_index',  this.defaultSelectPage)
        },


        beforeUpdatePassword () {
          let that = this
          that.dialogFormVisible = true
        },


        logoutCommon (that) {
          that.axios.post(that.$httpApi.httpUrl('/student/logout')).then(function (response) {
            if (response.data.code === 1) {
              that.$message({
                type: 'success',
                message: response.data.message
              })
              clearLoginInfo()
              setTimeout(function () {
                that.$router.push({name: 'login'})
              }, 1000)
            } else {
              that.$message({
                type: 'error',
                message: response.data.message
              })
            }
          })
        },

      // 退出
        logout () {
          let that = this
          that.$confirm('确定退出系统吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            that.logoutCommon(that)
          })
        },
      }
  };
</script>
<style lang="less" scoped>
  .el-dropdown-link {
    width: 50px;
    cursor: pointer;
    img{
      width: 32px;
      height: 32px;
      display: inline-block;
      vertical-align: middle;
    }
  }
  .header{
    width: 1200px;
    height: 100px;
    margin: 0 auto;
    position: relative;
    .el-dropdown{position: absolute;right: 0;top: 35px;height: 36px;line-height:36px;cursor: pointer;
      .cp{font-size: 36px;color: #009CDE;padding-right: 5px;vertical-align:middle;}
    }
    .systemtitle{width: 400px;height: 100%;;overflow: hidden;padding-top:20px;padding-bottom:10px;
      img{width: 100%;height: 100%;}
    }
    .head-nav{position: absolute;right: 150px;top: 30px; display: flex;border: 1px solid #52565a;border-radius: 16px;overflow: visible;
      .activeitem{padding: 15px;font-size: 16px;color: rgb(0, 0, 0);position: relative;
        &:first-child{border-radius: 16px 0 0 16px}
        &:last-child{border-radius: 0 16px 16px 0}
        .itemli{width: 94px;position: absolute;left: 0;top: 51px;z-index: 111;display: none;
          li{width: 100%;padding: 15px;color: rgb(0, 0, 0)}
          li:hover{color: #52565a;}
        }
      }
      .active{background-color: #52565a;color: #fff;}
      .activeitem:hover{background-color: #52565a;color: #fff;cursor: pointer;
        .itemli{display: block;background-color: #fff;border-radius: 0 0 6px 6px;}
      }
    }

  }
</style>

