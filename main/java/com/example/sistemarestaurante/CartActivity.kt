package com.example.sistemarestaurante // <--- CONFIRME QUE ESTE É O SEU PACOTE REAL

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityCartBinding // <--- AJUSTE O PACOTE

import com.example.sistemarestaurante.CartItem // <--- CONFIRME ESTA IMPORTAÇÃO
import com.example.sistemarestaurante.Dish // <--- CONFIRME ESTA IMPORTAÇÃO
import com.example.sistemarestaurante.CartAdapter // <--- CONFIRME ESTA IMPORTAÇÃO

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>() // <-- 'CartItem'
    private val SHIPPING_FEE = 5.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedDishesNames = intent.getStringArrayListExtra("cart_items_names")
        if (receivedDishesNames != null) {
            val dishData = getMockDishesForCart()
            for (dishName in receivedDishesNames) {
                val existingCartItem = cartItems.find { it.dish.name == dishName } // <-- 'dish'
                if (existingCartItem != null) {
                    existingCartItem.quantity++ // <-- 'quantity'
                } else {
                    val dish = dishData.find { it.name == dishName }
                    if (dish != null) {
                        cartItems.add(CartItem(dish, 1)) // <-- 'CartItem', 'dish'
                    }
                }
            }
        }

        setupRecyclerView()
        updateCartSummary()

        binding.btnCheckout.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                Toast.makeText(this, "Pedido finalizado! Obrigado!", Toast.LENGTH_LONG).show()
                // Em um app real, aqui você navegaria para uma tela de confirmação/pagamento (RF6)
                // E enviaria o pedido para o backend.
                // Limpa o carrinho
                cartItems.clear()
                updateCartSummary()
                cartAdapter.notifyDataSetChanged() // Notifica o adapter que o carrinho foi limpo
                finish() // Volta para a MenuActivity
            } else {
                Toast.makeText(this, "Seu carrinho está vazio!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems,
            onRemoveItemClickListener = { cartItem ->
                // Lógica para remover um item do carrinho (RF5)
                cartAdapter.removeItem(cartItem)
                updateCartSummary() // Recalcula o total
                Toast.makeText(this, "${cartItem.dish.name} removido do carrinho.", Toast.LENGTH_SHORT).show()
                checkEmptyCart()
            },
            onQuantityChangeListener = {
                // Se tivesse botões de +/-, a lógica de atualização seria aqui
                updateCartSummary()
                checkEmptyCart()
            }
        )
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = cartAdapter
        checkEmptyCart()
    }

    private fun updateCartSummary() {
        var subtotal = 0.0
        cartItems.forEach {
            subtotal += it.dish.price * it.quantity // <-- 'dish', 'price', 'quantity'
        }

        binding.tvSubtotal.text = "R$ %.2f".format(subtotal)
        binding.tvShipping.text = "R$ %.2f".format(SHIPPING_FEE)
        val total = subtotal + SHIPPING_FEE
        binding.tvTotal.text = "R$ %.2f".format(total)
    }

    private fun checkEmptyCart() {
        if (cartItems.isEmpty()) {
            binding.rvCartItems.visibility = View.GONE
            binding.tvEmptyCartMessage.visibility = View.VISIBLE
            binding.clOrderSummary.visibility = View.GONE // Esconde o resumo se o carrinho estiver vazio
        } else {
            binding.rvCartItems.visibility = View.VISIBLE
            binding.tvEmptyCartMessage.visibility = View.GONE
            binding.clOrderSummary.visibility = View.VISIBLE
        }
    }

    // Função mock para obter os pratos. Em um app real, você teria um repositório de dados.
    private fun getMockDishesForCart(): List<Dish> {
        val dishes = mutableListOf<Dish>()
        dishes.add(Dish("1", "Pizza Margherita", "Molho de tomate, mussarela e manjericão fresco.", 45.00, "Principal"))
        dishes.add(Dish("2", "Salada Caesar", "Alface americana, croutons, parmesão e molho caesar.", 32.50, "Entrada"))
        dishes.add(Dish("3", "Cheeseburger Clássico", "Pão brioche, hambúrguer de 180g, queijo cheddar, alface, tomate e picles.", 38.90, "Principal"))
        dishes.add(Dish("4", "Brownie com Sorvete", "Brownie de chocolate quentinho com uma bola de sorvete de creme.", 22.00, "Sobremesa"))
        dishes.add(Dish("5", "Coca-Cola", "Lata 350ml", 8.00, "Bebida", isAvailable = false)) // Exemplo indisponível
        dishes.add(Dish("6", "Suco de Laranja Natural", "Preparado na hora com laranjas frescas.", 12.00, "Bebida"))
        dishes.add(Dish("7", "Lasanha à Bolonhesa", "Camadas de massa, molho à bolonhesa, presunto e queijo.", 55.00, "Principal"))
        dishes.add(Dish("8", "Tiramisu", "Sobremesa italiana com café, queijo mascarpone e cacau.", 28.00, "Sobremesa"))
        return dishes
    }
}