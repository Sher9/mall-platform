/**
 * 下载 Blob 文件（兼容 Edge 浏览器）
 * @param blob - Blob 数据
 * @param fileName - 文件名
 */
export function downloadBlob(blob: Blob, fileName: string): void {
  // Edge 浏览器特定处理
  if ((window as any).navigator.msSaveOrOpenBlob) {
    (window as any).navigator.msSaveOrOpenBlob(blob, fileName)
    return
  }
  
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.style.display = 'none'
  document.body.appendChild(link)
  link.click()
  
  // 延迟释放 URL，确保下载完成
  setTimeout(() => {
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  }, 100)
}

/**
 * 下载文件（通过 URL）
 * @param url - 文件 URL
 * @param fileName - 文件名
 */
export function downloadFile(url: string, fileName: string): void {
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.style.display = 'none'
  document.body.appendChild(link)
  link.click()
  
  setTimeout(() => {
    document.body.removeChild(link)
  }, 100)
}
