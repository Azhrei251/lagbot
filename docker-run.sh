docker stop attendance-checker
docker rm attendance-checker
docker run -d --name attendance-checker --restart always attendance-checker
