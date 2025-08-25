# TextRover 🚀

A comprehensive full-stack text analysis application that counts vowels and consonants in text, featuring both online and offline modes with persistent history.

## 🌟 Features

- **Dual Mode Analysis**: Online (REST API) and offline (client-side) text processing
- **Vowel & Consonant Counting**: Supports extended character sets including accented letters
- **Persistent History**: PostgreSQL-backed analysis history with pagination
- **Modern UI**: Angular 17 with Angular Material and responsive design
- **Comprehensive Statistics**: Word count, character breakdown, percentages, and frequency analysis
- **International Support**: Extended vowels (á, é, í, ó, ú, etc.) and consonants (ñ, ç, etc.)
- **Docker Support**: Complete containerization with Docker Compose
- **Schema-First Development**: OpenAPI specification with automatic DTO generation
- **Comprehensive Testing**: 100% test coverage for critical components

## 🏗️ Architecture

### Technology Stack
- **Frontend**: Angular 17, Angular Material, TypeScript, Tailwind CSS
- **Backend**: Spring Boot 3, Java 17, Hibernate/JPA
- **Database**: PostgreSQL 15 with Liquibase migrations
- **Testing**: Jasmine/Karma (Frontend), JUnit 5 (Backend)
- **Build Tools**: Maven, npm, OpenAPI Generator
- **Containerization**: Docker, Docker Compose

### Project Structure
```
text-rover/
├── frontend/                    # Angular 17 Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── text-analysis/   # Main analysis component
│   │   │   ├── services/        # Business logic services
│   │   │   ├── models/          # TypeScript interfaces
│   │   │   ├── generated/       # Auto-generated OpenAPI DTOs
│   │   │   └── interceptors/    # HTTP interceptors
│   │   └── test.ts              # Test configuration
│   ├── package.json             # Dependencies and scripts
│   ├── karma.conf.js           # Test runner configuration
│   └── Dockerfile              # Frontend container
├── backend/                     # Spring Boot 3 Application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/textrover/
│   │   │   │   ├── controller/  # REST controllers
│   │   │   │   ├── service/     # Business logic
│   │   │   │   ├── entity/      # JPA entities
│   │   │   │   ├── repository/  # Data access layer
│   │   │   │   └── dto/         # Data transfer objects
│   │   │   └── resources/
│   │   │       ├── api/         # OpenAPI specification
│   │   │       └── db/          # Liquibase migrations
│   │   └── test/               # Comprehensive test suite
│   ├── pom.xml                 # Maven configuration
│   └── Dockerfile              # Backend container
├── database/
│   └── init.sql                # Database initialization
├── docker-compose.yml          # Multi-service orchestration
└── README.md                   # This file
```

## 🚀 Quick Start

### Prerequisites
- **Node.js** 18+ and npm
- **Java** 17+
- **Maven** 3.6+
- **Docker** and Docker Compose (optional)

### Option 1: Docker Compose (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd text-rover

# Start all services
docker-compose up -d

# Access the application
open http://localhost:4200
```

### Option 2: Manual Setup

1. **Start PostgreSQL Database**
```bash
docker-compose up -d postgres
```

2. **Start Backend Service**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

3. **Start Frontend Application**
```bash
cd frontend
npm install
npm start
```

4. **Access Application**
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

## 📖 Usage Guide

### Text Analysis Modes

**Online Mode** (Default)
- Uses REST API backend for processing
- Results are automatically saved to database
- Supports all analysis features

**Offline Mode**
- Client-side processing using TypeScript
- No network requests required
- Results stored locally in browser

### Analysis Features

1. **Character Counting**
   - Vowels: a, e, i, o, u (including accented variants)
   - Consonants: All other letters (including ñ, ç, etc.)

2. **Comprehensive Statistics**
   - Total letters, vowels, consonants
   - Digits and symbols count
   - Word count and percentages
   - Most frequent character analysis

3. **History Management**
   - Paginated history view (20 items per page)
   - Load more functionality
   - Clear local history vs. delete database history
   - Persistent storage across sessions

## 🧪 Testing

### Frontend Tests (34 tests)
```bash
cd frontend

# Run tests
npm test

# Run tests with coverage
npm test -- --code-coverage

# Coverage results in coverage/text-rover/
```

**Coverage Results:**
- Statements: 49.36% (78/158)
- Branches: 76.92% (30/39)
- Functions: 31.57% (12/38)
- Lines: 50.33% (76/151)

### Backend Tests
```bash
cd backend

# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

### Test Categories
- **Unit Tests**: Service and component logic
- **Integration Tests**: API endpoints and database operations
- **Performance Tests**: Large dataset processing
- **Accessibility Tests**: ARIA labels and keyboard navigation
- **Edge Case Tests**: Empty inputs, special characters, validation

## 🔧 Development

### Code Generation
The project uses schema-first development with OpenAPI:

```bash
# Generate frontend DTOs
cd frontend
npm run generate-api

# Backend DTOs are auto-generated during Maven build
cd backend
mvn clean compile
```

### Key Files Explained

**`test.ts`**: Angular test configuration file that:
- Initializes the Angular testing environment
- Imports all test specification files
- Configures Jasmine and Karma test runners
- Replaces webpack context for better compatibility

**`frontend.iml`** (removed): IntelliJ IDEA module file - not needed for the project

### Database Schema
```sql
-- Analysis results table
CREATE TABLE analysis_results (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Character counts table
CREATE TABLE analysis_character_counts (
    id BIGSERIAL PRIMARY KEY,
    analysis_result_id BIGINT REFERENCES analysis_results(id),
    character VARCHAR(1) NOT NULL,
    count INTEGER NOT NULL
);
```

## 🌐 API Reference

### Endpoints

**POST /api/analyze**
```json
{
  "type": "VOWELS|CONSONANTS",
  "text": "Hello World"
}
```

**GET /api/history?page=0&size=20**
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 5,
  "number": 0,
  "size": 20
}
```

**DELETE /api/history**
- Deletes all analysis history from database

## 🐳 Docker Configuration

### Services
- **Frontend**: Nginx-served Angular app (port 4200)
- **Backend**: Spring Boot application (port 8080)
- **Database**: PostgreSQL 15 (port 5432)

### Environment Variables
```env
# Database
POSTGRES_DB=textrover
POSTGRES_USER=textrover
POSTGRES_PASSWORD=password

# Backend
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/textrover
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Run tests: `npm test` (frontend) and `mvn test` (backend)
4. Commit changes: `git commit -m 'Add amazing feature'`
5. Push to branch: `git push origin feature/amazing-feature`
6. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔮 Future Enhancements

- [ ] Real-time text analysis as you type
- [ ] Export analysis results to CSV/PDF
- [ ] User authentication and personal history
- [ ] Advanced text statistics (readability scores, etc.)
- [ ] Multi-language support with i18n
- [ ] Dark/light theme toggle
- [ ] Text analysis API rate limiting
- [ ] Batch file processing
- [ ] Analysis result sharing via URLs

---

**Built with ❤️ using Angular 17 and Spring Boot 3**
