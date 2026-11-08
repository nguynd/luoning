import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import client from '../api/client'
import { formatDuration } from '../utils/format'

const cardClass = 'rounded-lg border border-slate-200 bg-white p-6 dark:border-slate-700 dark:bg-slate-800'

function LessonPage() {
  const { courseId, lessonId } = useParams()
  const [lesson, setLesson] = useState(null)
  const [lessons, setLessons] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([
      client.get(`/courses/${courseId}/lessons/${lessonId}`),
      client.get(`/courses/${courseId}/lessons`),
    ])
      .then(([lessonResponse, lessonsResponse]) => {
        setLesson(lessonResponse.data)
        setLessons(lessonsResponse.data)
        setError('')
      })
      .catch((err) => {
        if (err.response?.status === 401) {
          setError('Bạn cần đăng nhập để xem bài giảng')
        } else {
          setError(err.response?.data?.detail ?? 'Không tải được bài giảng')
        }
      })
  }, [courseId, lessonId])

  if (error) {
    return (
      <main className="mx-auto max-w-5xl px-4 py-8">
        <p className="mb-4 rounded-lg bg-red-50 p-4 text-red-700 dark:bg-red-950 dark:text-red-300">{error}</p>
        <Link className="text-teal-700 hover:underline dark:text-teal-400" to={`/courses/${courseId}`}>← Về trang khóa học</Link>
      </main>
    )
  }

  // Khi chuyen bai, lesson cu van con cho toi khi bai moi tai xong
  if (!lesson || lesson.id !== Number(lessonId)) {
    return <main className="mx-auto max-w-5xl px-4 py-8 text-slate-500">Đang tải...</main>
  }

  const position = lessons.findIndex((item) => item.id === lesson.id)
  const previous = lessons[position - 1]
  const next = lessons[position + 1]

  return (
    <main className="mx-auto grid max-w-6xl gap-6 px-4 py-8 lg:grid-cols-[1fr_300px]">
      <div>
        <Link className="text-sm text-teal-700 hover:underline dark:text-teal-400" to={`/courses/${courseId}`}>← Về trang khóa học</Link>

        <div className="mt-4 overflow-hidden rounded-lg bg-black">
          {lesson.videoUrl ? (
            // key giup trinh duyet tai lai video khi chuyen sang bai khac
            <video key={lesson.id} className="aspect-video w-full" src={lesson.videoUrl} controls />
          ) : (
            <p className="flex aspect-video items-center justify-center text-slate-300">Bài này chưa có video</p>
          )}
        </div>

        <h1 className="mt-4 text-2xl font-bold wrap-anywhere">{lesson.orderIndex}. {lesson.title}</h1>

        <div className="mt-6 flex justify-between gap-4">
          {previous ? (
            <Link className="text-teal-700 hover:underline dark:text-teal-400" to={`/courses/${courseId}/lessons/${previous.id}`}>← Bài trước</Link>
          ) : <span />}
          {next && (
            <Link className="text-teal-700 hover:underline dark:text-teal-400" to={`/courses/${courseId}/lessons/${next.id}`}>Bài tiếp theo →</Link>
          )}
        </div>
      </div>

      <aside className={`h-fit ${cardClass}`}>
        <h2 className="mb-3 font-semibold">Danh sách bài</h2>
        <ol className="space-y-1 text-sm">
          {lessons.map((item) => (
            <li key={item.id}>
              <Link
                className={`flex justify-between gap-2 rounded-md px-2 py-1.5 hover:bg-slate-100 dark:hover:bg-slate-700 ${
                  item.id === lesson.id ? 'bg-teal-50 font-semibold text-teal-800 dark:bg-teal-950 dark:text-teal-300' : ''
                }`}
                to={`/courses/${courseId}/lessons/${item.id}`}
              >
                <span>{item.orderIndex}. {item.title}</span>
                <span className="whitespace-nowrap text-slate-500 dark:text-slate-400">{formatDuration(item.durationSeconds)}</span>
              </Link>
            </li>
          ))}
        </ol>
      </aside>
    </main>
  )
}

export default LessonPage
