package si.um.feri.service

import com.feri.Crypto
import com.feri.UserServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.quarkus.grpc.GrpcService
import jakarta.inject.Inject
import si.um.feri.model.User
import si.um.feri.repository.UserRepository


@GrpcService
class UserService : UserServiceGrpc.UserServiceImplBase() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun registerUser(request: Crypto.RegistrationRequest, responseObserver: StreamObserver<Crypto.RegistrationResponse>) {
        try {
            // Check if the email already exists in the database
            val existingUser = userRepository.find("email", request.email).firstResult()
            if (existingUser != null) {
                responseObserver.onError(Status.ALREADY_EXISTS.withDescription("Email already registered").asRuntimeException())
                return
            }

            // Create a new user and persist it to the database
            val user = User(email = request.email, password = request.password)
            userRepository.persist(user)

            // Build a success response
            val response = Crypto.RegistrationResponse.newBuilder()
                .setSuccess(true)
                .setMessage("User registered successfully")
                .build()

            responseObserver.onNext(response)
            responseObserver.onCompleted()

        } catch (e: Exception) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.message).asRuntimeException())
        }
    }

    override fun loginUser(request: Crypto.LoginRequest, responseObserver: StreamObserver<Crypto.LoginResponse>) {
        try {
            // Attempt to find the user by email
            val user = userRepository.find("email", request.email).firstResult()
            if (user != null && user.password == request.password) {
                // User found and password matches
                val response = Crypto.LoginResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Login successful")
                    .setUserId(user.id?.toHexString() ?: "unknown")
                    .build()

                responseObserver.onNext(response)
            } else {
                // User not found or password does not match
                responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Invalid email or password").asRuntimeException())
                return
            }
            responseObserver.onCompleted()

        } catch (e: Exception) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.message).asRuntimeException())
        }
    }
    // Add other service methods if needed
}