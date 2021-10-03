<template>
  <div class="mod-role">
    <el-form :form="query" :inline="true" @keyup.enter.native="getRoleList()">
      <el-form-item>
        <el-input v-model="query.name" placeholder="角色名称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="getRoleList" icon="el-icon-search">查询</el-button>
        <el-button type="primary" @click="addRole">新增</el-button>
        <el-button @click="batchDeleteRole" type="danger">批量删除</el-button>
      </el-form-item>
    </el-form>

    <el-table
      border
      :data="roleList"
      @selection-change="selectRoleData"
      style="width: 100%">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>

      <el-table-column
        prop="name"
        align="center"
        label="角色名称">
      </el-table-column>

      <el-table-column
        prop="remark"
        align="center"
        label="备注" >
      </el-table-column>

      <el-table-column
        prop="create_date"
        align="center"
        :formatter="formatterCreateDate"
        label="创建时间">
      </el-table-column>

      <el-table-column align="center" label="操作">
        <template slot-scope="scope">
          <el-button type="success" v-if="hasPermission('system:role:savePermission')" @click="changePermission(scope.row)" size="small">权限设置</el-button>
          <el-button type="warning" v-if="hasPermission('system:role:update')" @click="editRole(scope.row)" size="small">修改</el-button>
          <el-button type="danger" v-if="hasPermission('system:role:deleteById')" @click="deleteById(scope.row)" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="权限设置" @opened="dialogOpened"  @closed="dialogClosed" :visible.sync="permissionVisible">
      <el-tree
        :data="permissionList"
        show-checkbox
        ref="tree"
        node-key="id"
        :default-checked-keys="checkedPermissionList"
        :props="defaultProps">
      </el-tree>

      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionVisible = false" >取 消</el-button>
        <el-button @click="saveRolePermission" type="primary">确 定</el-button>
      </div>
    </el-dialog>

    <el-pagination
      background
      @size-change="changeRoleSize"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="changeCurrentPage"
      :page-size="pageSize"
      :page-sizes="[10, 20, 30, 40]"
      :total="total">
    </el-pagination>

    <el-dialog
      :title="!form.id ? '新增角色' : '修改角色'"
      :close-on-click-modal="false"
      :visible.sync="visible">
      <el-form :model="form" :rules="rules" ref="form"
               label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="角色名称"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="备注"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button @click="saveOrUpdate" type="primary">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    data () {
      return {
        permissionList: [], // 权限列表
        checkedPermissionList: [], // 选中的权限集合
        currentPage: 1,
        total: 0,
        pageSize: 10, // 每页显示条数
        query: {
          name: ''
        },
        roleId: null,
        visible: false,
        roleList: [],
        form: {
          name: '',
          remark: ''
        },
        defaultProps: {
          children: 'children',
          label: 'name'
        },
        permissionVisible: false,
        checkRoleData: [],
        rules: {
          name: [
            {required: true, message: '请输入角色名称', trigger: 'blur'}
          ]
        }
      }
    },

    mounted () {
      this.getRoleList()
      this.getPermissionList() // 加载权限列表
    },

    methods: {

      dialogClosed () {
        this.$refs.tree.setCheckedKeys([])
        this.permissionVisible = false
      },
      dialogOpened () {
        this.$refs.tree.setCheckedKeys(this.checkedPermissionList)
      },

      // 权限设置
      changePermission (role) {
        this.roleId = role.id

        this.axios.get(this.$http.httpUrl('/system/menu/getRolePermission'), {
          params: {
            roleId: this.roleId
          }
        }).then(response => {
          if (response.data.code === 1) {
            this.permissionVisible = true
            this.checkedPermissionList = response.data.data
          }
        })
      },

      // 保存角色权限
      saveRolePermission () {
        //
        this.checkedPermissionList = []
        let checkedNodes = this.$refs.tree.getCheckedNodes() // 获取选中的节点数组
        if (checkedNodes.length > 0) {
          checkedNodes.forEach(node => {
            this.checkedPermissionList.push(node.id)
          })
        }
        // 获取所有选中的父类id集合
        let checkParentPermissionIds = this.$refs.tree.getHalfCheckedKeys()
        if (checkParentPermissionIds.length > 0) {
          this.checkedPermissionList = this.checkedPermissionList.concat(checkParentPermissionIds)
        }
        this.axios.post(this.$http.httpUrl('/system/role/savePermission'), {
          'permission': this.checkedPermissionList,
          'roleId': this.roleId
        }).then(response => {
          this.permissionVisible = false
          if (response.data.code === 1) {
            this.$message.success(response.data.message)
          } else {
            this.$message.error(response.data.message)
          }
        })
      },

      getPermissionList () {
        this.axios.get(this.$http.httpUrl('/system/menu/getMenuList'))
          .then(response => {
            this.permissionList = response.data.data.dataList
          })
      },

      changeRoleSize (val) {
        this.pageSize = val
        this.currentPage = 1
        this.getRoleList()
      },

      changeCurrentPage (val) {
        this.currentPage = val
        this.getRoleList()
      },

      addRole () {
        this.visible = true
        this.form = {
          name: '',
          remark: ''
        }
      },

      formatterCreateDate (row) {
        if (!row.create_date) {
          return '--'
        }
        return this.moment(row.create_date).format('YYYY-MM-DD HH:mm')
      },

      getRoleList () {
        this.axios.get(this.$http.httpUrl('/system/role'), {
          params: {
            offset: (this.currentPage - 1) * this.pageSize,
            limit: this.pageSize,
            name: this.query.name
          }
        }).then(response => {
          if (response.data.code === 1) {
            this.roleList = response.data.data.dataList
            this.total = response.data.data.total
          }
        })
      },

      // 修改角色
      editRole (role) {
        this.visible = true
        this.form = {
          id: role.id,
          name: role.name,
          remark: role.remark
        }
      },

      selectRoleData (val) {
        this.checkRoleData = val
      },

      // 批量删除角色
      batchDeleteRole () {
        if (this.checkRoleData.length === 0) {
          this.$message.error('请选择需要删除的角色')
          return false
        }
        let roleIds = []
        this.checkRoleData.forEach(role => {
          roleIds.push(role.id)
        })
        this.$confirm('是否删除选中的角色?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.axios.delete(this.$http.httpUrl('/system/role/batchDeleteByRoleIds'), {
            data: roleIds
          }).then(response => {
            if (response.data.code === 1) {
              this.$message.success(response.data.message)
              this.getRoleList()
            } else {
              this.$message.error(response.data.message)
            }
          })
        }).catch(() => {

        })
      },

      /**
       * 删除角色
       * @param role
       */
      deleteById (role) {
        this.$confirm('是否删除该角色?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.axios.delete(this.$http.httpUrl('/system/role'), {
            data: role
          }).then(response => {
            if (response.data.code === 1) {
              this.$message.success(response.data.message)
              this.getRoleList()
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

      saveOrUpdate () {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            this.axios.post(this.$http.httpUrl('/system/role'), this.form)
              .then((response) => {
                if (response.data.code === 1) {
                  this.$message.success(response.data.message)
                  this.visible = false
                  this.getRoleList()
                } else {
                  this.$message.error(response.data.message)
                }
              })
          }
        })
      }
    }
  }
</script>
