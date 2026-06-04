import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { ToastContainer, Zoom } from 'react-toastify';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import 'bootstrap-icons/font/bootstrap-icons.min.css';
import 'react-toastify/dist/ReactToastify.css';
import './hrms.css';

// ── Admin imports
import AdminApp from './adminComponents/AdminApp';
import AdminDashboard from './adminComponents/dashboard/AdminDashboard';
import FetchBranches from './adminComponents/branch/FetchBranches';
import AddBranch from './adminComponents/branch/AddBranch';
import UpdateBranch from './adminComponents/branch/UpdateBranch';
import FetchDesignations from './adminComponents/designation/FetchDesignations';
import AddDesignation from './adminComponents/designation/AddDesignation';
import UpdateDesignation from './adminComponents/designation/UpdateDesignation';
import FetchDepartments from './adminComponents/department/FetchDepartments';
import AddDepartment from './adminComponents/department/AddDepartment';
import UpdateDepartment from './adminComponents/department/UpdateDepartment';
import FetchEmployees from './adminComponents/employee/FetchEmployees';
import AddEmployee from './adminComponents/employee/AddEmployee';
import UpdateEmployee from './adminComponents/employee/UpdateEmployee';
import FetchTimeRecords from './adminComponents/timerecord/FetchTimeRecords';
import FetchLeaves from './adminComponents/leave/FetchLeaves';
import FetchJobHistory from './adminComponents/jobhistory/FetchJobHistory';
import FetchPayroll from './adminComponents/payroll/FetchPayroll';
import AddPayrollRun from './adminComponents/payroll/AddPayrollRun';
import FetchPerformance from './adminComponents/performance/FetchPerformance';
import AddPerformanceReview from './adminComponents/performance/AddPerformanceReview';
import FetchAuditLogs from './adminComponents/auditlog/FetchAuditLogs';
import FetchHelp from './adminComponents/help/FetchHelp';

// ── Employee imports
import UserHome from './userComponents/UserHome';
import EmployeeDashboard from './userComponents/dashboard/EmployeeDashboard';
import ApplyLeave from './userComponents/leave/ApplyLeave';
import MyLeaves from './userComponents/leave/MyLeaves';
import RaiseHelpRequest from './userComponents/help/RaiseHelpRequest';
import MyPayroll from './userComponents/payroll/MyPayroll';
import MyAttendance from './userComponents/attendance/MyAttendance';
import MyAuditLog from './userComponents/auditlog/MyAuditLog';

let router = createBrowserRouter([
  {
    path: '/admin',
    element: <AdminApp />,
    children: [
      { index: true,                          element: <AdminDashboard /> },
      { path: 'branches',                     element: <FetchBranches /> },
      { path: 'branches/add',                 element: <AddBranch /> },
      { path: 'branches/update/:id',          element: <UpdateBranch /> },
      { path: 'designations',                 element: <FetchDesignations /> },
      { path: 'designations/add',             element: <AddDesignation /> },
      { path: 'designations/update/:id',      element: <UpdateDesignation /> },
      { path: 'departments',                  element: <FetchDepartments /> },
      { path: 'departments/add',              element: <AddDepartment /> },
      { path: 'departments/update/:id',       element: <UpdateDepartment /> },
      { path: 'employees',                    element: <FetchEmployees /> },
      { path: 'employees/add',                element: <AddEmployee /> },
      { path: 'employees/update/:id',         element: <UpdateEmployee /> },
      { path: 'time-records',                 element: <FetchTimeRecords /> },
      { path: 'leaves',                       element: <FetchLeaves /> },
      { path: 'job-history',                  element: <FetchJobHistory /> },
      { path: 'payroll',                      element: <FetchPayroll /> },
      { path: 'payroll/add',                  element: <AddPayrollRun /> },
      { path: 'performance',                  element: <FetchPerformance /> },
      { path: 'performance/add',              element: <AddPerformanceReview /> },
      { path: 'audit-logs',                   element: <FetchAuditLogs /> },
      { path: 'help',                         element: <FetchHelp /> },
    ]
  },
  {
    path: '/employee/:employeeId',
    element: <UserHome />,
    children: [
      { index: true,                          element: <EmployeeDashboard /> },
      { path: 'leaves',                       element: <MyLeaves /> },
      { path: 'leaves/apply',                 element: <ApplyLeave /> },
      { path: 'help/raise',                   element: <RaiseHelpRequest /> },
      { path: 'payroll',                      element: <MyPayroll /> },
      { path: 'attendance',                   element: <MyAttendance /> },
      { path: 'audit-log',                    element: <MyAuditLog /> },
    ]
  }
]);

ReactDOM.createRoot(document.getElementById('root')).render(
  <>
    <RouterProvider router={router} />
    <ToastContainer
      position="top-right"
      autoClose={2000}
      hideProgressBar={false}
      newestOnTop={false}
      closeOnClick={false}
      rtl={false}
      pauseOnFocusLoss
      draggable
      pauseOnHover
      theme="light"
      transition={Zoom}
    />
  </>
);
