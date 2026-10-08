import React from 'react';
import { Disclaimer } from '../components/Disclaimer';
import { CaseForm } from '../components/CaseForm';
import { LoadingProgress } from '../components/LoadingProgress';
import { ErrorMessage } from '../components/ErrorMessage';
import type { ClinicalCase } from '../types/case';

interface NewCasePageProps {
  onSubmitCase: (caseData: Omit<ClinicalCase, 'id' | 'createdAt'>) => void;
  isLoading: boolean;
  error: string | null;
  onClearError: () => void;
}

export const NewCasePage: React.FC<NewCasePageProps> = ({
  onSubmitCase,
  isLoading,
  error,
  onClearError,
}) => {
  return (
    <div className="page-container new-case-page">
      <Disclaimer compact />

      {error && (
        <ErrorMessage
          message={error}
          onRetry={onClearError}
        />
      )}

      {isLoading ? (
        <LoadingProgress />
      ) : (
        <CaseForm onSubmit={onSubmitCase} isLoading={isLoading} />
      )}
    </div>
  );
};
