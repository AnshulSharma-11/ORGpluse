import React from 'react';

export default function DisplayAuditLogs({ logsValue, onFilter, FilterBar }) {
  return (
    <div className="hrms-content">
      <div className="page-header">
        <h4><i className="bi bi-file-earmark-text"></i> Audit Logs</h4>
        <span className="badge bg-light text-secondary border">
          <i className="bi bi-lock me-1"></i>Read Only
        </span>
      </div>

      <FilterBar onFilter={onFilter} />

      <div className="hrms-card">
        <div className="table-responsive">
          <table className="table hrms-table mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>User</th>
                <th>Action</th>
                <th>Entity Type</th>
                <th>Entity ID</th>
                <th>IP Address</th>
                <th>Timestamp</th>
              </tr>
            </thead>
            <tbody>
              {logsValue.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center text-muted py-4">No audit logs found.</td>
                </tr>
              ) : (
                logsValue.map((log, idx) => (
                  <tr key={log.id}>
                    <td>{idx + 1}</td>
                    <td>
                      {log.user
                        ? `${log.user.firstName} ${log.user.lastName}`
                        : log.userId || '—'}
                    </td>
                    <td>
                      <span className="badge bg-light text-dark border">{log.action || '—'}</span>
                    </td>
                    <td>{log.entityType || '—'}</td>
                    <td>{log.entityId || '—'}</td>
                    <td><code>{log.ipAddress || '—'}</code></td>
                    <td>
                      {log.timestamp
                        ? new Date(log.timestamp).toLocaleString()
                        : '—'}
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
