import React from 'react';
import { Tag } from 'lucide-react';

interface ClinicalTagProps {
  label: string;
}

export const ClinicalTag: React.FC<ClinicalTagProps> = ({ label }) => {
  return (
    <span className="clinical-chip">
      <Tag size={12} className="chip-icon" />
      <span>{label}</span>
    </span>
  );
};
