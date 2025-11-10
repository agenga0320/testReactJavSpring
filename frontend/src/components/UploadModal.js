import React from 'react';

// Props: visible, onClose, uploadFile, onFileChange, onUpload
const UploadModal = ({ visible, onClose, uploadFile, onFileChange, onUpload }) => {
  if (!visible) return null;

  return (
    <div className='modal-overlay' onClick={onClose}>
      <div className='modal-content' onClick={(e) => e.stopPropagation()}>
        <form className='modal-form'>
          <input type="file" accept=".csv" onChange={onFileChange} />
          <button className='upload-form-button' onClick={onUpload} type="submit">アップロード</button>
        </form>
      </div>
    </div>
  );
};

export default UploadModal;
