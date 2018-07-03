import api from '@/api'

export default {

  stores: new Map(),

  constructor () {
    this.stores = new Map()
  },

  addStores (isbn, stores) {
    if (!this.contains(isbn)) {
      this.stores.set(isbn, stores)
    }
  },

  contains (isbn) {
    return this.stores.get(isbn) !== undefined
  },

  async getStores (isbn) {
    if (!this.contains(isbn)) {
      let response = await api.get('stores/books/' + isbn)

      if (response !== null) {
        this.addStores(isbn, response.stores)
      }
    }

    return this.stores.get(isbn)
  }

}
