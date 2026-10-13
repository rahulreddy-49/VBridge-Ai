import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Sparkles, BookOpen, ShieldAlert, Printer, AlertOctagon } from 'lucide-react';
import type { AnalysisResult } from '../types/case';
import { api } from '../services/api';
import { CaseSummary } from '../components/CaseSummary';
import { ClinicalTag } from '../components/ClinicalTag';
import { LiteratureCard } from '../components/LiteratureCard';
import { EvidenceCard } from '../components/EvidenceCard';
import { DecisionSupportCard } from '../components/DecisionSupportCard';
import { ReferenceList } from '../components/ReferenceList';
import { Disclaimer } from '../components/Disclaimer';
import { LoadingProgress } from '../components/LoadingProgress';
import { ErrorMessage } from '../components/ErrorMessage';

interface CaseResultsPageProps {
  analysis?: AnalysisResult | null;
  onBackToNewCase?: () => void;
  onBackToDashboard?: () => void;
}

export const CaseResultsPage: React.FC<CaseResultsPageProps> = ({
  analysis: propAnalysis,
  onBackToNewCase,
  onBackToDashboard,
}) => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [analysis, setAnalysis] = useState<AnalysisResult | null>(propAnalysis || null);
  const [isLoading, setIsLoading] = useState<boolean>(!propAnalysis && Boolean(id));
  const [error, setError] = useState<string | null>(null);

  const loadAnalysisData = async (targetId: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await api.getAnalysisById(targetId);
      setAnalysis(data);
    } catch (err: any) {
      console.error('Error fetching analysis:', err);
      setError(err.message || `Unable to load analysis record for ID #${targetId}.`);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (!propAnalysis && id) {
      loadAnalysisData(Number(id));
    }
  }, [id, propAnalysis]);

  const handleBackToDashboard = () => {
    if (onBackToDashboard) {
      onBackToDashboard();
    } else {
      navigate('/');
    }
  };

  const handleBackToNewCase = () => {
    if (onBackToNewCase) {
      onBackToNewCase();
    } else {
      navigate('/case/new');
    }
  };

  const handlePrint = () => {
    window.print();
  };

  if (isLoading) {
    return (
      <div className="page-container results-page">
        <Disclaimer compact />
        <LoadingProgress />
      </div>
    );
  }

  if (error || !analysis) {
    return (
      <div className="page-container results-page">
        <Disclaimer compact />
        <ErrorMessage
          message={error || 'The requested analysis record was not found or has not been generated yet.'}
          onRetry={() => (id ? loadAnalysisData(Number(id)) : navigate('/case/new'))}
        />
        <div style={{ marginTop: '24px', display: 'flex', gap: '12px' }}>
          <button className="btn btn-secondary" onClick={handleBackToDashboard}>
            <ArrowLeft size={16} /> Return to Dashboard
          </button>
          <button className="btn btn-primary" onClick={handleBackToNewCase}>
            <Sparkles size={16} /> Create New Case
          </button>
        </div>
      </div>
    );
  }

  // Detect critical cardiovascular / ischemic emergency presentation
  const isCriticalEmergency = Boolean(
    analysis.keyClinicalFeatures?.some((f) => {
      const lower = f.toLowerCase();
      return lower.includes('troponin') || lower.includes('chest pain') || lower.includes('myocardial');
    }) ||
      analysis.clinicalCase?.chiefComplaint?.toLowerCase().includes('chest') ||
      analysis.clinicalCase?.labResults?.toLowerCase().includes('troponin')
  );

  return (
    <div className="page-container results-page">
      <Disclaimer compact />

      {/* EMERGENCY SAFETY BANNER */}
      {isCriticalEmergency && (
        <div
          className="emergency-alert-card"
          role="alert"
          style={{
            background: '#fff1f2',
            border: '1px solid #fecdd3',
            borderLeft: '5px solid #e11d48',
            borderRadius: '10px',
            padding: '16px 20px',
            marginBottom: '20px',
            display: 'flex',
            alignItems: 'flex-start',
            gap: '14px',
          }}
        >
          <AlertOctagon size={26} color="#e11d48" style={{ flexShrink: 0, marginTop: '2px' }} />
          <div>
            <h4 style={{ color: '#9f1239', fontWeight: 700, fontSize: '0.98rem', marginBottom: '4px' }}>
              CRITICAL NOTICE: POTENTIAL ACUTE CARDIAC PRESENTATION DETECTED
            </h4>
            <p style={{ color: '#881337', fontSize: '0.88rem', lineHeight: 1.5 }}>
              This synthetic case features acute chest discomfort and/or elevated cardiac biomarkers (Troponin),
              representing potential acute myocardial ischemia. In real clinical scenarios, immediate emergency evaluation
              (urgent 12-lead ECG, continuous hemodynamic monitoring, and acute cardiology intervention) is mandatory.
              This educational research simulator does NOT perform emergency triage and must NEVER delay medical care.
            </p>
          </div>
        </div>
      )}

      {/* Top Navigation & Status Bar */}
      <div className="results-top-bar">
        <button className="btn btn-secondary btn-sm" onClick={handleBackToDashboard}>
          <ArrowLeft size={14} />
          <span>Dashboard</span>
        </button>

        <div className="results-status-info">
          <span className="badge badge-demo">Synthetic Educational Simulation</span>
          {analysis.demoMode ? (
            <span className="badge badge-mode">Deterministic Demo Synthesis</span>
          ) : (
            <span className="badge badge-success">Live LLM Grounding</span>
          )}
        </div>

        <div className="results-actions">
          <button className="btn btn-outline btn-sm" onClick={handlePrint}>
            <Printer size={14} />
            <span>Print Report</span>
          </button>
          <button className="btn btn-primary btn-sm" onClick={handleBackToNewCase}>
            <Sparkles size={14} />
            <span>Analyze Another Case</span>
          </button>
        </div>
      </div>

      {/* 1. CASE OVERVIEW */}
      <section className="results-section">
        <div className="results-section-header">
          <div className="section-title-wrap">
            <span className="section-step-num">1</span>
            <div>
              <h3>Case Overview</h3>
              <p className="section-desc">Patient presentation and recorded clinical findings</p>
            </div>
          </div>
        </div>

        <CaseSummary clinicalCase={analysis.clinicalCase} />

        {/* 2. KEY CLINICAL TERMS */}
        <div className="key-features-panel" style={{ marginTop: '16px' }}>
          <div className="key-features-title">KEY CLINICAL TERMS &amp; BIOMARKERS EXTRACTED:</div>
          <div className="tags-cloud">
            {analysis.keyClinicalFeatures && analysis.keyClinicalFeatures.length > 0 ? (
              analysis.keyClinicalFeatures.map((tag, idx) => <ClinicalTag key={idx} label={tag} />)
            ) : (
              <span className="text-muted">No specific clinical feature patterns detected.</span>
            )}
          </div>
        </div>
      </section>

      {/* 3. RELEVANT MEDICAL LITERATURE */}
      <section className="results-section">
        <div className="results-section-header">
          <div className="section-title-wrap">
            <span className="section-step-num">2</span>
            <div>
              <h3>Relevant Medical Literature</h3>
              <p className="section-desc">
                Retrieved educational guideline and trial records ranked by clinical relevance
              </p>
            </div>
          </div>
          <span className="count-pill">
            <BookOpen size={13} />
            {analysis.relevantLiterature?.length || 0} Retrieved Records
          </span>
        </div>

        <div
          style={{
            background: '#eff6ff',
            border: '1px solid #bfdbfe',
            borderRadius: '8px',
            padding: '10px 14px',
            marginBottom: '16px',
            fontSize: '0.82rem',
            color: '#1e40af',
          }}
        >
          <strong>Scientific Methodology Notice:</strong> Retrieval relevance scores reflect lexical and semantic
          feature similarity against the query, not the medical level or GRADE hierarchy of scientific evidence.
        </div>

        <div className="literature-cards-grid">
          {analysis.relevantLiterature && analysis.relevantLiterature.length > 0 ? (
            analysis.relevantLiterature.map((lit, idx) => (
              <LiteratureCard key={lit.id || idx} literature={lit} index={idx} />
            ))
          ) : (
            <div className="empty-panel">No literature records retrieved for this query.</div>
          )}
        </div>
      </section>

      {/* 4. EVIDENCE SUMMARY */}
      <section className="results-section">
        <div className="results-section-header">
          <div className="section-title-wrap">
            <span className="section-step-num">3</span>
            <div>
              <h3>Evidence Summary</h3>
              <p className="section-desc">
                RAG synthesis summarizing evidence across retrieved medical literature
              </p>
            </div>
          </div>
        </div>

        <EvidenceCard
          summary={analysis.evidenceSummary}
          evidenceStrength={analysis.evidenceStrength}
          retrievedCount={analysis.relevantLiterature?.length || 0}
          isDemoMode={analysis.demoMode}
        />
      </section>

      {/* 5. CLINICAL DECISION SUPPORT CONSIDERATIONS */}
      <section className="results-section">
        <div className="results-section-header">
          <div className="section-title-wrap">
            <span className="section-step-num">4</span>
            <div>
              <h3>Clinical Considerations</h3>
              <p className="section-desc">
                Evidence-supported considerations and factors requiring professional evaluation
              </p>
            </div>
          </div>
        </div>

        <div className="considerations-list">
          {analysis.clinicalConsiderations && analysis.clinicalConsiderations.length > 0 ? (
            analysis.clinicalConsiderations.map((c, idx) => (
              <DecisionSupportCard key={idx} consideration={c} index={idx} />
            ))
          ) : (
            <div className="empty-panel">No clinical considerations generated.</div>
          )}
        </div>
      </section>

      {/* 6. SAFETY / LIMITATIONS */}
      <section className="results-section">
        <div className="safety-limitations-box">
          <div className="safety-header">
            <ShieldAlert size={20} className="icon-amber" />
            <div>
              <h4>Safety &amp; Simulation Limitations</h4>
              <p className="safety-subtitle">Essential factors requiring professional healthcare review</p>
            </div>
          </div>
          <p className="safety-content">{analysis.safetyLimitations}</p>
        </div>
      </section>

      {/* 7. REFERENCES */}
      <section className="results-section">
        <ReferenceList references={analysis.relevantLiterature} />
      </section>

      <div style={{ display: 'flex', justifyContent: 'center', gap: '16px', marginTop: '32px' }}>
        <button className="btn btn-secondary" onClick={handleBackToDashboard}>
          <ArrowLeft size={16} /> Return to Dashboard
        </button>
        <button className="btn btn-primary btn-large" onClick={handleBackToNewCase}>
          <Sparkles size={18} /> Start New Case Analysis
        </button>
      </div>
    </div>
  );
};
