import '../Styles/timetablepage.css';
import { useState, useEffect } from 'react';
import { API_ENDPOINTS } from '../config/apiConfig';

interface TimeTable {
  id?: number;
  subject: string;
  day: string;
  startTime: string;
  endTime: string;
  location: string;
  lecturer: string;
  notificationPreference: string;
  specialSchedule?: boolean;
  specialDate?: string;
  description?: string;
  title?: string;
  displayDate?: string; // Added for date context
}

const Timetablepage = () => {
  const [timetables, setTimetables] = useState<TimeTable[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingEntry, setEditingEntry] = useState<TimeTable | null>(null);
  const [viewMode, setViewMode] = useState<'all' | 'today' | 'special' | 'day'>('today');
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [isSpecialSchedule, setIsSpecialSchedule] = useState(false);
  const [selectedDay, setSelectedDay] = useState<string>('');

  // Form state
  const [formData, setFormData] = useState<TimeTable>({
    subject: '',
    day: 'MONDAY',
    startTime: '09:00',
    endTime: '10:00',
    location: '',
    lecturer: '',
    notificationPreference: 'NONE',
    specialSchedule: false,
    specialDate: '',
    description: ''
  });

  // Fetch timetable entries - UPDATED to handle special dates correctly
  const fetchTimetables = async () => {
    setIsLoading(true);
    setError('');
    try {
      let endpoint = API_ENDPOINTS.TIMETABLE;
      
      if (viewMode === 'today') {
        endpoint = API_ENDPOINTS.TIMETABLE_TODAY;
      } else if (viewMode === 'special') {
        endpoint = API_ENDPOINTS.TIMETABLE_SPECIAL;
      } else if (viewMode === 'day' && selectedDay) {
        endpoint = API_ENDPOINTS.TIMETABLE_BY_DAY(selectedDay);
      }
      
      const user = localStorage.getItem('user');
      const username = user ? (JSON.parse(user).username || 'default_user') : 'default_user';

      // append username filter when available
      const urlWithUser = endpoint.includes('?') ? `${endpoint}&username=${encodeURIComponent(username)}` : (endpoint.includes('/day/') || endpoint.includes('/today') || endpoint.includes('/special') ? `${endpoint}?username=${encodeURIComponent(username)}` : `${endpoint}?username=${encodeURIComponent(username)}`);

      const response = await fetch(urlWithUser, {
        method: 'GET',
        headers: {
          'Accept': 'application/json',
        },
        mode: 'cors'
      });
      
      if (response.ok) {
        let data = await response.json();
        
        // For "all" view, show only today's active schedules
        if (viewMode === 'all') {
          const today = new Date().toISOString().split('T')[0]; // YYYY-MM-DD format
          data = data.filter((entry: TimeTable) => {
            if (entry.specialSchedule && entry.specialDate) {
              // Only show special schedules if they match today's date
              return entry.specialDate === today;
            }
            // For regular schedules, check if they occur today
            try {
              const todayDate = new Date();
              const todayDay = todayDate.toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
              return entry.day === todayDay;
            } catch {
              return true;
            }
          });
        }
        
        setTimetables(data);
      } else {
        const errorText = await response.text();
        throw new Error(`Server error: ${response.status} - ${errorText}`);
      }
    } catch (error) {
      console.error('Fetch error:', error);
      setError('Error loading timetable. Please check if the server is running on port 8080.');
      setTimetables([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchTimetables();
  }, [viewMode, selectedDay]);

  // Handle form input changes
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    
    if (type === 'checkbox') {
      const checked = (e.target as HTMLInputElement).checked;
      setFormData(prev => ({
        ...prev,
        [name]: checked
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };

  // Handle schedule type change
  const handleScheduleTypeChange = (isSpecial: boolean) => {
    setIsSpecialSchedule(isSpecial);
    // Reset special date when switching types
    if (!isSpecial) {
      setFormData(prev => ({
        ...prev,
        specialDate: ''
      }));
    }
  };

  // Validate form data
  const validateForm = () => {
    if (!formData.subject.trim()) {
      setError('Subject is required');
      return false;
    }
    if (!formData.startTime || !formData.endTime) {
      setError('Start time and end time are required');
      return false;
    }
    
    // Convert time strings to comparable format
    const start = new Date(`1970-01-01T${formData.startTime}:00`);
    const end = new Date(`1970-01-01T${formData.endTime}:00`);
    
    if (start >= end) {
      setError('End time must be after start time');
      return false;
    }
    if (isSpecialSchedule && !formData.specialDate) {
      setError('Special date is required for special schedules');
      return false;
    }
    if (!isSpecialSchedule && !formData.day) {
      setError('Day is required for regular schedules');
      return false;
    }
    return true;
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    setMessage('');

    if (!validateForm()) {
      setIsLoading(false);
      return;
    }

    try {
      // Prepare data for backend
      const stored = localStorage.getItem('user');
      const currentUser = stored ? JSON.parse(stored) : null;
      const submitData: any = {
        username: currentUser?.username || 'default_user',
        subject: formData.subject,
        startTime: formData.startTime + ':00', // Add seconds for backend
        endTime: formData.endTime + ':00',     // Add seconds for backend
        location: formData.location,
        lecturer: formData.lecturer,
        notificationPreference: formData.notificationPreference,
        specialSchedule: isSpecialSchedule,
        description: formData.description
      };

      // Handle day/special date based on schedule type
      if (isSpecialSchedule) {
        submitData.specialDate = formData.specialDate;
        // Day will be auto-calculated by backend from specialDate
      } else {
        submitData.day = formData.day;
      }

      // Include ID for update operations
      if (editingEntry && editingEntry.id) {
        submitData.id = editingEntry.id;
      }

      const url = editingEntry && editingEntry.id
        ? API_ENDPOINTS.TIMETABLE_BY_ID(editingEntry.id)
        : API_ENDPOINTS.TIMETABLE;
      
      const method = editingEntry && editingEntry.id ? 'PUT' : 'POST';
      
      console.log('Submitting to:', url, 'with data:', submitData);
      
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: JSON.stringify(submitData),
        mode: 'cors'
      });

      // Handle response
      const contentType = response.headers.get('Content-Type');
      let result;
      
      if (contentType && contentType.includes('application/json')) {
        result = await response.json();
      } else {
        const text = await response.text();
        throw new Error(text || `HTTP ${response.status}: ${response.statusText}`);
      }

      if (response.ok) {
        setMessage(editingEntry ? 'Timetable updated successfully!' : 'Timetable added successfully!');
        handleCloseModal();
        fetchTimetables(); // Refresh the data
      } else {
        throw new Error(result.error || result.message || 'Operation failed');
      }
    } catch (error) {
      console.error('Submission error:', error);
      setError(error instanceof Error ? error.message : 'Failed to save timetable. Please check console for details.');
    } finally {
      setIsLoading(false);
    }
  };

  // Handle edit action
  const handleEdit = (entry: TimeTable) => {
    console.log('Editing entry:', entry);
    setEditingEntry(entry);
    setIsSpecialSchedule(entry.specialSchedule || false);
    
    // Convert backend time format (HH:mm:ss) to input format (HH:mm)
    const formattedEntry = {
      ...entry,
      startTime: entry.startTime ? entry.startTime.substring(0, 5) : '09:00',
      endTime: entry.endTime ? entry.endTime.substring(0, 5) : '10:00'
    };
    
    setFormData(formattedEntry);
    setIsModalOpen(true);
  };

  // Handle delete action
  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this timetable entry?')) {
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const response = await fetch(API_ENDPOINTS.TIMETABLE_BY_ID(id), {
        method: 'DELETE',
        headers: {
          'Accept': 'application/json',
        },
        mode: 'cors'
      });

      if (response.ok) {
        const result = await response.json();
        setMessage(result.message || 'Timetable deleted successfully!');
        fetchTimetables();
      } else if (response.status === 404) {
        throw new Error('Timetable entry not found. It may have already been deleted.');
      } else {
        throw new Error(`Server returned error: ${response.status} ${response.statusText}`);
      }
    } catch (error) {
      console.error('Delete error:', error);
      setError(error instanceof Error ? error.message : 'Error deleting timetable entry');
    } finally {
      setIsLoading(false);
    }
  };

  // Close modal and reset form
  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingEntry(null);
    setIsSpecialSchedule(false);
    setFormData({
      subject: '',
      day: 'MONDAY',
      startTime: '09:00',
      endTime: '10:00',
      location: '',
      lecturer: '',
      notificationPreference: 'NONE',
      specialSchedule: false,
      specialDate: '',
      description: ''
    });
    setError('');
  };

  // Format time for display
  const formatTime = (timeString: string) => {
    if (!timeString) return '';
    return timeString.substring(0, 5);
  };

  // Format date for display
  const formatDate = (dateString: string) => {
    if (!dateString) return '';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      });
    } catch {
      return dateString;
    }
  };

  // Get display date for schedule entry
  const getDisplayDate = (entry: TimeTable) => {
    if (entry.displayDate) {
      return formatDate(entry.displayDate);
    }
    if (entry.specialSchedule && entry.specialDate) {
      return formatDate(entry.specialDate);
    }
    return entry.day;
  };

  // Get current day of week
  const getCurrentDay = () => {
    const days = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
    return days[new Date().getDay()];
  };

  return (
    <div className="timetable-container2">
      <div className='image_text-container'>
        <div className="text-container">
          <h1>Effortlessly manage <br />your timetable:</h1>
          <p>
            Add, update, and delete <br />entries with just a few clicks.
          </p>
        </div>
      </div>

      {/* Messages */}
      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      {/* Action Buttons */}
      <div className="button-container">
        <button 
          className="button add" 
          onClick={() => setIsModalOpen(true)}
          disabled={isLoading}
        >
          {isLoading ? 'Loading...' : 'Add Schedule'}
        </button>
        
        <div className="view-buttons">
          <button 
            className={`button ${viewMode === 'today' ? 'active' : ''}`}
            onClick={() => {
              setViewMode('today');
              setSelectedDay('');
            }}
            disabled={isLoading}
          >
            📅 Today
          </button>
          <button 
            className={`button ${viewMode === 'all' ? 'active' : ''}`}
            onClick={() => {
              setViewMode('all');
              setSelectedDay('');
            }}
            disabled={isLoading}
          >
            📋 All (Today's)
          </button>
          <button 
            className={`button ${viewMode === 'special' ? 'active' : ''}`}
            onClick={() => {
              setViewMode('special');
              setSelectedDay('');
            }}
            disabled={isLoading}
          >
            ⭐ Special
          </button>
          <select 
            className="button day-selector"
            value={selectedDay}
            onChange={(e) => {
              setSelectedDay(e.target.value);
              setViewMode('day');
            }}
            disabled={isLoading}
          >
            <option value="">Select Day</option>
            <option value="MONDAY">Monday</option>
            <option value="TUESDAY">Tuesday</option>
            <option value="WEDNESDAY">Wednesday</option>
            <option value="THURSDAY">Thursday</option>
            <option value="FRIDAY">Friday</option>
            <option value="SATURDAY">Saturday</option>
            <option value="SUNDAY">Sunday</option>
          </select>
        </div>
      </div>

      {/* Timetable List */}
      <div className="timetable-list">
        <h2>
          {viewMode === 'today' ? `📅 Today's Schedule (${getCurrentDay()})` : 
           viewMode === 'special' ? '⭐ Special Schedules' : 
           viewMode === 'day' ? `📋 ${selectedDay} Schedules` : 
           '📋 Today\'s Active Schedules'}
        </h2>
        
        {isLoading ? (
          <div className="loading">Loading timetable...</div>
        ) : error ? (
          <div className="error">
            <p>{error}</p>
            <button onClick={fetchTimetables} className="retry-btn">
              Retry
            </button>
          </div>
        ) : timetables.length === 0 ? (
          <p>No timetable entries found.</p>
        ) : (
          <div className="timetable-table-container">
            <table className="timetable-table">
              <thead>
                <tr>
                  <th>Subject</th>
                  <th>Date/Day</th>
                  <th>Time</th>
                  <th>Location</th>
                  <th>Lecturer</th>
                  <th>Type</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {timetables.map(entry => (
                  <tr key={entry.id} className={entry.specialSchedule ? 'special-schedule' : ''}>
                    <td>
                      <strong>{entry.subject}</strong>
                      {entry.description && (
                        <div className="description">{entry.description}</div>
                      )}
                    </td>
                    <td>
                      {getDisplayDate(entry)}
                      {entry.specialSchedule && entry.day && (
                        <div className="day-name">({entry.day})</div>
                      )}
                    </td>
                    <td>{formatTime(entry.startTime)} - {formatTime(entry.endTime)}</td>
                    <td>{entry.location}</td>
                    <td>{entry.lecturer}</td>
                    <td>
                      {entry.specialSchedule ? (
                        <span className="special-badge">⭐ Special</span>
                      ) : (
                        <span className="regular-badge">📅 Regular</span>
                      )}
                    </td>
                    <td>
                      <button 
                        className="edit-btn"
                        onClick={() => handleEdit(entry)}
                        disabled={isLoading}
                      >
                        Edit
                      </button>
                      <button 
                        className="delete-btn"
                        onClick={() => entry.id && handleDelete(entry.id)}
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

      {/* Add/Edit Modal */}
      {isModalOpen && (
        <div className="modal-overlay" onClick={handleCloseModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>{editingEntry ? 'Edit Timetable Entry' : 'Add New Timetable Entry'}</h2>
            
            <form onSubmit={handleSubmit}>
              {/* Schedule Type Selection */}
              <div className="form-group">
                <label>Schedule Type:</label>
                <div className="schedule-type-options">
                  <label className="radio-option">
                    <input
                      type="radio"
                      name="scheduleType"
                      value="regular"
                      checked={!isSpecialSchedule}
                      onChange={() => handleScheduleTypeChange(false)}
                    />
                    📅 Regular Weekly Schedule
                  </label>
                  <label className="radio-option">
                    <input
                      type="radio"
                      name="scheduleType"
                      value="special"
                      checked={isSpecialSchedule}
                      onChange={() => handleScheduleTypeChange(true)}
                    />
                    ⭐ Special Schedule (One-time)
                  </label>
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="subject">Subject *</label>
                <input
                  type="text"
                  id="subject"
                  name="subject"
                  value={formData.subject}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter subject name"
                />
              </div>

              {!isSpecialSchedule && (
                <div className="form-group">
                  <label htmlFor="day">Day *</label>
                  <select
                    id="day"
                    name="day"
                    value={formData.day}
                    onChange={handleInputChange}
                    required
                  >
                    <option value="MONDAY">Monday</option>
                    <option value="TUESDAY">Tuesday</option>
                    <option value="WEDNESDAY">Wednesday</option>
                    <option value="THURSDAY">Thursday</option>
                    <option value="FRIDAY">Friday</option>
                    <option value="SATURDAY">Saturday</option>
                    <option value="SUNDAY">Sunday</option>
                  </select>
                </div>
              )}

              {isSpecialSchedule && (
                <div className="form-group">
                  <label htmlFor="specialDate">Date *</label>
                  <input
                    type="date"
                    id="specialDate"
                    name="specialDate"
                    value={formData.specialDate}
                    onChange={handleInputChange}
                    required={isSpecialSchedule}
                  />
                </div>
              )}

              {isSpecialSchedule && (
                <div className="form-group">
                  <label htmlFor="description">Description</label>
                  <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    placeholder="Optional description for special schedule"
                    rows={3}
                  />
                </div>
              )}

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
                <label htmlFor="location">Location</label>
                <input
                  type="text"
                  id="location"
                  name="location"
                  value={formData.location}
                  onChange={handleInputChange}
                  placeholder="Enter location"
                />
              </div>

              <div className="form-group">
                <label htmlFor="lecturer">Lecturer</label>
                <input
                  type="text"
                  id="lecturer"
                  name="lecturer"
                  value={formData.lecturer}
                  onChange={handleInputChange}
                  placeholder="Enter lecturer name"
                />
              </div>

              <div className="form-group">
                <label htmlFor="notificationPreference">Notification</label>
                <select
                  id="notificationPreference"
                  name="notificationPreference"
                  value={formData.notificationPreference}
                  onChange={handleInputChange}
                >
                  <option value="NONE">No Notification</option>
                  <option value="5_MINUTES">5 Minutes Before</option>
                  <option value="15_MINUTES">15 Minutes Before</option>
                  <option value="30_MINUTES">30 Minutes Before</option>
                  <option value="1_HOUR">1 Hour Before</option>
                </select>
              </div>

              <div className="modal-buttons">
                <button 
                  type="button" 
                  onClick={handleCloseModal}
                  disabled={isLoading}
                >
                  Cancel
                </button>
                <button 
                  type="submit" 
                  disabled={isLoading}
                >
                  {isLoading ? 'Processing...' : (editingEntry ? 'Update' : 'Add')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Timetablepage;