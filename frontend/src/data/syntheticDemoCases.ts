import type { ClinicalCase } from '../types/case';

export interface PredefinedDemoCase {
  title: string;
  badge: string;
  data: Omit<ClinicalCase, 'id' | 'createdAt'>;
}

export const SYNTHETIC_DEMO_CASES: PredefinedDemoCase[] = [
  {
    title: 'Demo Case 1: Acute Chest Discomfort & Elevated Troponin',
    badge: 'Cardiovascular / Ischemia',
    data: {
      age: 55,
      gender: 'Male',
      chiefComplaint: 'Acute substernal chest discomfort radiating to left arm',
      symptoms: 'Pressure-like retrosternal pain for 3 hours, associated diaphoresis and mild nausea',
      medicalHistory: 'Essential hypertension (6 years), hyperlipidemia',
      medications: 'Amlodipine 5mg daily, Atorvastatin 20mg daily',
      labResults: 'High-sensitivity Troponin T: 0.082 ng/mL (elevated), ECG: T-wave inversions in V3-V5',
      additionalInformation: 'Non-smoker, no prior documented myocardial infarction. Synthetic demo case.',
      isDemoCase: true,
    },
  },
  {
    title: 'Demo Case 2: Uncontrolled Hypertension & Type 2 Diabetes',
    badge: 'Metabolic & Cardiorenal',
    data: {
      age: 45,
      gender: 'Female',
      chiefComplaint: 'Uncontrolled blood pressure with chronic fatigue and polyuria',
      symptoms: 'Occasional morning occipital headaches, exertional fatigue, nocturia x3',
      medicalHistory: 'Hypertension diagnosed 5 years ago, Type 2 Diabetes Mellitus (8 years)',
      medications: 'Metformin 1000mg twice daily, Lisinopril 20mg daily',
      labResults: 'Blood pressure: 158/96 mmHg, HbA1c: 9.4%, Serum Creatinine: 1.1 mg/dL, Spot urine albumin/creatinine: 180 mg/g',
      additionalInformation: 'Sedentary desk worker, family history of premature cardiovascular disease. Synthetic demo case.',
      isDemoCase: true,
    },
  },
  {
    title: 'Demo Case 3: Exacerbating Nocturnal Cough & Wheezing',
    badge: 'Respiratory / Asthma',
    data: {
      age: 30,
      gender: 'Female',
      chiefComplaint: 'Worsening nocturnal cough and recurrent wheezing',
      symptoms: 'Exertional breathlessness, chest tightness worse at night and cold air exposure',
      medicalHistory: 'Childhood allergic rhinitis, intermittent asthma',
      medications: 'Albuterol MDI as needed (using 4-5 times per week)',
      labResults: 'Spirometry: FEV1 72% predicted with 15% bronchodilator reversibility, SpO2 96% on room air',
      additionalInformation: 'Symptoms exacerbated after recent upper respiratory tract viral infection. Synthetic demo case.',
      isDemoCase: true,
    },
  },
];
