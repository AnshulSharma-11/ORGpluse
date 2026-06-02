import React from 'react';
import { NavLink } from 'react-router-dom';

export default function AdminNavbar() {
  const link = ({ isActive }) => isActive ? 'nav-link active' : 'nav-link';

  return (
    <div className="hrms-sidebar">
      <div className="brand">
        <i className="bi bi-people-circle" style={{ fontSize: '1.4rem' }}></i>
        <div>
          ORGPLUSE
          <small>Admin Panel</small>
        </div>
      </div>

      <div className="section-label">Main</div>
      <NavLink to="/admin" end className={link}>
        <i className="bi bi-grid"></i> Dashboard
      </NavLink>
      <NavLink to="/admin/employees" className={link}>
        <i className="bi bi-people"></i> Employees
      </NavLink>

      <div className="section-label">Organisation</div>
      <NavLink to="/admin/departments" className={link}>
        <i className="bi bi-building"></i> Departments
      </NavLink>
      <NavLink to="/admin/branches" className={link}>
        <i className="bi bi-diagram-3"></i> Branches
      </NavLink>
      <NavLink to="/admin/designations" className={link}>
        <i className="bi bi-award"></i> Designations
      </NavLink>

      <div className="section-label">Operations</div>
      <NavLink to="/admin/time-records" className={link}>
        <i className="bi bi-clock"></i> Attendance
      </NavLink>
      <NavLink to="/admin/leaves" className={link}>
        <i className="bi bi-umbrella"></i> Leave
      </NavLink>
      <NavLink to="/admin/payroll" className={link}>
        <i className="bi bi-wallet2"></i> Payroll
      </NavLink>
      <NavLink to="/admin/performance" className={link}>
        <i className="bi bi-graph-up"></i> Performance
      </NavLink>

      <div className="section-label">Records</div>
      <NavLink to="/admin/job-history" className={link}>
        <i className="bi bi-briefcase"></i> Job History
      </NavLink>
      <NavLink to="/admin/audit-logs" className={link}>
        <i className="bi bi-file-text"></i> Audit Logs
      </NavLink>
      <NavLink to="/admin/help" className={link}>
        <i className="bi bi-headphones"></i> Help Desk
      </NavLink>

      <button
        className="logout-btn"
        onClick={() => alert('Logout clicked')}
      >
        <i className="bi bi-box-arrow-left"></i> Logout
      </button>
    </div>
  );
}
