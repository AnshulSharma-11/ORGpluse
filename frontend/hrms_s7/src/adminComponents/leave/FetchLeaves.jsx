import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { ADMIN_BASE } from '../../config/apiConfig';
import DisplayLeaves from './DisplayLeaves';

function FilterBar({ onFilter }) {
  let { register, handleSubmit, reset } = useForm();
  return (
    <form className="filter-bar" onSubmit={handleSubmit(onFilter)}>
      <input className="form-control" style={{ maxWidth: 180 }} placeholder="Employee ID" {...register('employeeId')} />
      <select className="form-select" style={{ maxWidth: 150 }} {...register('status')}>
        <option value="">All Status</option>
        <option value="PENDING">Pending</option>
        <option value="APPROVED">Approved</option>
        <option value="REJECTED">Rejected</option>
      </select>
      <select className="form-select" style={{ maxWidth: 160 }} {...register('leaveType')}>
        <option value="">All Types</option>
        <option value="SICK">Sick</option>
        <option value="CASUAL">Casual</option>
        <option value="EARNED">Earned</option>
        <option value="MATERNITY">Maternity</option>
        <option value="UNPAID">Unpaid</option>
      </select>
      <input type="date" className="form-control" style={{ maxWidth: 160 }} {...register('startDate')} />
      <input type="date" className="form-control" style={{ maxWidth: 160 }} {...register('endDate')} />
      <select className="form-select" style={{ maxWidth: 150 }} {...register('sortBy')}>
        <option value="">Sort By</option>
        <option value="startDate">Start Date</option>
        <option value="totalDays">Total Days</option>
      </select>
      <select className="form-select" style={{ maxWidth: 130 }} {...register('sortDirection')}>
        <option value="desc">Newest First</option>
        <option value="asc">Oldest First</option>
      </select>
      <button className="btn btn-primary btn-sm px-3">Apply</button>
      <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => { reset(); onFilter({}); }}>Reset</button>
    </form>
  );
}

export default function FetchLeaves() {
  let [leaves, setLeaves] = useState(null);
  let [isRefresh, setIsRefresh] = useState(false);
  let [filters, setFilters] = useState({});

  useEffect(() => {
    async function load() {
      try {
        let url = `${ADMIN_BASE}/leaves/filter?`;
        if (filters.employeeId)    url += `employeeId=${filters.employeeId}&`;
        if (filters.status)        url += `status=${filters.status}&`;
        if (filters.leaveType)     url += `leaveType=${filters.leaveType}&`;
        if (filters.startDate)     url += `startDate=${filters.startDate}&`;
        if (filters.endDate)       url += `endDate=${filters.endDate}&`;
        if (filters.sortBy)        url += `sortBy=${filters.sortBy}&`;
        if (filters.sortDirection) url += `sortDirection=${filters.sortDirection}&`;
        let res = await fetch(url);
        let obj = await res.json();
        setLeaves(obj.data ?? []);
      } catch {
        setLeaves([]);
        toast.error('Failed to load leaves');
      }
    }
    load();
  }, [isRefresh, filters]);

  async function approveLeave(leave) {
    try {
      // Build a clean update payload — keep all existing leave fields,
      // set status to APPROVED, and preserve the existing approvedBy if set.
      // We do NOT hardcode approvedBy: { id: 1 } — backend stores whatever
      // approvedBy was already on the record (or null if not yet assigned).
      let payload = {
        leaveType:  leave.leaveType,
        startDate:  leave.startDate,
        endDate:    leave.endDate,
        reason:     leave.reason,
        status:     'APPROVED',
        rejectionNote: leave.rejectionNote,
        employee:   { id: leave.employee?.id },
        approvedBy: leave.approvedBy ? { id: leave.approvedBy.id } : null,
      };
      let res = await fetch(`${ADMIN_BASE}/leaves/${leave.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (res.ok) { toast.success('Leave approved'); setIsRefresh(v => !v); }
      else toast.error('Failed to approve leave');
    } catch { toast.error('Failed to approve leave'); }
  }

  async function rejectLeave(leave) {
    try {
      let note = window.prompt('Rejection reason (optional):') ?? '';
      let payload = {
        leaveType:     leave.leaveType,
        startDate:     leave.startDate,
        endDate:       leave.endDate,
        reason:        leave.reason,
        status:        'REJECTED',
        rejectionNote: note,
        employee:      { id: leave.employee?.id },
        approvedBy:    leave.approvedBy ? { id: leave.approvedBy.id } : null,
      };
      let res = await fetch(`${ADMIN_BASE}/leaves/${leave.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (res.ok) { toast.success('Leave rejected'); setIsRefresh(v => !v); }
      else toast.error('Failed to reject leave');
    } catch { toast.error('Failed to reject leave'); }
  }

  async function deleteLeave(id) {
    try {
      let res = await fetch(`${ADMIN_BASE}/leaves/${id}`, { method: 'DELETE' });
      if (res.ok) { toast.success('Leave deleted'); setIsRefresh(v => !v); }
      else toast.error('Failed to delete leave');
    } catch { toast.error('Failed to delete leave'); }
  }

  if (leaves === null) return <div className="hrms-content"><div className="d-flex align-items-center gap-2"><span className="spinner-border spinner-border-sm"></span> Loading...</div></div>;

  return (
    <DisplayLeaves
      leavesValue={leaves}
      onApprove={approveLeave}
      onReject={rejectLeave}
      onDelete={deleteLeave}
      onFilter={setFilters}
      FilterBar={FilterBar}
    />
  );
}
