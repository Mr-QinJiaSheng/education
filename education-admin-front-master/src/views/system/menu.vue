<template>
    <div>
      <el-form :inline="true">
        <el-form-item>
          <el-button @click="addMenu" type="primary">新增菜单</el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="menuList"
        style="width: 100%;margin-bottom: 20px;"
        row-key="id"
        border>
        <el-table-column
          prop="name"
          label="菜单名称"
          align="center"
          sortable
          min-width="120">
        </el-table-column>
        <el-table-column
          prop="parent_name"
          label="上级菜单"
          align="center"
          sortable
          width="180">
        </el-table-column>

        <el-table-column
          prop="icon"
          align="center"
          width="80"
          label="菜单图标">
          <template slot-scope="scope">
            <icon-svg :name="scope.row.icon"></icon-svg>
          </template>
        </el-table-column>

        <el-table-column
          prop="type"
          align="center"
          width="100"
          label="菜单类型">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 1" size="small" type="success">目录</el-tag>
            <el-tag v-else-if="scope.row.type === 2" size="small" type="warning">菜单</el-tag>
            <el-tag v-else size="small" type="info">按钮</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="菜单图标"
          width="60"
          align="center"
          label="排序">
        </el-table-column>

        <el-table-column
          prop="url"
          align="center"
          width="100"
          label="菜单URL">
        </el-table-column>

        <el-table-column
          prop="permission"
          align="center"
          width="180"
          label="权限标识">
        </el-table-column>

        <el-table-column
          align="center"
          prop="菜单URL"
          label="操作">
          <template slot-scope="scope">
            <el-button
              size="small"
              type="warning"
              @click="updateMenu(scope.row)"
              icon="el-icon-edit" plain>修改</el-button>

            <el-button
              size="small"
              type="warning"
              icon="el-icon-delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <menu-form
        @closeMenuForm="closeMenuForm"
        :menuList="menuList"
        :formData="form"
        :showFlag="showMenuForm">
      </menu-form>
    </div>
</template>

<script>
    import menuForm from '@/components/system/menu-form'
    export default {
      name: 'menu.vue',
      components: {menuForm},
      data () {
        return {
          menuList: [],
          showMenuForm: false,
          form: {
            name: '', // 菜单名称,
            url: '', // 菜单url,
            type: 1, // 菜单目录
            permission: '', // 权限标识
            parent_id: 0, // 所属父类id,
            sort: 0,
            parentArrayId: []
          }
        }
      },

      mounted () {
        this.getMenuList() // 加载菜单列表
      },

      methods: {

        getMenuList () {
          this.axios.get(this.$http.httpUrl('/system/menu/getMenuList'))
            .then(response => {
              this.menuList = response.data.data.dataList
            })
        },

        addMenu () {
          this.showMenuForm = true
          this.form = {
            name: '', // 菜单名称,
            url: '', // 菜单url,
            type: 1, // 菜单目录
            permission: '', // 权限标识
            parent_id: 0, // 所属父类id,
            sort: 0,
            parentArrayId: []
          }
        },

        updateMenu (menu) {
          this.axios.get(this.$http.httpUrl('/system/menu/findById'), {
            params: {
              menuId: menu.id
            }
          }).then(response => {
            let data = response.data.data
            this.form = {
              id: data.id,
              name: data.name, // 菜单名称,
              url: data.url, // 菜单url,
              type: data.type, // 菜单目录
              permission: data.permission, // 权限标识
              parent_id: data.parent_id, // 所属父类id,
              parentArrayId: data.parentIds,
              sort: data.sort
            }
            this.showMenuForm = true
          })
        },

        closeMenuForm (val) {
          // 关闭menuForm组价
          this.showMenuForm = val
          this.getMenuList()
        }
      }
    }
</script>

<style scoped>

</style>
