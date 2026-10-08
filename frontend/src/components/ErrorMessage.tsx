import React from 'react';
import { AlertCircle, RefreshCw } from 'lucide-react';

interface ErrorMessageProps {
  message: string;
  onRetry?: () => void;
}

export const ErrorMessage: React.FC<ErrorMessageProps> = ({ message, onRetry }) => {
  return (
    <div className="error-banner">
      <div className="error-icon-col">
        <AlertCircle size={22} className="error-icon" />
      </div>
      <div className="error-content">
        <h4 className="error-title">Notice / Communication Error</h4>
        <p className="error-message-text">{message}</p>
        {onRetry && (
          <button className="btn btn-sm btn-outline-danger" onClick={onRetry}>
            <RefreshCw size={13} />
            <span>Retry Operation</span>
          </button>
        )}
      </div>
    </div>
  );
};
