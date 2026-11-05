import axios from 'axios'

// Moi request goi API deu dung chung cau hinh nay
const client = axios.create({
  baseURL: 'http://localhost:8080/api',
})

// Tu gan token vao moi request neu da dang nhap
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default client
