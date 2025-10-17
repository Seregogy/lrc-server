.PHONY: build docker-build-image docker-push run up

build:
	gradlew.bat shadowjar

docker-build-image: build
	docker build --no-cache -t reptiloidd/lrc-server .

docker-push: docker-build-image
	docker push reptiloidd/lrc-server

run: docker-build-image
	docker run -p 8080:8080 lrc-server

up: build docker-build-image docker-push run