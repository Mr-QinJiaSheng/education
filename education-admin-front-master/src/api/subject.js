import $http from '@/utils/httpRequest'
import axios from 'axios'

/**
 /**
 * 获取学校类型列表
 * @param callback
 */
export function getSubjectList (params, callback) {
  axios.get($http.httpUrl('/system/subject'), {
    params: params
  }).then(function (response) {
    callback(response)
  }).catch(function (error) {
    console.log(error)
  })
}
