import axios from 'axios';
import { ElMessage } from 'element-plus';

const request = axios.create({
  baseURL: '',
  timeout: 15000
});

request.interceptors.request.use((config) => {
  const username = localStorage.getItem('admin_username');
  const token = localStorage.getItem('admin_token');
  if (username && token) {
    config.headers['Username'] = encodeURIComponent(username);
    config.headers['Token'] = token;
  }
  return config;
});

request.interceptors.response.use(
  (resp) => {
    const data = resp.data;
    if (data && typeof data === 'object' && 'code' in data) {
      if (data.code !== '0') {
        ElMessage.error(data.message || '请求失败');
        return Promise.reject(data);
      }
      return data;
    }
    return data;
  },
  (err) => {
    const status = err.response?.status;
    if (status === 401) {
      ElMessage.error('登录已失效，请重新登录');
      localStorage.removeItem('admin_username');
      localStorage.removeItem('admin_token');
      if (location.pathname !== '/login') {
        location.href = '/login';
      }
    } else if (status === 403) {
      ElMessage.error(err.response?.data?.message || '无权限访问');
    } else {
      ElMessage.error(err.response?.data?.message || err.message || '网络错误');
    }
    return Promise.reject(err);
  }
);

export default request;
