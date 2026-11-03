import request from './request'

/** 分页查询 */
export const pageBooks = (params) => request.get('/book/page', { params })

/** 详情 */
export const getBook = (id) => request.get(`/book/${id}`)

/** 新增 */
export const createBook = (data) => request.post('/book', data)

/** 修改 */
export const updateBook = (data) => request.put('/book', data)

/** 删除单本 */
export const deleteBook = (id) => request.delete(`/book/${id}`)

/** 批量删除 */
export const deleteBooks = (ids) => request.delete('/book/batch', { data: ids })

/** 分类列表 */
export const listCategories = () => request.get('/book/categories')
