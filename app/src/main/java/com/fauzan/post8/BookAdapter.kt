package com.fauzan.post8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fauzan.post8.databinding.ItemBookBinding

class BookAdapter(
    private val books: List<Book>,
    private val onEdit: (Book) -> Unit,
    private val onDelete: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.tvTitle.text = book.title
            binding.tvRelease.text = book.releaseDate
            binding.tvDescription.text = book.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)

        // Set Listener untuk tombol Edit
        holder.binding.btnEdit.setOnClickListener {
            onEdit(book)
        }

        // Set Listener untuk tombol Delete
        holder.binding.btnDelete.setOnClickListener {
            onDelete(book)
        }
    }

    override fun getItemCount() = books.size
}