<template>
  <div class="home">
    <div class="banner">
      <swiper :options="swiperOption" >
        <swiper-slide>
          <img src="@/assets/img/education1.png" alt="" width="100%" height="500">
        </swiper-slide>
        <swiper-slide>
          <img src="@/assets/img/education3.jpg" alt="" width="100%" height="500">
        </swiper-slide>
        <div class="swiper-pagination" slot="pagination"></div>
      </swiper>
    </div>

    <div class="container">
      <div class="title">
        <h2 class="wow bounceInRight fast">推荐课程</h2>
        <el-divider class="wow   bounceInRight faster">
          <svg t="1564560540049" class="icon wow bounceInRight faster" viewBox="0 0 1027 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3478" width="64" height="64"><path d="M981.600015 373.726208 981.600015 543.26272l35.689472 35.699712-74.415104 77.443072-75.336704-75.158528 51.58912-46.90944L919.126799 400.489472c-218.90048 90.257408-289.074176 118.310912-337.620992 140.2112-48.54784 21.900288-83.506176 21.789696-131.652608 3.647488-48.1536-18.147328-277.363712-101.80096-391.17824-156.269568-75.948032-36.340736-80.837632-59.388928 1.304576-90.231808 107.200512-40.725504 284.38528-105.946112 378.305536-141.541376 55.593984-22.490112 84.995072-34.768896 136.026112-9.121792C665.431823 184.810496 873.641743 262.941696 977.174287 305.6896 1067.299599 345.020416 1006.710543 357.957632 981.600015 373.726208L981.600015 373.726208zM592.063247 607.809536c52.936704-21.84192 124.314624-57.957376 202.141696-91.298816L794.204943 784.20992c0 0-100.652032 107.07456-277.599232 107.07456-190.558208 0-293.490688-107.07456-293.490688-107.07456L223.115023 534.336512c60.068864 24.4736 127.521792 45.531136 209.153024 73.473024C482.579215 625.71008 546.421519 631.899136 592.063247 607.809536L592.063247 607.809536zM592.063247 607.809536" p-id="3479" fill="#e6e6e6"></path></svg>
        </el-divider>
        <h3 class="wow bounceInRight fast">以学员满意度、以学员学习效果为本</h3>
      </div>

      <div class="demoList">
        <ul class="clearfix">
          <li class="typeitem" :key="index" v-for="(course, index) in recommendCourseList">
            <img @click="studyCourse(course)" :src="fileHost + course.head_img" class="image">
            <p class="title">{{course.name}}</p>
            <p class="course_detail">{{course.describe}}</p>
            <div style="margin-top: 5px;line-height:35px;">
              <i class="el-icon-user-solid">{{course.study_number}}人练习过</i>
              <el-button
                style="float: right"
                v-if="course.collect_flag === 0"
                @click="collectCourse(index)" icon="el-icon-star-off" type="text">收藏</el-button>
              <el-button
                style="float: right; color: red"
                v-if="course.collect_flag === 1"
                @click="collectCourse(index)" icon="el-icon-star-off" type="text">已收藏</el-button>
            </div>
          </li>
        </ul>

        <div class="page">
          <el-pagination
            @current-change="changeIndexCoursePage"
            background
            :current-page="currentPage"
            :page-size="pageSize"
            layout="prev, pager, next, jumper"
            :total="totalCount">
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import  { swiper, swiperSlide } from 'vue-awesome-swiper'
  import 'swiper/dist/css/swiper.css'
  export default {
    components:{
      swiper, swiperSlide
    },
    data () {
      return {
        fileHost: this.$store.state.fileHost,
        currentPage: 1,
        pageSize: 20,
        totalCount: 0,
        recommendCourseList: [],
        swiperOption: {
          pagination: {
            el: '.swiper-pagination'
          },
          autoplay: {
            disableOnInteraction: false,
            delay: 3000
          },
          autoplayDisableOnInteraction: false,
          loop: true
        }
      };
    },

    mounted () {
      this.getRecommendCourseList()
    },

    methods: {

      collectCourse (index) {
        let courseInfo = this.recommendCourseList[index]
        if (courseInfo.collect_flag === 1) {
          courseInfo.collect_flag = 0
        } else {
          courseInfo.collect_flag = 1
        }
        let params = {
          course_id: courseInfo.id,
          collect_flag: courseInfo.collect_flag
        }
        this.axios.post(this.$httpApi.httpUrl('/student/course/collectCourse'), params)
          .then((response) => {

          })
      },

      studyCourse (course) {
        let courseOrPaperInfo = {
          courseId: course.id,
          name: course.name,
          paperId: '',
          examId: ''
        }
        this.$store.commit('updateCourseOrPaperInfo', courseOrPaperInfo)
        this.$router.push({
          name: 'courseDetail',
        })
      },

      getRecommendCourseList () {
          this.axios.get(this.$httpApi.httpUrl('/student/course/getCourseList'), {
            params: {
              offset: (this.currentPage - 1) * this.pageSize,
              limit: this.pageSize,
              recommendIndexFlag: 1
            }
          }).then(response => {
            this.recommendCourseList = response.data.data.dataList
            this.totalCount = response.data.data.total
          })
      },

      changeIndexCoursePage (val) {
        this.currentPage = val
      }
    },
  }

</script>

<style lang='scss' scoped>
  h2 {
    font-size: 36px;
    text-align: center;
    margin: 40px 0;
    font-weight: normal;
  }
  h3 {
    font-size: 18px;
    text-align: center;
    color: #737373;
    margin: 40px 0;
    font-weight: normal;
  }
  .el-divider__text, .el-link {
    font-weight: 500;
    font-size: 45px;
  }

  .demoList ul {
    width: 100%;
    box-sizing: border-box;
    margin-bottom: 100px;
  }
  .demoList{
    margin: 0 auto;
    padding-top: 30px;
  }

  .course_detail {
    padding-top: 6px;
    max-height: 48px;
    line-height: 20px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    font-size: 14px;
    color: #93999f
  }

  .demoList li {
    box-sizing: border-box;
    width: 192px;
    min-height: 160px;
    float: left;
    margin-right: 40px;
    margin-bottom: 30px;
    text-align: left;
    img{
      cursor: pointer;
      width: 100%;
      height:108px;
      display: block;
      border-radius: 8px;
      &:hover{
        opacity: 0.7;
       /* .course_title{
          color:#ca0000;
        }*/
      }
    }
  }

  .typeitem{
    cursor: pointer;
    width: 300px;
    .title {
      padding-top: 8px;
      font-weight: bold;
      &:hover {
        cursor: pointer;
        color: red;
      }
    }
  }

  .demoList li{
    width: 30%;
  }

  .demoList  .page {
    position: relative;
    left: 0;
    bottom: 50px;
    width: 100%;
    text-align: center;
  }

  .demoList li .image {
    height: 199px;
    width: 100%;
    display: block;
  }

  @keyframes turn-over {
    to {
      transform: rotateX(-180deg);
    }
  }
  .home{
    overflow: hidden;
  }
</style>
