!#/bin/sh
url='http://13.58.9.52:8080/'
attempts=1
timeout=5
online=false

echo "Checking status of $url."

while [ $attempts -gt 0 ];
do
  code=`curl -sL -u Admin:AssignmentS --connect-timeout 20 --max-time 30 -w "%{http_code}\\n" "$url" -o /dev/null`

  echo "Found code $code for $url."

  if [ "$code" = "200" ]; then
    echo "Website $url is online."
    online=true
    break
  else
    echo "Website $url seems to be offline. Waiting $timeout seconds."
    sleep $timeout
  fi
done

if $online; then
  echo "Monitor finished, website is online."
  exit 0
else
  echo "Monitor failed, website seems to be down."
  exit 1
fi
