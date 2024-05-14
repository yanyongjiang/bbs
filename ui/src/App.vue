<template>
  <div id="q-app">
    <router-view />
  </div>
</template>
<script>
import Vue from 'vue'
import axios from 'axios'
const url = 'http://localhost:8116/api/' /*设置全局请求地址https://www.yunh9.com/ http://localhost:8116/api*/

axios.interceptors.request.use(function (config) {
  // Do something before request is sent  
  const token = localStorage.getItem("token")
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  if (config.url.indexOf(url) === -1) {
    config.url = url + config.url;
  }
  return config;
}, function (error) {
  // Do something with request error  
  return Promise.reject(error);
});
// Add a response interceptor
axios.interceptors.response.use(function (response) { 
  if(response.headers.authorization){
    localStorage.setItem("token", response.headers.authorization)
  }
  return response;
}, function (error) {
  // Any status codes that falls outside the range of 2xx cause this function to trigger   
  // Do something with response error    
  const { status } = error.response
  if (status === 500) {
    localStorage.removeItem("token")
  }
  return Promise.reject(error);
});

var post = function post(url, params) {
  return new Promise((resolve, reject) => {
    axios.post(url, params)
      .then(res => {
        resolve(res.data);
      })
      .catch(err => {
        reject(err)
      })
  });
}
var get = function get(url, params) {
  return new Promise((resolve, reject) => {
    axios.get(url, params)
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
