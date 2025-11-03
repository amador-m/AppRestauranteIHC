package com.example.sistemarestaurante

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sistemarestaurante.databinding.ActivityProfileBinding
// (Removi a importação do auth, pois o FirebaseManager cuida disso)

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val avatars = mapOf(
        "Avatar 1" to "https://api.multiavatar.com/Binx.png",
        "Avatar 2" to "https://api.multiavatar.com/Maria.png",
        "Avatar 3" to "https://api.multiavatar.com/Pedro.png",
        "Avatar 4" to "https://api.multiavatar.com/Ana.png"
    )
    private var selectedAvatarUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserProfile()

        binding.tvChangePhoto.setOnClickListener { showAvatarSelectionDialog() }
        binding.ivProfileImage.setOnClickListener { showAvatarSelectionDialog() }
        binding.btnSaveProfile.setOnClickListener { saveUserProfile() }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSaveProfile.isEnabled = !isLoading
    }

    private fun loadUserProfile() {
        setLoading(true)
        FirebaseManager.getUserDetails { result ->
            setLoading(false)
            if (result.isSuccess) {
                val user = result.getOrNull()
                user?.let {
                    binding.etName.setText(it.name) // <-- ADICIONADO
                    binding.etUsername.setText(it.username)
                    binding.etDateOfBirth.setText(it.dateOfBirth)
                    binding.etPhone.setText(it.phone)

                    // --- SOLUÇÃO 1: Mostrar Tipo de Usuário ---
                    binding.tvUserType.text = "Logado como: ${it.userType.name}"
                    // ----------------------------------------

                    selectedAvatarUrl = it.profileImageUrl
                    if (selectedAvatarUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(selectedAvatarUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(binding.ivProfileImage)
                    }
                }
            } else {
                Toast.makeText(this, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserProfile() {
        setLoading(true)
        val name = binding.etName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val dob = binding.etDateOfBirth.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        // (Req 1/Bugfix) Chama a função atualizada do FirebaseManager
        FirebaseManager.updateUserDetails(name, username, dob, phone, selectedAvatarUrl) { result ->
            setLoading(false)
            if (result.isSuccess) {
                Toast.makeText(this, "Perfil salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao salvar: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // (A função showAvatarSelectionDialog não muda)
    private fun showAvatarSelectionDialog() {
        val avatarNames = avatars.keys.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Escolha um Avatar")
            .setItems(avatarNames) { dialog, which ->
                val selectedName = avatarNames[which]
                selectedAvatarUrl = avatars[selectedName] ?: ""

                Glide.with(this)
                    .load(selectedAvatarUrl)
                    .into(binding.ivProfileImage)

                dialog.dismiss()
            }
            .show()
    }
}