
import '../Styles/Header.css'
import { NavLink, useNavigate } from 'react-router-dom'; // Import useNavigate hook
const Header = () => {

  const navigate = useNavigate(); // Initialize the navigate function

  const handleLoginClick = () => {
    navigate('/loginpage'); // Programmatic navigation to the login page


  };
  return (
    <header className="header">
      <div className="logo">SEM-Tracker</div>

      <nav className="nav-links">
        <NavLink to="/" end>
        Homepage
      </NavLink>
      <NavLink to="/contactus">Contactus</NavLink>
      <NavLink to="/aboutuspage">Aboutuspage</NavLink>
        
        
        
        
      <div className='dropdown'>
          <a href="/more">More Link</a>
           <div className="dropdown-content">
            <NavLink to="/timetablepage">Time Table</NavLink>
           <NavLink to="/examsquizpage">Quiz & Exams</NavLink>
          
           
           <NavLink to="/attendancepage">Attendance</NavLink>
           <a href="#">Profile</a>
  </div>
      </div>
      </nav>

        <button type="button" className="login-btn" onClick={handleLoginClick}>
        Log In
      </button>
    </header>
  );
};

export default Header;

