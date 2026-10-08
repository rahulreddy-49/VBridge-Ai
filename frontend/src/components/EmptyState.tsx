import React from 'react';
import { FileQuestion } from 'lucide-react';

interface EmptyStateProps {
  title: string;
  description: string;
  actionText?: string;
  onAction?: () => void;
  icon?: React.ReactNode;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  title,
  description,
  actionText,
  onAction,
  icon,
}) => {
  return (
    <div className="empty-state-box">
      <div className="empty-state-icon">
        {icon || <FileQuestion size={36} />}
      </div>
      <h4 className="empty-state-title">{title}</h4>
      <p className="empty-state-desc">{description}</p>
      {actionText && onAction && (
        <button className="btn btn-secondary" onClick={onAction}>
          {actionText}
        </button>
      )}
    </div>
  );
};
