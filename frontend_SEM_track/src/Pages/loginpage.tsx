import '../Styles/loginpage.css';
import  { useState } from 'react';
const Loginpage = () =>{

     const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

    return(

        <div className="login-container">
      <div className="login-box">
        <h2>Login</h2>
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
          <button type="submit" className="login-btn2">Login</button>
        </form>
        <div className="login-footer">
          <a href="/sign-up" className="sign-up">Sign Up</a>
          <a href="/forgot-password" className="forgot-password">Forgot password?</a>
        </div>
      </div>
      <div className="image-container">
        <img src="/src/assets/login_image.jpg" alt="login background" />
      </div>
    </div>
    );
};
 export default Loginpage;