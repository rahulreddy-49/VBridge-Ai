import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, useNavigate } from 'react-router-dom';
import { Header } from './components/Header';
import { Sidebar } from './components/Sidebar';
import { DashboardPage } from './pages/DashboardPage';
import { NewCasePage } from './pages/NewCasePage';
import { CaseResultsPage } from './pages/CaseResultsPage';
import { LiteraturePage } from './pages/LiteraturePage';
import { NotFoundPage } from './pages/NotFoundPage';
import { api } from './services/api';
import type { AnalysisResult, ClinicalCase, HealthStatus, LiteratureRecord } from './types/case';
import { SYNTHETIC_DEMO_CASES } from './data/syntheticDemoCases';

const AppContent: React.FC = () => {
  const navigate = useNavigate();
  const [health, setHealth] = useState<HealthStatus | null>(null);
  const [cases, setCases] = useState<ClinicalCase[]>([]);
  const [literature, setLiterature] = useState<LiteratureRecord[]>([]);
  const [currentAnalysis, setCurrentAnalysis] = useState<AnalysisResult | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const loadInitialData = async () => {
    try {
      const [healthData, casesData, litData] = await Promise.allSettled([
        api.getHealth(),
        api.getAllCases(),
        api.getAllLiterature(),
      ]);

      if (healthData.status === 'fulfilled') {
        setHealth(healthData.value);
      }
      if (casesData.status === 'fulfilled') {
        setCases(casesData.value);
      }
      if (litData.status === 'fulfilled') {
        setLiterature(litData.value);
      }
    } catch (err: any) {
      console.warn('Initial data load warning:', err);
    }
  };

  // Initial load
  useEffect(() => {
    loadInitialData();
  }, []);

  const handleCreateAndAnalyzeCase = async (caseData: Omit<ClinicalCase, 'id' | 'createdAt'>) => {
    setIsLoading(true);
    setError(null);
    try {
      // 1. Create case in backend MySQL
      const created = await api.createCase(caseData);
      if (!created.id) {
        throw new Error('Case created without an ID');
      }

      // Refresh cases list
      const updatedCases = await api.getAllCases();
      setCases(updatedCases);

      // 2. Run RAG Analysis
      const analysisResult = await api.analyzeCase(created.id);
      setCurrentAnalysis(analysisResult);
      navigate(`/analysis/${created.id}`);
    } catch (err: any) {
      console.error('Case analysis error:', err);
      setError(err.message || 'Unable to complete the analysis. Please check the backend connection and try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSelectCaseToAnalyze = async (caseId: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const analysisResult = await api.analyzeCase(caseId);
      setCurrentAnalysis(analysisResult);
      navigate(`/analysis/${caseId}`);
    } catch (err: any) {
      console.error('Error analyzing existing case:', err);
      setError(err.message || 'Failed to analyze case. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleQuickLoadDemo = async (index: number) => {
    const demo = SYNTHETIC_DEMO_CASES[index];
    if (demo) {
      await handleCreateAndAnalyzeCase(demo.data);
    }
  };

  const handleSearchLiterature = async (query: string, topic: string) => {
    setIsLoading(true);
    try {
      const results = await api.searchLiterature({ query, topic, limit: 20 });
      setLiterature(results);
    } catch (err: any) {
      console.error('Literature search error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="app-layout">
      <Header
        health={health}
        onNavigateNewCase={() => {
          setError(null);
          navigate('/case/new');
        }}
      />

      <div className="app-main-body">
        <Sidebar
          caseCount={cases.length}
          literatureCount={literature.length}
        />

        <main className="app-content-area">
          <Routes>
            <Route
              path="/"
              element={
                <DashboardPage
                  health={health}
                  cases={cases}
                  literature={literature}
                  onNavigateNewCase={() => navigate('/case/new')}
                  onNavigateLiterature={() => navigate('/literature')}
                  onSelectCaseToAnalyze={handleSelectCaseToAnalyze}
                  onQuickLoadDemo={handleQuickLoadDemo}
                  onPrefillDemo={() => navigate('/case/new')}
                />
              }
            />
            <Route
              path="/case/new"
              element={
                <NewCasePage
                  onSubmitCase={handleCreateAndAnalyzeCase}
                  isLoading={isLoading}
                  error={error}
                  onClearError={() => setError(null)}
                />
              }
            />
            <Route
              path="/analysis/:id"
              element={
                <CaseResultsPage
                  analysis={currentAnalysis}
                  onBackToNewCase={() => navigate('/case/new')}
                  onBackToDashboard={() => navigate('/')}
                />
              }
            />
            <Route
              path="/literature"
              element={
                <LiteraturePage
                  literature={literature}
                  onSearch={handleSearchLiterature}
                  isLoading={isLoading}
                />
              }
            />
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </main>
      </div>
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
};

export default App;
