<template>
  <div class="mod-user">
    <div class="mod-user">
      <el-form :model="query" :inline="true" >
        <el-form-item>
          <el-input v-model="query.keyWord" placeholder="请输入年级名称"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button  type="success" @click="getSubjectList" icon="el-icon-search">查询</el-button>
          <el-button  type="primary" @click="addSubject">新增</el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="gradeInfoList"
        border
        v-loading="loading"
        style="width: 100%;">

        <el-table-column
          header-align="center"
          align="center"
          label="所属阶段">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.school_type === 1" size="small">小学</el-tag>
            <el-tag v-else-if="scope.row.school_type === 2" size="small">初中</el-tag>
            <el-tag v-else="scope.row.school_type === 3" size="small">高中</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="name"
          header-align="center"
          align="center"
          label="科目名称">
        </el-table-column>

        <el-table-column
          prop="name"
          header-align="center"
          align="center"
          label="所属年级">
        </el-table-column>

        <el-table-column
          prop="create_date"
          header-align="center"
          align="center"
          width="180"
          :formatter="formatterCreateDate"
          label="创建时间">
        </el-table-column>

        <el-table-column
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-dropdown>
              <span class="el-dropdown-link">
                操作<i class="el-icon-caret-bottom"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-if="scope.row.status === 1"
                  @click.native="updateCourse(scope.row)"
                  icon="el-icon-edit">修改</el-dropdown-item>
                <el-dropdown-item
                  @click.native="deleteCourseById(scope.row)"
                  v-if="scope.row.status === 1"
                  icon="el-icon-delete">删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        background
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        :total="totalCount"
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>

      <el-dialog
        :title="!form.id ? '新增年级' : '修改年级'"
        :close-on-click-modal="false"
        :visible.sync="showFlag">
        <el-form :model="form" :rules="rules" ref="form" @keyup.enter.native="saveOrUpdateGradeInfo()" label-width="80px">
          <el-form-item label="年级名称" prop="name">
            <el-input v-model="form.name" placeholder="年级名称"></el-input>
          </el-form-item>

          <el-form-item label="所属阶段" placeholder="所属阶段" prop="school_type">
            <el-select
              v-model="form.school_type"
              filterable
              clearable
              placeholder="请选择">
              <el-option
                v-for="item in schoolTypeList"
                :key="item.id"
                :label="item.value"
                :value="item.code">
              </el-option>
            </el-select>
          </el-form-item>

        </el-form>
        <span slot="footer"  class="dialog-footer">
          <el-button @click="showFlag = false">取消</el-button>
          <el-button type="primary" size="small" @click="saveOrUpdateGradeInfo()">保存并发布</el-button>
       </span>
      </el-dialog>
    </div>
  </div>
</template>
<script>
  /* eslint-disable no-unused-vars,no-trailing-spaces,no-redeclare */
  import {getDictValueByType} from '@/api/dict'

  export default {
    name: 'course',
    data () {
      return {
        schoolTypeList: [], // 学校类型列表
        gradeInfoList: [],
        currentPage: 1,
        pageSize: 10,
        totalCount: 0,
        showFlag: false,
        query: {
          keyWord: '',
          gradeType: ''
        },
        form: {
          school_type: '',
          name: ''
        },
        loading: true,
        rules: {
          school_type: {
            required: true, message: '请选择阶段', trigger: 'blur'
          },
          name: {
            required: true, message: '请输入课程介绍', trigger: 'blur'
          }
        }
      }
    },

    watch: {

    },

    mounted () {
      let params = {
        type: 'school_type'
      }
      // 获取所属阶段数据
      getDictValueByType(params, (response) => {
        this.schoolTypeList = response.data.data
      })

      this.getGradeInfoList()
    },

    methods: {

      getGradeInfoList () {

      },

      saveOrUpdateGradeInfo () {

      },

      clearForm () {
        this.form = {
          school_type: '',
          name: ''
        }
      },

      addGradeInfo () {
        this.clearForm()
        this.showFlag = true
      },

      formatterCreateDate (row, column) {
        if (!row.create_date) {
          return '--'
        }
        return this.moment(row.create_date).format('YYYY-MM-DD HH:mm')
      }
    }
  }
</script>
<style>
  .el-table__expand-icon {
    display: inline-block;
    margin-right: 15px;
    vertical-align:-5px;
  }

  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 90px;
    height: 90px;
    line-height: 90px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
