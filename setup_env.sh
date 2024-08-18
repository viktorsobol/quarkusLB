java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ "$java_version" > "17" ]]; then
    echo "Java version is greater than 17"
else
    echo "Java version is not greater than 17"
    exit 1

fi

if command -v python3 &>/dev/null; then
    echo "Python is installed"
else
    echo "Python is not installed"
    exit 1

fi

if command -v docker-compose &>/dev/null; then
    echo "docker-compose is installed"
else
    echo "docker-compose is not installed"
    exit 1
fi


cd ./LoadBalancer
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/loadbalancer-jvm .
cd ..

cd ./demo
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/demo-jvm .
cd ..

docker-compose up -d 



