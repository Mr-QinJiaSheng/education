<template>
  <div>
    <el-dialog
      @closed="closeAdminForm"
      :title="!form.id ? '新增管理员' : '修改管理员'"
      :close-on-click-modal="false"
      :visible.sync="showFlag">
      <el-form :model="form" :rules="rules" ref="form" label-width="80px">
        <el-form-item label="账号" prop="login_name">
          <el-input v-model="form.login_name" placeholder="登录帐号"></el-input>
        </el-form-item>

        <el-form-item label="所属学校">
          <el-select
            v-model="form.school_id"
            filterable
            clearable
            placeholder="所属学校">
            <el-option
              v-for="item in schoolList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" auto-complete="off" placeholder="姓名"></el-input>
        </el-form-item>

        <template v-if="!form.id">
          <el-form-item label="密码" prop="password" :class="{ 'is-required': !form.id }">
            <el-input v-model="form.password" @focus="changePasswordType" :type="passwordType" placeholder="密码"></el-input>
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword" :class="{ 'is-required': !form.id }">
            <el-input v-model="form.confirmPassword" :type="passwordType" placeholder="确认密码"></el-input>
          </el-form-item>
        </template>

        <el-form-item label="手机号">
          <el-input v-model="form.mobile" placeholder="手机号"></el-input>
        </el-form-item>

        <el-form-item label="角色" size="mini" prop="roleIds">
          <el-checkbox-group v-model="form.roleIds">
            <el-checkbox
              v-for="role in roleList"
              :key="role.id"
              :label="role.id">{{role.name}}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="状态" size="mini" prop="status">
          <el-radio-group v-model="form.disabled_flag">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer"  class="dialog-footer">
        <el-button @click="showFlag = false">取消</el-button>
        <el-button type="primary" @click="saveOrUpdateAdmin()">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import {isMobile} from '@/utils/validate'
  export default {
    name: 'menu-form',
    props: {
      formData: {
        type: Object,
        required: true
      },

      showFlag: {
        type: Boolean,
        required: false,
        default: false
      }
    },
    watch: {
      'showFlag' (val) {
        this.dialogFormVisible = val
      },

      'formData' (val) {
        this.form = val
      }
    },

    data () {
      let validatorPassword = (rule, value, callback) => {
        if (!value) {
          callback(new Error('请输入确认密码'))
        } else {
          if (this.form.password !== value) {
            callback(new Error('密码与确认密码不一致'))
          }
        }
        callback()
      }

      let validatorMobile = (rule, value, callback) => {
        if (value) {
          if (!isMobile(value)) {
            callback(new Error('手机号非法'))
          }
        }
        callback()
      }
      return {
        passwordType: 'text',
        dialogFormVisible: false,
        schoolList: [], // 学校列表
        roleList: [], // 角色列表
        form: {},
        rules: {
          name: [
            { required: true, message: '请输入账号名', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
          ],
          confirmPassword: [
            { required: true, validator: validatorPassword, trigger: 'blur' }
          ],
          roleIds: [
            { required: true, message: '请选择角色', trigger: 'blur' }
          ],
          mobile: [
            { required: true, validator: validatorMobile, trigger: 'blur' }
          ]
        }
      }
    },

    mounted () {
      this.form = this.formData
      this.dialogFormVisible = this.showFlag
      this.getRoleList()
    },

    methods: {

      // 添加或修改管理员
      saveOrUpdateAdmin () {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            delete (this.form.parentArrayId)
            this.axios.post(this.$http.httpUrl('/system/admin'), this.form)
              .then(response => {
                if (response.data.code === 1) {
                  this.$message.success(response.data.message)
                  this.closeAdminForm()
                } else {
                  this.$message.error(response.data.message)
                }
              })
          }
        })
      },

      changePasswordType () {
        this.passwordType = 'password'
      },

      closeAdminForm () {
        this.dialogFormVisible = false
        this.$emit('closeAdminForm', false)
      },

      getRoleList () {
        this.axios.get(this.$http.httpUrl('/system/role'), {
          params: {
          }
        }).then(response => {
          this.roleList = response.data.data.dataList
        })
      }
    }
  }
</script>

<style scoped>

</style>
