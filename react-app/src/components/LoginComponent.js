import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import '../login.css'; // Import the CSS file

function LoginComponent() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate(); // useNavigate hook for navigation

  useEffect(() => {
      const logout = async () => {
          try {
            const response = await axios.post('http://localhost:4567/api/logout');
          } catch (error) {
            console.error('Logout failed:', error.response?.data.message);
          }
      }

      logout();
  });

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