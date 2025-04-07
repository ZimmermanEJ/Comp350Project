import React from 'react';
import axios from 'axios';
import { Link, NavLink, Route, Routes, useParams, useLocation, useNavigate } from 'react-router-dom';
import ScheduleViewComponent from './ScheduleViewComponent';
import SearchComponent from './SearchComponent';
import '../Schedule.css'; // Import the CSS file

function ScheduleComponent() {
  const { userID, scheduleID } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const schedule = location.state?.schedule;
  const credits = location.state?.credits;
  const schedules = location.state?.schedules;
  const courses = location.state?.courses;

  const handleNavClick = async (path, state) => {
      if (path === '/') {
        try {
          await axios.post('http://localhost:4567/api/logout');
          navigate(path, { state });
        } catch (error) {
          console.error('Logout failed:', error.response?.data.message);
        }
      } else {
        navigate(path, { state });
      }
  };

  return (
    <div className="ScheduleComponent">
      <nav>
        <ul className="nav-list">
          <li className="nav-item" onClick={() => handleNavClick(`/home/${userID}`, { schedules })}>
            <div>
              <Link to={`/home/${userID}`} state={{ schedules }}>Home</Link>
            </div>
          </li>
          <li className="nav-item" onClick={() => handleNavClick(`/schedule/${userID}/${scheduleID}/view/`, { schedule, credits, schedules, courses })}>
            <div>
              <NavLink
                to={`/schedule/${userID}/${scheduleID}/view`}
                state={{ schedule, credits, schedules }}
                className={({ isActive }) => isActive ? 'active' : ''}
              >
                Schedule View
              </NavLink>
            </div>
          </li>
          <li className="nav-item" onClick={() => handleNavClick(`/schedule/${userID}/${scheduleID}/search`, { schedule, credits, schedules, courses })}>
            <div>
              <NavLink
                to={`/schedule/${userID}/${scheduleID}/search/`}
                state={{ schedule, credits, schedules }}
                className={({ isActive }) => isActive ? 'active' : ''}
              >
                Search
              </NavLink>
            </div>
          </li>
          <li className="nav-item" onClick={() => handleNavClick(`/`, {})}>
            <div>
              <Link to={`/`}>Logout</Link>
            </div>
          </li>
        </ul>
      </nav>
      <Routes>
        <Route path="view/" element={<ScheduleViewComponent schedule={schedule} />} />
        <Route path="search/" element={<SearchComponent schedule={schedule} />} />
      </Routes>
    </div>
  );
}

export default ScheduleComponent;