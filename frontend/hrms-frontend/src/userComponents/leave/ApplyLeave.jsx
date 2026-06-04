import React from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { ADMIN_BASE } from '../../config/apiConfig';

export default function ApplyLeave() {
  let { employeeId } = useParams();
  let { register, handleSubmit, formState: { errors } } = useForm();
  let nav = useNavigate();

  async function onSubmit(data) {
    try {
      let payload = {
        leaveType: data.leaveType,
        startDate: data.startDate,
        endDate: data.endDate,
        totalDays: parseInt(data.totalDays, 10),
        reason: data.reason,
        status: 'PENDING',
        employee: { id: parseInt(employeeId, 10) },
      };
      let res = await fetch(`${ADMIN_BASE}/leaves`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (res.ok) {
        toast.success('Leave application submitted!');
        nav(`/employee/${employeeId}/leaves`, { replace: true });
      } else {
        toast.error('Failed to submit leave application');
      }
    } catch {
      toast.error('Failed to submit leave application');
    }
  }

  return (
    <div>
      <div className="page-header">
        <h4><i className="bi bi-calendar-plus"></i> Apply for Leave</h4>
      </div>

      <div className="hrms-form-card">
        <form onSubmit={handleSubmit(onSubmit)} noValidate>

          <div className="mb-3">
            <label className="form-label">Leave Type</label>
            <select
              className={`form-select ${errors.leaveType ? 'is-invalid' : ''}`}
              {...register('leaveType', { required: 'Leave type is required' })}
            >
              <option value="">— Select Type —</option>
              <option value="SICK">Sick</option>
              <option value="CASUAL">Casual</option>
              <option value="EARNED">Earned</option>
              <option value="MATERNITY">Maternity</option>
              <option value="UNPAID">Unpaid</option>
            </select>
            <div style={{ minHeight: 20 }}>
              {errors.leaveType && (
                <div className="invalid-feedback d-block">{errors.leaveType.message}</div>
              )}
            </div>
          </div>

          <div className="row g-3 mb-1">
            <div className="col-md-6">
              <label className="form-label">Start Date</label>
              <input
                type="date"
                className={`form-control ${errors.startDate ? 'is-invalid' : ''}`}
                {...register('startDate', { required: 'Start date is required' })}
              />
              <div style={{ minHeight: 20 }}>
                {errors.startDate && (
                  <div className="invalid-feedback d-block">{errors.startDate.message}</div>
                )}
              </div>
            </div>

            <div className="col-md-6">
              <label className="form-label">End Date</label>
              <input
                type="date"
                className={`form-control ${errors.endDate ? 'is-invalid' : ''}`}
                {...register('endDate', { required: 'End date is required' })}
              />
              <div style={{ minHeight: 20 }}>
                {errors.endDate && (
                  <div className="invalid-feedback d-block">{errors.endDate.message}</div>
                )}
              </div>
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label">Total Days</label>
            <input
              type="number"
              min="1"
              className={`form-control ${errors.totalDays ? 'is-invalid' : ''}`}
              placeholder="e.g. 3"
              {...register('totalDays', {
                required: 'Total days is required',
                min: { value: 1, message: 'At least 1 day' },
              })}
            />
            <div style={{ minHeight: 20 }}>
              {errors.totalDays && (
                <div className="invalid-feedback d-block">{errors.totalDays.message}</div>
              )}
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label">Reason</label>
            <textarea
              className="form-control"
              rows={3}
              placeholder="Optional reason..."
              {...register('reason')}
            />
            <div style={{ minHeight: 20 }}></div>
          </div>

          <div className="d-flex gap-2">
            <button className="btn btn-primary px-4">Submit Application</button>
            <button
              type="button"
              className="btn btn-outline-secondary"
              onClick={() => nav(`/employee/${employeeId}/leaves`)}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
