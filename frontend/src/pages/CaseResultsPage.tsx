import React from 'react';
import { ArrowLeft, Sparkles, BookOpen, ShieldAlert, Printer } from 'lucide-react';
import type { AnalysisResult } from '../types/case';
import { CaseSummary } from '../components/CaseSummary';
import { ClinicalTag } from '../components/ClinicalTag';
import { LiteratureCard } from '../components/LiteratureCard';
import { EvidenceCard } from '../components/EvidenceCard';
import { DecisionSupportCard } from '../components/DecisionSupportCard';
import { ReferenceList } from '../components/ReferenceList';
import { Disclaimer } from '../components/Disclaimer';

interface CaseResultsPageProps {
  analysis: AnalysisResult;
  onBackToNewCase: () => void;
  onBackToDashboard: () => void;
}

export const CaseResultsPage: React.FC<CaseResultsPageProps> = ({
  analysis,
  onBackToNewCase,
  onBackToDashboard,
}) => {
  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="page-container results-page">
      <Disclaimer compact />

      {/* Top Navigation & Status Bar */}
      <div className="results-top-bar">
        <button className="btn btn-secondary btn-sm" onClick={onBackToDashboard}>
          <ArrowLeft size={14} />
          <span>Dashboard</span>
        </button>

        <div className="results-status-info">
          <span className="badge badge-demo">Synthetic Demo Analysis</span>
          {analysis.demoMode && (
            <span className="badge badge-mode">Demo Mode Grounding</span>
          )}
        </div>

        <div className="results-actions">
          <button className="btn btn-outline btn-sm" onClick={handlePrint}>
            <Printer size={14} />
            <span>Print Report</span>
          </button>
          <button className="btn btn-primary btn-sm" onClick={onBackToNewCase}>
            <Sparkles size={14} />
            <span>Analyze Another Case</span>
          </button>
        </div>
      </div>

      {/* 1. CASE SUMMARY */}
      <section className="results-section">
        <div className="results-section-header">
          <div className="section-title-wrap">
            <span className="section-step-num">1</span>
            <div>
              <h3>Case Summary</h3>
              <p className="section-desc">Patient presentation and recorded clinical findings</p>
            </div>
          </div>
        </div>

        <CaseSummary clinicalCase={analysis.clinicalCase} />

        {/* 2. KEY CLINICAL FEATURES */}
        <div className="key-features-panel">
          <div className="key-features-title">KEY CLINICAL FEATURES EXTRACTED:</div>
          <div className="tags-cloud">
            {analysis.keyClinicalFeatures && analysis.keyClinicalFeatures.length > 0 ? (
              analysis.keyClinicalFeatures.map((tag, idx) => (
                <ClinicalTag key={idx} label={tag} />
              ))
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

      {/* 4. RAG EVIDENCE SUMMARY */}
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

      {/* 5. CLINICAL DECISION SUPPORT SIMULATION */}
      <section className="results-section">
        <div className="results-section-header">
          <div className="section-title-wrap">
            <span className="section-step-num">4</span>
            <div>
              <h3>Clinical Decision Support Simulation</h3>
              <p className="section-desc">
                Evidence-supported clinical considerations and factors requiring professional evaluation
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
    </div>
  );
};
