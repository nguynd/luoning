import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import client from '../api/client'
import { formatPrice } from '../utils/format'

const cardClass = 'rounded-lg border border-slate-200 bg-white p-6 dark:border-slate-700 dark:bg-slate-800'
const inputClass = 'mt-1.5 block w-full rounded-md border border-slate-200 bg-slate-50 px-3 py-2.5 font-normal focus:border-teal-700 focus:outline-none dark:border-slate-700 dark:bg-slate-900'
const errorClass = 'mb-4 rounded-lg bg-red-50 p-4 text-red-700 dark:bg-red-950 dark:text-red-300'

const emptyForm = { title: '', description: '', price: 0 }

function InstructorDashboardPage() {
  const [courses, setCourses] = useState([])
  const [loadError, setLoadError] = useState('')
  const [form, setForm] = useState(emptyForm)
  const [formError, setFormError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    client.get('/instructor/courses')
      .then((response) => setCourses(response.data))
      .catch((err) => setLoadError(err.response?.data?.detail ?? 'Không tải được khóa học của bạn'))
  }, [])

  function handleChange(event) {
    setForm({ ...form, [event.target.name]: event.target.value })
  }

  async function handleCreate(event) {
    event.preventDefault()
    setFormError('')
    setSubmitting(true)
    try {
      const response = await client.post('/courses', { ...form, price: Number(form.price) })
      // Them khoa hoc moi len dau danh sach, khong can tai lai trang
      setCourses([response.data, ...courses])
      setForm(emptyForm)
    } catch (err) {
      setFormError(err.response?.data?.detail ?? 'Không tạo được khóa học')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="mx-auto grid max-w-5xl gap-6 px-4 py-8 lg:grid-cols-[1fr_340px]">
      <section>
        <h1 className="mb-1 text-3xl font-bold">Khóa học của tôi</h1>
        <p className="mb-6 text-slate-500 dark:text-slate-400">Chọn một khóa học để quản lý bài giảng.</p>

        {loadError && <p className={errorClass}>{loadError}</p>}
        {!loadError && courses.length === 0 && (
          <p className={`${cardClass} text-slate-500 dark:text-slate-400`}>Bạn chưa có khóa học nào.</p>
        )}

        <ul className="space-y-3">
          {courses.map((course) => (
            <li key={course.id}>
              <Link
                className={`flex items-center justify-between gap-4 ${cardClass} hover:border-teal-700`}
                to={`/instructor/courses/${course.id}`}
              >
                <span className="font-semibold wrap-anywhere">{course.title}</span>
                <span className="whitespace-nowrap text-teal-700 dark:text-teal-400">{formatPrice(course.price)}</span>
              </Link>
            </li>
          ))}
        </ul>
      </section>

      <form className={`h-fit ${cardClass}`} onSubmit={handleCreate}>
        <h2 className="mb-4 text-xl font-semibold">Tạo khóa học mới</h2>

        {formError && <p className={errorClass}>{formError}</p>}

        <label className="mb-4 block text-sm font-semibold">
          Tên khóa học
          <input className={inputClass} name="title" value={form.title} onChange={handleChange} maxLength={100} required />
        </label>

        <label className="mb-4 block text-sm font-semibold">
          Mô tả
          <textarea className={inputClass} name="description" rows={4} value={form.description} onChange={handleChange} maxLength={2000} />
        </label>

        <label className="mb-4 block text-sm font-semibold">
          Giá (VNĐ, nhập 0 nếu miễn phí)
          <input className={inputClass} type="number" name="price" min={0} step={1000} value={form.price} onChange={handleChange} required />
        </label>

        <button
          className="w-full cursor-pointer rounded-md bg-teal-700 px-4 py-2.5 font-semibold text-white hover:bg-teal-800 disabled:cursor-wait disabled:opacity-60"
          type="submit"
          disabled={submitting}
        >
          {submitting ? 'Đang tạo...' : 'Tạo khóa học'}
        </button>
      </form>
    </main>
  )
}

export default InstructorDashboardPage
