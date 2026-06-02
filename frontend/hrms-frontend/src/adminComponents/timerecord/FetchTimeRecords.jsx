import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { ADMIN_BASE } from '../../config/apiConfig';
import DisplayTimeRecords from './DisplayTimeRecords';

function FilterBar({ onFilter }) {
  const { register, handleSubmit, reset } = useForm();
  return (
    <form className="filter-bar" onSubmit={handleSubmit(onFilter)}>
      <input className="form-control" style={{ maxWidth: 180 }} placeholder="Employee ID" {...register('employeeId')} />
      <select className="form-select" style={{ maxWidth: 160 }} {...register('status')}>
        <option value="">All Status</option>
        <option value="PRESENT">Present</option>
        <option value="ABSENT">Absent</option>
        <option value="HALF_DAY">Half Day</option>
        <option value="ON_LEAVE">On Leave</option>
      </select>
      <input type="date" className="form-control" style={{ maxWidth: 160 }} placeholder="From Date" {...register('dateFrom')} />
      <input type="date" className="form-control" style={{ maxWidth: 160 }} placeholder="To Date" {...register('dateTo')} />
      <select className="form-select" style={{ maxWidth: 130 }} {...register('sortDirection')}>
        <option value="desc">Newest First</option>
        <option value="asc">Oldest First</option>
      </select>
      <button className="btn btn-primary btn-sm px-3">Apply</button>
      <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => { reset(); onFilter({}); }}>Reset</button>
    </form>
  );
}

export default function FetchTimeRecords() {
  const [records, setRecords] = useState(null);
  const [isDeleted, setIsDeleted] = useState(false);
  const [filters, setFilters] = useState({});

  useEffect(() => {
    async function load() {
      try {
        let url = `${ADMIN_BASE}/time-records/filter?`;
        if (filters.employeeId)    url += `employeeId=${filters.employeeId}&`;
        if (filters.status)        url += `status=${filters.status}&`;
        if (filters.dateFrom)      url += `dateFrom=${filters.dateFrom}&`;
        if (filters.dateTo)        url += `dateTo=${filters.dateTo}&`;
        if (filters.sortDirection) url += `sortDirection=${filters.sortDirection}&`;
        const res = await fetch(url);
        const obj = await res.json();
        setRecords(obj.data ?? []);
      } catch {
        setRecords([]);
        toast.error('Failed to load time records');
      }
    }
    load();
  }, [isDeleted, filters]);

  async function deleteRecord(id) {
    setIsDeleted(false);
    try {
      const res = await fetch(`${ADMIN_BASE}/time-records/${id}`, { method: 'DELETE' });
      if (res.ok) { toast.success('Record deleted'); setIsDeleted(true); }
      else toast.error('Could not delete record');
    } catch {
      toast.error('Could not delete record');
    }
  }

  if (records === null) return <div className="hrms-content"><div className="d-flex align-items-center gap-2"><span className="spinner-border spinner-border-sm"></span> Loading...</div></div>;

  return <DisplayTimeRecords recordsValue={records} onDelete={deleteRecord} onFilter={setFilters} FilterBar={FilterBar} />;
}
