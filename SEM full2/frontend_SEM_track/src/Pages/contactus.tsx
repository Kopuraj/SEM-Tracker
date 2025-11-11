import '../Styles/contactus.css';

const Contactus = () => {
  return (

  
    <div className="contact-container">
      <div className="contact-form-section">
        <form className="contact-form" onSubmit={(e) => e.preventDefault()}>
          <h1>Contact Us</h1>
          <input type="text" placeholder="Name" name="name" required />
          <input type="email" placeholder="Email" name="email" required />
          <textarea placeholder="What you want to say?" name="message" required />
          <button type="submit">Submit</button>
        </form>
      </div>

      <div className="contact-image-section">
        <div className="contact-message">
          <h2>Feel free to</h2>
          <h2>Contact Us!</h2>
        </div>
      </div>
    </div>
  
    
  );
};

export default Contactus;
