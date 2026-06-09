import React from 'react';
import { NavLink, useParams, useNavigate } from 'react-router-dom';


export default function UserNavbar() {
  const { employeeId } = useParams();

  const navigate = useNavigate();
  const base = `/employee/${employeeId}`;
  const linkClass = ({ isActive }) => isActive ? 'active' : '';

  

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
        <NavLink to={`${base}/profile`} className={linkClass}>
          <i className="bi bi-person-badge"></i> My Profile
        </NavLink>
        <NavLink to={`${base}/documents`} className={linkClass}>
          <i className="bi bi-folder2-open"></i> My Documents
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
        
      </div>
    </nav>
  );
}
