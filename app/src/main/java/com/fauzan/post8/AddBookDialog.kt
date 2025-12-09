package com.fauzan.post8

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.fauzan.post8.databinding.UploadDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddBookDialog(
    private val context: Context,
    private val booksRef: DatabaseReference,
    private val bookToEdit: Book? = null
) {

    fun show() {
        val dialogBinding = UploadDialogBinding.inflate(LayoutInflater.from(context))

        // --- PRE-FILL DATA UNTUK MODE EDIT ---
        if (bookToEdit != null) {
            dialogBinding.editTextTitleBook.setText(bookToEdit.title)
            dialogBinding.editTextRelease.setText(bookToEdit.releaseDate)

            // Isi kolom deskripsi jika sedang edit
            dialogBinding.editTextDeskBook.setText(bookToEdit.description)
        }

        // Setup DatePicker (Kode sama)
        dialogBinding.editTextRelease.setOnClickListener {
            // ... (kode date picker sama seperti sebelumnya) ...
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    dialogBinding.editTextRelease.setText(dateFormat.format(selectedCalendar.time))
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        val titleDialog = if (bookToEdit == null) "Tambah Tugas" else "Edit Tugas"
        val btnTitle = if (bookToEdit == null) "Simpan" else "Update"

        MaterialAlertDialogBuilder(context)
            .setTitle(titleDialog)
            .setView(dialogBinding.root)
            .setPositiveButton(btnTitle) { dialog, _ ->
                val title = dialogBinding.editTextTitleBook.text.toString()

                // AMBIL DATA DESKRIPSI
                val description = dialogBinding.editTextDeskBook.text.toString()

                val release = dialogBinding.editTextRelease.text.toString()

                if (title.isEmpty() || release.isEmpty()) {
                    Toast.makeText(context, "Judul dan Tanggal wajib diisi!", Toast.LENGTH_SHORT).show()
                } else {
                    // Panggil fungsi simpan dengan parameter description
                    saveOrUpdateData(title, description, release)
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Tambahkan parameter description di fungsi ini
    private fun saveOrUpdateData(title: String, description: String, release: String) {
        if (bookToEdit == null) {
            // --- MODE TAMBAH BARU ---
            val id = booksRef.push().key
            // Masukkan description ke dalam objek Book
            val newBook = Book(id, title, description, release)

            id?.let {
                booksRef.child(it).setValue(newBook)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Data berhasil ditambah!", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            // --- MODE UPDATE ---
            val bookId = bookToEdit.id
            // Masukkan description ke dalam objek Book yang diupdate
            val updatedBook = Book(bookId, title, description, release)

            if (bookId != null) {
                booksRef.child(bookId).setValue(updatedBook)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Data berhasil diupdate!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}