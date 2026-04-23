const escapeCsvCell = cell => {
  const cellStr = cell === null || cell === undefined ? '' : String(cell);
  if (cellStr.includes(',') || cellStr.includes('"') || cellStr.includes('\n') || cellStr.includes('\r')) {
    return `"${cellStr.replace(/"/g, '""')}"`;
  }
  return cellStr;
};

const formatDate = date => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export const exportToCsv = (data, columns, filenamePrefix = 'employees') => {
  if (!data || data.length === 0) {
    return;
  }

  const headers = columns.map(col => col.label);
  const rows = data.map(item =>
    columns.map(col => {
      const value = col.accessor ? col.accessor(item) : item[col.key];
      return value === null || value === undefined ? '' : value;
    })
  );

  const csvContent = [headers, ...rows]
    .map(row => row.map(escapeCsvCell).join(','))
    .join('\n');

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  const dateStr = formatDate(new Date());
  const filename = `${filenamePrefix}_${dateStr}.csv`;

  link.href = url;
  link.setAttribute('download', filename);
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
};
