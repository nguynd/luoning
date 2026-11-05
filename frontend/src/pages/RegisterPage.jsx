import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import client from '../api/client'
import { useAuth } from '../context/AuthContext'

const inputClass = 'mt-1.5 block w-full rounded-md border border-slate-200 bg-slate-50 px-3 py-2.5 font-normal focus:border-teal-700 focus:outline-none dark:border-slate-700 dark:bg-slate-900'

function FieldError({ message }) {
  if (!message) {
    return null
  }
  return <span className="mt-1 block font-normal text-red-700 dark:text-red-300">{message}</span>
}

function RegisterPage() {
  const [form, setForm] = useState({ fullName: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)
  const navigate = useNavigate()
  const { login } = useAuth()

  function handleChange(event) {
    setForm({ ...form, [event.target.name]: event.target.value })
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setFieldErrors({})
    setSubmitting(true)

    try {
      const response = await client.post('/auth/register', form)
      login(response.data.accessToken, response.data.user)
      navigate('/')
    } catch (err) {
      const data = err.response?.data
      setError(data?.detail ?? 'Không kết nối được máy chủ')
      setFieldErrors(data?.errors ?? {})
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="mx-auto max-w-5xl px-4 py-8">
      <form
        className="mx-auto mt-12 max-w-sm rounded-lg border border-slate-200 bg-white p-6 dark:border-slate-700 dark:bg-slate-800"
        onSubmit={handleSubmit}
      >
        <h1 className="mb-5 text-2xl font-bold">Đăng ký tài khoản</h1>

        {error && (
          <p className="mb-4 rounded-lg bg-red-50 p-4 text-red-700 dark:bg-red-950 dark:text-red-300">{error}</p>
        )}

        <label className="mb-4 block text-sm font-semibold">
          Họ tên
          <input className={inputClass} name="fullName" value={form.fullName} onChange={handleChange} required />
          <FieldError message={fieldErrors.fullName} />
        </label>

        <label className="mb-4 block text-sm font-semibold">
          Email
          <input className={inputClass} type="email" name="email" value={form.email} onChange={handleChange} required />
          <FieldError message={fieldErrors.email} />
        </label>

        <label className="mb-4 block text-sm font-semibold">
          Mật khẩu
          <input
            className={inputClass}
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            minLength={6}
            required
          />
          <FieldError message={fieldErrors.password} />
        </label>

        <button
          className="w-full cursor-pointer rounded-md bg-teal-700 px-4 py-2.5 font-semibold text-white hover:bg-teal-800 disabled:cursor-wait disabled:opacity-60"
          type="submit"
          disabled={submitting}
        >
          {submitting ? 'Đang đăng ký...' : 'Đăng ký'}
        </button>

        <p className="mt-4 text-center text-sm">
          Đã có tài khoản?{' '}
          <Link className="text-teal-700 hover:underline dark:text-teal-400" to="/login">Đăng nhập</Link>
        </p>
      </form>
    </main>
  )
}

export default RegisterPage
