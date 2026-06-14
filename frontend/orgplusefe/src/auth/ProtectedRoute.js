import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';

/**
 * Wraps a route and enforces authentication.
 *
 * Props
 *   role  – 'admin' | 'employee'
 *   children – the protected element
 *
 * Redirects to /admin/login or /employee/login, preserving
 * the attempted URL in location.state.from so the login page
 * can forward there after a successful sign-in.
 */
export default function ProtectedRoute({ role, children }) {
  const { isAdminAuth, isEmployeeAuth } = useAuth();
  const location = useLocation();

  const authenticated = role === 'admin' ? isAdminAuth : isEmployeeAuth;
  const loginPath     = role === 'admin' ? '/admin/login' : '/employee/login';

  if (!authenticated) {
    return <Navigate to={loginPath} state={{ from: location }} replace />;
  }

  return children;
}
