package com.example.sistemarestaurante

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityCartBinding

// (Req 3) Implementa a interface do adapter
class CartActivity : AppCompatActivity(), CartItemListener {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val SHIPPING_FEE = 5.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateCartSummary()

        binding.btnCheckout.setOnClickListener {
            if (CartManager.getCartItems().isNotEmpty()) {
                showConfirmOrderDialog()
            } else {
                Toast.makeText(this, "Seu carrinho está vazio.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Não precisamos do onResume, pois o CartManager é um Singleton
    // e o adapter será notificado por nós.

    private fun setupRecyclerView() {
        // (Req 3) Passa 'this' (a Activity) como listener
        cartAdapter = CartAdapter(CartManager.getCartItems(), this)

        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = cartAdapter
    }

    // --- SOLUÇÃO 3: Lógica dos botões ---

    // Função para atualizar a UI
    private fun refreshCartData() {
        cartAdapter.updateItems(CartManager.getCartItems())
        updateCartSummary()
    }

    override fun onIncreaseClicked(cartItem: CartItem) {
        // Usa o método addItem do seu CartManager, que já incrementa
        CartManager.addItem(cartItem.dish)
        refreshCartData()
    }

    override fun onDecreaseClicked(cartItem: CartItem) {
        if (cartItem.quantity > 1) {
            // Usa o método updateQuantity para diminuir
            CartManager.updateQuantity(cartItem.dish, cartItem.quantity - 1)
        } else {
            // Se for 1, remove o item
            CartManager.removeItem(cartItem.dish)
        }
        refreshCartData()
    }

    override fun onRemoveClicked(cartItem: CartItem) {
        // Usa o método removeItem do seu CartManager
        CartManager.removeItem(cartItem.dish)
        refreshCartData()
        Toast.makeText(this, "${cartItem.dish.name} removido.", Toast.LENGTH_SHORT).show()
    }

    // --- Fim da Solução 3 ---


    private fun updateCartSummary() {
        val subtotal = CartManager.getCartTotal()
        val total = subtotal + SHIPPING_FEE

        binding.tvSubtotal.text = String.format("R$ %.2f", subtotal)
        binding.tvShipping.text = String.format("R$ %.2f", SHIPPING_FEE)
        binding.tvTotal.text = String.format("R$ %.2f", total)

        if (subtotal > 0) {
            binding.tvEmptyCartMessage.visibility = View.GONE
            binding.rvCartItems.visibility = View.VISIBLE
        } else {
            binding.tvEmptyCartMessage.visibility = View.VISIBLE
            binding.rvCartItems.visibility = View.GONE
        }
    }

    private fun showConfirmOrderDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Pedido")
            .setMessage("Deseja finalizar o pedido no valor de ${binding.tvTotal.text}?")
            .setPositiveButton("Confirmar") { _, _ ->
                placeOrder()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun placeOrder() {
        binding.btnCheckout.isEnabled = false

        val order = Order(
            userId = FirebaseManager.getCurrentUserId() ?: "",
            items = CartManager.getCartItems(),
            totalPrice = CartManager.getCartTotal() + SHIPPING_FEE, // Total correto
            timestamp = System.currentTimeMillis(),
            status = OrderStatus.PENDENTE
        )

        // (Adicionei o shippingFee ao objeto Order, caso precise no futuro)
        // Se seu 'Order.kt' não tiver 'shippingFee', remova a linha abaixo
         order.shippingFee = SHIPPING_FEE

        FirebaseManager.placeOrder(order) { result ->
            binding.btnCheckout.isEnabled = true

            if (result.isSuccess) {
                Toast.makeText(this, "Pedido realizado com sucesso!", Toast.LENGTH_LONG).show()
                CartManager.clearCart()
                // 'refreshCartData()' atualiza a UI para mostrar carrinho vazio
                refreshCartData()
                // 'finish()' fecha a tela do carrinho
                finish()
            } else {
                Toast.makeText(this, "Erro ao finalizar pedido: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}