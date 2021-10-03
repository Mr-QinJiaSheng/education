import $http from '@/utils/httpRequest'
import axios from 'axios'

/**
/**
 * 根据字典类型获取字典值列表
 * @param callback
 */
export function getDictValueByType (params, callback) {
  axios.get($http.httpUrl('/dict/getDictValueByType'), {
    params: params
  }).then(function (response) {
    callback(response)
  }).catch(function (error) {
    console.log(error)
  })
}
