package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.sistemarestaurante.databinding.ActivityProfileBinding
import com.example.sistemarestaurante.databinding.DialogSelectAvatarBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var currentUserType: UserType = UserType.CLIENT
    private val avatars = mapOf(
        "Ícone 1" to R.drawable.icon1,
        "Ícone 2" to R.drawable.icon2,
        "Ícone 3" to R.drawable.icon3,
        "Ícone 4" to R.drawable.icon4,
        "Ícone 5" to R.drawable.icon5,
        "Ícone 6" to R.drawable.icon6
    )

    private var selectedAvatarName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUserDetailsListener()

        binding.tvChangePhoto.setOnClickListener { showAvatarSelectionDialog() }
        binding.ivProfileImage.setOnClickListener { showAvatarSelectionDialog() }
        binding.btnSaveProfile.setOnClickListener { saveUserProfile() }
        binding.btnInfo.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            FirebaseManager.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            binding.bottomNavigationView.selectedItemId = R.id.nav_profile
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Erro ao setar selectedItemId no onResume: ${e.message}")
        }
    }

    private fun saveUserProfile() {
        setLoading(true)
        val name = binding.etName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val dob = binding.etDateOfBirth.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        FirebaseManager.updateUserDetails(name, username, dob, phone, selectedAvatarName) { result ->
            setLoading(false)
            if (result.isSuccess) {
                Toast.makeText(this, "Perfil salvo com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro ao salvar: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupUserDetailsListener() {
        setLoading(true)
        FirebaseManager.addUserDetailsListener { result ->
            setLoading(false) 
            if (result.isSuccess) {
                val user = result.getOrNull()
                user?.let {
                    updateProfileUI(it)
                }
            } else {
                Toast.makeText(this, "Erro ao carregar perfil: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                setupNavigation()
            }
        }
    }

    private fun updateProfileUI(user: User) {
        currentUserType = user.userType
        binding.etName.setText(user.name)
        binding.etUsername.setText(user.username)
        binding.etDateOfBirth.setText(user.dateOfBirth)
        binding.etPhone.setText(user.phone)

        binding.tvUserType.text = "Logado como: ${user.userType.name}"

        if (user.userType == UserType.CLIENT) {
            binding.cardLoyalty.visibility = View.VISIBLE
            binding.tvCouponCount.text = user.coupons.toString()
            binding.tvPointsProgressText.text = "${user.loyaltyPoints} / 10 Pontos"
            binding.pbPoints.max = 10
            binding.pbPoints.progress = user.loyaltyPoints
        } else {
            binding.cardLoyalty.visibility = View.GONE
        }

        selectedAvatarName = user.profileImageUrl
        if (selectedAvatarName.isNotEmpty()) {
            val resourceId = getResourceIdByName(selectedAvatarName)
            Glide.with(this)
                .load(resourceId.takeIf { it != 0 } ?: R.drawable.ic_person)
                .placeholder(R.drawable.ic_person)
                .into(binding.ivProfileImage)
        } else {
            binding.ivProfileImage.setImageResource(R.drawable.ic_person) 
        }

        setupNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseManager.removeUserDetailsListener()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSaveProfile.isEnabled = !isLoading
    }

    private fun showAvatarSelectionDialog() {
        val dialogBinding = DialogSelectAvatarBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
        val avatarList = avatars.toList()
        val avatarAdapter = AvatarAdapter(avatarList) { (name, resourceId) ->
            selectedAvatarName = resources.getResourceEntryName(resourceId)
            Glide.with(this)
                .load(resourceId)
                .into(binding.ivProfileImage)
            dialog.dismiss()
        }

        dialogBinding.rvAvatarList.layoutManager = LinearLayoutManager(this)
        dialogBinding.rvAvatarList.adapter = avatarAdapter

        dialog.show()
    }

    private fun getResourceIdByName(name: String): Int {
        return resources.getIdentifier(name, "drawable", packageName)
    }

    private fun setupNavigation() {
        binding.bottomNavigationView.menu.clear()

        when (currentUserType) {
            UserType.CLIENT -> binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_client)
            UserType.ADMIN -> binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_admin)
            UserType.EMPLOYEE -> binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_employee)
        }

        binding.bottomNavigationView.selectedItemId = R.id.nav_profile

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == binding.bottomNavigationView.selectedItemId) {
                return@setOnItemSelectedListener false
            }

            when (item.itemId) {
                // Cliente
                R.id.nav_hits -> startActivity(Intent(this, HitsActivity::class.java))
                R.id.nav_menu -> startActivity(Intent(this, MenuActivity::class.java))
                R.id.nav_cart -> startActivity(Intent(this, CartActivity::class.java))
                R.id.nav_orders -> startActivity(Intent(this, OrderHistoryActivity::class.java))

                // Admin
                R.id.nav_manage_menu -> startActivity(Intent(this, AdminActivity::class.java))
                R.id.nav_manage_orders -> startActivity(Intent(this, EmployeeDashboardActivity::class.java))

                // Funcionário
                R.id.nav_view_orders -> startActivity(Intent(this, EmployeeDashboardActivity::class.java))
                R.id.nav_view_menu -> {
                    val intent = Intent(this, MenuActivity::class.java)
                    intent.putExtra("USER_TYPE", currentUserType.name)
                    startActivity(intent)
                }

                // Comum
                R.id.nav_profile -> {  }
            }
            true 
        }
    }

}
