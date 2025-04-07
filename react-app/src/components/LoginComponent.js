import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import '../login.css'; // Import the CSS file

function LoginComponent() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate(); // useNavigate hook for navigation

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:4567/api/login', null, {
        params: {
          email: email,
          password: password
        }
      });
      if (response.data.status === 'success') {
        // Redirect to the home page with user data
        console.log(response.data);
        navigate(`/home/${response.data.user.userID}`, { state: { user: response.data.user, schedules: response.data.schedules } });
      }
    } catch (error) {
      setMessage(error.response.data.message);
    }
  };

  return (
    <div className="login-container">
      <form className="login-form" onSubmit={handleLogin}>
        <h2>Login</h2>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>
        {message && <p>{message}</p>}
        <p>Dont have an account? <Link to="/signup">Signup</Link></p>
      </form>
    </div>
  );
}

export default LoginComponent;