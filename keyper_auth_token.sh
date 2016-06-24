#!/bin/bash

FIRST_NAME="$1"
LAST_NAME="$2"
EMAIL="$3"
PASSWORD="$4"

function display_usage {
    echo "Usage: $0 [first_name] [last_name] [email] [password]"
    echo ""
    echo "Example:"
    echo "$0 Jon Snow jon.snow@keyper.ws supersafepassword"
    echo ""
}

function print_auth_token {
    temp=`echo $json | sed 's/\\\\\//\//g' | sed 's/[{}]//g' | awk -v k="text" '{n=split($0,a,","); for (i=1; i<=n; i++) print a[i]}' | sed 's/\"\:\"/\|/g' | sed 's/[\,]/ /g' | sed 's/\"//g' | grep -w "auth_token"`
    echo "Keyper Auth Token: "
    echo ${temp##*|}
}

if [ -z "$FIRST_NAME" ]; then
    display_usage
    exit 1
fi

if [ -z "$LAST_NAME" ]; then
    display_usage
    exit 1
fi

if [ -z "$EMAIL" ]; then
    display_usage
    exit 1
fi

if [ -z "$PASSWORD" ]; then
    display_usage
    exit 1
fi

json=$(curl -s -X POST http://sandbox.api.keyper.io/api/users \
-H 'Content-Type: application/json' \
-H 'Authorization: APPSECRET app_secret="test_w5v283yeal"' \
-H 'App-Language: en, de-de;q=0.8' \
-d \
'
{
    "first_name" : "'$FIRST_NAME'",
    "last_name": "'$LAST_NAME'",
    "email": "'$EMAIL'",
    "password": "'$PASSWORD'"
}
')

print_auth_token