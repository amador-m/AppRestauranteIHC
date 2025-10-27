package com.example.sistemarestaurante

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await

object FirebaseManager {

    private const val TAG = "FirebaseManager"
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private const val USERS_COLLECTION = "users"
    private const val DISHES_COLLECTION = "dishes"

    // --- Funções de Usuário (RF1, RF2, RF10) ---

    suspend fun registerUser(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val newUser = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    userType = UserType.CLIENT
                )
                db.child(USERS_COLLECTION).child(newUser.id).setValue(newUser).await()

                Log.d(TAG, "User registered successfully: ${newUser.id}")
                Result.success(newUser)
            } else {
                Result.failure(Exception("Falha ao criar usuário no Auth."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID não encontrado após login.")

            val dataSnapshot = db.child(USERS_COLLECTION).child(uid).get().await()

            // AVISO CORRIGIDO AQUI:
            val user = dataSnapshot.getValue(User::class.java) // <--- MUDANÇA

            if (user != null) {
                Log.d(TAG, "Login successful. User type: ${user.userType}")
                Result.success(user)
            } else {
                auth.signOut()
                Result.failure(Exception("Perfil do usuário não encontrado no sistema."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}")
            Result.failure(e)
        } as Result<User>
    }

    fun logout() {
        auth.signOut()
    }

    // --- FUNÇÕES NOVAS: Gerenciamento de Pratos (RF14, RF15, RF16) ---

    suspend fun createOrUpdateDish(dish: Dish): Result<Unit> {
        return try {
            val dishId = if (dish.id.isEmpty()) {
                db.child(DISHES_COLLECTION).push().key ?: throw Exception("Falha ao criar ID para o prato")
            } else {
                dish.id
            }

            db.child(DISHES_COLLECTION).child(dishId).setValue(dish.copy(id = dishId)).await()
            Log.d(TAG, "Prato salvo com sucesso: $dishId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao salvar prato: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDishes(): Result<List<Dish>> {
        return try {
            val dataSnapshot = db.child(DISHES_COLLECTION).get().await()

            // AVISO CORRIGIDO AQUI:
            val dishes = dataSnapshot.children.mapNotNull { it.getValue(Dish::class.java) } // <--- MUDANÇA

            Log.d(TAG, "Buscou ${dishes.size} pratos")
            Result.success(dishes)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao buscar pratos: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteDish(dishId: String): Result<Unit> {
        return try {
            db.child(DISHES_COLLECTION).child(dishId).removeValue().await()
            Log.d(TAG, "Prato deletado: $dishId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao deletar prato: ${e.message}")
            Result.failure(e)
        }
    }
}