# quilmes-messaging-poc
Ab-inbev messaging solutions proposal

# Installation and configuration
First off, you should install Azure CLI, serverless, JDK, Maven and terraform locally:
 - [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest)
 - [Serverless Framework](https://www.serverless.com/framework/docs/providers/aws/guide/installation/)
 - [Terraform](https://learn.hashicorp.com/tutorials/terraform/install-cli)
 - [JDK](https://www.oracle.com/java/technologies/javase-downloads.html)
 - [Maven](https://maven.apache.org/install.html)

Assuming you have permissions, login into Azure using your credentials simply running `az login`.

Once you have done all previous steps, go ahead and deploy the azure functions that will act as webhooks to process Event Grid events:

```
$ cd webhooks/
$ sls plugin install --name serverless-azure-functions  # install serverless plugin for azure functions
$ sls deploy                                            # publish functions
```

Wait a few minutes and you should see your endpoints URLs in the console; copy these endpoint because you'll need the reference later on The look like the following lines:

```
Serverless: Deployed serverless functions:
Serverless: -> always-succeeds: [POST] sls-...azurewebsites.net/api/always-succeeds
Serverless: -> sometimes-fails: [POST] sls-...azurewebsites.net/api/sometimes-fails
```

Now go to `infrastructure/` folder and open up `main.tf` and update the lines <always-succeeds-url> and <sometimes-fails-url> using Serlverless' output as shown above. Then create the resources typing

```
$ cd ../infrastructure
$ terraform init
$ terraform apply
```

Then wait a couple of minutes until and copy the output value of `"sas_url_query_string"`, which looks like `sv=...&ss=q&srt=o&sp=####&se=2020-12-25&st=2020-08-30&spr=https&sig=...%.....%3D`. Copy that string and change Spring's application configuration. Go to `services/src/main/resources/`, open up `applications.properties` and then replace `<sas_token>` with the previous output.

Now you should generate the jar file to publish/consume messages. Go to `services` root folder and type

```
$ mvn clean package
$ cd ./target
$ java -jar messaging-poc-0.0.1-SNAPSHOT.jar
```

You're all set. You can use the exposed resources to publish messages.

# Services API

| HTTP Method        | Resource           | Expected payload  | Description                    |
| ------------------ |:------------------:| -----------------:| :-----------------------------:|
| `/hook/sb/message` | right-aligned      | `MessageSchema`   | Service bus message producer   |
| `/hook/sq/message` | right-aligned      | `MessageSchema`   | Storage queue message producer |
| `/hook/eg/event`   | centered           | `EventSchema`     | Event grid event publisher     |

# Model

## MessageSchema:
```
{
    "messageId": String,
    "employeeId": String,
    "employeeType": enum ["DIRECT" | "OUTSOURCE"]
}
```
## EventSchema:
```
{
    "eventId": String,
    "employeeId": String,
    "employeeType": enum ["DIRECT" | "OUTSOURCE"]
}
```