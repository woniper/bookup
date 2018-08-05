<template>
  <div id="app">

    <header>
      <nav class="navbar navbar-expend-lg navbar-dark bg-dark">
        <a class="navbar-brand" href="#">BOOKUP</a>
        <form class="form-inline my-2 my-lg-0">
          <b-select v-model="searchValue.selectedOption">
            <option>전체</option>
            <option
              v-for="option in storeOptions"
              :value="option.key">
              {{ option.name }}
            </option>
          </b-select>
          <input class="form-control mr-sm-2" type="search" v-model="searchValue.keyword" placeholder="Search" aria-label="Search">
          <router-link to="/">
            <button class="button is-primary" v-bind:class="isLoadingClass" v-on:click="search">Search</button>
          </router-link>
        </form>
      </nav>
    </header>

    <main>
      <router-view></router-view>
    </main>

  </div>
</template>

<script>
  import Vue from 'vue'
  import Buefy from 'buefy'
  import 'buefy/lib/buefy.css'

  Vue.use(Buefy)

  export default {
    name: 'app',

    data () {
      return {
        searchValue: {
          keyword: '',
          selectedOption: '전체'
        },

        isLoadingClass: '',
        storeOptions: [
          {
            key: 'KYOBO',
            name: '교보문고'
          },
          {
            key: 'ALADIN',
            name: '알라딘'
          },
          {
            key: 'BANDI',
            name: '반디앤루니스'
          }
        ]
      }
    },

    methods: {
      search () {
        let self = this
        this.isLoadingClass = 'is-loading'

        this.$eventBus.$emit('searchValue', this.searchValue)
        this.$eventBus.$emit('event', function () {
          self.isLoadingClass = ''
        })
      }
    }
  }
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
  }

  main {
    text-align: center;
    margin-top: 40px;
  }

</style>
