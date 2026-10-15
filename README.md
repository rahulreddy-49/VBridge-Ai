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
> - For suspected acute coronary syndromes, chest pain with elevated troponin, or acute distress, immediate in-person emergency evaluation is mandatory.
> - Use **synthetic / de-identified demo cases only**.

---

## 1. Project Overview
The **AI Medical Literature & Clinical Decision Support Simulator** is an evidence-oriented medical research workspace. It enables researchers, educators, and clinicians to enter synthetic clinical scenarios, extract clinical features, retrieve relevant medical literature from a curated biomedical dataset, synthesize evidence using Retrieval-Augmented Generation (RAG), and review structured, non-prescriptive clinical considerations with explicit source references and safety limitations.

## 2. Problem Statement
Clinicians, medical researchers, and students must navigate voluminous, rapidly evolving biomedical literature. Conventional search tools return unranked documents without synthesizing actionable clinical rationale, while generic generative AI chat interfaces frequently hallucinate citations, provide ungrounded diagnoses, and fail to highlight critical safety limitations and emergency evaluation requirements.

## 3. Proposed Solution
This simulator provides an end-to-end, source-grounded clinical research pipeline:
1. **Synthetic Case Intake**: Captures patient demographics, chief complaints, symptoms, medical history, medications, and laboratory findings.
2. **Clinical Feature Extraction**: Rule-based and pattern-matched extraction of key biomedical terms and risk factors (e.g., *Chest Pain*, *Elevated Troponin*, *Hypertension*, *Diabetes Mellitus*, *Dyspnea*, *Asthma*).
3. **Evidence Retrieval**: Lexical and semantic scoring of curated clinical trial guidelines and consensus statements stored in MySQL.
4. **Structured Evidence Synthesis**: RAG-driven synthesis linking presenting findings with published medical literature.
5. **Clinical Considerations**: Non-diagnostic considerations outlining evaluation pathways, guideline agreements, and limitations.
6. **Safety & Emergency Warnings**: Prominent emergency notices for acute conditions (e.g., acute myocardial ischemia) and transparent limitations.

## 4. Key Features
- **Interactive Medical Research Dashboard (`/`)**: Real-time statistics from MySQL records, primary workflow diagram, pre-configured synthetic demo scenarios, and recent case history.
- **New Clinical Case Form (`/case/new`)**: Field-level validation, age boundary constraints (0–130), "Prefer not to specify" gender option, input retention, and one-click demo presets.
- **Case Results & Analysis Workspace (`/analysis/:id`)**:
  - Acute cardiac emergency banner for chest pain / elevated troponin.
  - Collapsible Case Overview.
  - Extracted Key Clinical Terms tags.
  - Relevant Medical Literature Cards with relevance score methodology notices.
  - Structured Evidence Summary (findings, agreements, limitations).
  - Decision Support Considerations with guideline pathways.
  - Safety & Simulation Limitations box.
  - Numbered Source References corresponding to retrieved records.
  - Printable report generation (`window.print()`).
- **Medical Literature Library (`/literature`)**: Full repository browsing, keyword search, topic filtering, and record detail inspection modal with APA citation copy.
- **Deterministic Demo Mode**: Fully functional out-of-the-box without requiring external AI API keys.
- **Configurable External LLM**: Server-side support for Google Gemini and OpenAI APIs when configured via environment variables.

## 5. Technology Stack

### Frontend
- **React 19**: Modern UI component library.
- **Vite 8**: Next-generation frontend build tooling.
- **TypeScript**: Strict type definitions for cases, analysis results, and literature.
- **React Router DOM 7**: Client-side SPA routing (`/`, `/case/new`, `/analysis/:id`, `/literature`, `*`).
- **Lucide React**: Clean healthcare and research icons.
- **Custom Design System**: Responsive CSS with medical blue/indigo accents, clean cards, and accessible typography.

### Backend
- **Java 21 LTS**: Modern Java runtime.
- **Spring Boot 3.3.4**: Enterprise REST API framework.
- **Spring Web**: MVC controllers, CORS configuration, exception handling.
- **Spring Data JPA / Hibernate ORM**: Repository pattern and database persistence.
- **Bean Validation (`jakarta.validation`)**: Request input validation.
- **Maven**: Dependency and build management.

### Database
- **MySQL 8.0**: Relational persistence for cases, literature records, analysis results, and evidence links (`medlit_ai`).

---

## 6. Architecture & Primary Workflow

```
   USER (Browser)
        │  Enters synthetic case or selects demo preset
        ▼
   REACT + VITE + TYPESCRIPT FRONTEND (Port 5173)
        │  Typed REST calls (api.ts)
        ▼
   SPRING BOOT REST API (Port 8080)
        │
        ├─► CaseController & AnalysisController (Validates DTOs, manages lifecycle)
        │
        ├─► ClinicalFeatureExtractor
        │    • Normalizes input and identifies key clinical features
        │
        ├─► LiteratureService & RagService
        │    • Searches MySQL literature records
        │    • Computes relevance score based on features & query tokens
        │
        ├─► LlmService (Evidence Synthesis & Decision Support Engine)
        │    • Deterministic Demo Mode Synthesis (default)
        │    • Configurable Gemini / OpenAI Live AI integration
        │    • Associates findings with source identifiers
        │
        └─► MySQL 8.0 Persistence (`medlit_ai`)
             • clinical_cases, literature, analysis_results, evidence_sources
```

---

## 7. Quick Start with Docker (Recommended)

Run the entire application stack (MySQL 8.0, Spring Boot Java 21 backend, and React/Nginx frontend) with a single command:

```bash
docker compose up --build
```

- **Frontend Application:** [http://localhost:5173](http://localhost:5173)
- **Backend REST API:** [http://localhost:8080](http://localhost:8080)
- **Backend Health Check:** [http://localhost:8080/api/health](http://localhost:8080/api/health)
- **MySQL Database:** `localhost:3306` (Database: `medlit_ai`, User: `root`, Password: `root`)

### Useful Docker Commands:
```bash
# Run in background (detached)
docker compose up -d --build

# View container logs
docker compose logs -f

# Check container status
docker compose ps

# Stop containers
docker compose down

# Stop containers and remove database volume
docker compose down -v
```

---

## 8. Frontend Setup (Local / Non-Docker)

### Prerequisites
- Node.js v20+ and npm

### Installation & Run
```bash
cd frontend
npm install
npm run dev
```
The frontend starts on `http://localhost:5173/`.

### Build & Lint
```bash
npm run build   # Production bundle validation (tsc -b && vite build)
npm run lint    # Oxlint code quality verification
```

---

## 9. Backend Setup (Local / Non-Docker)

### Prerequisites
- Java 21 LTS (OpenJDK / Temurin)
- Maven (or bundled `mvnw.cmd` wrapper)
- MySQL 8.0 running on `localhost:3306`

### Installation & Run
```bash
cd backend
.\mvnw.cmd spring-boot:run
```
The backend initializes the database schema, seeds 12 curated guideline records, and listens on `http://localhost:8080`.

---

## 10. MySQL Setup (Local / Non-Docker)

Ensure MySQL is running on `localhost:3306`:
```sql
CREATE DATABASE IF NOT EXISTS medlit_ai;
```

Spring Boot auto-creates the tables on first startup via Hibernate (`spring.jpa.hibernate.ddl-auto=update`):
- `clinical_cases`: Patient presentation, history, vitals, labs.
- `literature`: Peer-reviewed guidelines, authors, year, journal, abstract, topic.
- `analysis_results`: Generated synthesis, considerations JSON, safety limitations.
- `evidence_sources`: Join table recording matched literature and relevance scores.

---

## 11. Environment Configuration

Copy `.env.example` to configure custom database credentials or external AI keys:
```properties
# Server & Database Settings
PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/medlit_ai?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# Simulator Mode (true = deterministic local demo mode)
DEMO_MODE=true

# Optional External LLM Keys (Server-side only — never exposed to client)
GEMINI_API_KEY=
OPENAI_API_KEY=

# Frontend Configuration
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## 12. REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Health status, database status, mode, and record counts |
| `GET` | `/api/status` | System readiness endpoint |
| `POST` | `/api/cases` | Create a new clinical case |
| `GET` | `/api/cases` | List all cases (newest first) |
| `GET` | `/api/cases/{id}` | Retrieve case details by ID |
| `POST` | `/api/cases/{id}/analyze` | Execute feature extraction, RAG retrieval, and synthesis |
| `GET` | `/api/analyses` | List all completed analyses |
| `GET` | `/api/analyses/{id}` | Retrieve analysis result by analysis ID or case ID |
| `GET` | `/api/literature` | List all literature records |
| `GET` | `/api/literature/{id}` | Retrieve specific literature record |
| `POST` | `/api/literature/search` | Search literature by query, topic, and limit |

---

## 13. Demo Mode
The simulator runs out-of-the-box in **Demo Mode** (`DEMO_MODE=true`):
- Uses deterministic evidence synthesis grounded on retrieved MySQL records.
- Provides 3 pre-configured synthetic demo scenarios:
  1. *Demo Case 1*: Acute Chest Discomfort & Elevated Troponin (Cardiovascular / Ischemia).
  2. *Demo Case 2*: Uncontrolled Hypertension & Type 2 Diabetes (Metabolic & Cardiorenal).
  3. *Demo Case 3*: Exacerbating Nocturnal Cough & Wheezing (Respiratory / Asthma).
- Clearly badges all outputs with "Synthetic Educational Simulation" and "Demo Mode Grounding".

---

## 14. Literature Retrieval & RAG Implementation
- **Feature Extraction**: `ClinicalFeatureExtractor` normalizes clinical text and matches symptom clusters.
- **Relevance Scoring**: `LiteratureServiceImpl` calculates relevance scores based on feature matches, topic relevance, and keyword occurrences (calibrated between 50% and 96%).
- **Evidence Context**: `RagServiceImpl` builds structured prompt context containing top-ranked records with full citations and abstracts.
- **Relevance Distinction**: The UI explicitly clarifies that retrieval relevance scores measure text similarity, not the clinical trial hierarchy of evidence (e.g., GRADE).

---

## 15. Testing Instructions

### Run Backend Unit & Integration Tests
```bash
cd backend
.\mvnw.cmd test
```
**Test Results**: 18 tests executed across `ClinicalFeatureExtractorTest`, `LiteratureServiceTest`, `CaseAnalysisServiceTest`, `HealthControllerTest`, `CaseControllerTest`, and `AnalysisControllerTest` with **0 failures, 0 errors**.

### Run Frontend Production Build & Lint
```bash
cd frontend
npm run build
npm run lint
```

---

## 16. Limitations
- Operates on a curated educational dataset of 12 clinical guideline records.
- Keyword and feature-based relevance scoring rather than high-dimensional embedding vectors.
- Educational prototype; does not ingest electronic health records (EHR/FHIR).

---

## 17. Medical Disclaimer
**Educational Research Prototype — This application does not provide a medical diagnosis or prescribe treatment. Its outputs may be incomplete or inaccurate and must not replace professional medical judgment. Do not use it to assess or manage a real patient's condition.**

---

## 18. Future Scope
- Integration with external PubMed / NCBI E-Utilities APIs for live literature retrieval.
- Vector embeddings and semantic search using vector extensions.
- Multi-guideline comparative conflict detection.
- PDF upload and automated parsing for institutional clinical guidelines.
