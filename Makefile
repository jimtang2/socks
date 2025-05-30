.PHONY: setup build test run clean reset

GRADLEW := ./gradlew

setup:
	mkdir -p tmp bin
	curl -L -o tmp/gradle-8.14-bin.zip https://services.gradle.org/distributions/gradle-8.14-bin.zip	
	unzip -d bin tmp/gradle-8.14-bin.zip
	mv bin/gradle-8.14 bin/gradlew
	./bin/gradlew/bin/gradle wrapper --gradle-version 8.14

build:
	$(GRADLEW) build

test:
	$(GRADLEW) test

run:
	$(GRADLEW) bootRun

clean:
	$(GRADLEW) clean

reset:
	rm -rf bin gradle build gradlew.bat .gradle build.gradle.lockfile

tree:
	tree --gitignore