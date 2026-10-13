import React from 'react';
import { useNavigate } from 'react-router-dom';
import { FileQuestion, ArrowLeft, Sparkles } from 'lucide-react';
import { Disclaimer } from '../components/Disclaimer';

export const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="page-container" style={{ textAlign: 'center', padding: '60px 20px' }}>
      <Disclaimer compact />
      <div style={{ maxWidth: '480px', margin: '40px auto 0' }}>
        <FileQuestion size={56} color="#64748b" style={{ marginBottom: '16px' }} />
        <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#0f172a', marginBottom: '8px' }}>
          Page Not Found
        </h2>
        <p style={{ color: '#64748b', fontSize: '0.95rem', lineHeight: 1.5, marginBottom: '24px' }}>
          The requested clinical case or simulator route does not exist. Please return to the dashboard or start a new clinical analysis.
        </p>
        <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
          <button className="btn btn-secondary" onClick={() => navigate('/')}>
            <ArrowLeft size={16} /> Return to Dashboard
          </button>
          <button className="btn btn-primary" onClick={() => navigate('/case/new')}>
            <Sparkles size={16} /> New Clinical Case
          </button>
        </div>
      </div>
    </div>
  );
};
