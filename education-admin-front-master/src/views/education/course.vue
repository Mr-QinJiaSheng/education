<template>
  <div class="mod-user">
    <div class="mod-user">

      <el-form :model="query" :inline="true" >
        <el-form-item>
          <el-input v-model="query.keyWord" placeholder="请输入课程名称"></el-input>
        </el-form-item>

        <el-form-item>
          <el-select
            v-model="query.gradeType"
            filterable
            clearable
            placeholder="请选择年级">
            <el-option
              v-for="item in queryGradeInfoList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button  type="success" @click="queryCourseList" icon="el-icon-search">查询</el-button>
          <el-button  type="primary" @click="addCourse">新增</el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="courseList"
        border
        v-loading="loading"
        style="width: 100%;">

        <el-table-column
          prop="name"
          header-align="center"
          align="center"
          label="课程名称">
        </el-table-column>

        <el-table-column
          prop="head_img"
          align="center"
          label="课程封面">
          <template slot-scope="scope">
            <img v-if="scope.row.head_img" style="width: 40px; height: 40px;" :src="fileUrl + scope.row.head_img"/>
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
          header-align="center"
          align="center"
          label="首页推荐">
          <template slot-scope="scope">
             <el-tag type="success" v-if="scope.row.recommend_index_flag">是</el-tag>
            <el-tag type="danger" v-else>否</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="status"
          header-align="center"
          align="center"
          label="状态">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 2" type="success" effect="dark">已上架</el-tag>
            <el-tag v-if="scope.row.status === 1" type="warning" effect="dark">草稿</el-tag>
            <el-tag v-if="scope.row.status === 0" type="danger" effect="dark">已下架</el-tag>
          </template>
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
                <el-dropdown-item icon="el-icon-tickets">试题管理</el-dropdown-item>
                <el-dropdown-item
                  @click.native="undercarriageCourse(scope.row,2)"
                  v-if="scope.row.status === 1"
                  icon="el-icon-upload2">上架</el-dropdown-item>
                <el-dropdown-item
                  v-if="scope.row.status === 2"
                  @click.native="undercarriageCourse(scope.row, 1)"
                  icon="el-icon-download">下架</el-dropdown-item>
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
        :title="!form.id ? '新增课程' : '修改课程'"
        :close-on-click-modal="false"
        :visible.sync="showFlag">
        <el-form :model="form" :rules="rules" ref="form" @keyup.enter.native="saveOrUpdateCourse()" label-width="80px">
          <el-form-item label="课程名称" prop="name">
            <el-input v-model="form.name" placeholder="课程名称"></el-input>
          </el-form-item>

          <el-form-item label="课程封面" prop="head_img">
            <div v-if="form.head_img">
              <ul class="el-upload-list el-upload-list--picture-card"
                  style="display: inline-block;">
                <li class="el-upload-list__item is-success">
                  <img :src="fileUrl + form.head_img" alt=""
                       class="el-upload-list__item-thumbnail">
                  <a class="el-upload-list__item-name"><i class="el-icon-document"></i></a>
                  <label class="el-upload-list__item-status-label">
                    <i class="el-icon-upload-success el-icon-check"></i></label> <i
                  class="el-icon-close"></i>
                  <i class="el-icon-close-tip">按 delete 键可删除</i>
                  <span class="el-upload-list__item-actions">
                        <span class="el-upload-list__item-preview"  @click="lookHeadImg"><i
                    class="el-icon-zoom-in"></i></span>
                          <span class="el-upload-list__item-delete" @click="deleteHeadImg"><i
                            class="el-icon-delete"></i></span>
                        </span>
                  <el-dialog
                    :visible.sync="headImageVisible"
                    custom-class="image-dialog"
                    append-to-body>
                    <img width="100%" :src="fileUrl + form.head_img" alt="">
                  </el-dialog>
                </li>
              </ul>
            </div>
            <div v-else>
              <el-upload
                :action="uploadAction"
                :headers="headers"
                :with-credentials="true"
                class="avatar-uploader"
                :on-success="uploadHeadImgSuccess"
                :before-upload="beforeUploadHeadImg"
                :show-file-list="false">
                <i class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </div>
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

          <el-form-item label="课程简介" prop="represent">
            <el-input type="textarea" v-model="form.represent" placeholder="请输入课程简介"></el-input>
          </el-form-item>

          <el-form-item label="首页推荐">
            <el-radio-group v-model="form.recommend_index_flag">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="排序">
            <el-input v-model="form.sort" placeholder="排序(数字越大,排名越靠前)"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer"  class="dialog-footer">
          <el-button @click="showFlag = false">取消</el-button>
          <el-button type="warning" v-if="form.status !== 2" size="small" @click="saveOrUpdateCourse(1)">保存草稿</el-button>
          <el-button type="primary" size="small" @click="saveOrUpdateCourse(2)">保存并发布</el-button>
       </span>
      </el-dialog>
    </div>
  </div>
</template>
<script>
  /* eslint-disable no-unused-vars,no-trailing-spaces,no-redeclare */
  import {getDictValueByType} from '@/api/dict'
  import {getGradeInfoList} from '@/api/grade-info'
  import {getSubjectList} from '@/api/subject'

  export default {
    name: 'course',
    data () {
      return {
        schoolTypeList: [], // 学校类型列表
        gradeInfoList: [], // 年级列表
        queryGradeInfoList: [],
        subjectList: [], // 科目列表
        courseList: [],
        uploadAction: this.$http.httpUrl('/upload/1'),
        currentPage: 1,
        pageSize: 10,
        totalCount: 0,
        showFlag: false,
        query: {
          keyWord: '',
          gradeType: ''
        },
        headers: {
          token: localStorage.getItem('token')
        },
        fileUrl: this.$http.getFileHost(), // 文件服务器地址 (服务的地址后面加上 /uploads)
        form: {
          head_img: '',
          name: '',
          status: null,
          recommend_index_flag: 0,
          represent: '',
          grade_type: '',
          school_type: '',
          subject_id: '',
          code: '',
          sort: 0
        },
        headImageVisible: false,
        loading: true,
        rules: {
          name: {
            required: true, message: '请选择阶段', trigger: 'blur'
          },
          head_img: {
            required: true, message: '请上传课程封面图', trigger: 'blur'
          },
          school_type: {
            required: true, message: '请选择阶段', trigger: 'blur'
          },
          grade_type: {
            required: true, message: '请选择年级', trigger: 'blur'
          },
          subject_id: {
            required: true, message: '请选择科目', trigger: 'blur'
          },
          represent: {
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
      getGradeInfoList(null, (response) => {
        this.gradeInfoList = response.data.data.dataList
        this.queryGradeInfoList = response.data.data.dataList
      })
      // 获取科目列表
      getSubjectList(null, (response) => {
        this.subjectList = response.data.data.dataList
      })

      this.getCourseList()
    },

    methods: {
      queryCourseList () {
        this.currentPage = 1
        this.getCourseList()
      },

      // 上架或下架
      undercarriageCourse (item, status) {
        let message = null
        if (status === 1) {
          message = '确定下架课程' + item.name
        } else if (status === 2) {
          message = '确定上架课程' + item.name
        }
        this.$confirm(message + '?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.form.id = item.id
          this.form = {
            head_img: item.head_img,
            name: item.name,
            grade_type: item.grade_type,
            school_type: item.school_type,
            subject_id: item.subject_id,
            sort: item.sort,
            status: status,
            represent: item.represent,
            id: item.id,
            recommend_index_flag: item.recommend_index_flag ? 1 : 0
          }
          this.saveCommon()
        })
      },

      formatterCreateDate (row, column) {
        if (!row.create_date) {
          return '--'
        }
        return this.moment(row.create_date).format('YYYY-MM-DD HH:mm')
      },

      updateCourse (item) {
        this.form = {
          head_img: item.head_img,
          name: item.name,
          grade_type: item.grade_type,
          school_type: item.school_type,
          subject_id: item.subject_id,
          sort: item.sort,
          status: item.status,
          represent: item.represent,
          id: item.id,
          recommend_index_flag: item.recommend_index_flag ? 1 : 0
        }
        this.showFlag = true
      },

      deleteCourseById (item) {
        let that = this
        that.$confirm('确定删除课程' + item.name + ',此操作将删除该课程及其关联数据, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(function () {
          that.axios.delete(that.$http.httpUrl('/system/course'), {
            params: {
              courseId: item.id
            }
          }).then(function (response) {
            if (response.data.code === 1) {
              that.$message({
                type: 'success',
                message: response.data.message
              })
              that.getCourseList()
            } else {
              that.$message({
                type: 'error',
                message: response.data.message
              })
            }
          })
        })
      },

      clearForm () {
        this.form = {
          status: null,
          recommend_index_flag: 0,
          head_img: '',
          name: '',
          grade_type: '',
          represent: '',
          school_type: '',
          subject_id: '',
          code: '',
          sort: 0
        }
      },

      getCourseList () {
        // this.loading = false

        this.axios.get(this.$http.httpUrl('/system/course'), {
          params: {
            offset: (this.currentPage - 1) * this.pageSize,
            limit: this.pageSize,
            keyWord: this.query.keyWord,
            gradeType: this.query.gradeType
          }
        }).then(response => {
          this.loading = false
          this.courseList = response.data.data.dataList
          this.totalCount = response.data.data.total
        })
      },

      saveOrUpdateCourse (status) {
        let that = this
        that.$refs['form'].validate((valid) => {
          if (valid) {
            this.form.status = status
            this.saveCommon()
          }
        })
      },

      saveCommon () {
        this.axios.post(this.$http.httpUrl('/system/course'), this.form)
          .then(response => {
            this.showFlag = false
            if (response.data.code === 1) {
              this.$message({
                type: 'success',
                message: response.data.message
              })
              this.getCourseList()
            } else {
              this.$message({
                type: 'error',
                message: response.data.message
              })
            }
          }).catch(error => {
            console.log(error)
          })
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

      addCourse () {
        this.showFlag = true
        this.clearForm()
      },

      lookHeadImg () {
        this.headImageVisible = true
      },

      deleteHeadImg () {
        this.$confirm('确定移除该课程封面图吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.axios.delete(this.$http.httpUrl('/deleteFile'), {
            params: {
              url: this.form.head_img
            }
          }).then(response => {
            this.form.head_img = ''
            if (response.data.code === 0) {
              this.$message.error(response.data.message)
            }
          })
        }).catch(() => {

        })
      },

      beforeUploadHeadImg (file) {
        let contentTypeArray = ['image/jpeg', ' image/jpeg', 'image/gif', 'image/png', 'image/x-png']
        // 校验文件类型  png jpg jpeg等
        let contentType = file.type // 获取文件content-type
        if (contentTypeArray.indexOf(contentType) === -1) {
          this.$message.error('图片格式错误, 只能上传JPG/PNG 格式,请重新上传')
          return false
        }
        // 检验文件的大小
        let flag = file.size / 1024 / 1024 < 1
        if (!flag) {
          this.$message.error('上传的图片不能超过1MB')
          return false
        }
        return true
      },

      uploadHeadImgSuccess (res, file) {
        if (res.code === 1) {
          this.$message.success(res.message)
          this.form.head_img = res.data
        } else {
          this.$message.error(res.message)
        }
      }
    },

    filters: {

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
