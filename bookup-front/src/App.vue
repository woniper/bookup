<template>
  <div id="app">

    <header>
      <nav class="navbar navbar-expend-lg navbar-dark bg-dark">
        <a class="navbar-brand" href="#">BOOKUP</a>
        <form class="form-inline my-2 my-lg-0">
          <input class="form-control mr-sm-2" type="search" v-model="keyword" placeholder="Search" aria-label="Search">
          <button class="btn btn-outline-success my-2 my-sm-0" v-on:click="search">Search</button>
        </form>
      </nav>
    </header>

    <main>
      <router-view></router-view>
    </main>

  </div>
</template>

<script>
  import api from '@/api'

  export default {
    name: 'app',

    data () {
      return {
        keyword: ''
      }
    },

    methods: {
      search () {
        this.$eventBus.$emit('bookSearchResponse', api.get('books/' + this.keyword + '?page=0&size=20'))
      }
    }

  }
</script>

<style>
  body {
    width: 70%;
    margin: 0 auto;
  }

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
