# GCP Pub/Sub 

## Usage

### Setup Gcp Security
To access the GCP Pub/Sub Playground, 
you need to have a GCP account and a project with the Pub/Sub API enabled. 
You can create a new project or use an existing one.

In order to access the GCP project a service account is required. 

The location of the service account key file is required to be set in the [reference.conf](common/src/main/resources/reference.conf) file.

### Create a Pub/Sub topic and subscription
Note that the topic and subscription must already exist in GCP and are hardcoded in the [Publish](publish/src/main/java/net/martinprobson/example/Publish.java) 
and [Subscribe](subscribe/src/main/java/net/martinprobson/example/Subscribe.java) classes.
### Run the application

To run the application, execute the following command:

```shell
./gradlew run
```

This will publish a message to the Pub/Sub topic and then pull the message from the subscription.


## ToDo 
* Add code to automatically create the topic and subscription if they do not exist.
