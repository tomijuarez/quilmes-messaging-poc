provider "azurerm" {
  version = "=2.20.0"
  features {}
}

# Grupo de recursos
resource "azurerm_resource_group" "messaging_poc" {
  name     = "messaging-poc"
  location = "West US 2"
}

# Crear topico de event grid
resource "azurerm_eventgrid_topic" "events_sample" {
  name                = "events-sample"
  location            = azurerm_resource_group.messaging_poc.location
  resource_group_name = azurerm_resource_group.messaging_poc.name
}

resource "azurerm_eventgrid_event_subscription" "always_succeeds_subscription" {
  name  = "alwaysSucceedsSubscription"
  scope = azurerm_eventgrid_topic.events_sample.id
  event_delivery_schema = "EventGridSchema"
  webhook_endpoint {
      url="<always-succeeds-url>"
  }
}

resource "azurerm_eventgrid_event_subscription" "sometimes_fails_subsciption" {
  name  = "sometimesFailsSubscription"
  scope = azurerm_eventgrid_topic.events_sample.id
  event_delivery_schema = "EventGridSchema"
  webhook_endpoint {
      url="<sometimes-fails-url>"
  }
}

# Crear storage queue
resource "azurerm_storage_account" "storage_account" {
  name                     = "storageaccountquilmes10"
  resource_group_name      = azurerm_resource_group.messaging_poc.name
  location                 = azurerm_resource_group.messaging_poc.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

resource "azurerm_storage_queue" "messages_sample" {
  name                 = "messages-sample-sq"
  storage_account_name = azurerm_storage_account.storage_account.name
}

data "azurerm_storage_account_sas" "messages_sample" {
  connection_string = azurerm_storage_account.storage_account.primary_connection_string

  resource_types {
    service   = false
    container = false
    object    = true
  }

  services {
    blob  = false
    queue = true
    table = false
    file  = false
  }

  start  = "2020-08-30"
  expiry = "2020-12-25"

  permissions {
    read    = true
    write   = true
    delete  = true
    list    = true
    add     = true
    create  = true
    update  = true
    process = true
  }
}

# Crear service bus queue (basic tier)

resource "azurerm_servicebus_namespace" "basic_subscription" {
  name                = "sb-basic-subscription"
  location            = azurerm_resource_group.messaging_poc.location
  resource_group_name = azurerm_resource_group.messaging_poc.name
  sku                 = "Basic"
}

resource "azurerm_servicebus_queue" "messages_sample" {
  name                = "messages-sample-sb"
  resource_group_name = azurerm_resource_group.messaging_poc.name
  namespace_name      = azurerm_servicebus_namespace.basic_subscription.name
}

# outputs

output "sas_url_query_string" {
  value = data.azurerm_storage_account_sas.messages_sample.sas
}