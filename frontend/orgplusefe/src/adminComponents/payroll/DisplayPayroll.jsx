import React from 'react';
import { Link } from 'react-router-dom';

function StatusBadge({ value }) {
  let cls = value ? `status-badge badge-${String(value).toLowerCase().replace(/ /g, '_')}` : '';
  return <span className={cls}>{value}</span>;
}

let MONTH_NAMES = ['', 'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'];


const handlePayment =
async (payroll) => {

    const response =
    await fetch(
        `http://localhost:8080/api/v1/admin/payroll/create-order?payrollId=${payroll.id}`,
        {
            method:"POST"
        }
    );

    const order =
    await response.json();

    const options = {

        key:
        "rzp_test_xxxxx",

        amount:
        order.amount,

        currency:
        order.currency,

        order_id:
        order.id,

        name:
        "ORGpluse Payroll",

        handler:
        async function(res){

            await fetch(
              "http://localhost:8080/api/v1/admin/payroll/verify",
              {
                method:"POST",
                headers:{
                 "Content-Type":
                 "application/json"
                },
                body:JSON.stringify({

                    payrollId:
                    payroll.id,

                    paymentId:
                    res.razorpay_payment_id
                })
              }
            );

            alert(
              "Salary Paid"
            );

            window.location.reload();
        }
    };

    const razor = new window.Razorpay(  options ); 
     razor.open();
};



export default function DisplayPayroll({ payrollsValue, onDelete, onFilter, FilterBar }) {
  return (
    <div className="hrms-content">
      <div className="page-header">
        <h4><i className="bi bi-wallet2"></i> Payroll Runs</h4>
        <Link to="/admin/payroll/add" className="btn btn-primary btn-sm">
          <i className="bi bi-plus-lg me-1"></i> New Payroll Run
        </Link>
      </div>
      <FilterBar onFilter={onFilter} />
      <div className="hrms-card">
        <div className="table-responsive">
          <table className="table hrms-table mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>Employee</th>
                <th>Month / Year</th>
                <th>Run Date</th>
                <th>Status</th>
                <th>Processed By</th>
                <th>Actions</th>
                <th>Pay Salary</th>
              </tr>
            </thead>
            <tbody>
              {payrollsValue.length === 0 ? (
                <tr><td colSpan={7} className="text-center text-muted py-4">No payroll records found.</td></tr>
              ) : (
                payrollsValue.map((p, idx) => (
                  <tr key={p.id}>
                    <td>{idx + 1}</td>
                    <td>
                      {p.employee
                        ? <><strong>{p.employee.firstName} {p.employee.lastName}</strong>
                            {p.employee.employeeCode && <div className="text-muted" style={{ fontSize: '0.8rem' }}>{p.employee.employeeCode}</div>}
                          </>
                        : <span className="text-muted">—</span>}
                    </td>
                    <td><strong>{MONTH_NAMES[p.month] || p.month} {p.year}</strong></td>
                    <td>{p.runDate || '—'}</td>
                    <td><StatusBadge value={p.status} /></td>
                    <td>{p.processedBy ? `${p.processedBy.firstName} ${p.processedBy.lastName}` : '—'}</td>
                    <td>
                      <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => { if (window.confirm('Delete this payroll record?')) onDelete(p.id); }}
                      >
                        <i className="bi bi-trash"></i>
                      </button>
                    </td>
                    <td>
                      <button
                          className="btn btn-success"
                          onClick={() =>
                              handlePayment(p)
                          }
                      >
                          Pay Salary
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
