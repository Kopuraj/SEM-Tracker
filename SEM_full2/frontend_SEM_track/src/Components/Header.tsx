
import '../Styles/Header.css';
import { NavLink, useNavigate } from 'react-router-dom';

const Header = () => {
  const navigate = useNavigate(); // Still keep navigation if you want to use it later

  const handleLoginClick = () => {
    navigate('/loginpage'); // Programmatic navigation to the login page
  };

  return (
    <header className="header">
      <div className="logo">SEM-Tracker</div>

      <nav className="nav-links">
        <NavLink to="/homepage" end>
          Homepage
        </NavLink>
        <NavLink to="/contactus">Contact Us</NavLink>
        <NavLink to="/aboutuspage">About Us</NavLink>

        <div className="dropdown">
          <a>More Link</a>
          <div className="dropdown-content">
            <NavLink to="/timetablepage">Time Table</NavLink>
            <NavLink to="/examsquizpage">Quiz & Exams</NavLink>
            <NavLink to="/attendancepage">Attendance</NavLink>
            <NavLink to="/ProfilePage">Profile</NavLink>
          </div>
        </div>
      </nav>

      {/* Just a static button now, no auth state check */}
      <button type="button" className="login-btn" onClick={handleLoginClick}>
        Log In
      </button>
    </header>
  );
};

export default Header;
