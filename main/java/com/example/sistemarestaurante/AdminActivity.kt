package com.example.sistemarestaurante

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityAdminBinding
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var adminDishAdapter: AdminDishAdapter
    // Esta lista agora será populada pelo Firebase
    private val dishesList = mutableListOf<Dish>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadDishesFromFirebase() // Carrega os dados do Firebase

        binding.fabAddDish.setOnClickListener {
            // Futuramente: Abrir uma tela ou dialog para adicionar um novo prato (RF14)
            // Por enquanto, vamos manter o Toast
            Toast.makeText(this, "Adicionar novo prato em desenvolvimento!", Toast.LENGTH_SHORT).show()
            // TODO: Chamar uma função como showAddEditDishDialog()
        }
    }

    private fun setupRecyclerView() {
        // Inicializa o adapter com a lista vazia
        adminDishAdapter = AdminDishAdapter(dishesList,
            onEditClickListener = { dish ->
                // Lógica para editar um prato (RF14, RF15)
                Toast.makeText(this, "Editar ${dish.name} em desenvolvimento!", Toast.LENGTH_SHORT).show()
                // TODO: Chamar uma função como showAddEditDishDialog(dish)
            },
            onDeleteClickListener = { dish ->
                // Lógica para excluir um prato (RF14)
                deleteDish(dish)
            },
            onToggleAvailabilityListener = { dish, isChecked ->
                // Lógica para ativar/desativar prato (RF16)
                toggleAvailability(dish, isChecked)
            }
        )
        binding.rvAdminDishes.layoutManager = LinearLayoutManager(this)
        binding.rvAdminDishes.adapter = adminDishAdapter
    }

    /**
     * Carrega a lista de pratos do Firebase (RF14)
     */
    private fun loadDishesFromFirebase() {
        lifecycleScope.launch {
            val result = FirebaseManager.getDishes()
            if (result.isSuccess) {
                val dishes = result.getOrNull() ?: emptyList()
                dishesList.clear()
                dishesList.addAll(dishes)
                adminDishAdapter.notifyDataSetChanged() // Atualiza o RecyclerView
            } else {
                Toast.makeText(this@AdminActivity, "Erro ao carregar pratos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Deleta um prato do Firebase (RF14)
     */
    private fun deleteDish(dish: Dish) {
        lifecycleScope.launch {
            val result = FirebaseManager.deleteDish(dish.id)
            if (result.isSuccess) {
                // Remove da lista local e notifica o adapter
                val position = dishesList.indexOf(dish)
                if (position != -1) {
                    dishesList.removeAt(position)
                    adminDishAdapter.notifyItemRemoved(position)
                }
                Toast.makeText(this@AdminActivity, "${dish.name} excluído!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@AdminActivity, "Falha ao excluir prato", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Atualiza a disponibilidade do prato no Firebase (RF16)
     */
    private fun toggleAvailability(dish: Dish, isChecked: Boolean) {
        // 1. Atualiza o objeto local (isso é possível agora com 'var isAvailable')
        dish.isAvailable = isChecked

        // 2. Salva o objeto *inteiro* de volta no Firebase
        lifecycleScope.launch {
            // Usamos createOrUpdateDish, que vai sobrescrever o prato com o novo status
            val result = FirebaseManager.createOrUpdateDish(dish)
            if (result.isSuccess) {
                Toast.makeText(this@AdminActivity, "${dish.name} ${if (isChecked) "disponível" else "indisponível"}.", Toast.LENGTH_SHORT).show()
            } else {
                // Se falhar, reverte a mudança visual
                dish.isAvailable = !isChecked
                adminDishAdapter.notifyItemChanged(dishesList.indexOf(dish))
                Toast.makeText(this@AdminActivity, "Falha ao atualizar status", Toast.LENGTH_SHORT).show()
            }
        }
    }

}