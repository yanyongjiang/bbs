<template>
  <div id="q-app">
    <router-view />
  </div>
</template>
<script>
import Vue from 'vue'
import axios from 'axios'
const url = 'https://www.yunh9.com/' /*设置全局请求地址https://www.yunh9.com/ http://localhost:8116/api*/

axios.interceptors.request.use(
  config => {
    let token = localStorage.getItem("x-auth-token");
    if (token) {
      config.headers.token = `${token}`;
    }
    if (config.url.indexOf(url) === -1) {
      config.url = url + config.url;
    }
    return config;
  },
  err => {
    return Promise.reject(err);
  }
);
var post=function post(url, params) {
  return new Promise((resolve, reject) => {
      axios.post(url,params)
      .then(res => {
          resolve(res.data);
      })
      .catch(err => {
          reject(err)
      })
  });
}
var get=function get(url, params) {
  return new Promise((resolve, reject) => {
      axios.get(url,params)
      .then(res => {
          resolve(res.data);
      })
      .catch(err => {
          reject(err)
      })
  });
}
Vue.prototype.$surl = url;
Vue.prototype.$axios = axios;
Vue.prototype.$post = post;
Vue.prototype.$get = get;

export default {
  name: 'App'
}
</script>
