import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import client from '../api/client'
import { useAuth } from '../context/AuthContext'
import { formatDuration, formatPrice } from '../utils/format'

const cardClass = 'rounded-lg border border-slate-200 bg-white p-6 dark:border-slate-700 dark:bg-slate-800'
const errorClass = 'mb-4 rounded-lg bg-red-50 p-4 text-red-700 dark:bg-red-950 dark:text-red-300'
const buttonClass = 'cursor-pointer rounded-md bg-teal-700 px-5 py-2.5 font-semibold text-white hover:bg-teal-800 disabled:cursor-wait disabled:opacity-60'

function CourseDetailPage() {
  const { courseId } = useParams()
  const { user, loading: authLoading } = useAuth()
  const [course, setCourse] = useState(null)
  const [lessons, setLessons] = useState([])
  const [enrolled, setEnrolled] = useState(false)
  const [error, setError] = useState('')
  const [enrollError, setEnrollError] = useState('')
  const [enrolling, setEnrolling] = useState(false)

  // Thong tin khoa hoc va danh sach bai la cong khai
  useEffect(() => {
    Promise.all([
      client.get(`/courses/${courseId}`),
      client.get(`/courses/${courseId}/lessons`),
    ])
      .then(([courseResponse, lessonsResponse]) => {
        setCourse(courseResponse.data)
        setLessons(lessonsResponse.data)
      })
      .catch((err) => {
        setError(err.response?.data?.detail ?? 'Không tải được khóa học')
      })
  }, [courseId])

  // Chi hoc vien moi can biet minh da dang ky khoa nay chua
  useEffect(() => {
    if (user?.role !== 'STUDENT') {
      return
    }
    client.get('/enrollments/me')
      .then((response) => {
        setEnrolled(response.data.some((enrollment) => enrollment.courseId === Number(courseId)))
      })
  }, [user, courseId])

  async function handleEnroll() {
    setEnrollError('')
    setEnrolling(true)
    try {
      await client.post(`/courses/${courseId}/enroll`)
      setEnrolled(true)
    } catch (err) {
      setEnrollError(err.response?.data?.detail ?? 'Không đăng ký được khóa học')
    } finally {
      setEnrolling(false)
    }
  }

  if (error) {
    return (
      <main className="mx-auto max-w-5xl px-4 py-8">
        <p className={errorClass}>{error}</p>
      </main>
    )
  }

  if (!course) {
    return <main className="mx-auto max-w-5xl px-4 py-8 text-slate-500">Đang tải...</main>
  }

  const isOwner = user?.id === course.instructorId
  const canWatch = enrolled || isOwner

  function renderAction() {
    if (authLoading) {
      return null
    }
    if (!user) {
      return <Link className={`inline-block ${buttonClass}`} to="/login">Đăng nhập để đăng ký</Link>
    }
    if (isOwner) {
      return <p className="font-semibold text-teal-700 dark:text-teal-400">Bạn là giảng viên của khóa học này</p>
    }
    if (enrolled) {
      return <p className="font-semibold text-teal-700 dark:text-teal-400">Bạn đã đăng ký khóa học này</p>
    }
    if (user.role !== 'STUDENT') {
      return null
    }
    return (
      <button className={buttonClass} type="button" onClick={handleEnroll} disabled={enrolling}>
        {enrolling ? 'Đang đăng ký...' : 'Đăng ký học'}
      </button>
    )
  }

  return (
    <main className="mx-auto max-w-5xl px-4 py-8">
      <Link className="text-sm text-teal-700 hover:underline dark:text-teal-400" to="/">← Tất cả khóa học</Link>

      <section className={`mt-4 ${cardClass}`}>
        <h1 className="mb-2 text-3xl font-bold wrap-anywhere">{course.title}</h1>
        <p className="mb-4 text-slate-500 dark:text-slate-400">Giảng viên: {course.instructorName}</p>
        <p className="mb-6 whitespace-pre-line wrap-anywhere">{course.description}</p>

        <div className="flex flex-wrap items-center justify-between gap-4 border-t border-slate-200 pt-4 dark:border-slate-700">
          <span className="text-2xl font-bold text-teal-700 dark:text-teal-400">{formatPrice(course.price)}</span>
          {renderAction()}
        </div>
        {enrollError && <p className={`mt-4 ${errorClass}`}>{enrollError}</p>}
      </section>

      <section className={`mt-6 ${cardClass}`}>
        <h2 className="mb-4 text-xl font-semibold">Nội dung khóa học ({lessons.length} bài)</h2>

        {lessons.length === 0 && <p className="text-slate-500 dark:text-slate-400">Khóa học chưa có bài giảng.</p>}

        <ol className="divide-y divide-slate-200 dark:divide-slate-700">
          {lessons.map((lesson) => (
            <li className="flex items-center justify-between gap-4 py-3" key={lesson.id}>
              <span>
                <span className="mr-3 text-slate-500 dark:text-slate-400">{lesson.orderIndex}.</span>
                {canWatch ? (
                  <Link className="hover:underline" to={`/courses/${courseId}/lessons/${lesson.id}`}>{lesson.title}</Link>
                ) : (
                  lesson.title
                )}
              </span>
              <span className="whitespace-nowrap text-sm text-slate-500 dark:text-slate-400">
                {formatDuration(lesson.durationSeconds)}
              </span>
            </li>
          ))}
        </ol>
      </section>
    </main>
  )
}

export default CourseDetailPage
