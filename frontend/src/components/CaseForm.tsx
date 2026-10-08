import React, { useState } from 'react';
import { Send, RotateCcw, AlertCircle } from 'lucide-react';
import type { ClinicalCase } from '../types/case';
import { SYNTHETIC_DEMO_CASES } from '../data/syntheticDemoCases';

interface CaseFormProps {
  onSubmit: (caseData: Omit<ClinicalCase, 'id' | 'createdAt'>) => void;
  isLoading: boolean;
}

export const CaseForm: React.FC<CaseFormProps> = ({ onSubmit, isLoading }) => {
  const [age, setAge] = useState<number | ''>(55);
  const [gender, setGender] = useState<string>('Male');
  const [chiefComplaint, setChiefComplaint] = useState<string>('Acute substernal chest discomfort radiating to left arm');
  const [symptoms, setSymptoms] = useState<string>('Pressure-like retrosternal pain for 3 hours, associated diaphoresis and mild nausea');
  const [medicalHistory, setMedicalHistory] = useState<string>('Essential hypertension (6 years), hyperlipidemia');
  const [medications, setMedications] = useState<string>('Amlodipine 5mg daily, Atorvastatin 20mg daily');
  const [labResults, setLabResults] = useState<string>('High-sensitivity Troponin T: 0.082 ng/mL (elevated), ECG: T-wave inversions in V3-V5');
  const [additionalInformation, setAdditionalInformation] = useState<string>('Non-smoker, no prior documented myocardial infarction');
  const [isDemoCase, setIsDemoCase] = useState<boolean>(true);
  const [errors, setErrors] = useState<{ age?: string; chiefComplaint?: string }>({});

  const handleLoadDemo = (index: number) => {
    const demo = SYNTHETIC_DEMO_CASES[index];
    if (demo) {
      setAge(demo.data.age);
      setGender(demo.data.gender);
      setChiefComplaint(demo.data.chiefComplaint);
      setSymptoms(demo.data.symptoms || '');
      setMedicalHistory(demo.data.medicalHistory || '');
      setMedications(demo.data.medications || '');
      setLabResults(demo.data.labResults || '');
      setAdditionalInformation(demo.data.additionalInformation || '');
      setIsDemoCase(true);
      setErrors({});
    }
  };

  const handleClear = () => {
    setAge('');
    setGender('Male');
    setChiefComplaint('');
    setSymptoms('');
    setMedicalHistory('');
    setMedications('');
    setLabResults('');
    setAdditionalInformation('');
    setIsDemoCase(false);
    setErrors({});
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const newErrors: { age?: string; chiefComplaint?: string } = {};
    if (age === '' || isNaN(Number(age)) || Number(age) < 0 || Number(age) > 130) {
      newErrors.age = 'Please enter a valid age (0 - 130)';
    }
    if (!chiefComplaint.trim()) {
      newErrors.chiefComplaint = 'Chief complaint is required';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setErrors({});
    onSubmit({
      age: Number(age),
      gender,
      chiefComplaint: chiefComplaint.trim(),
      symptoms: symptoms.trim() || undefined,
      medicalHistory: medicalHistory.trim() || undefined,
      medications: medications.trim() || undefined,
      labResults: labResults.trim() || undefined,
      additionalInformation: additionalInformation.trim() || undefined,
      isDemoCase,
    });
  };

  return (
    <form className="clinical-case-form" onSubmit={handleSubmit}>
      <div className="form-card-header">
        <div>
          <h3>Clinical Case Entry</h3>
          <p className="form-subtitle">
            Enter patient presentation details to initiate literature retrieval and evidence analysis
          </p>
        </div>
        <div className="demo-selectors">
          <span className="demo-selector-label">Load Preset Demo Case:</span>
          <div className="demo-pill-group">
            {SYNTHETIC_DEMO_CASES.map((d, i) => (
              <button
                key={i}
                type="button"
                className="demo-pill-btn"
                onClick={() => handleLoadDemo(i)}
                disabled={isLoading}
              >
                Case {i + 1}: {d.badge}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="form-section-synthetic-banner">
        <span className="synthetic-badge">Synthetic Demo Case</span>
        <span className="synthetic-text">
          Use only de-identified or synthetic data. System processes cases within an educational research context.
        </span>
      </div>

      <div className="form-grid">
        <div className="form-group col-span-1">
          <label htmlFor="patient-age">
            Patient Age <span className="required">*</span>
          </label>
          <input
            id="patient-age"
            type="number"
            min="0"
            max="130"
            className={`form-input ${errors.age ? 'input-error' : ''}`}
            placeholder="e.g. 55"
            value={age}
            onChange={(e) => setAge(e.target.value === '' ? '' : Number(e.target.value))}
            disabled={isLoading}
          />
          {errors.age && (
            <span className="form-error-text">
              <AlertCircle size={12} /> {errors.age}
            </span>
          )}
        </div>

        <div className="form-group col-span-1">
          <label htmlFor="patient-gender">
            Gender <span className="required">*</span>
          </label>
          <select
            id="patient-gender"
            className="form-select"
            value={gender}
            onChange={(e) => setGender(e.target.value)}
            disabled={isLoading}
          >
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other / Non-specified</option>
          </select>
        </div>

        <div className="form-group col-span-2">
          <label htmlFor="chief-complaint">
            Chief Complaint <span className="required">*</span>
          </label>
          <input
            id="chief-complaint"
            type="text"
            className={`form-input ${errors.chiefComplaint ? 'input-error' : ''}`}
            placeholder="e.g. Acute substernal chest discomfort radiating to left arm"
            value={chiefComplaint}
            onChange={(e) => setChiefComplaint(e.target.value)}
            disabled={isLoading}
          />
          {errors.chiefComplaint && (
            <span className="form-error-text">
              <AlertCircle size={12} /> {errors.chiefComplaint}
            </span>
          )}
        </div>

        <div className="form-group col-span-2">
          <label htmlFor="symptoms">Symptoms &amp; Clinical Details</label>
          <textarea
            id="symptoms"
            rows={2}
            className="form-textarea"
            placeholder="Describe onset, duration, character, radiation, and aggravating or relieving factors..."
            value={symptoms}
            onChange={(e) => setSymptoms(e.target.value)}
            disabled={isLoading}
          />
        </div>

        <div className="form-group col-span-1">
          <label htmlFor="medical-history">Medical History</label>
          <textarea
            id="medical-history"
            rows={2}
            className="form-textarea"
            placeholder="Past medical conditions, chronic illnesses, prior procedures..."
            value={medicalHistory}
            onChange={(e) => setMedicalHistory(e.target.value)}
            disabled={isLoading}
          />
        </div>

        <div className="form-group col-span-1">
          <label htmlFor="medications">Current Medications</label>
          <textarea
            id="medications"
            rows={2}
            className="form-textarea"
            placeholder="Current dosage, frequency, compliance..."
            value={medications}
            onChange={(e) => setMedications(e.target.value)}
            disabled={isLoading}
          />
        </div>

        <div className="form-group col-span-2">
          <label htmlFor="lab-results">Lab Results &amp; Diagnostic Findings</label>
          <textarea
            id="lab-results"
            rows={2}
            className="form-textarea"
            placeholder="Key biomarkers (e.g. Troponin, HbA1c, Creatinine), vitals (BP, SpO2), ECG, or imaging..."
            value={labResults}
            onChange={(e) => setLabResults(e.target.value)}
            disabled={isLoading}
          />
        </div>

        <div className="form-group col-span-2">
          <label htmlFor="additional-info">Additional Clinical Information</label>
          <input
            id="additional-info"
            type="text"
            className="form-input"
            placeholder="Social history, allergies, relevant risk factors, family history..."
            value={additionalInformation}
            onChange={(e) => setAdditionalInformation(e.target.value)}
            disabled={isLoading}
          />
        </div>
      </div>

      <div className="form-actions">
        <button
          type="button"
          className="btn btn-secondary"
          onClick={handleClear}
          disabled={isLoading}
        >
          <RotateCcw size={16} />
          <span>Clear</span>
        </button>

        <button
          type="submit"
          className="btn btn-primary btn-large"
          disabled={isLoading}
        >
          {isLoading ? (
            <>
              <span className="spinner-sm"></span>
              <span>Processing Case...</span>
            </>
          ) : (
            <>
              <Send size={18} />
              <span>Analyze Case</span>
            </>
          )}
        </button>
      </div>
    </form>
  );
};
