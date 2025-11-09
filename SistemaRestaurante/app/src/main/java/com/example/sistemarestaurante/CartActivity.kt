package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity(), CartItemListener {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartManager = CartManager
    private val SHIPPING_FEE = 5.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateCartSummary()
        setupNavigation()

        binding.btnCheckout.setOnClickListener {
            if (CartManager.getCartItems().isNotEmpty()) {
                showConfirmOrderDialog()
            } else {
                Toast.makeText(this, "Seu carrinho está vazio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(CartManager.getCartItems(), this)

        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun refreshCartData() {
        cartAdapter.updateItems(CartManager.getCartItems())
        updateCartSummary()
    }

    override fun onIncreaseClicked(cartItem: CartItem) {
        CartManager.addItem(cartItem.dish)
        refreshCartData()
    }

    override fun onDecreaseClicked(cartItem: CartItem) {
        if (cartItem.quantity > 1) {
            CartManager.updateQuantity(cartItem.dish, cartItem.quantity - 1)
        } else {
            CartManager.removeItem(cartItem.dish)
        }
        refreshCartData()
    }

    override fun onRemoveClicked(cartItem: CartItem) {
        CartManager.removeItem(cartItem.dish)
        refreshCartData()
        Toast.makeText(this, "${cartItem.dish.name} removido.", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        cartAdapter.updateItems(cartManager.getCartItems())
        updateCartSummary()
        binding.bottomNavigationView.selectedItemId = R.id.nav_cart
    }

    private fun setupNavigation() {
        binding.bottomNavigationView.selectedItemId = R.id.nav_cart
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_hits -> {
                    startActivity(Intent(this, HitsActivity::class.java))
                    true
                }
                R.id.nav_menu -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    true
                }
                R.id.nav_cart -> true 
                R.id.nav_orders -> {
                    startActivity(Intent(this, OrderHistoryActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

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
        val selectedPaymentId = binding.rgPaymentMethod.checkedRadioButtonId

        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Por favor, selecione uma forma de pagamento.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmar Pedido")
            .setMessage("Deseja finalizar o pedido no valor de ${binding.tvTotal.text}?")
            .setPositiveButton("Confirmar") { _, _ ->
                placeOrder(selectedPaymentId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun placeOrder(selectedPaymentId: Int) {
        binding.btnCheckout.isEnabled = false

        val paymentMethod = when (selectedPaymentId) {
            binding.rbPix.id -> binding.rbPix.text.toString()
            binding.rbCard.id -> binding.rbCard.text.toString()
            binding.rbMoney.id -> binding.rbMoney.text.toString()
            else -> "Não informado"
        }

        val order = Order(
            userId = FirebaseManager.getCurrentUserId() ?: "",
            items = CartManager.getCartItems(),
            totalPrice = CartManager.getCartTotal() + SHIPPING_FEE,
            timestamp = System.currentTimeMillis(),
            status = OrderStatus.PENDENTE,
            shippingFee = SHIPPING_FEE,
            paymentMethod = paymentMethod 
        )

        FirebaseManager.placeOrder(order) { result ->
            binding.btnCheckout.isEnabled = true

            if (result.isSuccess) {
                Toast.makeText(this, "Pedido realizado com sucesso!", Toast.LENGTH_LONG).show()

                FirebaseManager.addLoyaltyPointToCurrentUser { pointResult ->
                    if (pointResult.isSuccess) {
                        Log.d("CartActivity", "Ponto de fidelidade concedido")
                    } else {
                        Log.e("CartActivity", "Erro ao conceder ponto: ${pointResult.exceptionOrNull()?.message}")
                    }
                }

                CartManager.clearCart()
                startActivity(Intent(this, OrderHistoryActivity::class.java))
                finish()

            } else {
                Toast.makeText(this, "Erro ao finalizar pedido: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}
