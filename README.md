# MOOD JOURNAL API
*** 
## PROJECT OVERVIEW
***
#### A backend REST API for mood tracking, which contains CRUD entries for moods, user auth and mood stats over time. Fully dockerized

## TECH STACK
***
1. Java 21
2. Spring Boot 3
3. Spring Security
4. JWT Authentication
5. MySQL(Dockerized)
6. Docker and Docker Compose

## GETTING STARTED
***
1. Clone the repo: git clone <repo-url>
2. Build and run: docker-compose up --build
3. Access API: http://localhost:8080 (this is on your local machine)

## ENVIROMENT VARIABLES
SPRING_DATASOURCE_URL = jdbc:mysql://mysql:3306/moodjournal
SPRING_DATASOURCE_USERNAME = mooduser
SPRING_DATASOURCE_PASSWORD = moodpassword

## API Endpoints

### User Endpoints `/api/v1/users`

| Method | Endpoint    | Description             |
|--------|-------------|-------------------------|
| POST   | `/register` | Register a new user     |
| POST   | `/login`    | Authenticate user login |

### Mood Endpoints `/api/v1/moods`

| Method | Endpoint                  | Description                        |
|--------|----------------------------|------------------------------------|
| POST   | `/`                        | Add a new mood entry               |
| GET    | `/`                        | Get all mood entries for a user    |
| PUT    | `/user/{moodEntryId}`      | Update a mood entry                |
| DELETE | `/id`                      | Delete a mood entry                |
| GET    | `/user`                    | Get mood entry for a specific date |
| GET    | `/entry/{id}`              | Get mood entry by ID               |
| GET    | `/user/summary`            | Get mood summary statistics        |
| GET    | `/user/filter`             | Filter mood entries by mood        |


## MORE DETAILS
*** 
# Ports
1. 8080:8080 -> API
2. 3307:3306 -> Database

## TESTING THE API
Use Postman(Recommended)

## KNOWN ISSUES
1. No email verification
2. No rate limiting



