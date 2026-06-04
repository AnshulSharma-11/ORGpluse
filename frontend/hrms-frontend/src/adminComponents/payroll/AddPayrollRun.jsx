import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { ADMIN_BASE } from '../../config/apiConfig';

export default function AddPayrollRun() {
  let { register, handleSubmit, formState: { errors } } = useForm();
  let nav = useNavigate();
  let [employees, setEmployees] = useState([]);

  useEffect(() => {
    fetch(`${ADMIN_BASE}/employees`)
      .then(r => r.json())
      .then(obj => setEmployees(obj.data ?? []))
      .catch(() => toast.error('Failed to load employees'));
  }, []);

  async function onSubmit(data) {
    try {
      let payload = {
        month: parseInt(data.month, 10),
        year: parseInt(data.year, 10),
        runDate: data.runDate,
        status: data.status,
        payslipData: data.payslipData,
        processedBy: data.processedById ? { id: parseInt(data.processedById, 10) } : undefined,
      };
      let res = await fetch(`${ADMIN_BASE}/payroll`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (res.ok) { toast.success('Payroll run created!'); nav('/admin/payroll', { replace: true }); }
      else toast.error('Failed to create payroll run');
    } catch {
      toast.error('Failed to create payroll run');
    }
  }

  return (
    <div className="hrms-content">
      <div className="page-header">
        <h4><i className="bi bi-calendar-check"></i> New Payroll Run</h4>
      </div>
      <div className="hrms-form-card">
        <form onSubmit={handleSubmit(onSubmit)} noValidate>

          <div className="mb-3">
            <label className="form-label">Month</label>
            <select className={`form-select ${errors.month ? 'is-invalid' : ''}`}
              {...register('month', { required: 'Month is required' })}>
              <option value="">— Select Month —</option>
              {Array.from({ length: 12 }, (_, i) => (
                <option key={i + 1} value={i + 1}>
                  {new Date(0, i).toLocaleString('default', { month: 'long' })}
                </option>
              ))}
            </select>
            <div style={{ minHeight: 20 }}>
              {errors.month && <div className="invalid-feedback d-block">{errors.month.message}</div>}
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label">Year</label>
            <input type="number" className={`form-control ${errors.year ? 'is-invalid' : ''}`}
              placeholder={new Date().getFullYear()}
              {...register('year', { required: 'Year is required', min: { value: 2000, message: 'Invalid year' } })} />
            <div style={{ minHeight: 20 }}>
              {errors.year && <div className="invalid-feedback d-block">{errors.year.message}</div>}
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label">Run Date</label>
            <input type="date" className={`form-control ${errors.runDate ? 'is-invalid' : ''}`}
              {...register('runDate', { required: 'Run date is required' })} />
            <div style={{ minHeight: 20 }}>
              {errors.runDate && <div className="invalid-feedback d-block">{errors.runDate.message}</div>}
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label">Status</label>
            <select className={`form-select ${errors.status ? 'is-invalid' : ''}`}
              {...register('status', { required: 'Status is required' })}>
              <option value="">— Select Status —</option>
              <option value="DRAFT">Draft</option>
              <option value="PROCESSED">Processed</option>
              <option value="APPROVED">Approved</option>
              <option value="PAID">Paid</option>
            </select>
            <div style={{ minHeight: 20 }}>
              {errors.status && <div className="invalid-feedback d-block">{errors.status.message}</div>}
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label">Processed By</label>
            <select className="form-select" {...register('processedById')}>
              <option value="">— Select Employee —</option>
              {employees.map(e => (
                <option key={e.id} value={e.id}>{e.firstName} {e.lastName}</option>
              ))}
            </select>
            <div style={{ minHeight: 20 }}></div>
          </div>

          <div className="mb-3">
            <label className="form-label">Payslip Data <small className="text-muted">(JSON)</small></label>
            <textarea className="form-control" rows={5}
              placeholder='{"employees": [...], "totalAmount": 0}'
              {...register('payslipData')} />
            <div style={{ minHeight: 20 }}></div>
          </div>

          <div className="d-flex gap-2">
            <button className="btn btn-primary px-4">Create Payroll Run</button>
            <button type="button" className="btn btn-outline-secondary" onClick={() => nav('/admin/payroll')}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
}
