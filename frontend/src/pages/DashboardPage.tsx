import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Activity, BookOpen, Layers, ArrowRight, Play, Sparkles, FileEdit, Database, ArrowRightCircle } from 'lucide-react';
import type { ClinicalCase, HealthStatus, LiteratureRecord } from '../types/case';
import { DashboardCard } from '../components/DashboardCard';
import { Disclaimer } from '../components/Disclaimer';
import { EmptyState } from '../components/EmptyState';
import { SYNTHETIC_DEMO_CASES } from '../data/syntheticDemoCases';

interface DashboardPageProps {
  health: HealthStatus | null;
  cases: ClinicalCase[];
  literature: LiteratureRecord[];
  onNavigateNewCase: () => void;
  onNavigateLiterature: () => void;
  onSelectCaseToAnalyze: (caseId: number) => void;
  onQuickLoadDemo: (index: number) => void;
  onPrefillDemo?: (index: number) => void;
}

export const DashboardPage: React.FC<DashboardPageProps> = ({
  health,
  cases,
  literature,
  onNavigateNewCase,
  onNavigateLiterature,
  onSelectCaseToAnalyze,
  onQuickLoadDemo,
  onPrefillDemo,
}) => {
  const navigate = useNavigate();

  const totalCases = cases.length > 0 ? cases.length : (health?.caseCount ?? 0);
  const totalLit = literature.length > 0 ? literature.length : (health?.literatureCount ?? 0);

  return (
    <div className="page-container dashboard-page">
      <Disclaimer />

      {/* Header Info */}
      <div style={{ marginBottom: '20px' }}>
        <h2 style={{ fontSize: '1.45rem', fontWeight: 700, color: '#0f172a', marginBottom: '4px' }}>
          AI Medical Literature &amp; Clinical Decision Support Simulator
        </h2>
        <p style={{ color: '#475569', fontSize: '0.95rem' }}>
          Explore medical literature. Understand evidence. Support informed research.
        </p>
      </div>

      {/* Metric Cards Row — Strictly Real Records */}
      <div className="metrics-grid">
        <DashboardCard
          title="Clinical Cases"
          value={`${totalCases} Cases`}
          subtitle="Stored synthetic scenarios in MySQL"
          icon={<Activity size={22} />}
          tag={totalCases > 0 ? 'MySQL Store' : 'No Cases Yet'}
          variant="blue"
        />
        <DashboardCard
          title="Literature Guidelines"
          value={`${totalLit} Records`}
          subtitle="Evidence-based guidelines &amp; trials"
          icon={<BookOpen size={22} />}
          tag="Curated Dataset"
          variant="indigo"
        />
        <DashboardCard
          title="Evidence Engine"
          value={health?.demoMode ? 'Demo Mode' : 'Live LLM'}
          subtitle="RAG keyword &amp; feature retrieval"
          icon={<Layers size={22} />}
          tag={health?.demoMode ? 'Deterministic' : 'Configured API'}
          variant="green"
        />
        <DashboardCard
          title="Database Status"
          value={health?.status === 'UP' ? 'Connected' : 'Offline'}
          subtitle={health?.database || 'MySQL localhost:3306'}
          icon={<Database size={22} />}
          tag={health?.status === 'UP' ? 'Online' : 'Pending'}
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
            <button className="btn btn-primary btn-large" onClick={onNavigateNewCase} id="btn-hero-new-case">
              <Sparkles size={18} />
              <span>New Clinical Case</span>
            </button>
            <button className="btn btn-secondary btn-large" onClick={onNavigateLiterature} id="btn-hero-browse-lit">
              <BookOpen size={18} />
              <span>Browse {totalLit > 0 ? `${totalLit} ` : ''}Literature Records</span>
            </button>
          </div>
        </div>
      </div>

      {/* primary Workflow Pipeline Diagram */}
      <div
        className="workflow-pipeline-card"
        style={{
          background: '#ffffff',
          border: '1px solid #e2e8f0',
          borderRadius: '12px',
          padding: '20px 24px',
          marginBottom: '28px',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
        }}
      >
        <div style={{ marginBottom: '12px' }}>
          <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: '#1e293b' }}>
            Educational Research Simulation Workflow
          </h3>
          <p style={{ fontSize: '0.84rem', color: '#64748b' }}>
            Structured pipeline from synthetic patient input to guideline-grounded decision support
          </p>
        </div>
        <div
          className="workflow-steps"
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: '8px',
            overflowX: 'auto',
            paddingTop: '8px',
            paddingBottom: '4px',
          }}
        >
          {[
            'Synthetic Clinical Case',
            'Feature Extraction',
            'Literature Retrieval',
            'Evidence Analysis',
            'AI / Demo Summary',
            'Clinical Considerations',
            'Source References',
          ].map((step, idx, arr) => (
            <React.Fragment key={idx}>
              <div
                style={{
                  background: '#f8fafc',
                  border: '1px solid #cbd5e1',
                  borderRadius: '6px',
                  padding: '8px 12px',
                  textAlign: 'center',
                  fontSize: '0.8rem',
                  fontWeight: 600,
                  color: '#334155',
                  whiteSpace: 'nowrap',
                }}
              >
                {step}
              </div>
              {idx < arr.length - 1 && (
                <ArrowRightCircle size={16} color="#94a3b8" style={{ flexShrink: 0 }} />
              )}
            </React.Fragment>
          ))}
        </div>
      </div>

      {/* Preset Synthetic Demo Cases Section */}
      <div className="dashboard-section">
        <div className="section-header">
          <div>
            <h3>Pre-Configured Synthetic Demo Scenarios</h3>
            <p className="section-subtitle">
              Select any scenario to inspect or simulate the evidence-grounded research pipeline
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
                <span>
                  {demo.data.age}yo {demo.data.gender} &bull; {demo.data.medicalHistory}
                </span>
              </div>
              <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  style={{ flex: 1 }}
                  onClick={() => {
                    if (onPrefillDemo) onPrefillDemo(index);
                    else navigate('/case/new');
                  }}
                  title="Prefill this case in the clinical case entry form"
                >
                  <FileEdit size={13} />
                  <span>Edit in Form</span>
                </button>
                <button
                  type="button"
                  className="btn btn-primary btn-sm"
                  style={{ flex: 1.2 }}
                  onClick={() => onQuickLoadDemo(index)}
                  title="Run analysis immediately on this case"
                >
                  <Play size={13} />
                  <span>Analyze Now</span>
                </button>
              </div>
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

        {cases.length > 0 ? (
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
                {cases.slice(0, 10).map((c) => (
                  <tr key={c.id}>
                    <td>
                      <span className="case-id-tag">#{c.id}</span>
                    </td>
                    <td>
                      <strong>
                        {c.age}yo {c.gender}
                      </strong>
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
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyState
            title="No clinical cases recorded yet"
            description="Create your first synthetic clinical case or choose one of the three pre-configured demo cases above to start evidence synthesis."
            actionText="Enter New Case"
            onAction={onNavigateNewCase}
            icon={<Activity size={40} />}
          />
        )}
      </div>
    </div>
  );
};
