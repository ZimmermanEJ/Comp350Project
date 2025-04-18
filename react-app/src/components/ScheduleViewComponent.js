import React, { useState } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import '../ScheduleView.css';

function ScheduleViewComponent() {
  const location = useLocation();
  const schedule = location.state?.schedule;
  const credits = location.state?.credits;
  const schedules = location.state?.schedules;
  const initialCourses = location.state?.courses;
  const [courses, setCourses] = useState(initialCourses);

  const handleDropButton = async (referenceNumber) => {
    try {
      const response = await axios.delete('http://localhost:4567/api/course', {
        params: {
          userID: schedule.userID,
          scheduleID: schedule.scheduleID,
          referenceNumber: referenceNumber
        }
      });
      if (response.data.status === 'success') {
        console.log('Course deleted successfully');
        setCourses(courses.filter(course => course.referenceNumber !== referenceNumber));
      }
    } catch (error) {
      if (error.response != null) {
        console.error(error.response.data.message);
      } else {
        console.error('Error:', error);
      }
    }
  };

  const renderScheduleView = (schedule) => {
    if (!schedule) return null;

    const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"];
    const hours = [];
    for (let hour = 8; hour < 21; hour++) {
      for (let minute = 0; minute <= 45; minute += 15) {
        hours.push(hour + minute / 100);
      }
    }

    return (
      <>
        <div>
          <h2>{schedule.name} - {credits} credits</h2>
          <table>
            <thead>
              <tr>
                <th></th>
                {days.map(day => (
                  <th key={day}>{day}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {hours.map(hour => (
                <tr key={hour}>
                  <td>{(((hour - 1) % 12) + 1).toFixed(2)}{hour <= 11 ? "AM" : "PM"}</td>
                  {days.map((day, dayIndex) => (
                    <td key={dayIndex}>
                      {courses && courses.map((course, courseIndex) => {
                        const timeslot = course.timeSlot[dayIndex + 1];
                        if (timeslot && timeslot.length === 2 && hour >= timeslot[0] && hour < timeslot[1]) {
                          if (hour - 0.149 < timeslot[0]) {
                            return (
                              <div key={courseIndex} className="course-cell">
                                {course.title}
                                <br />
                                {(((timeslot[0] - 1) % 12) + 1).toFixed(2)} - {(((timeslot[1] - 1) % 12) + 1).toFixed(2)}
                                <button className="drop-button" onClick={() => handleDropButton(course.referenceNumber)}>X</button>
                              </div>
                            );
                          } else {
                            return (
                              <div key={courseIndex} className="course-cell">
                                <br />
                                <br />
                                <br />
                              </div>
                            );
                          }
                        }
                        return null;
                      })}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
          <div>
            {schedule.events.length > 0 ? (
              <>
                <h3>Events:</h3>
                <ul>
                  {schedule.events.map((event, index) => (
                    <li key={index}>{event}</li>
                  ))}
                </ul>
              </>
            ) : (
              <p>No events scheduled</p>
            )}
          </div>
        </div>
      </>
    );
  };

  return (
    <div>
      {schedule ? (
        <>
          <h1>Viewing {schedule.name}</h1>
          {renderScheduleView(schedule)}
        </>
      ) : (
        <p>Schedule not found</p>
      )}
    </div>
  );
}

export default ScheduleViewComponent;