# crokage
Crowd Knowledge Answer Generator

## Goal
To generate answers to API related questions.

## Input / Output
- Input: API related query in natural language
- Output: A brief explanation about the API usage (Java APIs) with examples in form of code snippets.

## How
Crokage receives as input an API related query in natural language. This query is inputted into two state-of-art API recommender tools which return a set of APIs related to this query. Crokage then uses these set of APIs to recover SO posts where they appear. The posts are sorted by relevance. The code of the top-k posts are then extracted as snippets into a answer template. Together with the code, Crokage builds a breaf explanation of the APIs being used. 

## Features and comparison with other state-of-art works (Answerbot and Biker)
- Answerbot is limited as it does not provide code
- Biker is limited as it does not provide comments to the codes, only javadoc
- Crokage address both limitations by providing relevant code and comments to these codes. 
