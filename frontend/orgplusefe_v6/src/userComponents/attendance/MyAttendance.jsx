import authFetch from '../../config/authFetch';
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { EMPLOYEE_BASE } from '../../config/apiConfig';

function StatusBadge({ value }) {
  let cls = value ? `status-badge badge-${String(value).toLowerCase().replace(/ /g, '_')}` : '';
  return <span className={cls}>{value}</span>;
}

export default function MyAttendance() {
  let { employeeId } = useParams();
  let [records, setRecords] = useState(null);
  let [filters, setFilters] = useState({});
  let { register, handleSubmit, reset } = useForm({
    defaultValues: {
      month: new Date().getMonth() + 1,
      year: new Date().getFullYear(),
    },
  });

  useEffect(() => {
    async function load() {
      try {
        let url = `${EMPLOYEE_BASE(employeeId)}/attendance?`;
        if (filters.month && filters.year) {
          let m = String(filters.month).padStart(2, '0');
          let y = filters.year;
          // Get actual last day of the month
          let lastDay = new Date(y, filters.month, 0).getDate();
          url += `dateFrom=${y}-${m}-01&dateTo=${y}-${m}-${String(lastDay).padStart(2, '0')}&`;
        }
        let res = await authFetch(url);
        let obj = await res.json();
        setRecords(obj.data?.content ?? []);
      } catch {
        setRecords([]);
        toast.error('Failed to load attendance');
      }
    }
    load();
  }, [employeeId, filters]);

  let totalPresent = records?.filter(r => r.status === 'PRESENT').length ?? 0;
  let totalHours = records?.reduce((sum, r) => sum + (r.hoursWorked ?? 0), 0).toFixed(1) ?? 0;

  return (
    <div>
      <div className="page-header">
        <h4><i className="bi bi-clock"></i> My Attendance</h4>
      </div>

      {/* Filter bar */}
      <form
        className="filter-bar mb-3"
        onSubmit={handleSubmit(data => setFilters(data))}
      >
        <select className="form-select" style={{ maxWidth: 150 }} {...register('month')}>
          {Array.from({ length: 12 }, (_, i) => (
            <option key={i + 1} value={i + 1}>
              {new Date(0, i).toLocaleString('default', { month: 'long' })}
            </option>
          ))}
        </select>
        <input
          type="number"
          className="form-control"
          style={{ maxWidth: 100 }}
          placeholder="Year"
          {...register('year')}
        />
        <button className="btn btn-primary btn-sm px-3">Apply</button>
        <button
          type="button"
          className="btn btn-outline-secondary btn-sm"
          onClick={() => {
            reset({ month: new Date().getMonth() + 1, year: new Date().getFullYear() });
            setFilters({});
          }}
        >
          Reset
        </button>
      </form>

      <div className="hrms-card">
        <div className="table-responsive">
          <table className="table hrms-table mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>Date</th>
                <th>Check In</th>
                <th>Check Out</th>
                <th>Hours Worked</th>
                <th>Status</th>
                <th>Remarks</th>
              </tr>
            </thead>
            <tbody>
              {records === null ? (
                <tr>
                  <td colSpan={7} className="text-center py-4">
                    <span className="spinner-border spinner-border-sm me-2"></span> Loading...
                  </td>
                </tr>
              ) : records.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center text-muted py-4">No records found.</td>
                </tr>
              ) : (
                records.map((r, idx) => (
                  <tr key={r.id}>
                    <td>{idx + 1}</td>
                    <td>{r.date || '—'}</td>
                    <td>{r.checkIn ? new Date(r.checkIn).toLocaleTimeString() : '—'}</td>
                    <td>{r.checkOut ? new Date(r.checkOut).toLocaleTimeString() : '—'}</td>
                    <td>{r.hoursWorked != null ? `${r.hoursWorked}h` : '—'}</td>
                    <td><StatusBadge value={r.status} /></td>
                    <td>{r.remarks || '—'}</td>
                  </tr>
                ))
              )}
            </tbody>
            {records && records.length > 0 && (
              <tfoot>
                <tr style={{ background: '#f8fafc', fontWeight: 600 }}>
                  <td colSpan={4} className="text-end">Summary:</td>
                  <td>{totalHours}h</td>
                  <td>
                    <span className="status-badge badge-present">{totalPresent} Present</span>
                  </td>
                  <td></td>
                </tr>
              </tfoot>
            )}
          </table>
        </div>
      </div>
    </div>
  );
}
