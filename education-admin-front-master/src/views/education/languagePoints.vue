<template>
  <div class="mod-user">
    <div class="mod-user">
      <el-form :model="query" :inline="true">
        <el-form-item>
          <el-input v-model="query.keyWord" clearable placeholder="知识点内容"></el-input>
        </el-form-item>

        <el-form-item>
          <el-select
            @change="changeSchoolType"
            v-model="query.school_type"
            filterable
            clearable
            placeholder="请选择所属阶段">
            <el-option
              v-for="item in schoolTypeList"
              :key="item.id"
              :label="item.value"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-select
            v-model="query.grade_type"
            filterable
            clearable
            placeholder="请选择年级">
            <el-option
              v-for="item in queryGradeInfoList"
              :key="item.id"
              :label="item.value"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="success" icon="el-icon-search">查询</el-button>
          <el-button  type="primary">新增</el-button>
          <!--  <el-button v-if="hasPermission(['system:languagePoints:batchDelete'])" @click="batchDelete" type="danger">批量删除</el-button>-->
        </el-form-item>
      </el-form>
      <el-table
        :data="treeLanguagePointsList"
        :indent="10"
        border
        row-key="id"
        v-loading="loading"
        lazy
        :load="loadChildren"
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
        style="width: 100%;">

        <el-table-column
          prop="name"
          header-align="center"
          align="center"
          label="知识点内容">
        </el-table-column>


        <el-table-column
          prop="grade_type"
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
          prop="grade_name"
          header-align="center"
          align="center"
          label="所属年级">
        </el-table-column>

        <el-table-column
          prop="subject_name"
          header-align="center"
          align="center"
          label="所属科目">
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
            <el-button
              size="small"
              type="warning"
              >修改</el-button>
            <el-button
             size="small"
             type="danger"
             v-if="hasPermission(['system:course:deleteById'])"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        background
        @size-change="sizeChangeHandler"
        @current-change="currentChangeHandler"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        :total="totalCount"
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>

    <el-dialog
        :title="!form.id ? '新增知识点' : '修改知识点'"
        :close-on-click-modal="false"
        :visible.sync="showFlag">
        <el-form :model="form" :rules="rules" ref="form" @keyup.enter.native="saveOrUpdate()" label-width="120px">
          <el-form-item label="知识点内容" prop="name">
            <el-input v-model="form.name" placeholder="知识点内容"></el-input>
          </el-form-item>

          <el-form-item label="所属阶段" placeholder="所属阶段" prop="school_type">
            <el-select
              @change="changeSchoolType"
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

          <el-form-item label="所属年级" placeholder="所属年级" prop="grade_type">
            <el-select
              @change="changeGradeType"
              v-model="form.grade_type"
              filterable
              clearable
              placeholder="请选择">
              <el-option
                v-for="item in gradeInfoList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="所属科目" placeholder="所属科目" prop="subject_id">
            <el-select
              @change="changeSubject"
              v-model="form.subject_id"
              filterable
              clearable
              placeholder="请选择">
              <el-option
                v-for="item in subjectList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="排序">
            <el-input v-model="form.sort" placeholder="排序(数字越大,排名越靠前)"></el-input>
          </el-form-item>

          <el-form-item label="所属父级" placeholder="父级知识点">
            <el-cascader
              clearable
              :options="formTreeLanguagePointsList"
              v-model="form.parentIdArray"
              change-on-select>
            </el-cascader>
          </el-form-item>
        </el-form>
        <span slot="footer"  class="dialog-footer">
          <el-button @click="showFlag = false">取消</el-button>
          <el-button type="primary" @click="saveOrUpdateLanguagePoints()">确定</el-button>
       </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
    import {getDictValueByType} from '@/api/dict'
    import {getGradeInfoList} from '@/api/grade-info'
    import {getSubjectList} from '@/api/subject'

    export default {
      name: 'languagePoints',
      data () {
        return {
          currentPage: 1,
          pageSize: 10,
          loading: true,
          query: {
            keyWord: '',
            grade_type: '',
            school_type: ''
          },
          totalCount: 0,
          schoolTypeList: [],
          gradeInfoList: [], // 年级列表
          queryGradeInfoList: [], // 年级列表，查询使用
          subjectList: [], // 科目列表,
          formTreeLanguagePointsList: [], // 表单所属父类tree列表
          treeLanguagePointsList: [], // 顶级知识点列表数据，
          form: {
            name: '',
            grade_type: null,
            subject_id: null,
            school_type: null,
            parent_id: null,
            sort: 0,
            parentIdArray: []
          }
        }
      },

      mounted () {
        let params = {
          type: 'school_type'
        }
        // 获取所属阶段数据
        getDictValueByType(params, (response) => {
          this.schoolTypeList = response.data.data
        })
        getGradeInfoList(null, (response) => {
          this.gradeInfoList = response.data.data.dataList
          this.queryGradeInfoList = response.data.data.dataList
        })
        // 获取科目列表
        getSubjectList(null, (response) => {
          this.subjectList = response.data.data.dataList
        })
        // 加载一级知识点列表
        this.getParentLanguagePointsList()
      },

      methods: {

        /**
         * 去获取parent_id 为0的数据
0         */
        getParentLanguagePointsList () {
          this.axios.get(this.$http.httpUrl('/system/languagePoints'), {
            params: {
              offset: (this.currentPage - 1) * this.pageSize,
              limit: this.pageSize,
              parentId: 0 // 查询最顶级的知识点
            }
          }).then(response => {
            if (response.data.code === 1) {
              this.treeLanguagePointsList = response.data.data.dataList
              this.totalCount = response.data.data.total
              this.loading = false
            }
          })
        },

        saveOrUpdateLanguagePoints () {

        },

        loadChildren (tree, treeNode, resolve) {
          let parentId = tree.id
          this.axios.get(this.$http.httpUrl('/system/languagePoints'), {
            params: {
              parentId: parentId// 查询最顶级的知识点
            }
          }).then(response => {
            if (response.data.code === 1) {
              resolve(response.data.data.dataList) // 根据父类id获取子节点数据
            }
          })
        },

        sizeChangeHandler (val) {
          this.pageSize = val
          this.currentPage = 1
        },

        currentChangeHandler (val) {
          this.currentPage = val
        },

        formatterCreateDate (row, column) {
          if (!row.create_date) {
            return '--'
          }
          return this.moment(row.create_date).format('YYYY-MM-DD HH:mm')
        },

        changeSchoolType (val) {
          let params = {
            schoolType: val
          }
          getGradeInfoList(params, (response) => {
            this.gradeInfoList = response.data.data.dataList
          })
        },

        changeGradeType (val) {
          let params = {
            gradeType: val,
            schoolType: this.form.school_type
          }
          getSubjectList(params, (response) => {
            this.subjectList = response.data.data.dataList
          })
        },

        changeSubject (val) {

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
</style>
