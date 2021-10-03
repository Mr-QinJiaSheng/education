<template>
  <div>
    <script id="editor" ref="ueditor" type="text/plain"></script>
  </div>
</template>
<script>
  import ueditor from 'ueditor'
  let thisPage = null
  export default {
    name: 'ueditor',
    data () {
      return {
        editor: null,
        config: {}
      }
    },

    beforeCreate () {
      thisPage = this
    },

    watch: {

    },

    mounted () {
      this.editor = ueditor.getEditor('editor', this.config)
      this.editor.addListener('ready', () => {
        this.editor.setShow(true) // 将富文本设置为显示状态
      //  editor.execCommand( 'focus' ); //编辑器家在完成后，让编辑器拿到焦点
      })
  },

    methods: {

    },

    filters: {

    }
  }
</script>
<style lang="scss">
  .edui-default .edui-editor-toolbarbox {
    position: relative !important;
  }

  .mod-demo-ueditor {
    position: relative;
    z-index: 510;
    > .el-alert {
      margin-bottom: 10px;
    }
  }
</style>
