import '../Styles/loginpage.css';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import loginImage from '../assets/login_image.jpg';

interface LoginResponse {
  success: boolean;
  message: string;
  user?: {
    id: number;
    username: string;
    email: string;
  };
}

const Loginpage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    // Basic validation
    if (!username.trim()) {
      setError('Username or email is required');
      setIsLoading(false);
      return;
    }
    if (!password.trim()) {
      setError('Password is required');
      setIsLoading(false);
      return;
    }

    try {
      console.log('Login attempt:', { username: username.trim() });

      const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: username.trim(),
          password: password
        }),
      });

      const data: LoginResponse = await response.json();
      console.log('Login response:', data);

      if (response.ok && data.success) {
        console.log('Login successful:', data.user);
        
        // Store user data in localStorage (optional)
        if (data.user) {
          localStorage.setItem('user', JSON.stringify(data.user));
        }
        
        // Clear form
        setUsername('');
        setPassword('');
        
        // Redirect to home page
        navigate('/homepage', { replace: true });
      } else {
        setError(data.message || 'Login failed. Please check your credentials.');
      }
    } catch (err) {
      console.error('Login error:', err);
      setError('Network error. Please check if the server is running.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSignupRedirect = () => {
    navigate('/signuppage');
  };

  const handleForgotPassword = () => {
    // For now, just show an alert. You can implement this later
    alert('Forgot password functionality will be implemented soon!');
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <input
              type="text"
              placeholder="Username or Email"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
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
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <button 
            type="submit"
            className="login-btn2"
            disabled={isLoading}
          >
            {isLoading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        <div className="login-footer">
          <button
            onClick={handleSignupRedirect}
            className="sign-up"
            type="button"
            style={{ 
              background: 'none', 
              border: 'none', 
              color: '#007bff', 
              textDecoration: 'underline', 
              cursor: 'pointer',
              fontSize: '16px',
              fontWeight: 'bold',
              margin: '10px 0'
            }}
          >
            Don't have an account? Sign Up Here!
          </button>
          <br />
          <button
            onClick={handleForgotPassword}
            className="forgot-password"
            type="button"
            style={{ background: 'none', border: 'none', color: 'inherit', textDecoration: 'underline', cursor: 'pointer' }}
          >
            Forgot password?
          </button>
        </div>
      </div>
      <div className="image-container">
        <img src={loginImage} alt="login background" />
      </div>
    </div>
  );
};

export default Loginpage;