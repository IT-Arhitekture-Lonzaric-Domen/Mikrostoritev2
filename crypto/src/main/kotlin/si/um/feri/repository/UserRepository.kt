package si.um.feri.repository

import jakarta.enterprise.context.ApplicationScoped
import si.um.feri.model.User
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository

@ApplicationScoped
class UserRepository : PanacheMongoRepository<User>