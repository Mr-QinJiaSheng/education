import $http from '@/utils/httpRequest'
import axios from 'axios'
/**
 * 根据阶段获取年级
 * @param val
 * @param result
 */
export function getGradeInfoList (params, callback) {
  axios.get($http.httpUrl('/system/grade'), {
    params: params
  }).then(function (response) {
    callback(response)
  }).catch(function (error) {
    console.log(error)
  })
}
