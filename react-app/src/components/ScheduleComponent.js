import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, NavLink, Route, Routes, useParams, useLocation, useNavigate } from 'react-router-dom';
import ScheduleViewComponent from './ScheduleViewComponent';
import SearchComponent from './SearchComponent';
import StatusSheetComponent from './StatusSheetComponent';
import '../Schedule.css'; // Import the CSS file

function ScheduleComponent() {
  const { userID, scheduleID } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const [schedule, setSchedule] = useState([]);
  const [credits, setCredits] = useState(location.state?.credits);
  const schedules = location.state?.schedules;
  const [courses, setCourses] = useState(location.state?.courses);

  const fetchSchedule = async () => {
      try {
        const response = await axios.get('http://localhost:4567/api/schedule', {
          params: { userID, scheduleID }
        });
        if (response.data.status === 'success') {
          setCredits(response.data.credits)
          setCourses(response.data.courses);
          setSchedule(response.data.schedule);
        } else {
        //              navigate(`/`);
        }
      } catch (error) {
        console.error(error.response?.data.message);
        //        navigate(`/`);
      }
  };

  useEffect(() => {
    fetchSchedule();
  }, [userID, scheduleID]);

  const handleNavClick = async (path, state) => {
      if (path === '/') {
        try {
          await axios.post('http://localhost:4567/api/logout');
          navigate(path, { state });
        } catch (error) {
          console.error('Logout failed:', error.response?.data.message);
        }
      } else if (path === `/home/${userID}`) {
        handleSaveSchedule();
      } else {
        navigate(path, { state });
      }
  };

  const handleSaveSchedule = async () => {
    try {
        const response = await axios.post('http://localhost:4567/api/saveschedule', null, {
          params: { userID, scheduleID }
        });
        if (response.data.status === 'success') {
          console.log('Schedule saved successfully');
        } else {
          console.error('Failed to save schedule:', response.data.message);
        }
    } catch (error) {
      console.error('Error saving schedule:', error.response?.data.message);
    }
  }

  return (
    <div className="ScheduleComponent">
      <nav>
        <ul className="nav-list">
          <li className="nav-item" onClick={() => handleNavClick(`/home/${userID}`, { schedules })}>
            <div>
              <Link to={`/home/${userID}`} state={{ schedules, userID }}>Home</Link>
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
          <li className="nav-item" onClick={() => handleNavClick(`/schedule/${userID}/${scheduleID}/search/`, { schedule, credits, schedules, courses })}>
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
          <li className="nav-item" onClick={() => handleNavClick(`/schedule/${userID}/${scheduleID}/statussheet/`, { schedule, credits, schedules, courses })}>
              <div>
                <NavLink
                  to={`/schedule/${userID}/${scheduleID}/statussheet`}
                  state={{ schedule, credits, schedules }}
                  className={({ isActive }) => isActive ? 'active' : ''}
                >
                  Status Sheet
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
        <Route path="statussheet/" element={<StatusSheetComponent schedule={schedule} />} />
      </Routes>
    </div>
  );
}

export default ScheduleComponent;