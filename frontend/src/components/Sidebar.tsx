import React from 'react';
import { LayoutDashboard, FilePlus, BookOpen, Info } from 'lucide-react';

interface SidebarProps {
  currentTab: 'dashboard' | 'new-case' | 'results' | 'literature';
  onSelectTab: (tab: 'dashboard' | 'new-case' | 'literature') => void;
  caseCount?: number;
  literatureCount?: number;
}

export const Sidebar: React.FC<SidebarProps> = ({
  currentTab,
  onSelectTab,
  caseCount = 0,
  literatureCount = 0,
}) => {
  return (
    <aside className="app-sidebar">
      <div className="sidebar-nav">
        <div className="nav-group-title">NAVIGATION</div>
        <button
          className={`nav-item ${currentTab === 'dashboard' ? 'active' : ''}`}
          onClick={() => onSelectTab('dashboard')}
        >
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </button>

        <button
          className={`nav-item ${currentTab === 'new-case' || currentTab === 'results' ? 'active' : ''}`}
          onClick={() => onSelectTab('new-case')}
        >
          <FilePlus size={18} />
          <span>Clinical Case Form</span>
          {caseCount > 0 && <span className="nav-badge">{caseCount}</span>}
        </button>

        <button
          className={`nav-item ${currentTab === 'literature' ? 'active' : ''}`}
          onClick={() => onSelectTab('literature')}
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
            Simulated environment with local MySQL evidence store.
          </p>
        </div>
      </div>
    </aside>
  );
};
