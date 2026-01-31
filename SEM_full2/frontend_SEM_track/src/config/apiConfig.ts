// API Configuration - switches between local development and Docker deployment

export const API_BASE_URL = (() => {
  // Check if we're running in Docker
  if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
    // Local development
    return 'http://localhost:8081';
  } else {
    // Production/Docker deployment: Use same origin for nginx reverse proxy
    // Nginx will route /auth, /api, /attendance, /marks, /students to backend:8081
    return window.location.origin;
  }
})();

export const API_ENDPOINTS = {
  // Authentication
  LOGIN: `${API_BASE_URL}/auth/login`,
  REGISTER: `${API_BASE_URL}/students/register`,

  // Timetable
  TIMETABLE: `${API_BASE_URL}/api/timetable`,
  TIMETABLE_TODAY: `${API_BASE_URL}/api/timetable/today`,
  TIMETABLE_SPECIAL: `${API_BASE_URL}/api/timetable/special`,
  TIMETABLE_WEEK: `${API_BASE_URL}/api/timetable/week`,
  TIMETABLE_UPCOMING: `${API_BASE_URL}/api/timetable/upcoming`,
  TIMETABLE_FUTURE_SPECIAL: `${API_BASE_URL}/api/timetable/special/future`,
  TIMETABLE_STATS: `${API_BASE_URL}/api/timetable/stats`,
  TIMETABLE_BY_DAY: (day: string) => `${API_BASE_URL}/api/timetable/day/${day}`,
  TIMETABLE_BY_ID: (id: string | number) => `${API_BASE_URL}/api/timetable/${id}`,

  // Marks/Exams
  MARKS_BY_STUDENT: (studentId: string) => `${API_BASE_URL}/marks/student/${encodeURIComponent(studentId)}`,
  MARKS: `${API_BASE_URL}/marks`,

  // Attendance
  ATTENDANCE_BY_STUDENT: (studentId: string) => `${API_BASE_URL}/attendance/student/${encodeURIComponent(studentId)}`,
  ATTENDANCE_SUMMARY: (studentId: string) => `${API_BASE_URL}/attendance/student/${encodeURIComponent(studentId)}/summary`,
  ATTENDANCE_SUBJECTS: (studentId: string) => `${API_BASE_URL}/attendance/student/${encodeURIComponent(studentId)}/subjects`,
  ATTENDANCE: `${API_BASE_URL}/attendance`,
  ATTENDANCE_BY_ID: (id: string | number) => `${API_BASE_URL}/attendance/${id}`,
};
