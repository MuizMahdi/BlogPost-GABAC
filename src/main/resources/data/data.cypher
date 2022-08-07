MERGE (hr:Department {id:1, name: "HR"})
MERGE (sales:Department {id:2, name: "Sales"})
MERGE (finance:Department {id:3, name: "Finance"})

MERGE (usr1:User {id:1, name: "Schmidt", country: "USA", years_of_experience: 2})
MERGE (usr2:User {id:2, name: "Jenko", country: "USA", years_of_experience: 3})
MERGE (usr3:User {id:3, name: "Jeff", country: "Mexico", years_of_experience: 7})
MERGE (usr4:User {id:4, name: "Eric", country: "USA", years_of_experience: 3})

MERGE (doc1:Document {id:1, title: "A Report"})
MERGE (doc2:Document {id:2, title: "A Contract"})
MERGE (doc3:Document {id:3, title: "A License"})

MERGE (report:DocumentType {id:1, name: "Report"})
MERGE (contract:DocumentType {id:2, name: "Contract"})
MERGE (license:DocumentType {id:3, name: "License"})

MERGE (hof:Position {id:1, name: "Head of Finance"})
MERGE (recruiter:Position {id:2, name: "Recruiter"})
MERGE (salesperson:Position {id:3, name: "Salesperson"})
MERGE (accountant:Position {id:4, name: "Accountant"})

MERGE (usr1)-[:MEMBER_OF]->(hr)
MERGE (usr2)-[:MEMBER_OF]->(sales)
MERGE (usr3)-[:MEMBER_OF]->(finance)
MERGE (usr4)-[:MEMBER_OF]->(finance)

MERGE (usr1)-[:HAS_POSITION]->(recruiter)
MERGE (usr2)-[:HAS_POSITION]->(salesperson)
MERGE (usr3)-[:HAS_POSITION]->(hof)
MERGE (usr3)-[:HAS_POSITION]->(accountant)
MERGE (usr4)-[:HAS_POSITION]->(accountant)

MERGE (sales)-[:PUBLISHES]->(report)
MERGE (hr)-[:PUBLISHES]->(contract)
MERGE (finance)-[:PUBLISHES]->(license)

MERGE (hof)-[:MANAGES]->(finance)
MERGE (usr3)-[:MANAGES]->(usr4)

MERGE (doc1)-[:HAS_TYPE]->(report)
MERGE (doc2)-[:HAS_TYPE]->(contract)
MERGE (doc3)-[:HAS_TYPE]->(license)

MERGE (usr1)-[:AUTHORIZED]->(doc2)
MERGE (usr2)-[:AUTHORIZED]->(doc1)
MERGE (usr3)-[:AUTHORIZED]->(doc3)
