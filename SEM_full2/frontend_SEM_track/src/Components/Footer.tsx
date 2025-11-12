import '../Styles/Footer.css';

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-content">
        <div className="quick-links">
          <h4>Quick Links</h4>
          <ul>
            <li><a href="#">Help & Support</a></li>
            <li><a href="#">FAQ</a></li>
            <li><a href="#">Privacy & Policy</a></li>
            <li><a href="#">Terms & Conditions</a></li>
          </ul>
        </div>
        <div className="contact-us">
          <h4>Contact Us</h4>
          <p>Email: <a href="mailto:thiruchelvamkopuraj@gmail.com">thiruchelvamkopuraj@gmail.com</a></p>
          <p>Phone: <a href="tel:+94741125425">+94741125425</a></p>
        </div>
      </div>
      <div className="footer-bottom-line">
        © {new Date().getFullYear()} Kopuraj T. All rights reserved.
      </div>
    </footer>
  );
};

export default Footer;



