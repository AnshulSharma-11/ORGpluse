import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ADMIN_BASE } from '../../config/apiConfig';

export default function AdminDashboard() {
  let [stats, setStats] = useState(null);

  useEffect(() => {
    async function load() {
      try {
        let [employees, departments, branches, payrolls] = await Promise.all([
          fetch(`${ADMIN_BASE}/employees`).then(r => r.json()),
          fetch(`${ADMIN_BASE}/departments`).then(r => r.json()),
          fetch(`${ADMIN_BASE}/branches`).then(r => r.json()),
          fetch(`${ADMIN_BASE}/payroll`).then(r => r.json()),
        ]);
        setStats({
          employees: employees.data?.length ?? 0,
          departments: departments.data?.length ?? 0,
          branches: branches.data?.length ?? 0,
          payrolls: payrolls.data?.length ?? 0,
        });
      } catch {
        setStats({ employees: 0, departments: 0, branches: 0, payrolls: 0 });
      }
    }
    load();
  }, []);

  let statCards = [
    {
      label: 'Total Employees',
      value: stats?.employees ?? '—',
      icon: 'bi-people-fill',
      bg: '#eff6ff',
      color: '#1d4ed8',
    },
    {
      label: 'Departments',
      value: stats?.departments ?? '—',
      icon: 'bi-building-fill',
      bg: '#f0fdf4',
      color: '#15803d',
    },
    {
      label: 'Branches',
      value: stats?.branches ?? '—',
      icon: 'bi-diagram-3-fill',
      bg: '#fff7ed',
      color: '#c2410c',
    },
    {
      label: 'Monthly Payroll',
      value: stats ? `${stats.payrolls} runs` : '—',
      icon: 'bi-wallet2',
      bg: '#f0fdf4',
      color: '#15803d',
    },
  ];

  let quickCards = [
    { label: 'Employees',    icon: 'bi-people',              cls: 'qc-blue',   to: '/admin/employees' },
    { label: 'Departments',  icon: 'bi-building',            cls: 'qc-green',  to: '/admin/departments' },
    { label: 'Branches',     icon: 'bi-diagram-3',           cls: 'qc-yellow', to: '/admin/branches' },
    { label: 'Payroll',      icon: 'bi-wallet2',             cls: 'qc-green',  to: '/admin/payroll' },
    { label: 'Designations', icon: 'bi-award',               cls: 'qc-purple', to: '/admin/designations' },
    { label: 'Attendance',   icon: 'bi-clock',               cls: 'qc-orange', to: '/admin/time-records' },
    { label: 'Leave',        icon: 'bi-umbrella',            cls: 'qc-teal',   to: '/admin/leaves' },
    { label: 'Performance',  icon: 'bi-graph-up-arrow',      cls: 'qc-pink',   to: '/admin/performance' },
    { label: 'Help Desk',    icon: 'bi-headphones',          cls: 'qc-indigo', to: '/admin/help' },
    { label: 'Payroll Run',  icon: 'bi-calendar-check',      cls: 'qc-lime',   to: '/admin/payroll/add' },
  ];

  return (
    <div className="hrms-content">
      <div className="page-header">
        <h4><i className="bi bi-grid"></i> Dashboard</h4>
        <span style={{ fontSize: '0.85rem', color: '#64748b' }}>
          {new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
        </span>
      </div>

      {/* Stats Row */}
      <div className="row g-3 mb-4">
        {statCards.map((card, i) => (
          <div className="col-sm-6 col-xl-3" key={i}>
            <div className="stat-card">
              <div className="stat-icon" style={{ background: card.bg, color: card.color }}>
                <i className={`bi ${card.icon}`}></i>
              </div>
              <div>
                <div className="stat-value">{card.value}</div>
                <div className="stat-label">{card.label}</div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Quick Access Grid */}
      <div className="section-title"><i className="bi bi-lightning-charge"></i> Quick Access</div>
      <div className="quick-grid">
        {quickCards.map((card, i) => (
          <Link key={i} to={card.to} className={`quick-card ${card.cls}`}>
            <i className={`bi ${card.icon}`}></i>
            <span>{card.label}</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
