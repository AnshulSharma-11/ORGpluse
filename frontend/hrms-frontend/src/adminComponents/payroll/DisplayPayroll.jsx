import React from 'react';
import { Link } from 'react-router-dom';

function StatusBadge({ value }) {
  const cls = value ? `status-badge badge-${String(value).toLowerCase().replace(/ /g, '_')}` : '';
  return <span className={cls}>{value}</span>;
}

const MONTH_NAMES = ['', 'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'];

export default function DisplayPayroll({ payrollsValue, onDelete, onFilter, FilterBar }) {
  return (
    <div className="hrms-content">
      <div className="page-header">
        <h4><i className="bi bi-wallet2"></i> Payroll Runs</h4>
        <Link to="/admin/payroll/add" className="btn btn-primary btn-sm">
          <i className="bi bi-plus-lg me-1"></i> New Payroll Run
        </Link>
      </div>
      <FilterBar onFilter={onFilter} />
      <div className="hrms-card">
        <div className="table-responsive">
          <table className="table hrms-table mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>Month / Year</th>
                <th>Run Date</th>
                <th>Status</th>
                <th>Processed By</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {payrollsValue.length === 0 ? (
                <tr><td colSpan={6} className="text-center text-muted py-4">No payroll runs found.</td></tr>
              ) : (
                payrollsValue.map((p, idx) => (
                  <tr key={p.id}>
                    <td>{idx + 1}</td>
                    <td><strong>{MONTH_NAMES[p.month] || p.month} {p.year}</strong></td>
                    <td>{p.runDate || '—'}</td>
                    <td><StatusBadge value={p.status} /></td>
                    <td>{p.processedBy ? `${p.processedBy.firstName} ${p.processedBy.lastName}` : '—'}</td>
                    <td>
                      <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => { if (window.confirm('Delete this payroll run?')) onDelete(p.id); }}
                      >
                        <i className="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
