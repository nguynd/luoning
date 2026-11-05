export function formatPrice(price) {
  return price === 0 ? 'Miễn phí' : `${price.toLocaleString('vi-VN')} ₫`
}

export function formatDuration(seconds) {
  if (seconds == null) {
    return ''
  }
  const minutes = Math.round(seconds / 60)
  return `${minutes} phút`
}
