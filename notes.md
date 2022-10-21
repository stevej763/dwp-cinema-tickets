# Task notes

## Key Thoughts

- First check the accountId is valid. must be > 0 and not null
- Need to check the total number of tickets, each TicketTypeRequest can have multiple tickets so need to add up the total from all of these - throw if > 20
- There must be at least one adult ticket in each order, as child and infant tickets cannot be purchased without - throw if no adult ticket
- Check there is at least one ticket in the request - throw if not
- Infants sit on adults laps, I interpret this to mean there must be one adult for every infant. However, based on the wording of the business rule this is something I would want to verify with a stakeholder/BA
- Only Adults and Children tickets should be counted towards a seat reservation, Infants do not.
- In the context of the domain a seat reservation is different from a ticket, so I will still count infant tickets towards the maximum order of 20, but not include them in the seat reservation request - this criteria would also need verifying with stakeholders
- Payment and booking code does not need to be implemented. This avoids needing to be concerned about any potential race conditions for this task
- Storing ticket prices: They could be static values, or I could simulate them being received from an external resource or DB. For this task I am going to assume they are static as the business rules define the prices and there is no mention of them changing.