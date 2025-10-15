build:
	./gradlew shadowjar

docker-build-image: build
	docker build -t lrc-server .

docker-push: docker-build-image
	docker push reptiloidd/lrc-server:latest

run: docker-build-image
	docker run -p 8080:8080 lrc-server

up: build docker-build-image docker-push run