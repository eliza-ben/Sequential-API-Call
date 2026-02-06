Run it
mvn spring-boot:run


Test:

curl -X POST "http://localhost:8080/flow/run" \
  -F "file=@/path/to/file.xlsx"

Step 1 curl
curl -X POST "https://sandboxauth.oncentrl.net/oauth/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Accept: application/json" \
  -H "Cache-Control: no-cache" \
  --data-urlencode "grant_type=password" \
  --data-urlencode "client_id=YOUR_CLIENT_ID" \
  --data-urlencode "client_secret=YOUR_CLIENT_SECRET" \
  --data-urlencode "username=YOUR_USERNAME" \
  --data-urlencode "password=YOUR_PASSWORD"
