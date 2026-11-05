import { useState } from 'react'
import client from '../api/client'
import { useAuth } from '../context/AuthContext'
import { Link, useNavigate } from 'react-router-dom'

function LoginPage() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [submitting, setSubmitting] = useState(false)
    const navigate = useNavigate()
    const { login } = useAuth()


    async function handleSubmit(event) {
        event.preventDefault()
        setError('')
        setSubmitting(true)

        try {
            const response = await client.post('/auth/login', { email, password })
            login(response.data.accessToken, response.data.user)
            navigate('/')
        } catch (err) {
            setError(err.response?.data?.detail ?? 'Không kết nối được máy chủ')
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <main className="mx-auto max-w-5xl px-4 py-8">
            <form className="mx-auto mt-12 max-w-sm rounded-lg p-6 border border-slate-200 bg-white dark:border-slate-700 dark:bg-slate-800" onSubmit={handleSubmit}>
                <h1 className="mb-5 text-2xl font-bold">Đăng nhập</h1>

                {error && <p className="mb-4 rounded-lg bg-red-50 p-4 text-red-700 dark:bg-red-950 dark:text-red-300">{error}</p>}

                <label className="mb-4 block text-sm font-semibold">
                    Email
                    <input
                        className="mt-1.5 block w-full rounded-md border border-slate-200 bg-slate-50 px-3 py-2.5 font-normal focus:border-teal-700 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                        type="email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        required
                    />
                </label>

                <label className="mb-4 block text-sm font-semibold">
                    Mật khẩu
                    <input
                        className="mt-1.5 block w-full rounded-md border border-slate-200 bg-slate-50 px-3 py-2.5 font-normal focus:border-teal-700 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        required
                    />
                </label>

                <button className="w-full cursor-pointer rounded-md bg-teal-700 px-4 py-2.5 font-semibold text-white hover:bg-teal-800 disabled:cursor-wait disabled:opacity-60" type="submit" disabled={submitting}>
                    {submitting ? 'Đang đăng nhập...' : 'Đăng nhập'}
                </button>
                <p className="mt-4 text-center text-sm">
                    Chưa có tài khoản?{' '}
                    <Link className="text-teal-700 hover:underline dark:text-teal-400" to="/register">Đăng ký</Link>
                </p>
            </form>
        </main>
    )
}

export default LoginPage
