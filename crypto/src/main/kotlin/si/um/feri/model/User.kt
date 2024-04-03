package si.um.feri.model

import org.bson.types.ObjectId
import io.quarkus.mongodb.panache.common.MongoEntity

@MongoEntity(collection="Users")
data class User(
    var id: ObjectId? = null,
    var email: String,
    var password: String
)