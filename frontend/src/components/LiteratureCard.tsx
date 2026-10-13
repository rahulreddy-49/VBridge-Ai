import React from 'react';
import { BookOpen, Calendar, Award, UserCheck } from 'lucide-react';
import type { LiteratureRecord } from '../types/case';

interface LiteratureCardProps {
  literature: LiteratureRecord;
  index?: number;
  onInspect?: (lit: LiteratureRecord) => void;
}

export const LiteratureCard: React.FC<LiteratureCardProps> = ({ literature, index, onInspect }) => {
  const score = literature.relevanceScore ?? 80;

  const getScoreColorClass = (val: number) => {
    if (val >= 85) return 'score-high';
    if (val >= 70) return 'score-medium';
    return 'score-low';
  };

  return (
    <div className="literature-card">
      <div className="lit-card-top">
        <div className="lit-card-topic-row">
          <span className="lit-topic-badge">{literature.topic}</span>
          <span className="lit-source-type-badge">{literature.sourceType}</span>
        </div>
        {literature.relevanceScore !== undefined && (
          <div
            className={`lit-relevance-pill ${getScoreColorClass(score)}`}
            title="Lexical & feature retrieval similarity score (not clinical trial grade)"
          >
            <Award size={13} />
            <span>Match: {score}%</span>
          </div>
        )}
      </div>

      <h4 className="lit-title">
        {index !== undefined && <span className="lit-index">[{index + 1}] </span>}
        {literature.title}
      </h4>

      <div className="lit-meta">
        <span className="lit-authors">
          <UserCheck size={13} /> {literature.authors}
        </span>
        <span className="lit-journal">
          <BookOpen size={13} /> {literature.journal}
        </span>
        <span className="lit-year">
          <Calendar size={13} /> {literature.publicationYear}
        </span>
      </div>

      <p className="lit-abstract">{literature.abstractText}</p>

      {onInspect && (
        <div style={{ marginTop: '12px', display: 'flex', justifyContent: 'flex-end' }}>
          <button
            type="button"
            className="btn btn-sm btn-outline"
            onClick={() => onInspect(literature)}
            style={{ fontSize: '0.8rem' }}
          >
            Inspect Record Details
          </button>
        </div>
      )}
    </div>
  );
};
