import axios from 'axios'

const client = axios.create({
  baseURL: process.env.API_URL,
  json: true
})

export default {
  async execute (method, resource, data) {
    return client({
      method,
      url: resource,
      data
    }).then(request => {
      return request.data
    })
  },

  get (resource) {
    return this.execute('get', resource)
  },

  post (resource, data) {
    return this.execute('post', resource, data)
  },

  delete (resource, data) {
    return this.execute('delete', resource, data)
  }
}
