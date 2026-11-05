import { createContext, useContext, useEffect, useState } from 'react'
import client from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  // Co token thi phai cho hoi backend xem token con dung khong
  const [loading, setLoading] = useState(() => Boolean(localStorage.getItem('accessToken')))

  useEffect(() => {
    if (!localStorage.getItem('accessToken')) {
      return
    }

    client.get('/users/me')
      .then((response) => setUser(response.data))
      .catch((err) => {
        // Chi xoa token khi backend tu choi, khong xoa khi mat ket noi
        if (err.response?.status === 401) {
          localStorage.removeItem('accessToken')
        }
      })
      .finally(() => setLoading(false))
  }, [])

  function login(accessToken, loggedInUser) {
    localStorage.setItem('accessToken', accessToken)
    setUser(loggedInUser)
  }

  function logout() {
    localStorage.removeItem('accessToken')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
