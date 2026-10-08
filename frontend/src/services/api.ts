import type { AnalysisResult, ClinicalCase, HealthStatus, LiteratureRecord, LiteratureSearchQuery } from '../types/case';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let errorDetail = 'Network request failed';
    try {
      const errJson = await response.json();
      errorDetail = errJson.message || errJson.error || JSON.stringify(errJson);
    } catch {
      errorDetail = response.statusText || `HTTP ${response.status}`;
    }
    throw new Error(errorDetail);
  }
  return response.json();
}

export const api = {
  async getHealth(): Promise<HealthStatus> {
    const res = await fetch(`${API_BASE_URL}/health`);
    return handleResponse<HealthStatus>(res);
  },

  async getAllCases(): Promise<ClinicalCase[]> {
    const res = await fetch(`${API_BASE_URL}/cases`);
    return handleResponse<ClinicalCase[]>(res);
  },

  async getDemoCases(): Promise<ClinicalCase[]> {
    const res = await fetch(`${API_BASE_URL}/cases/demo`);
    return handleResponse<ClinicalCase[]>(res);
  },

  async getCaseById(id: number): Promise<ClinicalCase> {
    const res = await fetch(`${API_BASE_URL}/cases/${id}`);
    return handleResponse<ClinicalCase>(res);
  },

  async createCase(caseData: Omit<ClinicalCase, 'id' | 'createdAt'>): Promise<ClinicalCase> {
    const res = await fetch(`${API_BASE_URL}/cases`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(caseData),
    });
    return handleResponse<ClinicalCase>(res);
  },

  async analyzeCase(caseId: number): Promise<AnalysisResult> {
    const res = await fetch(`${API_BASE_URL}/cases/${caseId}/analyze`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    });
    return handleResponse<AnalysisResult>(res);
  },

  async getAnalysisForCase(caseId: number): Promise<AnalysisResult> {
    const res = await fetch(`${API_BASE_URL}/cases/${caseId}/analysis`);
    return handleResponse<AnalysisResult>(res);
  },

  async getAllLiterature(): Promise<LiteratureRecord[]> {
    const res = await fetch(`${API_BASE_URL}/literature`);
    return handleResponse<LiteratureRecord[]>(res);
  },

  async searchLiterature(params: LiteratureSearchQuery): Promise<LiteratureRecord[]> {
    const res = await fetch(`${API_BASE_URL}/literature/search`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params),
    });
    return handleResponse<LiteratureRecord[]>(res);
  },
};
