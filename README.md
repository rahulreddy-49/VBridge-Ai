# AI Medical Literature & Clinical Decision Support Simulator

> **Evidence-grounded medical research and clinical reasoning simulation**

---

> [!IMPORTANT]
> **MEDICAL SAFETY & RESEARCH DISCLAIMER:**
> This application is an **Educational Research Prototype**. It is designed solely for simulating how Retrieval-Augmented Generation (RAG) and clinical terminology extraction can ground clinical decision support in peer-reviewed medical literature.
> 
> - It does **NOT** provide definitive medical diagnoses.
> - It does **NOT** issue medical prescriptions or autonomous treatment plans.
> - It does **NOT** replace licensed clinical judgment or healthcare professional evaluation.
> - Use **synthetic / de-identified demo cases only**.

---

## 1. Problem Statement
Clinicians and medical trainees face vast, rapidly updating biomedical literature. Traditional search engines return unorganized document links without synthesizing actionable clinical considerations, while conversational LLMs frequently suffer from hallucinations, lack verifiable source grounding, or fail to communicate crucial clinical risk factors and diagnostic limitations.

## 2. Objective
Build an educational full-stack simulator demonstrating an end-to-end RAG architecture that:
1. Accepts a synthetic clinical patient profile.
2. Extracts key clinical features and terminology.
3. Retrieves relevant evidence-based literature and clinical guidelines.
4. Generates non-prescriptive, structured clinical considerations with explicit confidence levels, supporting citations, and limitation factors.
5. Functions out-of-the-box in **Demo Mode** without external AI API dependencies.

---

## 3. Technology Stack

- **Frontend**:
  - React 19
  - Vite
  - TypeScript
  - Lucide React (Clean, modern clinical and research icons)
  - Custom Medical Research Design System (Vanilla CSS with medical blue/indigo accents, rounded cards, responsive grid)

- **Backend**:
  - Java 21 LTS
  - Spring Boot 3.3.4
  - Spring Web (REST Controllers, CORS configuration)
  - Spring Data JPA / Hibernate ORM
  - Bean Validation (`jakarta.validation`)

- **Database**:
  - MySQL Server 8.0 (Database: `medlit_ai`)

- **AI / RAG Pipeline**:
  - `RagService` & `ClinicalFeatureExtractor`: Semantic and clinical keyword indexing over curated medical literature.
  - `LlmService`: Abstraction for evidence synthesis and clinical considerations generation with deterministic fallback in **Demo Mode**.

---

## 4. System Architecture & RAG Workflow

```
 USER (Browser)
      │  Enters synthetic case or loads demo preset
      ▼
 REACT + VITE + TYPESCRIPT FRONTEND (Port 5173)
      │  HTTP POST /api/cases, /api/cases/{id}/analyze
      ▼
 SPRING BOOT REST API (Port 8080)
      │
      ├─► CaseController (Validates DTO, stores clinical_cases)
      │
      ├─► ClinicalFeatureExtractor
      │    • Extracts clinical terms (e.g., "Elevated Troponin", "Hypertension", "Dyspnea")
      │
      ├─► LiteratureService & RagService
      │    • Computes clinical relevance across MySQL literature records
      │    • Ranks matching guidelines & clinical trials
      │
      ├─► LlmService (Evidence Synthesis & Decision Support Engine)
      │    • Demo Mode Grounding / Optional External LLM
      │    • Generates evidence-supported clinical considerations
      │    • Produces safety limitations & source citations
      │
      └─► MySQL 8.0 Database (`medlit_ai`)
           • Tables: clinical_cases, literature, analysis_results, evidence_sources
```

---

## 5. Database Schema

The database `medlit_ai` is automatically initialized on application startup:

1. **`clinical_cases`**:
   - `id` (BIGINT, PK, Auto Increment)
   - `age` (INT)
   - `gender` (VARCHAR)
   - `chief_complaint` (TEXT)
   - `symptoms` (TEXT)
   - `medical_history` (TEXT)
   - `medications` (TEXT)
   - `lab_results` (TEXT)
   - `additional_information` (TEXT)
   - `is_demo_case` (BOOLEAN)
   - `created_at` (DATETIME)

2. **`literature`**:
   - `id` (BIGINT, PK, Auto Increment)
   - `title` (VARCHAR)
   - `authors` (VARCHAR)
   - `publication_year` (INT)
   - `journal` (VARCHAR)
   - `abstract_text` (TEXT)
   - `topic` (VARCHAR)
   - `source_type` (VARCHAR, e.g. "Demo Literature Record")

3. **`analysis_results`**:
   - `id` (BIGINT, PK, Auto Increment)
   - `case_id` (BIGINT, FK)
   - `summary` (TEXT)
   - `evidence_strength` (VARCHAR: "High", "Moderate", "Limited")
   - `key_features_json` (TEXT)
   - `clinical_considerations_json` (TEXT)
   - `safety_limitations` (TEXT)
   - `created_at` (DATETIME)

4. **`evidence_sources`**:
   - `id` (BIGINT, PK, Auto Increment)
   - `analysis_id` (BIGINT, FK)
   - `literature_id` (BIGINT, FK)
   - `relevance_score` (INT)

---

## 6. REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | System health, MySQL status, demo mode flag, and counts |
| `POST` | `/api/cases` | Create a new clinical case |
| `GET` | `/api/cases` | Retrieve all cases (sorted newest first) |
| `GET` | `/api/cases/demo` | Retrieve pre-seeded synthetic demo cases |
| `GET` | `/api/cases/{id}` | Get clinical case details by ID |
| `POST` | `/api/cases/{id}/analyze` | Execute RAG feature extraction, literature retrieval, and decision support synthesis |
| `GET` | `/api/cases/{id}/analysis` | Retrieve the latest existing analysis for a case |
| `GET` | `/api/literature` | Get all literature repository records |
| `GET` | `/api/literature/{id}` | Get specific literature record |
| `POST` | `/api/literature/search` | Search literature records by query, topic, and limit |

---

## 7. Synthetic Demo Cases & Dataset

The simulator seeds 3 synthetic clinical presentations covering common medical conditions:

1. **Demo Case 1 (Cardiovascular)**:
   - 55-year-old male with acute substernal chest discomfort radiating to left arm
   - Elevated High-sensitivity Troponin T (0.082 ng/mL), ECG T-wave inversions
   - Prior history: Essential hypertension, hyperlipidemia
2. **Demo Case 2 (Metabolic & Cardiorenal)**:
   - 45-year-old female with uncontrolled blood pressure, chronic fatigue, polyuria
   - Blood pressure 158/96 mmHg, HbA1c 9.4%, spot urine albumin/creatinine 180 mg/g
   - Comorbid Type 2 Diabetes and Hypertension
3. **Demo Case 3 (Respiratory / Asthma)**:
   - 30-year-old female with worsening nocturnal cough, breathlessness, wheezing
   - Spirometry FEV1 72% predicted with 15% bronchodilator reversibility

---

## 8. Setup & Running Instructions

### Prerequisites
- **Java**: OpenJDK 21 or higher
- **Node.js**: v20 or higher & npm
- **MySQL Server**: 8.0 running locally on `localhost:3306`

### 1. Database Setup
Log into MySQL and ensure the database exists (Spring Boot will also create it if not present):
```sql
CREATE DATABASE IF NOT EXISTS medlit_ai;
```

### 2. Configure Environment (Optional)
Copy `.env.example` to `.env` if custom credentials or external AI keys are used:
```bash
cp .env.example .env
```

### 3. Start Backend (Spring Boot)
From the project root:
```bash
cd backend
.\mvnw.cmd spring-boot:run
```
The backend initializes the MySQL schema, seeds the 12 medical literature records and 3 synthetic demo cases, and listens on `http://localhost:8080`.

Verify health endpoint:
```bash
curl http://localhost:8080/api/health
```

### 4. Start Frontend (React + Vite)
From the project root:
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## 9. Limitations & Ethical Considerations
- **Non-Diagnostic Scope**: Generated outputs represent educational simulations based on local records and simulated reasoning.
- **Controlled Knowledge Base**: The MVP operates on a curated set of 12 clinical records representing cardiology, hypertension, pulmonology, and endocrinology.
- **No Direct Clinical Action**: Healthcare providers must not use this system for real-time triage, acute prescribing, or emergency care.

## 10. Future Enhancements
- Integration with external biomedical APIs (PubMed / NCBI E-Utilities).
- Embeddings via BioLinkBERT or ClinicalBERT vectors stored in a dedicated vector index.
- Multi-document comparative guideline conflict analysis.
- PDF upload and automated parsing for institutional clinical guidelines.
