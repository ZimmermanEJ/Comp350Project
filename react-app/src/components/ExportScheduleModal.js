import React, { useState } from 'react';

function ExportScheduleModal({ isOpen, onClose, onExport }) {
  const [fileName, setFileName] = useState('');


  const handleExport = () => {
    onExport(fileName);
    setFileName('');
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <button className="close-button" onClick={onClose}>X</button>
        <h4>Export to Google and Outlook Calendar</h4>
        <input
          type="text"
          value={fileName}
          onChange={(e) => setFileName(e.target.value)}
          placeholder="Enter file name: (i.e. fall2025)"
        />

        <button className="confirm-button" onClick={handleExport}>Export</button>
      </div>
    </div>
  );
}

export default ExportScheduleModal;