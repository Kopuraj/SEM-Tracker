pipeline {
    agent any

    environment {
        BUILD_TAG = "${env.BUILD_NUMBER}"
        COMPOSE_PROJECT_NAME = "sem-tracker"
    }

    stages {
        stage('Cleanup Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout Code') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/Kopuraj/SEM-Tracker.git'
            }
        }

        stage('Build Frontend with Docker') {
            agent {
                docker {
                    image 'node:20-alpine'
                    args '-v $HOME/.npm:/root/.npm'
                }
            }
            steps {
                dir('SEM_full2/frontend_SEM_track') {
                    sh '''
                        echo "Installing frontend dependencies..."
                        npm ci --silent
                        echo "Building frontend..."
                        npm run build
                    '''
                }
            }
        }

        stage('Build Backend with Docker') {
            agent {
                docker {
                    image 'maven:3.8-openjdk-17'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                dir('SEM_full2/sem-tracker') {
                    sh '''
                        echo "Building backend with Maven..."
                        mvn clean package -DskipTests
                    '''
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                dir('SEM_full2') {
                    sh '''
                        echo "Building Docker images using Docker Compose..."
                        docker compose build --no-cache
                    '''
                }
            }
        }

        stage('Stop Old Containers') {
            steps {
                dir('SEM_full2') {
                    sh '''
                        echo "Stopping and removing old containers..."
                        docker compose down -v || true
                    '''
                }
            }
        }

        stage('Deploy Application') {
            steps {
                dir('SEM_full2') {
                    sh '''
                        echo "Starting containers with Docker Compose..."
                        docker compose up -d
                        echo "Waiting for services to be healthy..."
                        sleep 15
                        docker compose ps
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    echo "Checking running containers..."
                    docker ps --filter "name=sem"
                '''
            }
        }
    }

    post {
        always {
            echo '--- Pipeline Execution Finished ---'
        }
        success {
            echo '✅ SEM Tracker deployed successfully!'
            echo 'Frontend: http://localhost:5173'
            echo 'Backend: http://localhost:8081'
            echo 'MySQL: localhost:3306'
        }
        failure {
            echo '❌ Pipeline failed. Check logs above.'
            sh 'docker compose -f SEM_full2/docker-compose.yml logs || true'
        }
    }
}
