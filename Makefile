## help: Prints this help message
help:
	@echo "Usage: \n"
	@sed -n 's/^##//p' ${MAKEFILE_LIST} | column -t -s ':' |  sed -e 's/^/ /'

## clean: Clean the files and directories generated during build
clean:
	cd app/movies-stats && ./gradlew clean

## test: Run the tests
test: clean
	cd app/movies-stats && ./gradlew test

## package: Build and package the source code into an uber-jar
package: test
	cd app/movies-stats && \
	./gradlew :functions:add-movies:build && \
	./gradlew :functions:add-stat:build && \
	./gradlew :functions:get-movie-and-stat:build