import '../Styles/signuppage.css';
import  { useState } from 'react';
const  Signuppage= () =>{
     const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState(''); // Added missing email state


    return(
        <div className="signup-container">
      <div className="signup-box">
        <h2>Signup</h2>
        <form>
          <div className="input-group">
            <input
              type="text"
              placeholder="Username"
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
            <div className="input-group">
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="signup-btn2">Create Account</button>
        </form>
        <div className="signup-footer">
          <a href="/loginpage" className="sign-up">Login</a>
          
        </div>
      </div>
      <div className="image-container">
        <img src="/src/assets/login_image.jpg" alt="login background" />
      </div>
    </div>


    );
};
export default Signuppage;