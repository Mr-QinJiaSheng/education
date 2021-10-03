<template>
  <aside class="site-sidebar" :class="'site-sidebar--' + sidebarLayoutSkin">
    <div class="site-sidebar__inner">
      <el-menu
        :default-active="menuActiveName || 'home'"
        :collapse="sidebarFold"
        :collapseTransition="false"
        class="site-sidebar__menu">
        <el-menu-item index="home" @click="$router.push({ name: 'home' })">
          <icon-svg name="shouye" class="site-sidebar__menu-icon"></icon-svg>
          <span slot="title">首页</span>
        </el-menu-item>

        <el-submenu v-for="(menu, parentIndex) in menuList" :key="parentIndex" :index="parentIndex + ''">
          <template slot="title">
            <icon-svg :name="menu.icon || ''" class="site-sidebar__menu-icon"></icon-svg>
            <span>{{ menu.name }}</span>
          </template>
          <el-menu-item
              v-if="menu.children.length > 0"
              v-for="(subMenu, index) in menu.children" :key="index"
              :index="subMenu.id + ''"
              @click="$router.push(subMenu.url)">
            <icon-svg :name="subMenu.icon || ''" class="site-sidebar__menu-icon"></icon-svg>
            <span>{{subMenu.name}} </span>
          </el-menu-item>
        </el-submenu>
      </el-menu>
    </div>
  </aside>
</template>

<script>
  import { isURL } from '@/utils/validate'
  export default {
    data () {
      return {

      }
    },
    computed: {
      sidebarLayoutSkin: {
        get () { return this.$store.state.common.sidebarLayoutSkin }
      },
      sidebarFold: {
        get () { return this.$store.state.common.sidebarFold }
      },
      menuList: {
        get () { return this.$store.state.common.menuList },
        set (val) { this.$store.commit('common/updateMenuList', val) }
      },
      menuActiveName: {
        get () { return this.$store.state.common.menuActiveName },
        set (val) { this.$store.commit('common/updateMenuActiveName', val) }
      },
      mainTabs: {
        get () { return this.$store.state.common.mainTabs },
        set (val) { this.$store.commit('common/updateMainTabs', val) }
      },
      mainTabsActiveName: {
        get () { return this.$store.state.common.mainTabsActiveName },
        set (val) { this.$store.commit('common/updateMainTabsActiveName', val) }
      }

    },

    watch: {
     // '$route':'routeHandle'（监听器后面直接跟方法名）
      '$route' () {
        this.routeHandle(this.$route)
      }
    },

    created () {
      this.routeHandle(this.$route)
    },

    mounted () {
      this.menuList = JSON.parse(localStorage.getItem('userInfo')).menuList
    },

    methods: {
      // 路由操作
      routeHandle (route) {
        if (route.meta.isTab) {
          // tab选中, 不存在先添加
          let tab = this.mainTabs.filter(item => item.name === route.name)[0]
          if (!tab) {
            tab = {
              menuId: route.meta.menuId || route.name,
              name: route.name,
              title: route.meta.title,
              type: isURL(route.meta.iframeUrl) ? 'iframe' : 'module',
              iframeUrl: route.meta.iframeUrl || '',
              params: route.params,
              query: route.query
            }
            this.mainTabs = this.mainTabs.concat(tab)
          }
          this.menuActiveName = tab.menuId + ''
          this.mainTabsActiveName = tab.name
        }
      }
    }
  }
</script>
