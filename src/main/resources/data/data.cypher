// DATA
MERGE (dpt1:Department {id:1, name: "HR", responsibilities: ["Contracting"]})
MERGE (dpt2:Department {id:2, name: "Sales", responsibilities: ["Reporting"]})
MERGE (dpt3:Department {id:3, name: "Finance", responsibilities: ["Licensing"]})

MERGE (pos1:Position {id:1, name: "Head of Finance"})

MERGE (usr1:User {id:1, name: "Schmidt", country: "USA", joining_date: date("2019-09-30")})
MERGE (usr2:User {id:2, name: "Jenko", country: "USA", joining_date: date("2019-09-30")})
MERGE (usr3:User {id:3, name: "Jeff", country: "Mexico", joining_date: date("2019-09-30")})

MERGE (doc1:Document {id:1, title: "Doc A", type: "Report", creation_date: date("2019-09-30")})
MERGE (doc2:Document {id:2, title: "Doc B", type: "Contract", creation_date: date("2019-09-30")})
MERGE (doc3:Document {id:3, title: "Doc C", type: "License", creation_date: date("2019-09-30")})

// RELATIONS

