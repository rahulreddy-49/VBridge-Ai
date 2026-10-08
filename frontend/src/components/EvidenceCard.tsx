import React from 'react';
import { Layers, Info } from 'lucide-react';

interface EvidenceCardProps {
  summary: string;
  evidenceStrength: 'High' | 'Moderate' | 'Limited';
  retrievedCount: number;
  isDemoMode: boolean;
}

export const EvidenceCard: React.FC<EvidenceCardProps> = ({
  summary,
  evidenceStrength,
  retrievedCount,
  isDemoMode,
}) => {
  return (
    <div className="evidence-panel-card">
      <div className="evidence-panel-header">
        <div className="panel-title-group">
          <Layers className="panel-icon" size={20} />
          <div>
            <h3>RAG Evidence Synthesis</h3>
            <span className="panel-subtitle">
              Synthesized from {retrievedCount} retrieved literature source records
            </span>
          </div>
        </div>

        <div className="evidence-metrics">
          <div className="metric-pill">
            <span className="metric-pill-label">Evidence Strength:</span>
            <span className={`metric-strength strength-${evidenceStrength.toLowerCase()}`}>
              {evidenceStrength}
            </span>
          </div>
          <div className="metric-pill">
            <span className="metric-pill-label">Sources Used:</span>
            <span className="metric-count">{retrievedCount}</span>
          </div>
        </div>
      </div>

      <div className="evidence-body">
        <p className="evidence-text">{summary}</p>
      </div>

      <div className="evidence-footer-notice">
        <Info size={14} />
        <span>
          {isDemoMode
            ? 'Evidence strength and literature relevance are illustrative prototype scores based on demo records.'
            : 'Evidence strength based on calibrated biomedical literature retrieval algorithms.'}
        </span>
      </div>
    </div>
  );
};
