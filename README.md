# Natural Language Personal Assistant

### Authors

- __Gabriel Boroghina__, SSA
- __Mihaela Catrina__, SSA

### Description

The application exposes some virtual assistance functionalities based on a conversational agent REST API that can
receive a natural language utterance (in Romanian), do some NLP processing, execute some actions in response to the
user's request and return a text reply.

There are 2 flows implemented in the project:

1. Linking image assets to some natural language descriptions and getting those assets when needed using natural
   language request as well.

_E.g. of interactions_:

Link an image as asset:

```
- [User] "Tema la chimie este urmatoarea:"
- [Agent] (Displays the action button to link a photo)
- [User] (Takes a picture of the homework)
- [Agent] "Retinut" (Links the picture to the description in a knowledge graph inside the Conversational Agent)
```

Retrieve the asset:

```
- [User] "Care era tema la chimie?"
- [Agent] (Parses the sentence and detects the intent and the entity - returns the UUIDs of the linked images)
- [User] (Navigates the grid of linked assets - RecyclerView)
```

2. Getting local transportation (Bucharest STB) indications for reaching a destination from some known preferred
   locations (e.g. home - the only one implemented for the moment)

_E.g. of interactions_:

```
- [User] "Cum ajung la parcul titan?" / "As dori sa merg la piata presei libere"
- [Agent] (Detects the intent - transportation - and extracts the destination)
- [Agent] (Calls the STB APIs - one for finding exact places for the provided destination and one for finding routes -
  and shows the list of possible routes in a vertical RecyclerView list containing an inner RecyclerView for the route's segments)
```

For storing the preferred locations, we are using a __Room__ database, although for the moment only one such location is
introduced (at the application startup - there isn't a form for configuring preferred locations yet)