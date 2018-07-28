import api from '@/api'

export default {

  libraries: new Map(),

  constructor () {
    this.libraries = new Map()
  },

  addLibraries (isbn, libraries) {
    if (!this.contains(isbn)) {
      this.libraries.set(isbn, libraries)
    }
  },

  contains (isbn) {
    return this.libraries.get(isbn) !== undefined
  },

  async getLibraries (isbn) {
    if (!this.contains(isbn)) {
      let response = await api.get('libraries/' + isbn)

      if (response !== null) {
        this.addLibraries(isbn, response.items)
      }
    }

    return this.libraries.get(isbn)
  },

  convertModalData (libraries) {
    return libraries.map(x => {
      return {
        name: x.libraryName,
        href: 'https://map.naver.com/?query=' + x.libraryName
      }
    })
  }

}
