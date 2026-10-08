import React from 'react';
import { Shield, ShieldAlert, ShieldCheck } from 'lucide-react';

interface ConfidenceIndicatorProps {
  level: 'High' | 'Moderate' | 'Limited';
}

export const ConfidenceIndicator: React.FC<ConfidenceIndicatorProps> = ({ level }) => {
  const getBadgeConfig = () => {
    switch (level) {
      case 'High':
        return {
          icon: <ShieldCheck size={14} />,
          className: 'confidence-high',
          label: 'High Confidence',
        };
      case 'Moderate':
        return {
          icon: <Shield size={14} />,
          className: 'confidence-moderate',
          label: 'Moderate Confidence',
        };
      case 'Limited':
      default:
        return {
          icon: <ShieldAlert size={14} />,
          className: 'confidence-limited',
          label: 'Limited Confidence',
        };
    }
  };

  const config = getBadgeConfig();

  return (
    <span className={`confidence-badge ${config.className}`}>
      {config.icon}
      <span>{config.label}</span>
    </span>
  );
};
