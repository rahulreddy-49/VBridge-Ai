export interface ClinicalCase {
  id?: number;
  age: number;
  gender: string;
  chiefComplaint: string;
  symptoms?: string;
  medicalHistory?: string;
  medications?: string;
  labResults?: string;
  additionalInformation?: string;
  isDemoCase?: boolean;
  createdAt?: string;
}

export interface LiteratureRecord {
  id: number;
  title: string;
  authors: string;
  publicationYear: number;
  journal: string;
  abstractText: string;
  topic: string;
  sourceType: string;
  relevanceScore?: number;
}

export interface ClinicalConsideration {
  title: string;
  description: string;
  confidenceLevel: 'High' | 'Moderate' | 'Limited';
  supportingEvidence: string;
  relevantSources: string[];
  potentialRisksAndLimitations: string;
}

export interface AnalysisResult {
  id: number;
  caseId: number;
  clinicalCase: ClinicalCase;
  keyClinicalFeatures: string[];
  summary: string;
  evidenceStrength: 'High' | 'Moderate' | 'Limited';
  evidenceSummary: string;
  clinicalConsiderations: ClinicalConsideration[];
  safetyLimitations: string;
  relevantLiterature: LiteratureRecord[];
  demoMode: boolean;
  disclaimer: string;
  createdAt: string;
}

export interface HealthStatus {
  status: string;
  database: string;
  demoMode: boolean;
  caseCount: number;
  literatureCount: number;
  timestamp: string;
}

export interface LiteratureSearchQuery {
  query?: string;
  topic?: string;
  limit?: number;
}
