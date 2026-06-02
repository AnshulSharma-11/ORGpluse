import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { ADMIN_BASE } from '../../config/apiConfig';
import DisplayAuditLogs from './DisplayAuditLogs';

function FilterBar({ onFilter }) {
  const { register, handleSubmit, reset } = useForm();
  return (
    <form className="filter-bar" onSubmit={handleSubmit(onFilter)}>
      <input
        className="form-control"
        style={{ maxWidth: 160 }}
        placeholder="User ID"
        {...register('userId')}
      />
      <input
        className="form-control"
        style={{ maxWidth: 160 }}
        placeholder="Action"
        {...register('action')}
      />
      <input
        className="form-control"
        style={{ maxWidth: 160 }}
        placeholder="Entity Type"
        {...register('entityType')}
      />
      <input
        type="date"
        className="form-control"
        style={{ maxWidth: 160 }}
        {...register('dateFrom')}
      />
      <input
        type="date"
        className="form-control"
        style={{ maxWidth: 160 }}
        {...register('dateTo')}
      />
      <button className="btn btn-primary btn-sm px-3">Apply</button>
      <button
        type="button"
        className="btn btn-outline-secondary btn-sm"
        onClick={() => { reset(); onFilter({}); }}
      >
        Reset
      </button>
    </form>
  );
}

export default function FetchAuditLogs() {
  const [logs, setLogs] = useState(null);
  const [filters, setFilters] = useState({});

  useEffect(() => {
    async function load() {
      try {
        let url = `${ADMIN_BASE}/audit-logs/filter?sortBy=timestamp&sortDirection=desc&`;
        if (filters.userId)     url += `userId=${filters.userId}&`;
        if (filters.action)     url += `action=${encodeURIComponent(filters.action)}&`;
        if (filters.entityType) url += `entityType=${encodeURIComponent(filters.entityType)}&`;
        if (filters.dateFrom)   url += `dateFrom=${filters.dateFrom}&`;
        if (filters.dateTo)     url += `dateTo=${filters.dateTo}&`;
        const res = await fetch(url);
        const obj = await res.json();
        setLogs(obj.data ?? []);
      } catch {
        setLogs([]);
        toast.error('Failed to load audit logs');
      }
    }
    load();
  }, [filters]);

  if (logs === null) return (
    <div className="hrms-content">
      <div className="d-flex align-items-center gap-2">
        <span className="spinner-border spinner-border-sm"></span> Loading...
      </div>
    </div>
  );

  return (
    <DisplayAuditLogs
      logsValue={logs}
      onFilter={setFilters}
      FilterBar={FilterBar}
    />
  );
}
