import { useEffect, useState } from 'react'
import client from '../api/client'
import { formatPrice } from '../utils/format'
import { Link } from 'react-router-dom'

function CourseListPage() {
  const [courses, setCourses] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    client.get('/courses')
      .then((response) => setCourses(response.data.content))
      .catch(() => setError('Không tải được danh sách khóa học. Bạn kiểm tra backend đã chạy chưa.'))
      .finally(() => setLoading(false))
  }, [])

  return (
    <main className="mx-auto max-w-5xl px-4 py-8">
      <h1 className="mb-1 text-3xl font-bold">Khóa học</h1>
      <p className="mb-6 text-slate-500 dark:text-slate-400">Chọn một khóa học để bắt đầu.</p>
      {loading && <p className="rounded-lg p-4 border border-slate-200 bg-white dark:border-slate-700 dark:bg-slate-800 text-slate-500 dark:text-slate-400">Đang tải khóa học...</p>}
      {error && <p className="mb-4 rounded-lg bg-red-50 p-4 text-red-700 dark:bg-red-950 dark:text-red-300">{error}</p>}
      {!loading && !error && courses.length === 0 && (
        <p className="rounded-lg p-4 border border-slate-200 bg-white dark:border-slate-700 dark:bg-slate-800 text-slate-500 dark:text-slate-400">Chưa có khóa học nào.</p>
      )}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {courses.map((course) => (
          <article className="flex flex-col rounded-lg p-5 border border-slate-200 bg-white dark:border-slate-700 dark:bg-slate-800" key={course.id}>
            <h2 className="mb-2 text-lg font-semibold wrap-anywhere">
              <Link className="hover:underline" to={`/courses/${course.id}`}>{course.title}</Link>
            </h2>
            <p className="mb-4 line-clamp-3 wrap-anywhere text-slate-500 dark:text-slate-400">{course.description}</p>
            <div className="mt-auto flex justify-between gap-3 border-t border-slate-200 pt-3 text-sm dark:border-slate-700">
              <span className="text-slate-500 dark:text-slate-400">{course.instructorName}</span>
              <span className="font-semibold whitespace-nowrap text-teal-700 dark:text-teal-400">{formatPrice(course.price)}</span>
            </div>
          </article>
        ))}
      </div>
    </main>
  )
}

export default CourseListPage
