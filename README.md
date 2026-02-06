Run it
mvn spring-boot:run



Test:

curl -X POST "http://localhost:8080/flow/run" \
  -F "file=@/path/to/file.xlsx"
