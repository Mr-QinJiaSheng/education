export function hasPermission (permission) {
  let userInfo = JSON.parse(localStorage.getItem('userInfo'))
  let permissionList = userInfo.permissionList
  let flag = permissionList.indexOf(permission)
  return flag !== -1
}
