pipeline {
    agent any

    environment {
        COMPOSE_PROJECT_NAME = "sem-tracker"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/Kopuraj/SEM-Tracker.git'
            }
        }

        stage('Stop Old Containers') {
            steps {
                dir('SEM_full2') {
                    sh '''
                        echo "Stopping old containers if any..."
                        docker compose down -v || true
                    '''
                }
            }
        }

        stage('Build and Deploy') {
            steps {
                dir('SEM_full2') {
                    sh '''
                        echo "Building and starting all services with Docker Compose..."
                        docker compose up -d --build
                        
                        echo "Waiting for services to initialize..."
                        sleep 30
                        
                        echo "Checking service status..."
                        docker compose ps
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    echo "=== Running Containers ==="
                    docker ps --filter "name=sem"
                    
                    echo ""
                    echo "=== Backend Logs (Last 20 lines) ==="
                    docker logs backend_container_new --tail 20 || echo "Backend not ready yet"
                    
                    echo ""
                    echo "=== Frontend Logs (Last 20 lines) ==="
                    docker logs frontend_container_new --tail 20 || echo "Frontend not ready yet"
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
            echo ''
            echo 'Access your application at:'
            echo '  Frontend: http://localhost:5173'
            echo '  Backend:  http://localhost:8081'
            echo '  MySQL:    localhost:3306'
        }
        failure {
            echo '❌ Pipeline failed!'
            echo 'Checking logs...'
            sh '''
                docker compose -f SEM_full2/docker-compose.yml logs --tail 50 || true
            '''
        }
    }
}
