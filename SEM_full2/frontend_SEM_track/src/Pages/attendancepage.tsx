import '../Styles/attendance.css';
import attendanceImg1 from "../assets/Attendancepage_image1.jpg"; 
import attendanceImg2 from "../assets/Attendancepage _image2.jpg";
import { useState, useEffect } from 'react';
import React from 'react';
import { API_ENDPOINTS } from '../config/apiConfig';

interface AttendanceRecord {
  id?: number;
  moduleName: string;
  attendanceDate: string;
  startTime: string;
  endTime: string;
  attendedHours: number;
  studentId: string;
}

interface SubjectAttendanceSettings {
  id?: number;
  studentId: string;
  subjectName: string;
  totalScheduledHours: number;
  passPercentage: number;
  totalAttendedHours: number;
  attendancePercentage: number;
}

const Attendancepage = () => {
  const [attendanceRecords, setAttendanceRecords] = useState<AttendanceRecord[]>([]);
  const [subjectSettings, setSubjectSettings] = useState<SubjectAttendanceSettings[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSettingsModalOpen, setIsSettingsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [viewMode, setViewMode] = useState<'add' | 'view' | 'settings'>('view');
  const [attendanceSummary, setAttendanceSummary] = useState<any>(null);
  const [editingRecord, setEditingRecord] = useState<AttendanceRecord | null>(null);
  
  const [settingsFormData, setSettingsFormData] = useState<SubjectAttendanceSettings>({
    studentId: (JSON.parse(localStorage.getItem('user') || 'null')?.username) || 'default_user',
    subjectName: '',
    totalScheduledHours: 0,
    passPercentage: 75,
    totalAttendedHours: 0,
    attendancePercentage: 0
  });
  
  const [formData, setFormData] = useState<AttendanceRecord>({
    moduleName: '',
    attendanceDate: new Date().toISOString().split('T')[0],
    startTime: '09:00',
    endTime: '10:00',
    attendedHours: 1.0,
    studentId: (JSON.parse(localStorage.getItem('user') || 'null')?.username) || 'default_user'
  });

  // Fetch attendance records
  const fetchAttendance = async () => {
    setIsLoading(true);
    setError('');
    try {
      const user = JSON.parse(localStorage.getItem('user') || 'null');
      const studentId = user?.username || 'default_user';
      const response = await fetch(API_ENDPOINTS.ATTENDANCE_BY_STUDENT(studentId), {
        method: 'GET',
        headers: { 'Accept': 'application/json' },
        mode: 'cors'
      });
      
      if (response.ok) {
        const data = await response.json();
        setAttendanceRecords(data);
      } else {
        throw new Error('Failed to fetch attendance records');
      }
    } catch (error) {
      console.error('Fetch error:', error);
      setError('Error loading attendance records');
    } finally {
      setIsLoading(false);
    }
  };

  // Fetch attendance summary
  const fetchAttendanceSummary = async () => {
    try {
      const user = JSON.parse(localStorage.getItem('user') || 'null');
      const studentId = user?.username || 'default_user';
      const response = await fetch(API_ENDPOINTS.ATTENDANCE_SUMMARY(studentId), {
        method: 'GET',
        headers: { 'Accept': 'application/json' },
        mode: 'cors'
      });
      
      if (response.ok) {
        const data = await response.json();
        setAttendanceSummary(data);
      }
    } catch (error) {
      console.error('Summary fetch error:', error);
    }
  };

  // Fetch subject attendance settings
  const fetchSubjectSettings = async () => {
    try {
      const user = JSON.parse(localStorage.getItem('user') || 'null');
      const studentId = user?.username || 'default_user';
      const response = await fetch(`http://localhost:8080/attendance/student/${encodeURIComponent(studentId)}/subjects`, {
        method: 'GET',
        headers: { 'Accept': 'application/json' },
        mode: 'cors'
      });
      
      if (response.ok) {
        const data = await response.json();
        setSubjectSettings(data);
      }
    } catch (error) {
      console.error('Subject settings fetch error:', error);
    }
  };

  useEffect(() => {
    fetchAttendance();
    fetchAttendanceSummary();
    fetchSubjectSettings();
  }, []);

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    setMessage('');

    try {
      const user = JSON.parse(localStorage.getItem('user') || 'null');
      const submitData = {
        ...formData,
        studentId: user?.username || 'default_user'
      };

      const url = editingRecord && editingRecord.id
        ? API_ENDPOINTS.ATTENDANCE_BY_ID(editingRecord.id)
        : API_ENDPOINTS.ATTENDANCE;
      
      const method = editingRecord && editingRecord.id ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: JSON.stringify(submitData),
        mode: 'cors'
      });

      if (response.ok) {
        setMessage(editingRecord ? 'Attendance record updated successfully!' : 'Attendance record added successfully!');
        setIsModalOpen(false);
        setEditingRecord(null);
        fetchAttendance();
        fetchAttendanceSummary();
        fetchSubjectSettings();
        setFormData({
          moduleName: '',
          attendanceDate: new Date().toISOString().split('T')[0],
          startTime: '09:00',
          endTime: '10:00',
          attendedHours: 1.0,
          studentId: (JSON.parse(localStorage.getItem('user') || 'null')?.username) || 'default_user'
        });
      } else {
        throw new Error('Failed to add attendance record');
      }
    } catch (error) {
      console.error('Submission error:', error);
      setError('Failed to add attendance record');
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? parseFloat(value) || 0 : value
    }));
  };

  // Handle subject settings form submission
  const handleSettingsSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    setMessage('');

    try {
      const user = JSON.parse(localStorage.getItem('user') || 'null');
      const studentId = user?.username || 'default_user';
      
      const submitData = {
        ...settingsFormData,
        studentId: studentId
      };

      const response = await fetch(API_ENDPOINTS.ATTENDANCE_SUBJECTS(studentId), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: JSON.stringify(submitData),
        mode: 'cors'
      });

      if (response.ok) {
        setMessage('Subject settings saved successfully!');
        setIsSettingsModalOpen(false);
        fetchSubjectSettings();
        setSettingsFormData({
          studentId: studentId,
          subjectName: '',
          totalScheduledHours: 0,
          passPercentage: 75,
          totalAttendedHours: 0,
          attendancePercentage: 0
        });
      } else {
        throw new Error('Failed to save subject settings');
      }
    } catch (error) {
      console.error('Settings submission error:', error);
      setError('Failed to save subject settings');
    } finally {
      setIsLoading(false);
    }
  };

  // Handle edit attendance record
  const handleEditRecord = (record: AttendanceRecord) => {
    setEditingRecord(record);
    setFormData({
      ...record,
      attendanceDate: record.attendanceDate.split('T')[0],
      startTime: record.startTime.substring(0, 5),
      endTime: record.endTime.substring(0, 5)
    });
    setIsModalOpen(true);
  };

  // Handle delete attendance record
  const handleDeleteRecord = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this attendance record?')) {
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const response = await fetch(API_ENDPOINTS.ATTENDANCE_BY_ID(id), {
        method: 'DELETE',
        headers: { 'Accept': 'application/json' },
        mode: 'cors'
      });

      if (response.ok) {
        setMessage('Attendance record deleted successfully!');
        fetchAttendance();
        fetchAttendanceSummary();
        fetchSubjectSettings();
      } else {
        throw new Error('Failed to delete attendance record');
      }
    } catch (error) {
      console.error('Delete error:', error);
      setError('Failed to delete attendance record');
    } finally {
      setIsLoading(false);
    }
  };

  // Calculate needed hours for a subject
  const calculateNeededHours = (subject: SubjectAttendanceSettings) => {
    const requiredHours = (subject.passPercentage / 100.0) * subject.totalScheduledHours;
    return Math.max(0, requiredHours - subject.totalAttendedHours);
  };

  // Open add attendance modal
  const openAddModal = () => {
    setEditingRecord(null);
    setFormData({
      moduleName: '',
      attendanceDate: new Date().toISOString().split('T')[0],
      startTime: '09:00',
      endTime: '10:00',
      attendedHours: 1.0,
      studentId: (JSON.parse(localStorage.getItem('user') || 'null')?.username) || 'default_user'
    });
    setIsModalOpen(true);
  };

  // Close modal and reset view mode
  const closeModal = () => {
    setIsModalOpen(false);
    setIsSettingsModalOpen(false);
    setViewMode('view');
  };

  return(
    <div className="attendance-page">
      {/* Messages */}
      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      {/* Main Section */}
      <main className="main container">
        <div className="grid">
          {/* Left side - Image 1 and Buttons */}
          <section className="left-section">
            <div className="image-container-left">
              <img src={attendanceImg1} alt="Student raising hand" className="main-image1"/>
            </div>
            <div className="attendancepage_buttons">
              <button 
                className={`Att_page_button1 ${viewMode === 'add' ? 'active' : ''}`}
                onClick={openAddModal}
              >
               <span className="star">★</span> Add <span className="arrow">▼</span>
              </button>
              <button 
                className={`Att_page_button2 ${viewMode === 'view' ? 'active' : ''}`}
                onClick={() => setViewMode('view')}
              >
               <span className="star">★</span> View <span className="arrow">▼</span>
              </button>
              <button 
                className={`Att_page_button3 ${viewMode === 'settings' ? 'active' : ''}`}
                onClick={() => {
                  setIsSettingsModalOpen(true);
                  setViewMode('settings');
                }}
              >
                <span className="star">⚙️</span> Settings <span className="arrow">▼</span>
              </button>
            </div>
          </section>

          {/* Right side - Text and Image 2 */}
          <section className="right-section">
            <div className='main_text1'>
              <h1>Never Miss a Beat <br/>with Your <br/>Attendance</h1>
              <h2>
                View your attendance percentage, <br/>log presences, and maintain perfect<br/>
                records
              </h2>
            </div>
            <div className="image-container-right">
              <img src={attendanceImg2} alt="Business growth graph" className="main-image2"/>
            </div>
          </section>
        </div>
      </main>

      {/* Add/Edit Attendance Modal */}
      {isModalOpen && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>{editingRecord ? 'Edit Attendance Record' : 'Add Attendance Record'}</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="moduleName">Module Name *</label>
                <input
                  type="text"
                  id="moduleName"
                  name="moduleName"
                  value={formData.moduleName}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter module name"
                />
              </div>

              <div className="form-group">
                <label htmlFor="attendanceDate">Date *</label>
                <input
                  type="date"
                  id="attendanceDate"
                  name="attendanceDate"
                  value={formData.attendanceDate}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="startTime">Start Time *</label>
                <input
                  type="time"
                  id="startTime"
                  name="startTime"
                  value={formData.startTime}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="endTime">End Time *</label>
                <input
                  type="time"
                  id="endTime"
                  name="endTime"
                  value={formData.endTime}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="attendedHours">Attended Hours *</label>
                <input
                  type="number"
                  id="attendedHours"
                  name="attendedHours"
                  value={formData.attendedHours}
                  onChange={handleInputChange}
                  step="0.5"
                  min="0"
                  required
                />
              </div>

              <div className="modal-buttons">
                <button type="button" onClick={closeModal} disabled={isLoading}>
                  Cancel
                </button>
                <button type="submit" disabled={isLoading}>
                  {isLoading ? 'Saving...' : editingRecord ? 'Update Record' : 'Add Record'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Subject Settings Modal */}
      {isSettingsModalOpen && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Subject Attendance Settings</h2>
            <form onSubmit={handleSettingsSubmit}>
              <div className="form-group">
                <label htmlFor="subjectName">Subject Name *</label>
                <input
                  type="text"
                  id="subjectName"
                  name="subjectName"
                  value={settingsFormData.subjectName}
                  onChange={(e) => setSettingsFormData(prev => ({...prev, subjectName: e.target.value}))}
                  required
                  placeholder="Enter subject name"
                />
              </div>

              <div className="form-group">
                <label htmlFor="totalScheduledHours">Total Scheduled Hours *</label>
                <input
                  type="number"
                  id="totalScheduledHours"
                  name="totalScheduledHours"
                  value={settingsFormData.totalScheduledHours}
                  onChange={(e) => setSettingsFormData(prev => ({...prev, totalScheduledHours: parseFloat(e.target.value) || 0}))}
                  required
                  min="0"
                  step="0.5"
                />
              </div>

              <div className="form-group">
                <label htmlFor="passPercentage">Pass Percentage *</label>
                <input
                  type="number"
                  id="passPercentage"
                  name="passPercentage"
                  value={settingsFormData.passPercentage}
                  onChange={(e) => setSettingsFormData(prev => ({...prev, passPercentage: parseFloat(e.target.value) || 75}))}
                  required
                  min="0"
                  max="100"
                  step="1"
                />
              </div>

              <div className="modal-buttons">
                <button type="button" onClick={closeModal} disabled={isLoading}>
                  Cancel
                </button>
                <button type="submit" disabled={isLoading}>
                  {isLoading ? 'Saving...' : 'Save Settings'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* View Attendance Records */}
      {viewMode === 'view' && (
        <div className="attendance-list">
          <h2>Your Attendance Records</h2>
          
          {/* Subject-wise Attendance Summary */}
          {subjectSettings.length > 0 && (
            <div className="subject-attendance-summary">
              <h3>📚 Subject-wise Attendance</h3>
              <div className="subject-cards">
                {subjectSettings.map(subject => {
                  const neededHours = calculateNeededHours(subject);
                  const isEligible = subject.attendancePercentage >= subject.passPercentage;
                  
                  return (
                    <div key={subject.id} className={`subject-card ${isEligible ? 'eligible' : 'not-eligible'}`}>
                      <h4>{subject.subjectName}</h4>
                      <div className="subject-stats">
                        <div className="stat">
                          <span className="label">Attended:</span>
                          <span className="value">{subject.totalAttendedHours.toFixed(1)}h</span>
                        </div>
                        <div className="stat">
                          <span className="label">Scheduled:</span>
                          <span className="value">{subject.totalScheduledHours.toFixed(1)}h</span>
                        </div>
                        <div className="stat">
                          <span className="label">Percentage:</span>
                          <span className="value">{subject.attendancePercentage.toFixed(1)}%</span>
                        </div>
                        <div className="stat">
                          <span className="label">Pass Required:</span>
                          <span className="value">{subject.passPercentage}%</span>
                        </div>
                        <div className="stat">
                          <span className="label">Needed:</span>
                          <span className="value">{neededHours.toFixed(1)}h</span>
                        </div>
                        <div className="stat">
                          <span className="label">Status:</span>
                          <span className={`status ${isEligible ? 'passed' : 'failed'}`}>
                            {isEligible ? '✅ Eligible' : '❌ Not Eligible'}
                          </span>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
          
          {/* Overall Attendance Summary */}
          {attendanceSummary && (
            <div className="attendance-summary">
              <h3>📊 Overall Attendance Summary</h3>
              <div className="summary-cards">
                <div className="summary-card">
                  <h4>Total Hours Attended</h4>
                  <p className="big-number">{attendanceSummary.totalAttendedHours?.toFixed(1) || 0}h</p>
                </div>
                <div className="summary-card">
                  <h4>Overall Percentage</h4>
                  <p className="big-number">{attendanceSummary.overallAttendancePercentage?.toFixed(1) || 0}%</p>
                </div>
                <div className="summary-card">
                  <h4>Total Records</h4>
                  <p className="big-number">{attendanceSummary.totalRecords || 0}</p>
                </div>
              </div>
              {attendanceSummary.overallAttendancePercentage < 75 && (
                <div className="warning-message">
                  ⚠️ Your overall attendance is below 75%. You need to attend more classes to maintain eligibility.
                </div>
              )}
            </div>
          )}
          
          {isLoading ? (
            <div className="loading">Loading attendance records...</div>
          ) : attendanceRecords.length === 0 ? (
            <p>No attendance records found.</p>
          ) : (
            <div className="attendance-table-container">
              <table className="attendance-table">
                <thead>
                  <tr>
                    <th>Module</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Hours</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {attendanceRecords.map(record => (
                    <tr key={record.id}>
                      <td>{record.moduleName}</td>
                      <td>{new Date(record.attendanceDate).toLocaleDateString()}</td>
                      <td>
                        {(record.startTime.length > 5 ? record.startTime.substring(0,5) : record.startTime)}
                        {" - "}
                        {(record.endTime.length > 5 ? record.endTime.substring(0,5) : record.endTime)}
                      </td>
                      <td>{record.attendedHours}h</td>
                      <td>
                        <button 
                          className="edit-btn"
                          onClick={() => handleEditRecord(record)}
                          disabled={isLoading}
                        >
                          Edit
                        </button>
                        <button 
                          className="delete-btn"
                          onClick={() => record.id && handleDeleteRecord(record.id)}
                          disabled={isLoading}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Attendancepage;