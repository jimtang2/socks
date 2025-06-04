.PHONY: setup build test run clean reset

GRADLEW := ./gradlew

setup:
	mkdir -p tmp bin
	curl -L -o tmp/gradle-8.14-bin.zip https://services.gradle.org/distributions/gradle-8.14-bin.zip	
	unzip -d bin tmp/gradle-8.14-bin.zip
	mv bin/gradle-8.14 bin/gradlew
	./bin/gradlew/bin/gradle wrapper --gradle-version 8.14

reset:
	rm -rf bin build \
		gradle gradlew gradlew.bat \
		.gradle build.gradle.lockfile

build:
	$(GRADLEW) build

test:
	$(GRADLEW) test --warning-mode all

run:
	$(GRADLEW) bootRun

clean:
	$(GRADLEW) clean

tree:
	tree --gitignore

docker:
	docker build -t socks-builder -f docker/builder.dockerfile docker
	docker build -t socks .
	docker run --rm -p 8000:8000 socks

