<template>
    <div>
      <el-form :form="query" :inline="true">
        <el-form-item>
          <el-input v-model="query.name" placeholder="用户名/手机号" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            type="success"
            @click="searchAdminList"
            icon="el-icon-search">查询</el-button>
          <el-button @click="addAdmin"  v-if="hasPermission('system:admin:save')" type="primary">新增</el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="adminList"
        border
        v-loading="loading"
        style="width: 100%;">

        <el-table-column
          prop="login_name"
          header-align="center"
          align="center"
          width="100"
          label="账号">
        </el-table-column>

        <el-table-column
          prop="name"
          header-align="center"
          align="center"
          width="80"
          label="姓名">
        </el-table-column>

        <el-table-column
          prop="mobile"
          header-align="center"
          align="center"
          width="120"
          label="手机号">
        </el-table-column>

        <el-table-column
          prop="school_name"
          header-align="center"
          align="center"
          :show-overflow-tooltip="true"
          width="120"
          label="所属学校">
        </el-table-column>

        <el-table-column
          prop="login_count"
          header-align="center"
          align="center"
          width="80"
          label="登录次数">
        </el-table-column>

        <el-table-column
          prop="login_ip"
          header-align="center"
          align="center"
          min-width="100"
          label="登录ip">
        </el-table-column>

        <el-table-column
          prop="disabled_flag"
          header-align="center"
          align="center"
          width="80"
          label="状态">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.disabled_flag" size="small" type="danger">禁用</el-tag>
            <el-tag v-else size="small">正常</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="last_login_time"
          header-align="center"
          align="center"
          min-width="140"
          :formatter="formatterLastLoginDate"
          label="最后登录时间">
        </el-table-column>

        <el-table-column
          prop="create_date"
          header-align="center"
          align="center"
          min-width="140"
          :formatter="formatterCreateDate"
          label="创建时间">
        </el-table-column>

        <el-table-column
          header-align="center"
          align="center"
          width="250"
          label="操作">
          <template slot-scope="scope">
            <el-button
              v-if="hasPermission('system:admin:resettingPassword')"
              @click="resettingPassword(scope.row)"
              icon="el-icon-setting"
              size="small"
              type="text">重置密码</el-button>
            <el-button
              size="small"
              v-if="hasPermission('system:admin:update')"
              @click="editAdmin(scope.row)"
              icon="el-icon-edit"
              type="text">修改</el-button>
            <el-button
              v-if="hasPermission('system:admin:deleteById')"
              size="small"
              @click="deleteByAdminId(scope.row)"
              icon="el-icon-delete"
              type="text">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        background
        @size-change="changeAdminSize"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="changeAdminCurrentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="totalCount">
      </el-pagination>


      <el-dialog
        title="重置密码"
        :close-on-click-modal="false"
        :visible.sync="passwordFlag">
        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" @keyup.enter.native="updatePassword()" label-width="80px">
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="passwordForm.password" placeholder="密码"></el-input>
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="确认密码"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer"  class="dialog-footer">
        <el-button @click="passwordFlag = false">取消</el-button>
        <el-button type="primary" @click="updatePassword()">修改</el-button>
      </span>
      </el-dialog>

      <admin-form
        @closeAdminForm="closeAdminForm"
       :formData="adminForm"
       :showFlag="showAdminForm">
      </admin-form>
    </div>
</template>

<script>
    import adminForm from '@/components/system/admin-form'
    export default {
      name: 'admin',
      components: {adminForm},
      data () {
        return {
          loading: true,
          adminList: [],
          passwordFlag: false,
          passwordForm: {
            adminId: null,
            encrypt: '',
            password: '',
            confirmPassword: ''
          },
          passwordRules: {
            password: [
              {required: true, message: '请输入密码', trigger: 'blur'}
            ],
            confirmPassword: [
              {required: true, message: '请输入确认密码', trigger: 'blur'}
            ]
          },
          query: {
            name: ''
          },
          showAdminForm: false,
          currentPage: 1,
          pageSize: 10,
          totalCount: 0,
          adminForm: {
            login_name: '',
            name: '',
            password: '',
            confirmPassword: '',
            mobile: '',
            school_id: 0,
            roleIds: [], // 角色id集合
            disabled_flag: 0
          }
        }
      },

      mounted () {
        this.getAdminList()
      },

      methods: {

        editAdmin (adminInfo) {
          this.axios.get(this.$http.httpUrl('/system/admin/getRoleIdsByAdminId'), {
            params: {
              adminId: adminInfo.id
            }
          }).then(response => {
            this.adminForm = {
              id: adminInfo.id,
              login_name: adminInfo.login_name,
              name: adminInfo.login_name,
              mobile: adminInfo.mobile,
              school_id: 0,
              roleIds: response.data.data, // 角色id集合
              disabled_flag: adminInfo.disabled_flag ? 1 : 0
            }
            this.showAdminForm = true
          })
        },

        getAdminList () {
          this.axios.get(this.$http.httpUrl('/system/admin'), {
            params: {
              offset: (this.currentPage - 1) * this.pageSize,
              limit: this.pageSize,
              keyWord: this.query.name
            }
          }).then(response => {
            this.loading = false
            this.adminList = response.data.data.dataList
            this.totalCount = response.data.data.total
          })
        },

        deleteByAdminId (admin) {
          let params = {
            id: admin.id,
            super_flag: admin.super_flag
          }
          this.$confirm('是否删除该管理员?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.axios.delete(this.$http.httpUrl('/system/admin/deleteById'), {
              data: params
            }).then(response => {
              if (response.data.code === 1) {
                this.$message.success(response.data.message)
                this.getAdminList()
              } else {
                this.$message.error(response.data.message)
              }
            })
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除'
            })
          })
        },
        resettingPassword (adminInfo) {
          this.passwordForm.id = adminInfo.id
          this.passwordForm.encrypt = adminInfo.encrypt
          this.passwordFlag = true
        },

        updatePassword () {
          this.$refs['passwordForm'].validate((valid) => {
            if (valid) {
              if (!this.passwordForm.password === this.passwordForm.confirmPassword) {
                this.$message.error('密码与确认密码不一致')
                return false
              }
              this.axios.post(this.$http.httpUrl('/system/admin/resettingPassword'), this.passwordForm)
                .then(response => {
                  if (response.data.code === 1) {
                    this.$message.success(response.data.message)
                    this.passwordFlag = false
                  } else {
                    this.$message.error(response.data.message)
                  }
                })
            }
          })
        },

        addAdmin () {
          this.showAdminForm = true
          this.adminForm = {
            login_name: '',
            name: '',
            password: '',
            confirmPassword: '',
            mobile: '',
            school_id: 0,
            roleIds: [], // 角色id集合
            disabled_flag: 0
          }
        },

        closeAdminForm () {
          this.showAdminForm = false
          this.getAdminList()
        },

        changeAdminSize (val) {
          this.pageSize = val
          this.currentPage = 1
          this.getAdminList()
        },

        changeAdminCurrentPage (val) {
          this.currentPage = val
          this.getAdminList()
        },

        searchAdminList () {
          this.currentPage = 1
          this.getAdminList()
        },

        formatterCreateDate (row) {
          if (!row.create_date) {
            return '--'
          }
          return this.moment(row.create_date).format('YYYY-MM-DD HH:mm')
        },

        formatterLastLoginDate (row) {
          if (!row.last_login_time) {
            return '--'
          }
          return this.moment(row.last_login_time).format('YYYY-MM-DD HH:mm')
        }
      }
    }
</script>

<style scoped>

</style>
