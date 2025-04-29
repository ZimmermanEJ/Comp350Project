import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../loadingAI.css'; // Import the CSS

function LoadingAIComponent() {
  const [progress, setProgress] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const interval = setInterval(() => {
      setProgress((prevProgress) => {
        if (prevProgress >= 95) {
          return 95;
        }
        return prevProgress + (100 / 45);
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [navigate]);

  return (
    <div className="loading-overlay">
      <div className="loading-modal">
        <h1 className="loading-title">Generating Your AI Schedule...</h1>
        <div className="loading-bar-container">
          <div
            className="loading-bar-progress"
            style={{ width: `${progress}%` }}
          />
        </div>
        <p className="loading-description">This may take up to 45 seconds...</p>
      </div>
    </div>
  );
}

export default LoadingAIComponent;
