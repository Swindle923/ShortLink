import request from '@/utils/request';

export const login = (data) =>
  request.post('/api/short-link/console/v1/login', data);

export const logout = () =>
  request.post('/api/short-link/console/v1/logout');

export const session = () =>
  request.get('/api/short-link/console/v1/session');

export const pageUsers = (params) =>
  request.get('/api/short-link/console/v1/users', { params });

export const freezeUser = (username) =>
  request.post('/api/short-link/console/v1/users/freeze', null, { params: { username } });

export const unfreezeUser = (username) =>
  request.post('/api/short-link/console/v1/users/unfreeze', null, { params: { username } });

export const updateUserRole = (username, role) =>
  request.post('/api/short-link/console/v1/users/role', null, { params: { username, role } });

export const pageLinks = (params) =>
  request.get('/api/short-link/console/v1/links', { params });

export const disableLink = (fullShortUrl, gid) =>
  request.post('/api/short-link/console/v1/links/disable', null, { params: { fullShortUrl, gid } });

export const enableLink = (fullShortUrl, gid) =>
  request.post('/api/short-link/console/v1/links/enable', null, { params: { fullShortUrl, gid } });

export const opsOverview = (params) =>
  request.get('/api/short-link/console/v1/ops/overview', { params });

export const opsHighRisk = (params) =>
  request.get('/api/short-link/console/v1/ops/high-risk', { params });

export const opsLifecycleAlerts = (params) =>
  request.get('/api/short-link/console/v1/ops/lifecycle-alerts', { params });

export const pageAuditLogs = (params) =>
  request.get('/api/short-link/console/v1/audit-logs', { params });
