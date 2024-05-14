<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn dense flat round icon="menu" @click="left = !left" />
        <q-toolbar-title>
          {{u.lid}}
        </q-toolbar-title>
        <div class="q-gutter-xs">
        <q-btn dense flat round icon="power_settings_new">
        <q-menu>
          <q-list style="min-width: 100px">
            <q-item clickable v-close-popup v-if="!login">
              <q-item-section><q-btn dense flat round color="white" text-color="black" label="登陆" @click="log = true"/></q-item-section>
            </q-item>
            <q-item clickable v-close-popup v-if="!login">
              <q-item-section><q-btn dense flat round v-if="!login" color="white" text-color="black" label="注册" @click="reg = true"/></q-item-section>
            </q-item>
            <q-item clickable v-close-popup v-if="login">
              <q-item-section>
               <q-btn dense flat round color="white" text-color="black" label="退出" @click="logout"/>
              </q-item-section>
            </q-item>
            <q-item clickable v-close-popup>
              <q-item-section><a href="/note-v1.apk" download="note-v1.apk">app下载(Android)</a></q-item-section>
            </q-item>
          </q-list>
        </q-menu>
      </q-btn>
        </div>
      </q-toolbar>
    </q-header>
    <q-drawer show-if-above v-model="left" side="left" bordered>
       <q-list padding class="rounded-borders text-primary">
      <q-item
        clickable
        v-ripple
        :active="link === 'travel'"
        @click="toTravel"
        active-class="mainlayout-my-menu-link"
      >
        <q-item-section avatar>
         <q-icon name="navigation"/>
        </q-item-section>
        <q-item-section>旅游景点</q-item-section>
      </q-item>

      <q-item
        clickable
        v-ripple
        :active="link === 'comtel'"
        @click="toComtel"
        active-class="mainlayout-my-menu-link"
      >
        <q-item-section avatar>
        <q-icon name="computer"/>
        </q-item-section>
        <q-item-section>IT技术</q-item-section>
      </q-item>

      <q-item
        clickable
        v-ripple
        :active="link === 'pernote'"
        @click="toPernote"
        active-class="mainlayout-my-menu-link"
      >
        <q-item-section avatar>
        <q-icon name="event_note"/>
        </q-item-section>
        <q-item-section>个人资料</q-item-section>
      </q-item>

       
    </q-list>
    </q-drawer>
    <q-page-container>
      <component v-bind:is="currentTabComponent" :currentlink="link"  v-if="showCurCom"></component>
    </q-page-container>
    <q-footer elevated class="bg-grey-8 text-white">
      <q-toolbar>
        <q-toolbar-title>
        <div class="quote text-center text-weight-thin">
          <div class="" style="font-size: 16px;"><a href="https://beian.miit.gov.cn/" target="_blank">湘ICP备2021019327号</a> 联系:804553932@qq.com</div>
        </div>
        </q-toolbar-title>
       
      </q-toolbar>
    </q-footer>
    <q-dialog id="regdia" v-model="reg">
      <q-card>
        <q-card-section>
          <div class="text-h6">注册</div>
        </q-card-section>
        <q-separator />
        <q-card-section class="scroll" style="width: 300px">
        <div class="q-gutter-y-md column" style="max-width: 300px">
          <q-input color="purple-12" v-model="regln" label="账号">
        <template v-slot:prepend>
          <q-icon name="perm_identity" />
        </template>
      </q-input>
      <q-input color="purple-12" v-model="regpass" label="密码">
        <template v-slot:prepend>
          <q-icon name="lock" />
        </template>
      </q-input>
        </div>
        </q-card-section>
        <q-separator />
        <q-card-actions align="right">
          <q-btn flat label="确定" color="primary" @click="doRegSubmit"/>
          <q-btn flat label="取消" color="primary" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
    <q-dialog id="logdia" v-model="log">
      <q-card>
        <q-card-section>
          <div class="text-h6">登陆</div>
        </q-card-section>
        <q-separator />
        <q-card-section class="scroll" style="width: 300px">
        <div class="q-gutter-y-md column" style="max-width: 300px">
          <q-input color="purple-12" v-model="logln" label="账号" @keydown.enter.native="loginEnterFun1">
        <template v-slot:prepend>
          <q-icon name="perm_identity" />
        </template>
      </q-input>
      <q-input type="password" color="purple-12" v-model="logpass" label="密码" ref="logpassInput" @keydown.enter.native="doLogSubmit">
        <template v-slot:prepend>
          <q-icon name="lock" />
        </template>
      </q-input>
        </div>
        </q-card-section>
        <q-separator />
        <q-card-actions align="right">
          <q-btn flat label="确定" color="primary" @click="doLogSubmit"/>
          <q-btn flat label="取消" color="primary" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
        <q-dialog v-model="alert">
      <q-card>
        <q-card-section>
          <div class="text-h6">提示</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
        {{errmsg}}
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="OK" color="primary" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-layout>
</template>
<script>
import Index from 'pages/Index.vue'
import md5 from 'js-md5'
import sha256 from 'js-sha256'
import Vue from 'vue'
export default {
  components: {
     Index
  },
  data () {
    return {
      showCurCom:true,
      login:false,
      alert:false,
      errmsg:'',
      currentTabComponent:'Index',
      left: false,
      link: 'travel',
      reg: false,
      regln: '',
      regpass: '',
      log: false,
      logln: '',
      logpass: '',
      u:{
        lid:'游客'
      },
      errmsgMap:{
        'lid is exist':'账号已存在',
        'pw is empty':'密码为空',
        'lid is not exist':'账号不存在',
        'password is wrong':'密码不正确'
      }
    }
  },
  mounted() {
    var me=this;
    var userData = localStorage.getItem("$u");
    if(userData){
      var uObj=JSON.parse(userData)
      me.u=uObj;
      Vue.prototype.$u=uObj;
      me.log=false;
      me.login=true;
    }
  },
  methods: {
    loginEnterFun1(){
       this.$refs.logpassInput.focus();
    },
    doRegSubmit () {
      var me=this;
      var param={
        lid:me.regln,
        pw:me.regpass
      }
      me.$post('bbs/user/reg',param).then(r => {
          if(r.result!=0){
             me.alert=true
             me.errmsg=me.errmsgMap[r.errmsg]
          }else{
             me.log=true
             me.reg=false
          }
      });
    },
    doLogSubmit(){
      var me=this;
      var param={ 
        lid:me.logln,
        pw:sha256(md5(me.logpass))
      }
      me.$post('bbs/user/log',param).then(r => {
      
          if(r.result!=0){
             me.alert=true
             me.errmsg=me.errmsgMap[r.errmsg]
          }else{
             me.u=r.data;
             Vue.prototype.$u=me.u;
             localStorage.setItem("token", r.token);
             localStorage.setItem("$u", JSON.stringify(me.u));
             me.log=false;
             me.login=true;
          }
      });
    },
    logout(){
      var me=this
      me.u={
        lid:'游客'
      };
      localStorage.removeItem("token")
      localStorage.removeItem("$u")
      delete Vue.prototype.$u
      me.login=false
      me.toTravel()
    },
    toTravel(){
      this.link='travel'
      this.showCurCom=false
      this.$nextTick(() => (this.showCurCom=true,this.currentTabComponent='Index'))
    },
    toComtel(){
      this.link='comtel'
      this.showCurCom=false
      this.$nextTick(() => (this.showCurCom=true,this.currentTabComponent='Index'))
    },
    toPernote(){
      this.link='pernote'
      this.showCurCom=false
      this.$nextTick(() => (this.showCurCom=true,this.currentTabComponent='Index'))
    }
  }
}
</script>
<style lang="sass">
.mainlayout-my-menu-link
  color: white
  background: #1976d2
</style>
