import React from 'react';

export default function DisplayJobHistory({ historyValue, onDelete, onFilter, FilterBar }) {
  return (
    <div className="hrms-content">
      <div className="page-header">
        <h4><i className="bi bi-briefcase"></i> Job History</h4>
      </div>
      <FilterBar onFilter={onFilter} />
      <div className="hrms-card">
        <div className="table-responsive">
          <table className="table hrms-table mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>Employee</th>
                <th>Change Type</th>
                <th>Old Dept</th>
                <th>New Dept</th>
                <th>Old Designation</th>
                <th>New Designation</th>
                <th>Effective Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {historyValue.length === 0 ? (
                <tr><td colSpan={9} className="text-center text-muted py-4">No job history found.</td></tr>
              ) : (
                historyValue.map((h, idx) => (
                  <tr key={h.id}>
                    <td>{idx + 1}</td>
                    <td>{h.employee ? `${h.employee.firstName} ${h.employee.lastName}` : '—'}</td>
                    <td><span className="badge bg-light text-dark border">{h.changeType}</span></td>
                    <td>{h.oldDepartment?.name || '—'}</td>
                    <td>{h.newDepartment?.name || '—'}</td>
                    <td>{h.oldDesignation?.title || '—'}</td>
                    <td>{h.newDesignation?.title || '—'}</td>
                    <td>{h.effectiveDate || '—'}</td>
                    <td>
                      <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => { if (window.confirm('Delete this job history record?')) onDelete(h.id); }}
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
