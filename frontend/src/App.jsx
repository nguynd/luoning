import { Link, Route, Routes } from 'react-router-dom'
import CourseListPage from './pages/CourseListPage'
import LoginPage from './pages/LoginPage'
import { useAuth } from './context/AuthContext'
import RegisterPage from './pages/RegisterPage'
import CourseDetailPage from './pages/CourseDetailPage'


function App() {

  const { user, loading, logout } = useAuth()

  return (
    <>
      <header className="border-b border-slate-200 bg-white dark:border-slate-700 dark:bg-slate-800">
        <div className="mx-auto flex max-w-5xl items-center justify-between gap-4 p-4">
          <Link className="text-xl font-bold text-teal-700 dark:text-teal-400" to="/">Luoning</Link>
          {!loading && (user ? (
            <div className="flex items-center gap-3">
              <span>{user.fullName}</span>
              <button className="cursor-pointer rounded-md border border-slate-200 px-3 py-1.5 hover:bg-slate-100 dark:border-slate-700 dark:hover:bg-slate-700" type="button" onClick={logout}>
                Đăng xuất
              </button>
            </div>
          ) : (
            <Link className="hover:underline" to="/login">Đăng nhập</Link>
          ))}
        </div>
      </header>

      <Routes>
        <Route path="/" element={<CourseListPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/courses/:courseId" element={<CourseDetailPage />} />
      </Routes>
    </>
  )
}

export default App
