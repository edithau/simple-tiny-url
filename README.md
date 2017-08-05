# SimpleTinyURL

SimpleTinyURL is a SIMPLE url shortener JSON service.  I created it to understand how to implement a url shortener and to refresh my Java memory.  To create (encode) an url, try this:

`curl -X POST 'http://localhost:8080/simple?url=www.google.com' `

To get redirected (decode) to the url referenced by the tiny url, try this:

`curl -X GET  http://localhost:8080/simple/ChN7N1z`


### Features
  - write thru in memory cache; backed by a mysql database
  - stateless service; horizontally scallable, no locking mechanism is needed at the service level
  - when a collision is detected, resolve it by appending a prefix on the request URL 
  - MD5 encoding with 7 chars to support 3.5 billion addresses
  - simple JSON API on Tomcat

### To Run
You will need:
- JDK 1.8
- maven
- access to a mysql database 

1. configure database properties (in DataSourceFactory)
2. `mvn clean package; sh target/bin/webapp` at the repo root to start the service

### TO-Do
- move configurations to a properties file
- validate requests



License
----
MIT

**Free Software, Hell Yeah!**
