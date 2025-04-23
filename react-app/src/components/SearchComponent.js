import React, { useState } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';

function SearchComponent() {

    const location = useLocation();
    const schedule = location.state?.schedule;




    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const handleInputChange = (event) => {
        setSearchTerm(event.target.value);
    };

    const handleSearch = async (event) => {
        if (event.key === 'Enter') {
            console.log('enter pressed');
            try {
                const response = await axios.get('http://localhost:4567/api/search', {
                    params: { searchString: searchTerm }
                });
                setSearchResults(response.data.courses);
            } catch (error) {
                console.error('Error fetching search results:', error);
            }
        }
    };

    const handleAddToSchedule = async (course) => {
            try {
                console.log('Adding course:', course);
                console.log('Schedule ID:', schedule.scheduleID);
                console.log('User ID:', schedule.userID);
                console.log('Reference Number:', course.referenceNumber);

                const response = await axios.put('http://localhost:4567/api/addToSchedule', null, {
                   params: {
                       userID: schedule.userID,
                       scheduleID: schedule.scheduleID,
                       referenceNumber: course.referenceNumber
                   }
                });
                if (response.data.status === 'success') {
                    alert(`Course ${course.department} ${course.courseNumber} added to schedule!`);
                } else {
                    alert(`Failed to add course: ${response.data.message}`);
                }
            } catch (error) {
                console.error('Error adding course to schedule:', error);
            }
        };

    return (
            <div>
                <input
                    type="text"
                    placeholder="Enter search term"
                    value={searchTerm}
                    onChange={handleInputChange}
                    onKeyDown={handleSearch}
                />
                <h1>Search Page</h1>
                <ul>
                    {searchResults.map((course, index) => (
                        <li key={index}>
                            {course.department} {course.courseNumber} {course.sectionCode}, Ref: {course.referenceNumber}
                            <button onClick={() => handleAddToSchedule(course)}>Add to Schedule</button>
                        </li>
                    ))}
                </ul>
            </div>
        );
}

export default SearchComponent;