# SPRINGBOOT OAUTH2 CLIENT (REDDIT) - OAUTH2 WORKSHOP

Springboot minimal configuration in order to interact with reddit API using OAuth2 Authorization grant code flow.

USEFUL DOCUMENTATION                               | LINKS
---                                                | ---
The OAuth 2.0 Authorization Framework              | https://tools.ietf.org/html/rfc6749
Reddit Github API documentation                    | https://github.com/reddit-archive/reddit/wiki/API
Reddit Github OAuth documentation                  | https://github.com/reddit-archive/reddit/wiki/OAuth2
Reddit live API documentation                      | https://www.reddit.com/dev/api
Reddit development subreddit                       | https://www.reddit.com/r/redditdev/

## TECHNOLOGY STACK
COMPONENT                           | TECHNOLOGY              | FOR MORE INFORMATION
---                                 | ---                     |---
Languages & Frameworks              |`spring boot`            | https://spring.io/projects/spring-boot
Java Tools                          |`lombok` `maven`         | https://projectlombok.org/ & https://maven.apache.org/
Security                            |`spring security`        | https://spring.io/projects/spring-security
User Management and Authentication  |`starter-oauth2-client`  | 
HTTP client library                 |`webclient`              | https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html
Libraries                           |`fastjson`               | https://github.com/alibaba/fastjson

## OAUTH2 AUTHORIZATION CODE FLOW
The authorization code grant type flow has the following steps:

 - the application sends the user to the authorization server
    ```
    https://www.reddit.com/api/v1/authorize?client_id=CLIENT_ID&response_type=TYPE&state=RANDOM_STRING&redirect_uri=URI&duration=DURATION&scope=SCOPE_STRING
    ```
 - the user approves the app's request
 - the user is redirected back to the application with an authorization code in the query string
    ```
     http://localhost:8080/reddit?state=STATE&code=CODE
    ```
 - the application exchanges the authorization code for the access token 
    ```
    https://www.reddit.com/api/v1/access_token
   
   with data {
   "grant_type"="authorization_code",
   "code"="CODE",
   "redirect_uri"="URI"
    }
   
   with headers {
   Basic Auth = ("user" = "client_id", "password" = "client_secret")
   Content-type = application/x-www-form-urlencoded
   User-Agent = "<platform>:<appid>:<version>, (by /u/username)'
   }
    ```
   
## PROJECT STRUCTURE
```
*---java
|   *---org
|       *---oauth2workshop
|           |   Oauth2workshopApplication.java
|           |
|           *---configuration (contains @configuration classes - modifies OAuth2 behavior)
|           |       CustomAuth2AccessTokenResponseClientConfiguration.java (authorization request customization)
|           |       CustomAuthorizationRequestResolverConfiguration.java (token response customization)
|           |       CustomOAuth2UserServiceConfiguration.java
|           |       WebClientConfiguration.java
|           |       WebSecurityConfiguration.java
|           |
|           *---controller (contains @controller classes - converts the payload and dispatches to the correct service)
|           |       RedditOAuth2Controller.java
|           |
|           *---model (contains domain models)
|           |       RedditAccessTokenModel.java
|           |
|           *---service (contains @service interfaces - business logic processes - executes http client requests)
|                   RedditOAuth2Service.java 
|
*---resources (contains the application properties)
       application.yml
```

## APPLICATION.YML
```
reddit:
  user-agent:                                                              <-- user-agent is mandatory by reddit API (otherwise error 429)
spring:
  security:
    oauth2:
      client:
        registration:
          reddit:                                                          <-- registration id
            client-id:                                                     <-- client-id provided by reddit
            client-secret:                                                 <-- client-secret provided by reddit
            authorization-grant-type: authorization_code                   <-- oauth2 grant type
            redirect-uri: '{baseUrl}/login/oauth2/code/{registrationId}'   <-- redirect-uri for the callback (must be the same as the one given on reddit)
            scope:                                                         <-- scopes
              - identity
              - read
              - history
        provider:
          reddit:
            authorization-uri: https://www.reddit.com/api/v1/authorize     <-- reddit authorization end-point
            token-uri: https://www.reddit.com/api/v1/access_token          <-- reddit access token end-point
            user-info-uri: https://oauth.reddit.com/api/v1/me              <-- reddit resource end-point
            user-name-attribute: 'name'
```

## EXPLORE REST APIs

METHOD | PATH                   | DESCRIPTION                                            |
-------|------------------------|--------------------------------------------------------|
GET    | /api/v1/prefs          | retrieve user's the preference settings                |
GET    | /api/v1/users/popular  | retrieve popular reddit users                          |
