MERGE (hr:Department {id:1, name: "HR"})
MERGE (sales:Department {id:2, name: "Sales"})
MERGE (finance:Department {id:3, name: "Finance"})

MERGE (contracting:Responsibility {id:1, name: "Contracting"})
MERGE (licensing:Responsibility {id:2, name: "Licensing"})
MERGE (reporting:Responsibility {id:3, name: "Reporting"})

MERGE (hof:Position {id:1, title: "Head of Finance"})

MERGE (usr1:User {id:1, name: "Schmidt", country: "USA", joining_date: date("2015-09-30")})
MERGE (usr2:User {id:2, name: "Jenko", country: "USA", joining_date: date("2019-02-05")})
MERGE (usr3:User {id:3, name: "Jeff", country: "Mexico", joining_date: date("2021-11-22")})

MERGE (doc1:Document {id:1, title: "A Report", type: "Report", creation_date: date("2022-05-03")})
MERGE (doc2:Document {id:2, title: "A Contract", type: "Contract", creation_date: date("2022-06-07")})
MERGE (doc3:Document {id:3, title: "A License", type: "License", creation_date: date("2022-06-20")})

MERGE (usr3)-[:HAS_POSITION]->(hof)
MERGE (usr3)-[:MANAGES]->(usr2)
MERGE (usr3)-[:MANAGES]->(usr1)

MERGE (usr1)-[:AUTHORIZED]->(doc1)
MERGE (usr2)-[:AUTHORIZED]->(doc2)
MERGE (usr3)-[:AUTHORIZED]->(doc3)

MERGE (usr1)-[:MEMBER_OF]->(hr)
MERGE (usr2)-[:MEMBER_OF]->(sales)
MERGE (usr3)-[:MEMBER_OF]->(finance)

MERGE (finance)-[:RESPONSIBLE_FOR]->(licensing)
MERGE (sales)-[:RESPONSIBLE_FOR]->(reporting)
MERGE (hr)-[:RESPONSIBLE_FOR]->(contracting)
