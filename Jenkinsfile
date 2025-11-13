pipeline {
    agent any

    environment {
        COMPOSE_PROJECT_NAME = "sem-tracker"
        DOCKER_HUB_REPO = "sem_track_hub"
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
                        echo "Stopping and removing old containers..."
                        docker compose down -v || true
                        
                        echo "Force removing any leftover containers..."
                        docker rm -f mysql_container || true
                        docker rm -f backend_container_new || true
                        docker rm -f frontend_container_new || true
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

        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh '''
                            echo "Logging in to Docker Hub..."
                            echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin
                            
                            echo "Tagging images..."
                            docker tag backend_new:latest ${DOCKER_USER}/${DOCKER_HUB_REPO}:backend
                            docker tag frontend_new:latest ${DOCKER_USER}/${DOCKER_HUB_REPO}:frontend
                            docker tag backend_new:latest ${DOCKER_USER}/${DOCKER_HUB_REPO}:backend-${BUILD_NUMBER}
                            docker tag frontend_new:latest ${DOCKER_USER}/${DOCKER_HUB_REPO}:frontend-${BUILD_NUMBER}
                            
                            echo "Pushing images to Docker Hub..."
                            docker push ${DOCKER_USER}/${DOCKER_HUB_REPO}:backend
                            docker push ${DOCKER_USER}/${DOCKER_HUB_REPO}:frontend
                            docker push ${DOCKER_USER}/${DOCKER_HUB_REPO}:backend-${BUILD_NUMBER}
                            docker push ${DOCKER_USER}/${DOCKER_HUB_REPO}:frontend-${BUILD_NUMBER}
                            
                            echo "✅ Docker Hub push completed!"
                        '''
                    }
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
            echo '========================================='
            echo 'Access your application at:'
            echo '  Frontend: http://localhost:5173'
            echo '  Backend:  http://localhost:8081'
            echo '  MySQL:    localhost:3306'
            echo ''
            echo '✅ Docker images pushed to Docker Hub!'
            echo '  Repository: ${DOCKER_HUB_REPO}'
            echo '========================================='
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
