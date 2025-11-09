package com.example.sistemarestaurante

import android.content.Intent // <-- VERIFIQUE SE O IMPORT ESTÁ AQUI
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityAdminBinding
import com.example.sistemarestaurante.databinding.DialogAddEditDishBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var dishAdapter: AdminDishAdapter
    private val dishList = mutableListOf<Dish>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupNavigation()

        FirebaseManager.addMenuListener { result ->
            if (result.isSuccess) {
                dishList.clear()
                dishList.addAll(result.getOrNull() ?: emptyList())
                dishAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Erro ao carregar pratos.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabAddDish.setOnClickListener {
            showAddOrUpdateDishDialog(null)
        }
    }

    private fun setupRecyclerView() {
        dishAdapter = AdminDishAdapter(
            dishes = dishList,
            onEditClickListener = { dish ->
                showAddOrUpdateDishDialog(dish)
            },
            onDeleteClickListener = { dish ->
                deleteDish(dish)
            },
            onToggleAvailabilityListener = { dish, isAvailable ->
                val updatedDish = dish.copy(isAvailable = isAvailable)
                updateDishInFirebase(updatedDish)
            }
        )
        binding.rvAdminDishes.layoutManager = LinearLayoutManager(this)
        binding.rvAdminDishes.adapter = dishAdapter
    }

    private fun showAddOrUpdateDishDialog(dishToEdit: Dish?) {
        val dialogBinding = DialogAddEditDishBinding.inflate(layoutInflater)
        val isEditing = dishToEdit != null

        if (isEditing) {
            dialogBinding.etDishName.setText(dishToEdit?.name)
            dialogBinding.etDishDescription.setText(dishToEdit?.description)
            dialogBinding.etDishPrice.setText(dishToEdit?.price.toString())
            dialogBinding.etDishImageUrl.setText(dishToEdit?.imageUrl)
            // --- LINHA ADICIONADA (1/3) ---
            dialogBinding.etDishCategory.setText(dishToEdit?.category)
        }

        AlertDialog.Builder(this)
            .setTitle(if (isEditing) "Editar Prato" else "Adicionar Prato")
            .setView(dialogBinding.root)
            .setPositiveButton(if (isEditing) "Salvar" else "Adicionar") { dialog, _ ->
                val name = dialogBinding.etDishName.text.toString().trim()
                val description = dialogBinding.etDishDescription.text.toString().trim()
                val price = dialogBinding.etDishPrice.text.toString().toDoubleOrNull() ?: 0.0
                val imageUrl = dialogBinding.etDishImageUrl.text.toString().trim()
                // --- LINHA ADICIONADA (2/3) ---
                val category = dialogBinding.etDishCategory.text.toString().trim()

                if (name.isEmpty() || description.isEmpty() || price <= 0 || category.isEmpty()) { // Categoria adicionada à verificação
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val dish = Dish(
                    id = dishToEdit?.id ?: "",
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    // --- LINHA ADICIONADA (3/3) ---
                    category = category,
                    isAvailable = dishToEdit?.isAvailable ?: true
                )

                if (isEditing) {
                    updateDishInFirebase(dish)
                } else {
                    addDishToFirebase(dish)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun addDishToFirebase(dish: Dish) {
        FirebaseManager.addDish(dish) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Prato adicionado!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDishInFirebase(dish: Dish) {
        FirebaseManager.updateDish(dish) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Prato atualizado!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteDish(dish: Dish) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza que deseja excluir '${dish.name}'?")
            .setPositiveButton("Excluir") { _, _ ->
                FirebaseManager.deleteDish(dish.id) { result ->
                    if (result.isSuccess) {
                        Toast.makeText(this, "Prato excluído.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao excluir: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupNavigation() {
        binding.bottomNavigationView.selectedItemId = R.id.nav_manage_menu
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_manage_menu -> true // Já estamos aqui
                R.id.nav_manage_orders -> {
                    startActivity(Intent(this, EmployeeDashboardActivity::class.java))
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

    override fun onDestroy() {
        super.onDestroy()
        FirebaseManager.removeMenuListener()
    }
}