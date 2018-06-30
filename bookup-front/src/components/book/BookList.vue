<template>
  <div>
    <div class="container" v-for="book in bookSearchResponse.content">
      <div class="row">
        <div class="col-md-2">
          <a href="#">
            <img class="img-book img-fluid rounded mb-3 mb-md-0" v-bind:src="book.image">
          </a>
        </div>
        <div class="col-md-10">
          <h3 class="book-title">{{ book.title }}</h3>
          <p class="book-description">{{ book.description }}</p>
          <p class="book-description">가격 : {{ book.price }} 원</p>
        </div>
      </div>
      <hr>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'search',
    data () {
      return {
        bookSearchResponse: {}
      }
    },

    created () {
      this.$eventBus.$on('bookSearchResponse', this.onReceive)
    },

    methods: {
      async onReceive (bookSearchResponse) {
        this.bookSearchResponse = await bookSearchResponse
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
