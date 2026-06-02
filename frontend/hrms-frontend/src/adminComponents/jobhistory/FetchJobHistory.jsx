import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { ADMIN_BASE } from '../../config/apiConfig';
import DisplayJobHistory from './DisplayJobHistory';

function FilterBar({ onFilter }) {
  const { register, handleSubmit, reset } = useForm();
  return (
    <form className="filter-bar" onSubmit={handleSubmit(onFilter)}>
      <input className="form-control" style={{ maxWidth: 180 }} placeholder="Employee ID" {...register('employeeId')} />
      <select className="form-select" style={{ maxWidth: 200 }} {...register('changeType')}>
        <option value="">All Change Types</option>
        <option value="DEPARTMENT_CHANGE">Department Change</option>
        <option value="DESIGNATION_CHANGE">Designation Change</option>
      </select>
      <input type="date" className="form-control" style={{ maxWidth: 160 }} placeholder="From Date" {...register('effectiveDateFrom')} />
      <input type="date" className="form-control" style={{ maxWidth: 160 }} placeholder="To Date" {...register('effectiveDateTo')} />
      <select className="form-select" style={{ maxWidth: 130 }} {...register('sortDirection')}>
        <option value="desc">Newest First</option>
        <option value="asc">Oldest First</option>
      </select>
      <button className="btn btn-primary btn-sm px-3">Apply</button>
      <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => { reset(); onFilter({}); }}>Reset</button>
    </form>
  );
}

export default function FetchJobHistory() {
  const [history, setHistory] = useState(null);
  const [isDeleted, setIsDeleted] = useState(false);
  const [filters, setFilters] = useState({});

  useEffect(() => {
    async function load() {
      try {
        let url = `${ADMIN_BASE}/job-history/filter?`;
        if (filters.employeeId)       url += `employeeId=${filters.employeeId}&`;
        if (filters.changeType)       url += `changeType=${filters.changeType}&`;
        if (filters.effectiveDateFrom) url += `effectiveDateFrom=${filters.effectiveDateFrom}&`;
        if (filters.effectiveDateTo)  url += `effectiveDateTo=${filters.effectiveDateTo}&`;
        if (filters.sortDirection)    url += `sortDirection=${filters.sortDirection}&`;
        const res = await fetch(url);
        const obj = await res.json();
        setHistory(obj.data ?? []);
      } catch {
        setHistory([]);
        toast.error('Failed to load job history');
      }
    }
    load();
  }, [isDeleted, filters]);

  async function deleteRecord(id) {
    setIsDeleted(false);
    try {
      const res = await fetch(`${ADMIN_BASE}/job-history/${id}`, { method: 'DELETE' });
      if (res.ok) { toast.success('Record deleted'); setIsDeleted(true); }
      else toast.error('Could not delete record');
    } catch { toast.error('Could not delete record'); }
  }

  if (history === null) return <div className="hrms-content"><div className="d-flex align-items-center gap-2"><span className="spinner-border spinner-border-sm"></span> Loading...</div></div>;

  return <DisplayJobHistory historyValue={history} onDelete={deleteRecord} onFilter={setFilters} FilterBar={FilterBar} />;
}
