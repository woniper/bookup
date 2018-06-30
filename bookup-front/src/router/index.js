import Vue from 'vue'
import Router from 'vue-router'
import BookList from '@/components/book/BookList'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'bookList',
      component: BookList
    }
  ]
})
