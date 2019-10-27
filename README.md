# vape-sso-authentication-service

# Setup

1. Install Maven [here](https://maven.apache.org/install.html)       
2. navigate to project root and run `mvn clean install`     

# Mongo setup
> If wish to use local mongo instance, configure in `application.yml` file.       
> By default, this servie connects to a sandbox Mongo instance hosted on AWS. So make sure you have internet connection. alternatiely, use your local instance.     
> Access [MongoDB Web GUI here](https://cloud.mongodb.com/v2/5db502f6553855be5261c291#clusters  )    
> Note: Sandbox instance is currently on AWS, N. Virginia (us-east-1) server    


# Swagger
Copy and paste to swagger editor for easier view and edit. [here](http://editor.swagger.io])


# JWT

SSO is enabled by JSON Web Token (JWT), which this service generates for end users      

Decode JWT [here](https://jwt.io)       
And use the `secret-key` found in application.yml


# TODO
1. Add Junify unit test
2. Add PMD plugin
3. Add CMD plugin
