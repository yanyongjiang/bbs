<template>
  <div class="q-pa-md">
    <q-table v-if="postTab" :data="data" :columns="columns" row-key="id" no-data-label="查询无数据"
      :pagination.sync="pagination" :rows-per-page-options="[3, 5, 7, 10, 15, 20, 25, 50]" :loading="loading"
      @request="onRequestTableData" rows-per-page-label="每页">
      <template v-slot:top-left>
        <div class="q-gutter-sm">
          <q-btn color="primary" label="新增" @click="newPost">
          </q-btn>
        </div>
      </template>
      <template v-slot:top-right>
        <q-input dense v-model="searchkey" placeholder="请输入主题或发布人" @keydown.enter.native="onRequestTableData">
          <template v-slot:append>
            <q-icon name="search" @click="onRequestTableData" />
          </template>
        </q-input>
      </template>
      <template v-slot:body="props">
        <q-tr :props="props">
          <q-td v-for="col in props.cols" :key="col.name" :props="props" @click="colclick(col, props)">
            {{ col.value }}
          </q-td>
        </q-tr>
      </template>
    </q-table>
    <q-form v-if="addPost" @submit="savePost" @reset="onReset" class="q-gutter-md">
      <q-input filled v-model="tm" label="主题 *" hint="请输入主题" lazy-rules
        :rules="[val => val && val.length > 0 || '请输入主题']" />

      <q-input filled type="textarea" v-model="cont" label="内容 *" lazy-rules
        :rules="[val => val && val.length > 0 || '请输入内容']" />

      <q-file v-model="files" label="点击这里选择文件" outlined use-chips multiple append @input="afterAddPostFile">
        <template v-slot:prepend>
          <q-icon name="attach_file" />
        </template>
      </q-file>
      <div>
        <q-btn label="确定" type="submit" color="primary" />
        <q-btn label="取消" type="reset" color="primary" flat class="q-ml-sm" />
      </div>
    </q-form>
    <div v-if="onePost">
      <q-btn label="返回" color="primary" @click="onePostBack" />
      <div class="q-gutter-xs row">
        <q-chip color="teal" text-color="white" icon="event" :label="postTitle" :title="postTitle">
        </q-chip>
      </div>
      <div>
        <q-card class="my-card">
          <q-card-section>
            <div v-html="postCon"></div>
          </q-card-section>
        </q-card>
      </div>
      <div v-if="pkfiles.length > 0">
        <q-chip outline color="primary" text-color="white" icon="attachment" v-for="f in pkfiles" :key="f.fpa">
          <a :href="f.fpa" :download="f.fname">{{ f.fname }}</a>
        </q-chip>
      </div>
      <div>
        <q-img v-for="(imgUrl, i) in imgUrls" :key="i" :src="imgUrl" />
      </div>
    </div>
  </div>
</template>

<script>
import { Dialog,Loading } from 'quasar'

export default {
  props: {
    currentlink: {
      type: String
    }
  },
  data() {
    return {
      loading: false,
      pagination: {
        page: 1,
        rowsPerPage: 20,
        rowsNumber: 10
      },
      tm: '',
      cont: '',
      fileSaveUrl: this.$surl + 'bbs/user/savepost',
      onePost: false,
      postTitle: '',
      postCon: '',
      imgUrls: [],
      pkfiles: [],
      searchkey: '',
      files: null,
      addPost: false,
      postTab: true,
      columns: [
        { name: 'tm', required: true, label: '主题', align: 'left', field: row => row.tm, sortable: true, style: 'color:#1976D2;cursor:pointer' },
        { name: 'ngd', align: 'left', label: '日期', field: 'ngd', sortable: true, style: 'width: 50px' },
        { name: 'lid', label: '发布人', field: 'lid', sortable: true, style: 'width: 50px' },
        { name: 'operate', label: '操作', field: 'operate', sortable: true, style: 'width: 50px;color:#1976D2;cursor:pointer', format: (val, row) => "删除" }
      ],
      data: []
    }
  },
  mounted() {
    var me = this
    var height = document.documentElement.clientHeight - 100 - 32 - 64 - 48 - 50
    var rowsPerPage = parseInt(height / 48)
    me.pagination.rowsPerPage = rowsPerPage
    this.onRequestTableData({
      pagination: this.pagination
    })
  },
  methods: {
    newPost() {
      this.postTab = false
      this.addPost = true
      this.onePost = false
    },
    savePost() {
      var me = this
      var url = me.fileSaveUrl
      var formData = new FormData()
      formData.append('tm', me.tm)
      formData.append('cont', me.cont)
      if (me.$u) {
        formData.append('lid', me.$u.lid)
        formData.append('uid', me.$u.id)
      } else {
        formData.append('lid', '游客')
      }
      if (me.files && me.files.length > 0) {
        for (var i = 0; i < me.files.length; i++) {
          formData.append('files', me.files[i]);
        }
      }

      formData.append('pty', me.getPty())
      Loading.show()
      me.$post(url, formData).then(r => {
        Loading.hide()
        if (r.result != 0) {
          me.alert = true
          me.errmsg = me.errmsgMap[r.errmsg]
        } else {
          me.listPost();
        }
      })
    },
    onReset() {
      this.postTab = true
      this.addPost = false
      this.onePost = false
    },
    listPost() {
      var me = this
      me.addPost = false
      me.postTab = true
      var props = {}
      var pagination = {}
      pagination.page = 1
      pagination.rowsPerPage = me.pagination.rowsPerPage
      props.pagination = pagination
      me.onRequestTableData(props);
    },
    removePost(param) {
      var me=this;
      Dialog.create({
        title: '提示',
        message: '确定删除吗?',
        cancel: true,
        persistent: true,
        ok:'确定',
        cancel:'取消'
      }).onOk(() => {
        var me = this;
        Loading.show()
        me.$post(this.$surl + 'bbs/user/removePost', param).then(r => {
          Loading.hide()
          if (r.result != 0) {
            me.alert = true
            me.errmsg = me.errmsgMap[r.errmsg]
          } else {
            me.listPost();
          }
        });
      }).onCancel(() => {
        
      }).onDismiss(() => {
        
      })

    },
    colclick(col, row) {
      var me = this
      var param = {
        id: row.row.id
      }
      if (col.name == 'operate') {
        return me.removePost(param)
      }
      this.postTab = false
      this.addPost = false
      this.onePost = true

      me.$post(this.$surl + 'bbs/user/getonepost', param).then(r => {
        me.postTitle = r.data.tm
        me.postCon = r.data.cont
        me.postCon = me.postCon.replace(/\n/g, "<br>")
        var imgs = []
        var pkfiles = []
        var attachList = r.data.attachList
        attachList.forEach((item, i) => {
          if (item.fname && (item.fname.endsWith('.jpg') || item.fname.endsWith('.png'))) {
            imgs.push(me.$surl + 'img/' + item.fpa)
          } else {
            var f = {
              fname: item.fname,
              fpa: me.$surl + 'bbs/user/downloadfile?fpa=' + item.fpa + '&fname=' + item.fname
            }
            pkfiles.push(f)
          }
        })
        me.imgUrls = imgs
        me.pkfiles = pkfiles
      })

    },
    onePostBack() {
      this.postTab = true
      this.addPost = false
      this.onePost = false
    },
    onRequestTableData(props_) {
      var me = this
      var props = {}
      if (!props_ || !props_.pagination) {
        var pagination = {}
        pagination.page = 1
        pagination.rowsPerPage = me.pagination.rowsPerPage
        props.pagination = pagination
      } else {
        props = props_
      }
      var pagination = props.pagination
      var page = pagination.page
      var rowsNumber = pagination.rowsNumber
      var rowsPerPage = pagination.rowsPerPage
      var start = (page - 1) * rowsPerPage
      var limit = rowsPerPage
      var param = {
        start: start,
        limit: limit,
        pty: me.getPty()
      }
      if (me.searchkey) param.searchkey = me.searchkey
      if (me.$u) param.uid = me.$u.id
      me.$post(this.$surl + 'bbs/user/listpost', param).then(r => {
        me.data = r.data.posts
        me.data.forEach((item, i) => {
          if (!item.lid) item.lid = '游客'
        })
        me.pagination.rowsNumber = r.data.count
        me.pagination.page = parseInt(start / rowsPerPage) + 1
        me.pagination.rowsPerPage = rowsPerPage
      });
    },
    getPty() {
      var me = this
      if (me.currentlink == 'travel') {
        return '1'
      }
      if (me.currentlink == 'comtel') {
        return '2'
      }
      if (me.currentlink == 'pernote') {
        return '3'
      }
      return '4'
    },
    afterAddPostFile(files){
      var me=this;
      if(me.tm==''){
         me.tm=files[0].name;
      }
      if(me.cont==''){
        me.cont=files[0].name;
      }
    }
  }
}
</script>
