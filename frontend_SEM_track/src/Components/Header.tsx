
import '../Styles/Header.css'

const Header = () => {
  return (
    <header className="header">
      <div className="logo">SEM-Tracker</div>

      <nav className="nav-links">
        <a href="/">Home Page</a>
        <a href="/about">About Us</a>
        <a href="/contact">Contact Us</a>
        
      <div className='dropdown'>
          <a href="/more">More Link</a>
           <div className="dropdown-content">
           <a href="#">Time Table</a>
           <a href="#">Quiz & Exams</a>
           <a href="#">Attendance</a>
           <a href="#">Profile</a>
  </div>
      </div>
      </nav>

      <button className="login-btn">Log In</button>
    </header>
  );
};

export default Header;

