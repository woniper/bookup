import api from '@/api'

export default {

  stores: new Map(),

  constructor () {
    this.stores = new Map()
  },

  init () {
    this.stores.clear()
  },

  addStores (isbn, stores) {
    if (!this.contains(isbn)) {
      this.stores.set(isbn, stores)
    }
  },

  contains (isbn) {
    return this.stores.get(isbn) !== undefined
  },

  async getStores (isbn, selectedStore) {
    if (!this.contains(isbn)) {
      let response = await api.get(this.getStoreUrl(isbn, selectedStore))

      if (response !== null) {
        this.addStores(isbn, response.stores)
      }
    }

    return this.stores.get(isbn)
  },

  getStoreUrl (keyword, selectedStore) {
    if (selectedStore !== '전체' || selectedStore === null || selectedStore === undefined) {
      return 'stores/books/' + keyword + '?stores=' + selectedStore
    }

    return 'stores/books/' + keyword
  },

  convertModalData (stores) {
    return stores.map(x => {
      return {
        name: x.storeName,
        href: x.href
      }
    })
  }

}
