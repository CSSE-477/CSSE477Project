#!/bin/bash

response=$(curl --write-out %{http_code} --silent localhost:8080)
status_code=$(echo "$response" | sed -n '$p')
case "$status_code" in
	200) echo 'curl success!' \
		 && rm -rf /home/csse/tmp
		 ;;
	  *) echo 'curl failed!' \
	  	 && rm -rf /home/csse/YoloSwagSWS \
	  	 && mv /home/csse/tmp/YoloSwagSWS /home/csse \
	  	 && rm -rf /home/csse/tmp \
	  	 && service webserver stop \
	  	 && service webserver start \
	  	 && echo 'rolled back to previous version'
	  	 exit 1
	  	 ;;
esac
