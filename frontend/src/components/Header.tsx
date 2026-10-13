import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Stethoscope, Sparkles } from 'lucide-react';
import type { HealthStatus } from '../types/case';

interface HeaderProps {
  health: HealthStatus | null;
  onNavigateNewCase?: () => void;
}

export const Header: React.FC<HeaderProps> = ({ health, onNavigateNewCase }) => {
  const navigate = useNavigate();

  const handleBrandClick = () => {
    navigate('/');
  };

  const handleNewCaseClick = () => {
    if (onNavigateNewCase) {
      onNavigateNewCase();
    } else {
      navigate('/case/new');
    }
  };

  return (
    <header className="app-header">
      <div className="header-brand" onClick={handleBrandClick} style={{ cursor: 'pointer' }} id="header-brand">
        <div className="brand-logo">
          <Stethoscope className="icon-main" size={26} />
        </div>
        <div className="brand-titles">
          <div className="title-row">
            <h1>AI Medical Literature &amp; Clinical Decision Support Simulator</h1>
            <span className="badge badge-demo">DEMO SIMULATOR</span>
          </div>
          <p className="subtitle">
            Evidence-grounded medical research and clinical reasoning simulation
          </p>
        </div>
      </div>

      <div className="header-actions">
        <div className="system-status-indicator">
          <span className={`status-dot ${health?.status === 'UP' ? 'online' : 'checking'}`}></span>
          <span className="status-label">
            {health?.status === 'UP' ? 'System Ready' : 'Connecting...'}
          </span>
          {health?.demoMode && (
            <span className="badge badge-mode">Demo Mode</span>
          )}
        </div>

        <button className="btn btn-primary" onClick={handleNewCaseClick} id="header-btn-new-case">
          <Sparkles size={16} />
          <span>New Clinical Case</span>
        </button>
      </div>
    </header>
  );
};
