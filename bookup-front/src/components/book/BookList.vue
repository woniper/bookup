<template>
  <div>
    <v-infinite-scroll @bottom="nextPage" class="scroll" style="max-height: 80vh; overflow-y: scroll;">
      <div class="container-fluid" v-for="book in content">
        <div class="row">
          <div class="col-md-2">
            <img class="img-book img-fluid rounded mb-3 mb-md-0" v-bind:src="book.image">
            <div style="margin-top: 3px;">
              <button type="button" class="btn btn-info" v-on:click="openStores(book)">서점</button>
              <button type="button" class="btn btn-warning" v-on:click="openLibraries(book)">도서관</button>
            </div>
          </div>

          <div class="col-md-8">
            <h3 class="book-title">{{ book.title }}</h3>
            <p class="book-description">{{ book.description }}</p>
            <p class="book-description">가격 : {{ book.price }} 원</p>
          </div>
        </div>
        <hr>
      </div>
    </v-infinite-scroll>

    <b-notification :closable="false">
      <b-loading :is-full-page="true" :active.sync="isLoading" :can-cancel="true"></b-loading>
    </b-notification>
  </div>

</template>

<script>
  import Vue from 'vue'
  import InfiniteScroll from 'v-infinite-scroll'
  import 'v-infinite-scroll/dist/v-infinite-scroll.css'
  import api from '@/api'
  import StoreModel from '../../models/StoreModel'
  import LibraryModel from '../../models/LibraryModel'
  import Buefy from 'buefy'
  import 'buefy/lib/buefy.css'

  Vue.use(InfiniteScroll)
  Vue.use(Buefy)

  const Modal = {
    props: [
      'list',
      'title'
    ],

    template: `
      <form action="">
        <div class="modal-card" style="width: auto">
          <header class="modal-card-head">
            <p class="modal-card-title">{{ title }}</p>
          </header>
          <section class="modal-card-body">
            <div class="container-fluid" v-for="data in list" style="cursor: pointer;">
              <div class="row">
                <a v-bind:href="data.href" v-if="data.href !== ''" target="_blank">
                  <p> {{ data.name }} </p>
                </a>

                <p v-if="data.href === ''"> {{ data.name }} </p>

                <hr>
              </div>
            </div>
          </section>
          <footer class="modal-card-foot">
              <a class="button is-danger is-fullwidth" @click="$parent.close()">close</a>
          </footer>
        </div>
    </form>
    `
  }

  export default {
    name: 'search',

    components: {
      Modal: Modal
    },

    data () {
      return {
        isLoading: false,
        keyword: '',
        pageable: {
          page: 0,
          last: false
        },
        content: [],
        stores: [],
        libraries: []
      }
    },

    created () {
      this.$eventBus.$on('keyword', this.onReceiveKeyword)
    },

    methods: {
      nextPage () {
        if (!this.pageable.last) {
          this.pageable.page++
          this.request()
        }
      },

      async request () {
        this.$eventBus.$on('keyword', this.onReceiveKeyword)
        let response = await api.get('books/' + this.keyword + '?page=' + this.pageable.page + '&size=20')
        this.content.push.apply(this.content, response.content)
        this.pageable.last = response.last
      },

      onReceiveKeyword (keyword) {
        this.scrollToTop()
        this.keyword = keyword
        api.get('books/' + keyword + '?page=0&size=20').then(response => {
          this.content = response.content
          this.pageable.page++
          this.pageable.last = response.last
        })
      },

      scrollToTop () {
        document.querySelector('.scroll').scrollTop = 0
      },

      openStores (book) {
        this.isLoading = true
        StoreModel.getStores(book.isbn).then(stores => {
          this.stores = stores
          this.isLoading = false

          if (stores.length <= 0) {
            this.stores = []
            this.isLoading = false
            this.toast('모든 서점에서 도서를 찾을 수 없습니다.')
            return
          }

          this.openModal('도서 보유 서점', StoreModel.convertModalData(this.stores))
        })
      },

      openLibraries (book) {
        this.isLoading = true
        LibraryModel.getLibraries(book.isbn).then(libraries => {
          this.libraries = libraries
          this.isLoading = false

          if (libraries.length <= 0) {
            this.libraries = []
            this.isLoading = false
            this.toast('모든 도서관에서 도서를 찾을 수 없습니다.')
            return
          }

          this.openModal('도서 보유 도서관', LibraryModel.convertModalData(this.libraries))
        })
      },

      toast: function (message) {
        this.$toast.open({
          duration: 2000,
          message: message,
          position: 'is-bottom',
          type: 'is-danger'
        })
      },

      openModal (title, list) {
        this.$modal.open({
          width: '50%',
          props: {
            title: title,
            list: list
          },
          component: Modal
        })
      }
    }
  }
</script>

<style>
  .book-title {
    text-align: left;
  }

  .book-description {
    text-align: left;
  }

  .img-book {
    width: 118px;
    height: 160px;
    margin: 0 auto;
  }
</style>
