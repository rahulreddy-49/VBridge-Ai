import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { LayoutDashboard, FilePlus, BookOpen, Layers, Info } from 'lucide-react';

interface SidebarProps {
  caseCount?: number;
  literatureCount?: number;
}

export const Sidebar: React.FC<SidebarProps> = ({
  caseCount = 0,
  literatureCount = 0,
}) => {
  const location = useLocation();
  const navigate = useNavigate();
  const pathname = location.pathname;

  const isDashboard = pathname === '/';
  const isNewCase = pathname === '/case/new';
  const isAnalysis = pathname.startsWith('/analysis');
  const isLiterature = pathname.startsWith('/literature');

  return (
    <aside className="app-sidebar">
      <div className="sidebar-nav">
        <div className="nav-group-title">NAVIGATION</div>
        <button
          className={`nav-item ${isDashboard ? 'active' : ''}`}
          onClick={() => navigate('/')}
          id="nav-dashboard"
        >
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </button>

        <button
          className={`nav-item ${isNewCase ? 'active' : ''}`}
          onClick={() => navigate('/case/new')}
          id="nav-case-form"
        >
          <FilePlus size={18} />
          <span>New Case Form</span>
          {caseCount > 0 && <span className="nav-badge">{caseCount}</span>}
        </button>

        {isAnalysis && (
          <button className="nav-item active" style={{ cursor: 'default' }} id="nav-analysis">
            <Layers size={18} />
            <span>Case Analysis</span>
            <span className="nav-badge" style={{ background: '#3b82f6' }}>Active</span>
          </button>
        )}

        <button
          className={`nav-item ${isLiterature ? 'active' : ''}`}
          onClick={() => navigate('/literature')}
          id="nav-literature"
        >
          <BookOpen size={18} />
          <span>Medical Literature</span>
          {literatureCount > 0 && <span className="nav-badge">{literatureCount}</span>}
        </button>
      </div>

      <div className="sidebar-footer">
        <div className="simulation-badge-box">
          <div className="badge-box-header">
            <Info size={14} />
            <span>Research Sandbox</span>
          </div>
          <p className="badge-box-text">
            Simulated educational environment grounded on local MySQL evidence store.
          </p>
        </div>
      </div>
    </aside>
  );
};
