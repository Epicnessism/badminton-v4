# Badminton Stringing Tracker

Spring Boot API with Angular UI for tracking badminton racket stringing jobs.

## Project Structure

```
badminton-v4/
├── src/                          # Spring Boot backend
├── ui/                           # Angular frontend
├── docker-compose-dev-local.yml            # LocalStack for local DynamoDB
├── localstack-init/              # DynamoDB table init scripts
└── amplify.yml                   # AWS Amplify build config
```

## Local Development

### Prerequisites
- Java 21
- Node.js 18+
- Docker & Docker Compose

### 1. Start LocalStack (DynamoDB)

```bash
docker-compose -f docker-compose-dev-local.yml up
```

This starts LocalStack with DynamoDB on port 4566 and automatically creates the `badmintonDb` table with the `name-index` GSI.

### 2. Run the Backend API

In IntelliJ:
1. Edit Run Configuration
2. Set **Active profiles** to `dev-local`
3. Run the application

Or via command line:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev-local
```

The API will be available at `http://localhost:8080`

### 3. Run the Angular UI

```bash
cd ui
npm install
npm start
```

The UI will be available at `http://localhost:4200`

### Verify Setup

```bash
# Check LocalStack health
curl http://localhost:4566/_localstack/health

# List DynamoDB tables
aws --endpoint-url=http://localhost:4566 dynamodb list-tables --region us-east-2

# Test API
curl http://localhost:8080/user
```

## AWS Deployment

### Backend (Lambda + API Gateway)
Deploy using SAM or your existing deployment pipeline.

### Frontend (AWS Amplify)
1. Connect GitHub repo to AWS Amplify
2. Amplify auto-detects `amplify.yml` in the root
3. The build config points to the `ui/` subfolder
4. Push to deploy

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/user` | Create a user |
| GET | `/user/{userId}` | Get a user |
| POST | `/stringing` | Create a stringing |
| GET | `/stringing/{id}` | Get a stringing |
| PUT | `/stringing/{id}` | Update a stringing |
| GET | `/stringing/stringer/{userId}` | Get stringings by stringer |
| GET | `/stringing/owner/{userId}` | Get stringings by owner |
