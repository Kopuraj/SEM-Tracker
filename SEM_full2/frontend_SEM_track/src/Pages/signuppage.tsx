import '../Styles/signuppage.css';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import loginImage from '../assets/login_image.jpg';
import { API_ENDPOINTS } from '../config/apiConfig';

interface Student {
  username: string;
  password: string;
  email: string;
}

interface ApiResponse {
  success: boolean;
  message: string;
  data?: any;
}

const Signuppage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    setMessage('');

    // Basic client-side validation
    if (!username.trim()) {
      setError('Username is required');
      setIsLoading(false);
      return;
    }
    if (username.trim().length < 3) {
      setError('Username must be at least 3 characters long');
      setIsLoading(false);
      return;
    }
    if (!email.trim()) {
      setError('Email is required');
      setIsLoading(false);
      return;
    }
    if (!password.trim()) {
      setError('Password is required');
      setIsLoading(false);
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters long');
      setIsLoading(false);
      return;
    }

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email.trim())) {
      setError('Please enter a valid email address');
      setIsLoading(false);
      return;
    }

    const studentData: Student = {
      username: username.trim(),
      password,
      email: email.trim()
    };

    try {
      console.log('Sending registration request:', { username: studentData.username, email: studentData.email });
      
      const response = await fetch(API_ENDPOINTS.REGISTER, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(studentData),
      });

      const data: ApiResponse = await response.json();
      console.log('Response received:', data);

      if (response.ok && data.success) {
        setMessage(data.message || 'Registration successful! You can now login.');
        // Reset form
        setUsername('');
        setPassword('');
        setEmail('');
        
        // Redirect to login page after 2 seconds
        setTimeout(() => {
          navigate('/loginpage');
        }, 2000);
      } else {
        setError(data.message || 'Registration failed. Please try again.');
      }
    } catch (error) {
      console.error('Network error:', error);
      setError('Network error. Please check if the server is running.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleLoginRedirect = () => {
    navigate('/loginpage');
  };

  return (
    <div className="signup-container">
      <div className="signup-box">
        <h2>Signup</h2>
        
        {message && <div className="success-message">{message}</div>}
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              minLength={3}
              maxLength={50}
            />
          </div>
          <div className="input-group">
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="input-group">
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={6}
            />
          </div>
          
          <button 
            type="submit"
            className="signup-btn2"
            disabled={isLoading}
          >
            {isLoading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>
        
        <div className="signup-footer">
          <button 
            onClick={handleLoginRedirect}
            className="sign-up"
            type="button"
            style={{ background: 'none', border: 'none', color: 'inherit', textDecoration: 'underline', cursor: 'pointer' }}
          >
            Already have an account? Login
          </button>
        </div>
      </div>
      <div className="image-container">
        <img src={loginImage} alt="signup background" />
      </div>
    </div>
  );
};

export default Signuppage;