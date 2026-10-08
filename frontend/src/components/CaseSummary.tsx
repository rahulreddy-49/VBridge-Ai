import React from 'react';
import { User, Activity, FileText, Pill, FlaskConical, AlertCircle } from 'lucide-react';
import type { ClinicalCase } from '../types/case';

interface CaseSummaryProps {
  clinicalCase: ClinicalCase;
}

export const CaseSummary: React.FC<CaseSummaryProps> = ({ clinicalCase }) => {
  return (
    <div className="case-summary-card">
      <div className="summary-header">
        <div className="patient-demographics">
          <div className="avatar-icon">
            <User size={20} />
          </div>
          <div>
            <h3 className="patient-title">
              {clinicalCase.age}-Year-Old {clinicalCase.gender}
            </h3>
            <span className="case-id-badge">
              Case #{clinicalCase.id || 'NEW'} {clinicalCase.isDemoCase && '• Synthetic Demo'}
            </span>
          </div>
        </div>
        <div className="chief-complaint-badge">
          <strong>Chief Complaint:</strong> {clinicalCase.chiefComplaint}
        </div>
      </div>

      <div className="summary-grid">
        {clinicalCase.symptoms && (
          <div className="summary-field">
            <div className="field-label">
              <Activity size={14} /> Symptoms &amp; Details
            </div>
            <div className="field-value">{clinicalCase.symptoms}</div>
          </div>
        )}

        {clinicalCase.medicalHistory && (
          <div className="summary-field">
            <div className="field-label">
              <FileText size={14} /> Medical History
            </div>
            <div className="field-value">{clinicalCase.medicalHistory}</div>
          </div>
        )}

        {clinicalCase.medications && (
          <div className="summary-field">
            <div className="field-label">
              <Pill size={14} /> Current Medications
            </div>
            <div className="field-value">{clinicalCase.medications}</div>
          </div>
        )}

        {clinicalCase.labResults && (
          <div className="summary-field">
            <div className="field-label">
              <FlaskConical size={14} /> Lab &amp; Diagnostic Findings
            </div>
            <div className="field-value field-value-highlight">{clinicalCase.labResults}</div>
          </div>
        )}

        {clinicalCase.additionalInformation && (
          <div className="summary-field col-span-2">
            <div className="field-label">
              <AlertCircle size={14} /> Additional Clinical Information
            </div>
            <div className="field-value">{clinicalCase.additionalInformation}</div>
          </div>
        )}
      </div>
    </div>
  );
};
