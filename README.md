# Bitcoin rates

### Build and run all tests
```
./gradlew[.bat] clean build
```

### Build fat jar
```
./gradlew[.bat] clean shadowJar
```

### Build docker image
```
docker build -t user/bitcoin .
```

### Run docker image
```
docker run -it user/bitcoin .
```