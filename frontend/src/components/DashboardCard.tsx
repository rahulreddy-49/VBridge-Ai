import React from 'react';

interface DashboardCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon: React.ReactNode;
  tag?: string;
  variant?: 'blue' | 'indigo' | 'green' | 'amber';
}

export const DashboardCard: React.FC<DashboardCardProps> = ({
  title,
  value,
  subtitle,
  icon,
  tag = 'Demo Metric',
  variant = 'blue',
}) => {
  return (
    <div className={`metric-card metric-card-${variant}`}>
      <div className="metric-header">
        <span className="metric-title">{title}</span>
        <div className="metric-icon">{icon}</div>
      </div>
      <div className="metric-body">
        <div className="metric-value">{value}</div>
        {subtitle && <div className="metric-subtitle">{subtitle}</div>}
      </div>
      <div className="metric-footer">
        <span className="metric-badge">{tag}</span>
      </div>
    </div>
  );
};
