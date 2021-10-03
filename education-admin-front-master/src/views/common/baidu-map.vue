<template>
  <div>
    <div id="r-result">请输入:<input type="text" id="suggestId" size="20" value="百度" style="width:150px;" /></div>
    <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
    <div class="panel-body dtCon" id="allmap"></div>
  </div>

</template>
<script>
  import {Map} from '@/utils/map'
  export default {
    name: 'baidu-map',
    data () {
      return {
        ak: 'GZOzV3OpQdPNW4iAB4L9DfjiavGkaEBc' // 开发密钥
      }
    },

    created () {

    },
    mounted () {

      let that = this
      that.$nextTick(() => {
        Map.BaiDuMap(that.ak).then(BMap => {
          // 百度地图API功能
          var map = new BMap.Map("allmap");


          var geolocation = new BMap.Geolocation();
          geolocation.getCurrentPosition(function(r){
            if(this.getStatus() == BMAP_STATUS_SUCCESS){
              var mk = new BMap.Marker(r.point);
              map.addOverlay(mk);
              map.panTo(r.point);
              var point = new BMap.Point(r.point.lng, r.point.lat);
              map.centerAndZoom(point,12);
              alert('您的位置：'+r.point.lng+','+r.point.lat);
            }
            else {
              alert('failed'+this.getStatus());
            }
          })


          // 百度地图API功能
          function G(id) {
            return document.getElementById(id);
          }

      //    var map = new BMap.Map("l-map");
     //     map.centerAndZoom("北京",12);                   // 初始化地图,设置城市和地图级别。

          var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
            {"input" : "suggestId"
              ,"location" : map
            });

          ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
            var str = "";
            var _value = e.fromitem.value;
            var value = "";
            if (e.fromitem.index > -1) {
              value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
            }
            str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

            value = "";
            if (e.toitem.index > -1) {
              _value = e.toitem.value;
              value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
            }
            str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
            G("searchResultPanel").innerHTML = str;
          });

          var myValue;
          ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
            var _value = e.item.value;
            myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
            G("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;

            setPlace();
          });

          function setPlace(){
            map.clearOverlays();    //清除地图上所有覆盖物
            function myFun(){
              var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
              map.centerAndZoom(pp, 18);
              map.addOverlay(new BMap.Marker(pp));    //添加标注
            }
            var local = new BMap.LocalSearch(map, { //智能搜索
              onSearchComplete: myFun
            });
            local.search(myValue);
          }

          var geoc = new BMap.Geocoder();

          map.addEventListener("click", function(e){
            var pt = e.point; // 获取具体的经纬度信息
            geoc.getLocation(pt, function(rs){
              var addComp = rs.addressComponents;
              alert(addComp.province + ", " + addComp.city + ", " + addComp.district + ", " + addComp.street + ", " + addComp.streetNumber);
            });
          });
        })
      })
    },

    methods: {

    }
  }
</script>

<style scoped>
  *{ padding: 0; margin: 0; outline: 0; }
  li{list-style: none;}
  a{text-decoration: none;color: #000000;}
  p{margin: 0;}

  #r-result a {color: #1989fa}
  #r-result a:hover {
    color: #5daf34;
  }

  .panel-body {height: 580px;width: 100%}
  p{width: 15%;height: 35px;background: #e6a23c;text-align: center;;border-radius: 4px;line-height: 35px;margin: auto;margin-top: 20px;}
  p a{color: #fff}
</style>
