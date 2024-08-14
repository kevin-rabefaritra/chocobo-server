db = db.getSiblingDB('admin');
db.auth('4ser', 'm0ngo');

db = db.getSiblingDB('chocobo');
db.createUser({
        user: "4ser",
        pwd: "m0ngo",
        roles: [
            {
                role: "readWrite",
                db: "chocobo"
            }
        ]
    }
)
db = new Mongo().getDB("chocobo");