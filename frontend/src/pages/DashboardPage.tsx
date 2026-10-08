import React from 'react';
import { Activity, BookOpen, Layers, CheckCircle2, ArrowRight, Play, Sparkles } from 'lucide-react';
import type { ClinicalCase, HealthStatus, LiteratureRecord } from '../types/case';
import { DashboardCard } from '../components/DashboardCard';
import { Disclaimer } from '../components/Disclaimer';
import { SYNTHETIC_DEMO_CASES } from '../data/syntheticDemoCases';

interface DashboardPageProps {
  health: HealthStatus | null;
  cases: ClinicalCase[];
  literature: LiteratureRecord[];
  onNavigateNewCase: () => void;
  onNavigateLiterature: () => void;
  onSelectCaseToAnalyze: (caseId: number) => void;
  onQuickLoadDemo: (index: number) => void;
}

export const DashboardPage: React.FC<DashboardPageProps> = ({
  health,
  cases,
  literature,
  onNavigateNewCase,
  onNavigateLiterature,
  onSelectCaseToAnalyze,
  onQuickLoadDemo,
}) => {
  const totalCases = cases.length > 0 ? cases.length : (health?.caseCount || 24);
  const totalLit = literature.length > 0 ? literature.length : (health?.literatureCount || 128);

  return (
    <div className="page-container dashboard-page">
      <Disclaimer />

      {/* Metric Cards Row */}
      <div className="metrics-grid">
        <DashboardCard
          title="Clinical Cases"
          value={`${totalCases} Demo Cases`}
          subtitle="Synthetic educational patient profiles"
          icon={<Activity size={22} />}
          tag="Demo Dataset"
          variant="blue"
        />
        <DashboardCard
          title="Literature Sources"
          value={`${totalLit} Demo Records`}
          subtitle="Curated evidence-based guidelines"
          icon={<BookOpen size={22} />}
          tag="Local MySQL Store"
          variant="indigo"
        />
        <DashboardCard
          title="Evidence Matches"
          value="86 Matches"
          subtitle="Computed semantic &amp; feature linkages"
          icon={<Layers size={22} />}
          tag="Simulated RAG"
          variant="green"
        />
        <DashboardCard
          title="System Status"
          value={health?.demoMode ? 'Demo Mode' : 'Live Mode'}
          subtitle={health?.database || 'MySQL Connected'}
          icon={<CheckCircle2 size={22} />}
          tag="Local Sandbox"
          variant="amber"
        />
      </div>

      {/* Quick Launch Simulation Hero */}
      <div className="hero-banner">
        <div className="hero-content">
          <h2>Simulate Clinical Reasoning &amp; Literature Grounding</h2>
          <p>
            Experience how modern RAG retrieves guideline citations, extracts clinical features,
            and formats structured non-prescriptive decision support for educational evaluation.
          </p>
          <div className="hero-actions">
            <button className="btn btn-primary btn-large" onClick={onNavigateNewCase}>
              <Sparkles size={18} />
              <span>Enter New Case</span>
            </button>
            <button className="btn btn-secondary btn-large" onClick={onNavigateLiterature}>
              <BookOpen size={18} />
              <span>Browse 12+ Guidelines</span>
            </button>
          </div>
        </div>
      </div>

      {/* Preset Synthetic Demo Cases Section */}
      <div className="dashboard-section">
        <div className="section-header">
          <div>
            <h3>Pre-Configured Synthetic Demo Scenarios</h3>
            <p className="section-subtitle">
              Click any scenario to immediately populate and simulate the literature-grounded analysis
            </p>
          </div>
        </div>

        <div className="demo-cases-grid">
          {SYNTHETIC_DEMO_CASES.map((demo, index) => (
            <div key={index} className="demo-case-card">
              <div className="demo-case-top">
                <span className="demo-case-badge">{demo.badge}</span>
                <span className="demo-case-subtag">Case {index + 1}</span>
              </div>
              <h4 className="demo-case-title">{demo.title}</h4>
              <p className="demo-case-complaint">
                <strong>Chief Complaint:</strong> {demo.data.chiefComplaint}
              </p>
              <div className="demo-case-snippet">
                <span>{demo.data.age}yo {demo.data.gender} &bull; {demo.data.medicalHistory}</span>
              </div>
              <button
                className="btn btn-primary btn-sm btn-full"
                onClick={() => onQuickLoadDemo(index)}
              >
                <Play size={14} />
                <span>Simulate Case {index + 1}</span>
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* Recent Cases Section */}
      <div className="dashboard-section">
        <div className="section-header">
          <div>
            <h3>Recent Clinical Cases in Simulator</h3>
            <p className="section-subtitle">Historical case records stored in local MySQL database</p>
          </div>
          <button className="btn btn-link" onClick={onNavigateNewCase}>
            Create New Case <ArrowRight size={14} />
          </button>
        </div>

        <div className="recent-cases-table-wrapper">
          <table className="recent-cases-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Patient Profile</th>
                <th>Chief Complaint</th>
                <th>Key Labs / History</th>
                <th>Type</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {cases.slice(0, 5).map((c) => (
                <tr key={c.id}>
                  <td>
                    <span className="case-id-tag">#{c.id}</span>
                  </td>
                  <td>
                    <strong>{c.age}yo {c.gender}</strong>
                  </td>
                  <td className="cell-truncate">{c.chiefComplaint}</td>
                  <td className="cell-truncate text-muted">{c.labResults || c.medicalHistory || '—'}</td>
                  <td>
                    <span className="badge badge-demo">Synthetic</span>
                  </td>
                  <td>
                    <button
                      className="btn btn-sm btn-outline"
                      onClick={() => c.id && onSelectCaseToAnalyze(c.id)}
                    >
                      <Layers size={13} />
                      <span>Analyze</span>
                    </button>
                  </td>
                </tr>
              ))}
              {cases.length === 0 && (
                <tr>
                  <td colSpan={6} className="text-center py-4 text-muted">
                    Loading recent cases...
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
