import axios from 'axios';

const API_URL = 'https://employee-management-app-gdm5.onrender.com/api/employees';

// Get all employees with pagination and search
export const getAllEmployees = async (page = 0, size = 10, keyword = '') => {
  const params = new URLSearchParams();
  params.append('page', page);
  params.append('size', size);
  if (keyword && keyword.trim()) {
    params.append('keyword', keyword.trim());
  }
  const response = await axios.get(`${API_URL}?${params.toString()}`);
  return response.data;
};

// Get employee by ID
export const getEmployeeById = async id => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};

// Add a new employee
export const addEmployee = async employee => {
  const response = await axios.post(API_URL, employee);
  return response.data;
};

// Update an existing employee
export const updateEmployee = async (id, employee) => {
  const response = await axios.put(`${API_URL}/${id}`, employee);
  return response.data;
};

// Delete an employee
export const deleteEmployee = async id => {
  await axios.delete(`${API_URL}/${id}`);
};
