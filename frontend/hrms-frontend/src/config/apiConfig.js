export const ADMIN_BASE = "http://localhost:8080/api/v1/admin";
export const EMPLOYEE_BASE = (employeeId) =>
  `http://localhost:8080/api/v1/admin/employees/${employeeId}`;
