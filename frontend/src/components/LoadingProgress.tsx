import React, { useEffect, useState } from 'react';
import { CheckCircle2, Loader2 } from 'lucide-react';

interface LoadingProgressProps {
  currentStepIndex?: number;
}

const STEPS = [
  'Processing case information',
  'Searching available literature',
  'Retrieving relevant evidence',
  'Preparing the summary',
  'Finalizing references',
];

export const LoadingProgress: React.FC<LoadingProgressProps> = () => {
  const [activeStep, setActiveStep] = useState<number>(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setActiveStep((prev) => (prev < STEPS.length - 1 ? prev + 1 : prev));
    }, 450);

    return () => clearInterval(timer);
  }, []);

  return (
    <div className="loading-progress-card">
      <div className="loading-header">
        <div className="loading-spinner-ring">
          <Loader2 className="spinner-rotate" size={28} />
        </div>
        <div>
          <h3>Simulating Clinical Evidence Analysis</h3>
          <p className="loading-subtitle">
            Running RAG pipeline against medical literature repository
          </p>
        </div>
      </div>

      <div className="progress-steps-list">
        {STEPS.map((step, idx) => {
          const isDone = idx < activeStep;
          const isCurrent = idx === activeStep;

          return (
            <div
              key={idx}
              className={`progress-step-item ${isDone ? 'step-done' : ''} ${isCurrent ? 'step-active' : ''}`}
            >
              <div className="step-icon-col">
                {isDone ? (
                  <CheckCircle2 size={18} className="step-check" />
                ) : isCurrent ? (
                  <div className="step-pulse-dot" />
                ) : (
                  <div className="step-pending-dot">{idx + 1}</div>
                )}
              </div>
              <div className="step-text">{step}</div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
