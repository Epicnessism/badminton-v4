# Badminton Stringing Tracker UI

Angular frontend for the Badminton Stringing Tracker application.

## Development

### Prerequisites
- Node.js 18+
- npm

### Setup
```bash
cd ui
npm install
```

### Run locally
```bash
npm start
```
Navigate to `http://localhost:4200/`. The app will automatically reload on file changes.

### Build for production
```bash
npm run build
```
Build artifacts are stored in `dist/badminton-ui/`.

## Deployment with AWS Amplify

1. Connect your GitHub repository to AWS Amplify
2. Set the app root to `ui`
3. Amplify will auto-detect Angular and configure build settings
4. Environment variables are handled via `environment.prod.ts`

## API Configuration

- **Development**: `http://localhost:8080`
- **Production**: Configured in `src/environments/environment.prod.ts`
