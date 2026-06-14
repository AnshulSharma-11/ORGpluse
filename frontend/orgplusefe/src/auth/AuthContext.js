import React, { createContext, useContext, useState, useCallback } from 'react';

// ─────────────────────────────────────────────
//  Storage helpers
//  Two separate slots so admin and employee
//  sessions never collide.
// ─────────────────────────────────────────────
const ADMIN_TOKEN_KEY    = 'orgpluse_admin_token';
const ADMIN_USER_KEY     = 'orgpluse_admin_user';
const EMPLOYEE_TOKEN_KEY = 'orgpluse_employee_token';
const EMPLOYEE_USER_KEY  = 'orgpluse_employee_user';

function readJson(key) {
  try { return JSON.parse(sessionStorage.getItem(key)); } catch { return null; }
}
function writeJson(key, val) {
  sessionStorage.setItem(key, JSON.stringify(val));
}
function clearKeys(...keys) {
  keys.forEach(k => sessionStorage.removeItem(k));
}

// ─────────────────────────────────────────────
//  Context
// ─────────────────────────────────────────────
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // ── Admin state ──────────────────────────
  const [adminToken, setAdminToken] = useState(() => readJson(ADMIN_TOKEN_KEY));
  const [adminUser,  setAdminUser]  = useState(() => readJson(ADMIN_USER_KEY));

  // ── Employee state ───────────────────────
  const [employeeToken, setEmployeeToken] = useState(() => readJson(EMPLOYEE_TOKEN_KEY));
  const [employeeUser,  setEmployeeUser]  = useState(() => readJson(EMPLOYEE_USER_KEY));

  // ── Admin helpers ────────────────────────
  const loginAdmin = useCallback((token, user) => {
    writeJson(ADMIN_TOKEN_KEY, token);
    writeJson(ADMIN_USER_KEY,  user);
    setAdminToken(token);
    setAdminUser(user);
  }, []);

  const logoutAdmin = useCallback(() => {
    clearKeys(ADMIN_TOKEN_KEY, ADMIN_USER_KEY);
    setAdminToken(null);
    setAdminUser(null);
  }, []);

  // ── Employee helpers ─────────────────────
  const loginEmployee = useCallback((token, user) => {
    writeJson(EMPLOYEE_TOKEN_KEY, token);
    writeJson(EMPLOYEE_USER_KEY,  user);
    setEmployeeToken(token);
    setEmployeeUser(user);
  }, []);

  const logoutEmployee = useCallback(() => {
    clearKeys(EMPLOYEE_TOKEN_KEY, EMPLOYEE_USER_KEY);
    setEmployeeToken(null);
    setEmployeeUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{
      // admin
      adminToken, adminUser, loginAdmin, logoutAdmin,
      isAdminAuth: !!adminToken,
      // employee
      employeeToken, employeeUser, loginEmployee, logoutEmployee,
      isEmployeeAuth: !!employeeToken,
    }}>
      {children}
    </AuthContext.Provider>
  );
}

// ── Convenience hook ─────────────────────────
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>');
  return ctx;
}
