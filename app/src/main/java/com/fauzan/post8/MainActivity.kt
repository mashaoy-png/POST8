package com.fauzan.post8

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fauzan.post8.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class MainActivity : AppCompatActivity() {

    private lateinit var rvBooks: RecyclerView
    private lateinit var booksRef : DatabaseReference
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvBooks = binding.rvBooks
        rvBooks.layoutManager = LinearLayoutManager(this)

        booksRef = FirebaseDatabase.getInstance().getReference("books")

        fetchData()
        setupAddButton()
    }

    private fun fetchData() {
        booksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()
                for (data in snapshot.children) {
                    val book = data.getValue(Book::class.java)
                    book?.id = data.key
                    book?.let { books.add(it) }
                }

                // --- LOGIKA EMPTY STATE ---
                if (books.isEmpty()) {
                    // Jika data kosong: Munculkan Empty State, Hilangkan RecyclerView
                    binding.layoutEmptyState.visibility = View.VISIBLE
                    binding.rvBooks.visibility = View.GONE
                } else {
                    // Jika ada data: Hilangkan Empty State, Munculkan RecyclerView
                    binding.layoutEmptyState.visibility = View.GONE
                    binding.rvBooks.visibility = View.VISIBLE

                    // Pasang adapter seperti biasa
                    rvBooks.adapter = BookAdapter(books,
                        onEdit = { book ->
                            val dialog = AddBookDialog(this@MainActivity, booksRef, book)
                            dialog.show()
                        },
                        onDelete = { book ->
                            showDeleteConfirmation(book)
                        }
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ... (Fungsi setupAddButton dan showDeleteConfirmation TETAP SAMA seperti sebelumnya) ...
    private fun setupAddButton() {
        binding.fabAddBooks.setOnClickListener {
            val dialog = AddBookDialog(this, booksRef)
            dialog.show()
        }
    }

    private fun showDeleteConfirmation(book: Book) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Tugas")
            .setMessage("Yakin ingin menghapus tugas '${book.title}'?")
            .setPositiveButton("Hapus") { _, _ ->
                if (book.id != null) {
                    booksRef.child(book.id!!).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
