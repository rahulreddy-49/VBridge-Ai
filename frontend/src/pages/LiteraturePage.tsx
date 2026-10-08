import React, { useState } from 'react';
import { Search, BookOpen, Filter, RotateCcw } from 'lucide-react';
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

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSearch(searchTerm, selectedTopic === 'All Topics' ? '' : selectedTopic);
  };

  const handleReset = () => {
    setSearchTerm('');
    setSelectedTopic('All Topics');
    onSearch('', '');
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
        <span>Showing <strong>{literature.length}</strong> medical literature records</span>
      </div>

      {/* Cards Grid */}
      {literature.length > 0 ? (
        <div className="literature-cards-grid">
          {literature.map((lit, idx) => (
            <LiteratureCard key={lit.id || idx} literature={lit} index={idx} />
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
    </div>
  );
};
