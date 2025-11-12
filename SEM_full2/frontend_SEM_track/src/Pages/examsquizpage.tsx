import '../Styles/examsquizpage.css'
import examquizimage from "../assets/examsquizpage_image.jpg";
import { useState, useEffect } from 'react';

interface QuizRecord {
  id?: number;
  studentId: string;
  subject: { id: number; name?: string };
  obtainedMarks: number;
  totalMarks: number;
  passMarks: number;
  assessmentType: string;
  assessmentDate: string;
  remarks?: string;
}

const ExamsQuizpage = () => {
  const [quizRecords, setQuizRecords] = useState<QuizRecord[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [viewMode, setViewMode] = useState<'add' | 'view'>('view');
  
  const [formData, setFormData] = useState<QuizRecord>({
    studentId: (JSON.parse(localStorage.getItem('user') || 'null')?.username) || 'default_user',
    subject: { id: 1 },
    obtainedMarks: 0,
    totalMarks: 100,
    passMarks: 50,
    assessmentType: 'QUIZ',
    assessmentDate: new Date().toISOString().split('T')[0],
    remarks: ''
  });

  // Fetch quiz records
  const fetchQuizzes = async () => {
    setIsLoading(true);
    setError('');
    try {
      const user = JSON.parse(localStorage.getItem('user') || 'null');
      const studentId = user?.username || 'default_user';
      const response = await fetch(`http://localhost:8080/marks/student/${encodeURIComponent(studentId)}`, {
        method: 'GET',
        headers: {
          'Accept': 'application/json',
        },
        mode: 'cors'
      });
      
      if (response.ok) {
        const data = await response.json();
        setQuizRecords(data);
      } else {
        throw new Error('Failed to fetch quiz records');
      }
    } catch (error) {
      console.error('Fetch error:', error);
      setError('Error loading quiz records');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchQuizzes();
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

      const response = await fetch('http://localhost:8080/marks', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: JSON.stringify(submitData),
        mode: 'cors'
      });

      if (response.ok) {
        setMessage('Quiz record added successfully!');
        setIsModalOpen(false); // Close the modal
        setViewMode('view'); // Switch back to view mode
        fetchQuizzes();
        setFormData({
          studentId: (JSON.parse(localStorage.getItem('user') || 'null')?.username) || 'default_user',
          subject: { id: 1 },
          obtainedMarks: 0,
          totalMarks: 100,
          passMarks: 50,
          assessmentType: 'QUIZ',
          assessmentDate: new Date().toISOString().split('T')[0],
          remarks: ''
        });
      } else {
        throw new Error('Failed to add quiz record');
      }
    } catch (error) {
      console.error('Submission error:', error);
      setError('Failed to add quiz record');
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? parseInt(value) || 0 : value
    }));
  };

  const calculatePercentage = (obtained: number, total: number) => {
    return total > 0 ? ((obtained / total) * 100).toFixed(1) : '0';
  };

  const getGrade = (percentage: number) => {
    if (percentage >= 90) return 'A+';
    if (percentage >= 80) return 'A';
    if (percentage >= 70) return 'B+';
    if (percentage >= 60) return 'B';
    if (percentage >= 50) return 'C+';
    if (percentage >= 40) return 'C';
    return 'F';
  };

  // Open add quiz modal
  const openAddModal = () => {
    setIsModalOpen(true);
    setViewMode('add');
  };

  // Close modal and reset view mode
  const closeModal = () => {
    setIsModalOpen(false);
    setViewMode('view');
  };

  return(
    <div className="exam-container">
      {/* Messages */}
      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      {/* Header */}
      <header className="exam-header">
        <div className="search-box">
          <input
            type="text"
            placeholder="Search your exams"
            aria-label="Search exams"
          />
          <button aria-label="Search">
            <span className="material-icons">🔍</span>
          </button>
        </div>
      </header>

      {/* Main content */}
      <main className="exam-main">
        <h1>Add your quizzes & exam <br></br>details and see your progress</h1>
        <div className="exam-image">
          <img
             src={examquizimage}
            alt="Students taking exam"
          />
        </div>
      </main>

      {/* Action buttons */}
      <footer className="exam-footer">
        <button 
          className={`EQ_btn ${viewMode === 'add' ? 'active' : ''}`}
          onClick={openAddModal} // Changed to open modal directly
        >
          <span className="star">★</span> Add <span className="arrow">▼</span>
        </button>
        <button 
          className={`EQ_btn ${viewMode === 'view' ? 'active' : ''}`}
          onClick={() => setViewMode('view')}
        >
          <span className="star">★</span> View <span className="arrow">▼</span>
        </button>
      </footer>

      {/* Add Quiz Modal */}
      {isModalOpen && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Add Quiz/Exam Record</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="assessmentType">Assessment Type *</label>
                <select
                  id="assessmentType"
                  name="assessmentType"
                  value={formData.assessmentType}
                  onChange={handleInputChange}
                  required
                >
                  <option value="QUIZ">Quiz</option>
                  <option value="EXAM">Exam</option>
                  <option value="ASSIGNMENT">Assignment</option>
                  <option value="PROJECT">Project</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="subject">Subject ID *</label>
                <input
                  type="number"
                  id="subject"
                  name="subject"
                  value={formData.subject.id}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    subject: { ...prev.subject, id: parseInt(e.target.value) || 1 }
                  }))}
                  required
                  min="1"
                />
              </div>

              <div className="form-group">
                <label htmlFor="obtainedMarks">Obtained Marks *</label>
                <input
                  type="number"
                  id="obtainedMarks"
                  name="obtainedMarks"
                  value={formData.obtainedMarks}
                  onChange={handleInputChange}
                  required
                  min="0"
                />
              </div>

              <div className="form-group">
                <label htmlFor="totalMarks">Total Marks *</label>
                <input
                  type="number"
                  id="totalMarks"
                  name="totalMarks"
                  value={formData.totalMarks}
                  onChange={handleInputChange}
                  required
                  min="1"
                />
              </div>

              <div className="form-group">
                <label htmlFor="passMarks">Pass Marks *</label>
                <input
                  type="number"
                  id="passMarks"
                  name="passMarks"
                  value={formData.passMarks}
                  onChange={handleInputChange}
                  required
                  min="0"
                />
              </div>

              <div className="form-group">
                <label htmlFor="assessmentDate">Assessment Date *</label>
                <input
                  type="date"
                  id="assessmentDate"
                  name="assessmentDate"
                  value={formData.assessmentDate}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="remarks">Remarks</label>
                <textarea
                  id="remarks"
                  name="remarks"
                  value={formData.remarks}
                  onChange={handleInputChange}
                  placeholder="Optional remarks"
                  rows={3}
                />
              </div>

              <div className="modal-buttons">
                <button 
                  type="button" 
                  onClick={closeModal}
                  disabled={isLoading}
                >
                  Cancel
                </button>
                <button 
                  type="submit" 
                  disabled={isLoading}
                >
                  {isLoading ? 'Adding...' : 'Add Record'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* View Quiz Records */}
      {viewMode === 'view' && (
        <div className="quiz-list">
          <h2>Your Quiz & Exam Records</h2>
          {isLoading ? (
            <div className="loading">Loading quiz records...</div>
          ) : quizRecords.length === 0 ? (
            <p>No quiz records found.</p>
          ) : (
            <div className="quiz-table-container">
              <table className="quiz-table">
                <thead>
                  <tr>
                    <th>Type</th>
                    <th>Subject</th>
                    <th>Date</th>
                    <th>Marks</th>
                    <th>Percentage</th>
                    <th>Grade</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {quizRecords.map(record => {
                    const percentage = parseFloat(calculatePercentage(record.obtainedMarks, record.totalMarks));
                    const grade = getGrade(percentage);
                    const passed = record.obtainedMarks >= record.passMarks;
                    
                    return (
                      <tr key={record.id}>
                        <td>{record.assessmentType}</td>
                        <td>Subject {record.subject.id}</td>
                        <td>{new Date(record.assessmentDate).toLocaleDateString()}</td>
                        <td>{record.obtainedMarks}/{record.totalMarks}</td>
                        <td>{percentage}%</td>
                        <td>{grade}</td>
                        <td>
                          <span className={`status ${passed ? 'passed' : 'failed'}`}>
                            {passed ? '✅ Passed' : '❌ Failed'}
                          </span>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default ExamsQuizpage;