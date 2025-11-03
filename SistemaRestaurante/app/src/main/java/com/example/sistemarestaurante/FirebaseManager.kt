package com.example.sistemarestaurante

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

/**
 * Objeto Singleton para gerenciar todas as interações com o Firebase.
 * (Versão completa e correta)
 */
object FirebaseManager {

    // --- DECLARAÇÕES ÚNICAS ---
    private const val TAG = "FirebaseManager"
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://sistemarestaurante-775a3-default-rtdb.firebaseio.com/")
    private val usersRef = database.getReference("users")
    private val menuRef = database.getReference("dishes")
    private val ordersRef = database.getReference("orders")

    // --- Listeners ---
    private var menuListener: ValueEventListener? = null
    private var allOrdersListener: ValueEventListener? = null
    private var clientOrdersListener: ValueEventListener? = null

    // --- FUNÇÕES DE AUTENTICAÇÃO E USUÁRIO ---

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun registerUser(email: String, pass: String, name: String, onResult: (Result<FirebaseUser>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = task.result?.user
                if (firebaseUser != null) {
                    val user = User(
                        email = email,
                        userType = UserType.CLIENT,
                        name = name,
                        username = "",
                        profileImageUrl = "",
                        dateOfBirth = "",
                        phone = ""
                    )
                    usersRef.child(firebaseUser.uid).setValue(user)
                        .addOnSuccessListener { onResult(Result.success(firebaseUser)) }
                        .addOnFailureListener { onResult(Result.failure(it)) }
                } else {
                    onResult(Result.failure(Exception("Falha ao obter usuário após criação")))
                }
            } else {
                onResult(Result.failure(task.exception ?: Exception("Erro desconhecido no registro.")))
            }
        }
    }

    fun loginUser(email: String, pass: String, onResult: (Result<User>) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result?.user?.uid ?: ""
                usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            Log.d(TAG, "Login successful. User type: ${user.userType}")
                            onResult(Result.success(user))
                        } else {
                            Log.e(TAG, "User data not found in database.")
                            onResult(Result.failure(Exception("Usuário não encontrado no banco de dados.")))
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Database error during login.", error.toException())
                        onResult(Result.failure(error.toException()))
                    }
                })
            } else {
                Log.e(TAG, "Login failed.", task.exception)
                onResult(Result.failure(task.exception ?: Exception("Erro desconheciodo no login.")))
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    // --- FUNÇÕES DE PERFIL DE USUÁRIO ---

    fun getUserDetails(onResult: (Result<User>) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(Result.failure(Exception("Usuário não logado")))
            return
        }
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    onResult(Result.success(user))
                } else {
                    onResult(Result.failure(Exception("Usuário não encontrado no banco de dados.")))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(Result.failure(error.toException()))
            }
        })
    }

    fun updateUserDetails(
        name: String,
        username: String,
        dateOfBirth: String,
        phone: String,
        profileImageUrl: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(Result.failure(Exception("Usuário não logado")))
            return
        }
        val profileUpdates = mapOf(
            "name" to name,
            "username" to username,
            "dateOfBirth" to dateOfBirth,
            "phone" to phone,
            "profileImageUrl" to profileImageUrl
        )
        usersRef.child(userId).updateChildren(profileUpdates)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    // --- FUNÇÕES DO CARDÁPIO (ADMIN E CLIENTE) ---

    fun addDish(dish: Dish, onResult: (Result<Unit>) -> Unit) {
        val dishId = menuRef.push().key ?: ""
        dish.id = dishId
        menuRef.child(dishId).setValue(dish)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun updateDish(dish: Dish, onResult: (Result<Unit>) -> Unit) {
        menuRef.child(dish.id).setValue(dish)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun deleteDish(dishId: String, onResult: (Result<Unit>) -> Unit) {
        menuRef.child(dishId).removeValue()
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun addMenuListener(onResult: (Result<List<Dish>>) -> Unit) {
        removeMenuListener()
        menuListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dishes = snapshot.children.mapNotNull { it.getValue(Dish::class.java) }
                Log.d(TAG, "Listener: Cardápio atualizado com ${dishes.size} pratos")
                onResult(Result.success(dishes))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Listener do cardápio cancelado", error.toException())
                onResult(Result.failure(error.toException()))
            }
        }
        menuRef.addValueEventListener(menuListener!!)
    }

    fun removeMenuListener() {
        menuListener?.let {
            menuRef.removeEventListener(it)
            menuListener = null
            Log.d(TAG, "Listener do cardápio removido.")
        }
    }


    // --- FUNÇÕES DE PEDIDOS (CLIENTE E FUNCIONÁRIO) ---

    fun placeOrder(order: Order, onResult: (Result<String>) -> Unit) {
        val orderId = ordersRef.push().key ?: ""
        order.orderId = orderId
        order.userId = getCurrentUserId() ?: "" // Garante que o ID do usuário seja salvo

        ordersRef.child(orderId).setValue(order)
            .addOnSuccessListener {
                Log.d(TAG, "Pedido salvo com sucesso: $orderId")
                onResult(Result.success(orderId))
            }
            .addOnFailureListener {
                Log.e(TAG, "Falha ao salvar pedido", it)
                onResult(Result.failure(it))
            }
    }

    fun addAllOrdersListener(onResult: (Result<List<Order>>) -> Unit) {
        removeAllOrdersListener()
        allOrdersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                Log.d(TAG, "Listener: Pedidos atualizados, ${orders.size} pedidos")
                onResult(Result.success(orders))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Listener de Pedidos cancelado", error.toException())
                onResult(Result.failure(error.toException()))
            }
        }
        ordersRef.addValueEventListener(allOrdersListener!!)
    }

    fun removeAllOrdersListener() {
        allOrdersListener?.let {
            ordersRef.removeEventListener(it)
            allOrdersListener = null
            Log.d(TAG, "Listener de Pedidos removido")
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus, onResult: (Result<Unit>) -> Unit) {
        ordersRef.child(orderId).child("status").setValue(newStatus)
            .addOnSuccessListener {
                Log.d(TAG, "Status do pedido $orderId atualizado para $newStatus")
                onResult(Result.success(Unit))
            }
            .addOnFailureListener {
                Log.e(TAG, "Falha ao atualizar status", it)
                onResult(Result.failure(it))
            }
    }

    fun addClientOrdersListener(onResult: (Result<List<Order>>) -> Unit) {
        removeClientOrdersListener()
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(Result.failure(Exception("Usuário não está logado.")))
            return
        }

        clientOrdersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                Log.d(TAG, "Listener Cliente: ${orders.size} pedidos encontrados.")
                onResult(Result.success(orders))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Listener de Pedidos do Cliente foi cancelado.", error.toException())
                onResult(Result.failure(error.toException()))
            }
        }
        ordersRef.orderByChild("userId")
            .equalTo(userId)
            .addValueEventListener(clientOrdersListener!!)
    }

    fun removeClientOrdersListener() {
        clientOrdersListener?.let {
            val userId = getCurrentUserId()
            if (userId != null) {
                ordersRef.orderByChild("userId")
                    .equalTo(userId)
                    .removeEventListener(it)
            } else {
                ordersRef.removeEventListener(it)
            }
            clientOrdersListener = null
            Log.d(TAG, "Listener de Pedidos do Cliente removido.")
        }
    }
}