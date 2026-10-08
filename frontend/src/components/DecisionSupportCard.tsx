import React from 'react';
import { AlertTriangle, BookMarked, CheckCircle2 } from 'lucide-react';
import type { ClinicalConsideration } from '../types/case';
import { ConfidenceIndicator } from './ConfidenceIndicator';

interface DecisionSupportCardProps {
  consideration: ClinicalConsideration;
  index: number;
}

export const DecisionSupportCard: React.FC<DecisionSupportCardProps> = ({
  consideration,
  index,
}) => {
  return (
    <div className="decision-support-card">
      <div className="dsc-header">
        <div className="dsc-title-row">
          <span className="dsc-index-badge">Consideration {index + 1}</span>
          <ConfidenceIndicator level={consideration.confidenceLevel} />
        </div>
        <h4 className="dsc-heading">{consideration.title}</h4>
      </div>

      <p className="dsc-description">{consideration.description}</p>

      <div className="dsc-section">
        <div className="dsc-section-title">
          <CheckCircle2 size={15} className="text-emerald" />
          <span>Supporting Evidence &amp; Guideline Pathways</span>
        </div>
        <p className="dsc-section-content">{consideration.supportingEvidence}</p>
      </div>

      {consideration.relevantSources && consideration.relevantSources.length > 0 && (
        <div className="dsc-section">
          <div className="dsc-section-title">
            <BookMarked size={15} className="text-blue" />
            <span>Retrieved Literature Foundations ({consideration.relevantSources.length})</span>
          </div>
          <ul className="dsc-source-list">
            {consideration.relevantSources.map((src, i) => (
              <li key={i}>{src}</li>
            ))}
          </ul>
        </div>
      )}

      {consideration.potentialRisksAndLimitations && (
        <div className="dsc-section dsc-limitations-box">
          <div className="dsc-section-title text-amber">
            <AlertTriangle size={15} />
            <span>Factors Requiring Professional Clinical Evaluation</span>
          </div>
          <p className="dsc-section-content">{consideration.potentialRisksAndLimitations}</p>
        </div>
      )}
    </div>
  );
};
