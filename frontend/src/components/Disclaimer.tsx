import React from 'react';
import { AlertTriangle } from 'lucide-react';

interface DisclaimerProps {
  compact?: boolean;
}

export const Disclaimer: React.FC<DisclaimerProps> = ({ compact = false }) => {
  if (compact) {
    return (
      <div className="disclaimer-banner disclaimer-compact">
        <AlertTriangle size={15} className="disclaimer-icon" />
        <span>
          <strong>Educational Research Prototype:</strong> Not a substitute for professional medical advice, clinical diagnosis, or treatment decisions.
        </span>
      </div>
    );
  }

  return (
    <div className="disclaimer-banner">
      <div className="disclaimer-icon-wrapper">
        <AlertTriangle size={20} className="disclaimer-icon" />
      </div>
      <div className="disclaimer-content">
        <div className="disclaimer-heading">Clinical Research Prototype — Educational Simulation</div>
        <p className="disclaimer-text">
          This system is an educational research simulator grounded on synthetic datasets and educational literature records.
          It does <strong>NOT</strong> provide medical diagnoses, treatment prescriptions, or autonomous triage. All retrieved information
          and clinical considerations require verification by a licensed healthcare professional.
        </p>
      </div>
    </div>
  );
};
