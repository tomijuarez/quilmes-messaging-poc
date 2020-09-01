module.exports.processEvent = function (context, req) {
  const subscriptionValidationEvent = "Microsoft.EventGrid.SubscriptionValidationEvent";
  const employeeEventType = "Quilmes.Employee.New";
  const parsedReq = JSON.parse(req['rawBody']);

  parsedReq.forEach(rawEvent => {
      const eventPayload = rawEvent.data;

      context.log(rawEvent);

      if (rawEvent.eventType == subscriptionValidationEvent) {
          context.res = {
              status: 200,
              body: { "ValidationResponse": eventPayload.validationCode },
              headers: { 'Content-Type': 'application/json' }
          };
      } else if (rawEvent.eventType == employeeEventType) {
          context.log(`Event ${eventPayload.eventId} received and processed :)`);
          context.res = {
              status: 200,
              body: `Event ${eventPayload.eventId} received and processed :)`,
              headers: { 'Content-Type': 'application/json' }
          };
      } else {
          context.log('>:v');
          context.res = {
              status: 400,
              body: { message: 'nel' },
              headers: { 'Content-Type': 'application/json' }
          };
      }
  });

  context.done();
};