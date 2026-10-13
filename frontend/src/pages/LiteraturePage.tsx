import React, { useState } from 'react';
import { Search, BookOpen, Filter, RotateCcw, X, Copy, Check } from 'lucide-react';
import type { LiteratureRecord } from '../types/case';
import { LiteratureCard } from '../components/LiteratureCard';
import { Disclaimer } from '../components/Disclaimer';
import { EmptyState } from '../components/EmptyState';

interface LiteraturePageProps {
  literature: LiteratureRecord[];
  onSearch: (query: string, topic: string) => void;
  isLoading: boolean;
}

const TOPICS = [
  'All Topics',
  'Cardiovascular Disease',
  'Hypertension',
  'Diabetes',
  'Asthma',
  'Pneumonia',
  'Chronic Kidney Disease',
  'Pulmonary Embolism',
];

export const LiteraturePage: React.FC<LiteraturePageProps> = ({
  literature,
  onSearch,
  isLoading,
}) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTopic, setSelectedTopic] = useState('All Topics');
  const [inspectingRecord, setInspectingRecord] = useState<LiteratureRecord | null>(null);
  const [copiedCitation, setCopiedCitation] = useState(false);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSearch(searchTerm, selectedTopic === 'All Topics' ? '' : selectedTopic);
  };

  const handleReset = () => {
    setSearchTerm('');
    setSelectedTopic('All Topics');
    onSearch('', '');
  };

  const handleCopyCitation = () => {
    if (!inspectingRecord) return;
    const citation = `${inspectingRecord.authors} (${inspectingRecord.publicationYear}). ${inspectingRecord.title}. ${inspectingRecord.journal}.`;
    navigator.clipboard.writeText(citation);
    setCopiedCitation(true);
    setTimeout(() => setCopiedCitation(false), 2000);
  };

  return (
    <div className="page-container literature-page">
      <Disclaimer compact />

      <div className="literature-page-header">
        <div>
          <h2>Medical Literature Repository</h2>
          <p className="page-subtitle">
            Curated evidence records used by the simulator RAG pipeline for clinical justification
          </p>
        </div>
        <span className="badge badge-demo">Local Educational Dataset</span>
      </div>

      {/* Search & Filter Bar */}
      <form className="literature-search-bar" onSubmit={handleSearchSubmit}>
        <div className="search-input-wrap">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            className="search-input"
            placeholder="Search guidelines, clinical trials, keywords (e.g. Troponin, SGLT2, GINA)..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        <div className="topic-select-wrap">
          <Filter size={16} className="filter-icon" />
          <select
            className="topic-select"
            value={selectedTopic}
            onChange={(e) => {
              setSelectedTopic(e.target.value);
              onSearch(searchTerm, e.target.value === 'All Topics' ? '' : e.target.value);
            }}
          >
            {TOPICS.map((topic) => (
              <option key={topic} value={topic}>
                {topic}
              </option>
            ))}
          </select>
        </div>

        <button type="submit" className="btn btn-primary" disabled={isLoading}>
          {isLoading ? 'Searching...' : 'Search'}
        </button>

        {(searchTerm || selectedTopic !== 'All Topics') && (
          <button type="button" className="btn btn-secondary btn-icon-only" onClick={handleReset} title="Reset filters">
            <RotateCcw size={16} />
          </button>
        )}
      </form>

      {/* Results Header */}
      <div className="literature-results-count">
        <span>
          Showing <strong>{literature.length}</strong> medical literature records
        </span>
      </div>

      {/* Cards Grid */}
      {literature.length > 0 ? (
        <div className="literature-cards-grid">
          {literature.map((lit, idx) => (
            <LiteratureCard
              key={lit.id || idx}
              literature={lit}
              index={idx}
              onInspect={(item) => setInspectingRecord(item)}
            />
          ))}
        </div>
      ) : (
        <EmptyState
          title="No literature records matched your search"
          description="Try modifying your keyword search or select 'All Topics'."
          actionText="Reset Search"
          onAction={handleReset}
          icon={<BookOpen size={40} />}
        />
      )}

      {/* Record Inspection Modal */}
      {inspectingRecord && (
        <div
          className="modal-backdrop"
          onClick={() => setInspectingRecord(null)}
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(15, 23, 42, 0.6)',
            backdropFilter: 'blur(2px)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
            padding: '20px',
          }}
        >
          <div
            className="modal-container"
            onClick={(e) => e.stopPropagation()}
            style={{
              background: '#ffffff',
              borderRadius: '12px',
              maxWidth: '680px',
              width: '100%',
              maxHeight: '90vh',
              overflowY: 'auto',
              boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
              padding: '24px 28px',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '16px' }}>
              <div>
                <span className="lit-topic-badge" style={{ marginBottom: '8px', display: 'inline-block' }}>
                  {inspectingRecord.topic}
                </span>
                <span className="lit-source-type-badge" style={{ marginLeft: '8px', display: 'inline-block' }}>
                  {inspectingRecord.sourceType}
                </span>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a', lineHeight: 1.35, marginTop: '8px' }}>
                  {inspectingRecord.title}
                </h3>
              </div>
              <button
                type="button"
                className="btn btn-secondary btn-icon-only"
                onClick={() => setInspectingRecord(null)}
                title="Close"
              >
                <X size={18} />
              </button>
            </div>

            <div style={{ background: '#f8fafc', padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', fontSize: '0.88rem', border: '1px solid #e2e8f0' }}>
              <div style={{ marginBottom: '6px' }}>
                <strong style={{ color: '#475569' }}>Authors:</strong> {inspectingRecord.authors}
              </div>
              <div style={{ marginBottom: '6px' }}>
                <strong style={{ color: '#475569' }}>Journal / Source:</strong> {inspectingRecord.journal}
              </div>
              <div>
                <strong style={{ color: '#475569' }}>Publication Year:</strong> {inspectingRecord.publicationYear}
              </div>
            </div>

            <div style={{ marginBottom: '24px' }}>
              <h4 style={{ fontSize: '0.95rem', fontWeight: 600, color: '#1e293b', marginBottom: '8px' }}>
                Abstract &amp; Summary Excerpt:
              </h4>
              <p style={{ color: '#334155', lineHeight: 1.6, fontSize: '0.92rem', whiteSpace: 'pre-line' }}>
                {inspectingRecord.abstractText}
              </p>
            </div>

            <div style={{ borderTop: '1px solid #e2e8f0', paddingTop: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ fontSize: '0.8rem', color: '#64748b' }}>
                Curated educational record &bull; Local evidence store
              </span>
              <button
                type="button"
                className="btn btn-outline btn-sm"
                onClick={handleCopyCitation}
              >
                {copiedCitation ? (
                  <>
                    <Check size={14} className="text-emerald" />
                    <span>Citation Copied!</span>
                  </>
                ) : (
                  <>
                    <Copy size={14} />
                    <span>Copy APA Citation</span>
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
