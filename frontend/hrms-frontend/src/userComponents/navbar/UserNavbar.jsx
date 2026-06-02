import React from 'react';
import { NavLink, useParams } from 'react-router-dom';

export default function UserNavbar() {
  const { employeeId } = useParams();
  const base = `/employee/${employeeId}`;

  const linkClass = ({ isActive }) =>
    isActive ? 'active' : '';

  return (
    <nav className="user-navbar">
      <NavLink to={base} end className="brand">
        <i className="bi bi-people-circle"></i>
        ORGPLUSE Employee
      </NavLink>

      <div className="nav-links">
        <NavLink to={base} end className={linkClass}>
          <i className="bi bi-grid"></i> Dashboard
        </NavLink>
        <NavLink to={`${base}/leaves`} className={linkClass}>
          <i className="bi bi-umbrella"></i> My Leaves
        </NavLink>
        <NavLink to={`${base}/help/raise`} className={linkClass}>
          <i className="bi bi-headphones"></i> Help
        </NavLink>
        <NavLink to={`${base}/payroll`} className={linkClass}>
          <i className="bi bi-wallet2"></i> Payroll
        </NavLink>
        <NavLink to={`${base}/attendance`} className={linkClass}>
          <i className="bi bi-clock"></i> Attendance
        </NavLink>
        <NavLink to={`${base}/audit-log`} className={linkClass}>
          <i className="bi bi-file-text"></i> Audit Log
        </NavLink>
        <button
          className="nav-links"
          style={{
            background: 'none', border: 'none', cursor: 'pointer',
            color: '#f87171', padding: '6px 12px', borderRadius: '7px',
            fontSize: '0.86rem', display: 'flex', alignItems: 'center', gap: '6px',
          }}
          onClick={() => alert('Logout clicked')}
        >
          <i className="bi bi-box-arrow-right"></i> Logout
        </button>
      </div>
    </nav>
  );
}
