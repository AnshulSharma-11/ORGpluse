import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { ADMIN_BASE } from '../../config/apiConfig';

export default function MyAuditLog() {
  const { employeeId } = useParams();
  const [logs, setLogs] = useState(null);

  useEffect(() => {
    fetch(
      `${ADMIN_BASE}/audit-logs/filter?userId=${employeeId}&sortBy=timestamp&sortDirection=desc`
    )
      .then(r => r.json())
      .then(obj => setLogs(obj.data ?? []))
      .catch(() => { setLogs([]); toast.error('Failed to load audit logs'); });
  }, [employeeId]);

  if (logs === null) return (
    <div className="d-flex align-items-center gap-2 mt-4">
      <span className="spinner-border spinner-border-sm"></span> Loading...
    </div>
  );

  return (
    <div>
      <div className="page-header">
        <h4><i className="bi bi-file-text"></i> My Audit Log</h4>
        <span className="badge bg-light text-secondary border">
          <i className="bi bi-lock me-1"></i>Read Only
        </span>
      </div>

      <div className="hrms-card">
        <div className="table-responsive">
          <table className="table hrms-table mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>Action</th>
                <th>Entity Type</th>
                <th>Entity ID</th>
                <th>IP Address</th>
                <th>Timestamp</th>
              </tr>
            </thead>
            <tbody>
              {logs.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center text-muted py-4">No audit records found.</td>
                </tr>
              ) : (
                logs.map((log, idx) => (
                  <tr key={log.id}>
                    <td>{idx + 1}</td>
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
