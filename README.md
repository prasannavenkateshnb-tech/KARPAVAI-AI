# KARPAVAI.AI – Intelligent Placement & Interview Preparation Platform

A simple final-year full-stack project using Java 17, Spring Boot, Spring Data JPA, Spring Security, MySQL, HTML5, CSS3 and Vanilla JavaScript.

## Features
- Student registration/login with BCrypt
- Separate admin login and role protection
- Company explorer
- Company preparation page
- Question bank with search
- Admin question CRUD foundation
- Interview experience section
- Student question solving and revision bookmarks
- Persistent MySQL progress
- Rule-based Interview Readiness Engine
- Weakness/progress visualization
- Seed data for 15 companies and representative 10-year-style records
- Responsive modern UI

## Run locally
1. Install Java 17 and MySQL 8+.
2. Create the database:
   `CREATE DATABASE prepforge;`
3. Open `src/main/resources/application.properties`.
4. Replace `YOUR_PASSWORD` with your local MySQL password.
5. From the project directory run:
   `mvn spring-boot:run`
6. Open http://localhost:8080/

## Demo accounts
Admin: admin@prepforge.com / Admin@123
Student: student@prepforge.com / Student@123

## Important data note
Seed records are clearly demo/study records. They are not claims that a particular company genuinely asked those exact questions in a given year. Historical authenticity should be verified and source-backed before being presented as fact.

## Main API
POST /api/auth/register
POST /api/auth/login
GET /api/public/stats
GET /api/companies
GET /api/companies/{id}
POST/PUT/DELETE /api/companies/admin...
GET /api/questions
POST/PUT/DELETE /api/questions/admin...
GET /api/interview-experiences
POST/PUT/DELETE /api/interview-experiences/admin...
GET /api/student/dashboard
GET /api/student/readiness
POST /api/questions/{id}/solve
POST /api/questions/{id}/bookmark
GET /api/student/bookmarks

## Project structure
src/main/java/com/prepforge/
controller, service, repository, entity, security, config, exception

Frontend is in src/main/resources/static.

## Viva flow
Register/login → explore company → practice question → mark solved → readiness updates → bookmark revision → admin manages question data.


## Spring AI + Ollama features

KARPAVAI.AI includes optional Spring AI integration using Spring AI 1.0.9 and a local Ollama model. No external API key is required. The existing rule-based mock-test scoring remains in place as a fallback.

AI features:
- Subjective answer evaluation
- AI mock interviews
- AI-generated practice questions
- Personalized study plans
- AI performance analysis
- Company preparation guidance

### Enable local AI

1. Install Ollama from https://ollama.com/
2. Start Ollama.
3. Download a model, for example:
   `ollama pull llama3.2`
4. Keep Ollama running on its default address `http://localhost:11434`.
5. Start KARPAVAI.AI with:
   `mvn spring-boot:run`

Optional model override:
Windows PowerShell:
`$env:OLLAMA_MODEL="llama3.2"`

Windows CMD:
`set OLLAMA_MODEL=llama3.2`

No API key is required. AI requests stay on your local machine through Ollama. If Ollama is unavailable, the existing KARPAVAI.AI functionality continues to work and the AI endpoints return a temporary-unavailable response.

## JSON reliability fix

This version fixes the Ollama structured-output problem where `BeanOutputConverter` could fail because the local model returned malformed JSON such as a missing `:` after a property name.

The fix includes:
- Ollama JSON mode enabled globally with `spring.ai.ollama.chat.options.format=json`.
- Lower temperature (`0.1`) for more deterministic structured responses.
- Larger context/output limits for study plans and other AI features.
- Stricter JSON-only instructions for all six AI features.
- A single automatic retry with even stricter JSON rules when structured conversion fails.
- Server-side logging for AI conversion/model failures while keeping safe messages in the browser.
- Existing KARPAVAI.AI features and UI are preserved.

### Before starting
Make sure Ollama is running and the model exists:

`ollama list`

If needed:

`ollama pull llama3.2`

Then set your MySQL password in `src/main/resources/application.properties` by replacing `YOUR_PASSWORD`.

Start:

`mvn spring-boot:run`

Open:

`http://localhost:8080/student/ai-lab.html`

The AI buttons should now use Ollama JSON mode for all six AI features.

## AI Answer Evaluation — Corrected

The AI Answer Evaluation feature was strengthened to prevent false or invented feedback.

### Changes
- Added an evidence-first evaluation prompt.
- Correct answers are credited even when grammar is imperfect.
- Correct terminology such as `blueprint/template`, `instance`, `dynamic memory allocation`, and recursion is accepted.
- Weaknesses must be supported by the student's actual answer.
- Missing concepts must be relevant and genuinely absent.
- The evaluator is explicitly forbidden from claiming the student said something they did not say.
- Added a second AI audit pass that checks the first evaluation against the original question and answer.
- Added response normalization: score is clamped to 0–10 and null/blank list entries are removed.
- The UI now displays `None identified` when there are no weaknesses or missing concepts instead of implying that every answer must contain an error.

### Examples fixed
1. `A prime number ... exactly has two factors` is recognized as correct.
2. `A class is a template or blueprint for an object` is recognized as a valid basic definition.
3. `new keyword ... Dynamic memory allocation` is not incorrectly reported as static memory allocation.
4. `object is an instance of class` is credited as the correct core definition; only the misleading `physical form` wording is treated as an issue.

### Note about build verification
The source was updated as a complete project. The execution environment used to prepare this archive does not have Maven installed, so a local `mvn package` build could not be run here. Run the project's normal Maven command on a machine with Maven installed and Ollama configured.
