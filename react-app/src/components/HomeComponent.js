import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import CreateScheduleModal from './CreateScheduleModal';
import LoadingAI from './LoadingAIComponent';
import '../home.css';

function HomeComponent() {
  const { userID } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const initialUser = location.state?.user;
  const [user, setUser] = useState(initialUser);
  const [schedules, setSchedules] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await axios.get('http://localhost:4567/api/user', {
          params: { userID }
        });
        if (response.data.status === 'success') {
          setUser(response.data.user);
        } else {
          navigate(`/`); // failed to load user
        }
      } catch (error) {
        console.error(error.response?.data.message);
        navigate(`/`);
      }
    };
    fetchUser();

    const fetchSchedules = async () => {
      try {
        const response = await axios.get('http://localhost:4567/api/schedules', {
          params: { userID }
        });
        if (response.data.status === 'success') {
          setSchedules(response.data.schedules);
        } else {
            navigate(`/`);
        }
      } catch (error) {
        console.error(error.response?.data.message);
        navigate(`/`);
      }
    };

    fetchSchedules();
  }, [userID]);

  const handleScheduleClick = async (scheduleID) => {
    try {
      const response = await axios.get('http://localhost:4567/api/schedule', {
        params: { userID, scheduleID }
      });
      if (response.data.status === 'success') {
        navigate(`/schedule/${userID}/${scheduleID}/view/`, {
          state: {
            schedule: response.data.schedule,
            credits: response.data.credits,
            courses: response.data.courses,
            schedules,
            user
          }
        });
      }
    } catch (error) {
      console.error(error.response?.data.message);
    }
  };

  const handleLogout = async () => {
    try {
        await axios.post('http://localhost:4567/api/logout');
        navigate('/');
    } catch (error) {
        console.error('Logout failed:', error.response?.data.message);
    }
  }

  const handleCreateSchedule = async (name, useAI, showFields, major, year) => {
    if (useAI) {
        setLoading(true);
    }
    if (useAI && showFields) {
        try {
            const response = await axios.put('http://localhost:4567/api/setmajoryear', null, {
              params: { userID: user.userID, major, year }
            });
        } catch (error) {
            console.error('Error saving major and year:', error.response?.data.message);
        }
    }
    try {
      let response = null;
      if (useAI && !showFields) {
          response = await axios.post('http://localhost:4567/api/schedule', null, {
            params: { userID, name, useAI, major: user.major, year: user.year }
          });
      } else {
          response = await axios.post('http://localhost:4567/api/schedule', null, {
            params: { userID, name, useAI, major, year }
          });
      }
      setLoading(false);
      if (response.data.status === 'success') {
        setSchedules([...schedules, response.data.schedule]);
        setIsModalOpen(false);
        navigate(`/schedule/${userID}/${response.data.schedule.scheduleID}/view/`, {
          state: {
            schedule: response.data.schedule,
            credits: 0,
            courses: response.data.courses,
            schedules,
            user
          }
        });
      }
    } catch (error) {
      console.error(error.response?.data.message);
    }
  };

  const handleDeleteSchedule = async (scheduleID) => {
    try {
      const response = await axios.delete('http://localhost:4567/api/schedule', {
        params: { userID, scheduleID }
      });
      if (response.data.status === 'success') {
        setSchedules(schedules.filter(schedule => schedule.scheduleID !== scheduleID));
      }
    } catch (error) {
      console.error(error.response?.data.message);
    }
  };

  return (
    <>
      {loading ? (
        <LoadingAI />
      ) : (
        <div className="container">
          <h1>Welcome {user ? user.name : 'User'}!</h1>
          <button className="logout-button" onClick={() => handleLogout(`/`, {})}>Logout</button>
          <div className="schedules-container">
            <div className="schedules-list">
              <ul>
                {schedules.length > 0 ? schedules.map((schedule, index) => (
                  <li key={index} className="card">
                    <span onClick={() => handleScheduleClick(schedule.scheduleID)}>
                      {schedule.scheduleName}
                    </span>
                    <button onClick={() => handleDeleteSchedule(schedule.scheduleID)}>X</button>
                  </li>
                )) : <li>No schedules available</li>}
              </ul>
              <button onClick={() => setIsModalOpen(true)} className="create-button">+ New</button>
            </div>
          </div>
          <CreateScheduleModal
            isOpen={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            onCreate={handleCreateSchedule}
          />
        </div>
      )}
    </>
  );

}

export default HomeComponent;