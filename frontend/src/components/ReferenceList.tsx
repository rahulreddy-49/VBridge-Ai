import React from 'react';
import { Bookmark } from 'lucide-react';
import type { LiteratureRecord } from '../types/case';

interface ReferenceListProps {
  references: LiteratureRecord[];
}

export const ReferenceList: React.FC<ReferenceListProps> = ({ references }) => {
  if (!references || references.length === 0) {
    return null;
  }

  return (
    <div className="reference-list-card">
      <div className="reference-list-header">
        <Bookmark size={18} className="icon-blue" />
        <div>
          <h4>Retrieved Medical Literature References</h4>
          <span className="reference-subtitle">
            Cataloged sources supporting this clinical simulation session
          </span>
        </div>
      </div>

      <div className="reference-items">
        {references.map((ref, idx) => (
          <div key={ref.id || idx} className="reference-item">
            <span className="reference-index">[{idx + 1}]</span>
            <div className="reference-content">
              <div className="reference-title">{ref.title}</div>
              <div className="reference-citation">
                <span className="reference-authors">{ref.authors}</span> &bull;{' '}
                <span className="reference-journal">{ref.journal}</span> ({ref.publicationYear}) &bull;{' '}
                <span className="reference-badge">{ref.sourceType}</span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
