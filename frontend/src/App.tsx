import React, { useEffect, useState } from 'react';
import { Header } from './components/Header';
import { Sidebar } from './components/Sidebar';
import { DashboardPage } from './pages/DashboardPage';
import { NewCasePage } from './pages/NewCasePage';
import { CaseResultsPage } from './pages/CaseResultsPage';
import { LiteraturePage } from './pages/LiteraturePage';
import { api } from './services/api';
import type { AnalysisResult, ClinicalCase, HealthStatus, LiteratureRecord } from './types/case';
import { SYNTHETIC_DEMO_CASES } from './data/syntheticDemoCases';

export const App: React.FC = () => {
  const [currentTab, setCurrentTab] = useState<'dashboard' | 'new-case' | 'results' | 'literature'>('dashboard');
  const [health, setHealth] = useState<HealthStatus | null>(null);
  const [cases, setCases] = useState<ClinicalCase[]>([]);
  const [literature, setLiterature] = useState<LiteratureRecord[]>([]);
  const [currentAnalysis, setCurrentAnalysis] = useState<AnalysisResult | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Initial load
  useEffect(() => {
    loadInitialData();
  }, []);

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
      setCurrentTab('results');
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
    setCurrentTab('new-case');
    try {
      const analysisResult = await api.analyzeCase(caseId);
      setCurrentAnalysis(analysisResult);
      setCurrentTab('results');
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
          setCurrentTab('new-case');
        }}
      />

      <div className="app-main-body">
        <Sidebar
          currentTab={currentTab}
          onSelectTab={(tab) => {
            setError(null);
            setCurrentTab(tab);
          }}
          caseCount={cases.length}
          literatureCount={literature.length}
        />

        <main className="app-content-area">
          {currentTab === 'dashboard' && (
            <DashboardPage
              health={health}
              cases={cases}
              literature={literature}
              onNavigateNewCase={() => setCurrentTab('new-case')}
              onNavigateLiterature={() => setCurrentTab('literature')}
              onSelectCaseToAnalyze={handleSelectCaseToAnalyze}
              onQuickLoadDemo={handleQuickLoadDemo}
            />
          )}

          {currentTab === 'new-case' && (
            <NewCasePage
              onSubmitCase={handleCreateAndAnalyzeCase}
              isLoading={isLoading}
              error={error}
              onClearError={() => setError(null)}
            />
          )}

          {currentTab === 'results' && currentAnalysis && (
            <CaseResultsPage
              analysis={currentAnalysis}
              onBackToNewCase={() => setCurrentTab('new-case')}
              onBackToDashboard={() => setCurrentTab('dashboard')}
            />
          )}

          {currentTab === 'literature' && (
            <LiteraturePage
              literature={literature}
              onSearch={handleSearchLiterature}
              isLoading={isLoading}
            />
          )}
        </main>
      </div>
    </div>
  );
};

export default App;
