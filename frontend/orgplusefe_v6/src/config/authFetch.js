/**
 * authFetch — drop-in replacement for fetch() that automatically attaches
 * the JWT Authorization header for the currently logged-in user.
 *
 * HOW IT WORKS:
 * - Reads tokens from sessionStorage via the same keys AuthContext uses.
 * - Admin routes  (/api/v1/admin/*)    → uses orgpluse_admin_token
 * - Employee routes (/api/v1/employee/*) → uses orgpluse_employee_token
 * - Falls back to whichever token is present.
 *
 * Content-Type handling:
 * - Defaults to application/json for all requests.
 * - When body is FormData (file uploads), Content-Type is NOT set so the
 *   browser can set it automatically with the correct multipart boundary.
 *
 * USAGE (identical to fetch):
 *   const res = await authFetch(`${ADMIN_BASE}/employees`);
 *   const res = await authFetch(`${ADMIN_BASE}/employees`, { method: 'POST', body: JSON.stringify(payload) });
 *   const res = await authFetch(`${EMPLOYEE_BASE}/documents`, { method: 'POST', body: formData });
 */

const ADMIN_TOKEN_KEY    = 'orgpluse_admin_token';
const EMPLOYEE_TOKEN_KEY = 'orgpluse_employee_token';

function getToken(url) {
  try {
    if (url && url.includes('/api/v1/admin')) {
      return JSON.parse(sessionStorage.getItem(ADMIN_TOKEN_KEY));
    }
    if (url && url.includes('/api/v1/employee')) {
      return JSON.parse(sessionStorage.getItem(EMPLOYEE_TOKEN_KEY));
    }
    return (
      JSON.parse(sessionStorage.getItem(ADMIN_TOKEN_KEY)) ||
      JSON.parse(sessionStorage.getItem(EMPLOYEE_TOKEN_KEY))
    );
  } catch {
    return null;
  }
}

export default function authFetch(url, options = {}) {
  const token = getToken(url);
  const isFormData = options.body instanceof FormData;

  const headers = {
    // Only set Content-Type for non-FormData — browser sets it automatically for FormData
    ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
    ...(options.headers || {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  return fetch(url, { ...options, headers });
}
