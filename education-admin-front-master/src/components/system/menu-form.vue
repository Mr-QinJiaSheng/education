<template>
    <div>
      <el-dialog @closed="closeMenuForm" :title="!form.id ? '添加菜单' : '修改菜单'" :visible.sync="dialogFormVisible">
        <el-form :model="form" :rules="rules" ref="form">
          <el-form-item label="菜单名称" prop="name" :label-width="formLabelWidth">
            <el-input v-model="form.name" placeholder="菜单名称" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item label="菜单URL" :label-width="formLabelWidth">
            <el-input v-model="form.url" placeholder="菜单URL" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item label="权限标识"  :label-width="formLabelWidth">
            <el-input v-model="form.permission" placeholder="请输入权限标识" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item label="菜单类型" :label-width="formLabelWidth">
            <el-radio-group v-model="form.type">
              <el-radio :label="1">目录</el-radio>
              <el-radio :label="2">菜单</el-radio>
              <el-radio :label="3">按钮</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="所属菜单" placeholder="请选择所属菜单" :label-width="formLabelWidth">
            <el-cascader
              v-model="form.parentArrayId"
              :props="props"
              :options="parentMenuList"
              @change="changeParentMenu"></el-cascader>
          </el-form-item>

          <el-form-item label="排序" :label-width="formLabelWidth">
            <el-input v-model="form.sort" placeholder="请输入排序" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveOrUpdateMenu">确 定</el-button>
        </div>
      </el-dialog>
    </div>
</template>

<script>
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
        },

        menuList: {
          type: Array,
          required: true,
          default: []
        }
      },
      watch: {
        'showFlag' (val) {
          this.dialogFormVisible = val
        },

        'formData' (val) {
          this.form = val
        },

        'menuList' (val) {
          this.parentMenuList = val
        }
      },

      data () {
        return {
          props: {
            value: 'id',
            label: 'name',
            checkStrictly: true
          },
          parentMenuList: [], // 所属菜单列表
        //  parentArrayId: [],
          formLabelWidth: '100px',
          dialogFormVisible: false,
          form: {},
          rules: {
            name: [
              { required: true, message: '请输入菜单名称', trigger: 'blur' }
            ]
          }
        }
      },

      mounted () {
        this.form = this.formData
        this.dialogFormVisible = this.showFlag
        this.parentMenuList = this.menuList
       // this.getParentMenuList()
      },

      methods: {
        changeParentMenu (val) {

        },

        closeMenuForm () {
          this.dialogFormVisible = false //
          this.$emit('closeMenuForm', false)
        },

        saveOrUpdateMenu () {
          if (this.form.parentArrayId.length > 0) {
            this.form.parent_id = this.form.parentArrayId[this.form.parentArrayId.length - 1]
          }
          this.$refs['form'].validate((valid) => {
            if (valid) {
              delete (this.form.parentArrayId)
              this.axios.post(this.$http.httpUrl('/system/menu'), this.form)
                .then(response => {
                  if (response.data.code === 1) {
                    this.$message.success(response.data.message)
                  } else {
                    this.$message.error(response.data.message)
                  }
                  this.closeMenuForm()
                })
            } else {
            }
          })
        }
      }
    }
</script>

<style scoped>

</style>
