MERGE (license:DocumentType {id:1, name: "License"})

MERGE (doc1:Document {id:1, title: "A License"})
MERGE (doc1)-[:HAS_TYPE]->(license)

MERGE (finance:Department {id:1, name: "Finance"})
MERGE (finance)-[:PUBLISHES]->(license)

MERGE (hof:Position {id:1, name: "Head of Finance"})
MERGE (accountant:Position {id:2, name: "Accountant"})
MERGE (hof)-[:MANAGES]->(finance)

MERGE (usr1:User {id:1, name: "Schmidt", country: "USA", years_of_experience: 2})
MERGE (usr2:User {id:2, name: "Jeff", country: "Mexico", years_of_experience: 7})

MERGE (usr1)-[:MEMBER_OF]->(finance)
MERGE (usr2)-[:MEMBER_OF]->(finance)

MERGE (usr1)-[:HAS_POSITION]->(accountant)
MERGE (usr2)-[:HAS_POSITION]->(accountant)
MERGE (usr2)-[:HAS_POSITION]->(hof)

MERGE (usr2)-[:MANAGES]->(usr1)
MERGE (usr2)-[:AUTHORIZED]->(doc1)
