package com.example.chatapp.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso


class MessagesAdaptor(
    private val context: Context,
    private val messages: MutableList<ChatMessage>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val RECIEVER_TYPE_HOLDER = 1
    private val SENDER_TYPE_HOLDER = 2
    private val IMAGE_TYPE_HOLDER_ME = 3
    private val IMAGE_TYPE_HOLDER_SENDER = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == IMAGE_TYPE_HOLDER_ME) {
            ImageHolderME(
                LayoutInflater.from(parent.context).inflate(R.layout.me_image, parent, false)
            )
        } else if (viewType == IMAGE_TYPE_HOLDER_SENDER) {
            ImageHolderSender(
                LayoutInflater.from(parent.context).inflate(R.layout.sender_image, parent, false)
            )
        } else if (viewType == RECIEVER_TYPE_HOLDER) {
            MeViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.me, parent, false)
            )
        } else {
            SenderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.sender, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is ImageHolderME) {
            if (message.image.isEmpty()) {
                Toast.makeText(context, "No image", Toast.LENGTH_SHORT).show()
            } else {
                // to load images using Picasso library
                Picasso.get()
                    .load(message.image)
                    .placeholder(R.drawable.chat_app)   // in case when there is no image
                    .into(holder.meImage)
            }
        } else if (holder is ImageHolderSender) {
            if (message.image.isEmpty()) {
                Toast.makeText(context, "No image", Toast.LENGTH_SHORT).show()
            } else {
                // to load images using Picasso library
                Picasso.get()
                    .load(message.image)
                    .placeholder(R.drawable.chat_app)   // in case when there is no image
                    .into(holder.senderImage)
            }
        } else if (holder is MeViewHolder) {
            holder.textViewMessage.text = message.message
        } else if (holder is SenderViewHolder) {
            holder.textViewSender.text = message.message
            if (message.sender.profileImage.isEmpty()) {
                holder.senderProfileImage.setImageResource(R.drawable.ic_profile)
            } else {
                Picasso.get()
                    .load(message.sender.profileImage)
                    .placeholder(R.drawable.chat_app)
                    .into(holder.senderProfileImage)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid!! == message.sender.id && message.image.isNotEmpty()) {
            IMAGE_TYPE_HOLDER_ME
        } else if (FirebaseAuth.getInstance().currentUser?.uid != message.sender.id && message.image.isNotEmpty()) {
            IMAGE_TYPE_HOLDER_SENDER
        } else if (FirebaseAuth.getInstance().currentUser?.uid == message.sender.id) {
            RECIEVER_TYPE_HOLDER
        } else {
            SENDER_TYPE_HOLDER
        }
    }

    inner class MeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMessage: TextView = itemView.findViewById(R.id.text_view_me)
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSender: TextView = itemView.findViewById(R.id.sender_text_view)
        val senderProfileImage: CircularImageView = itemView.findViewById(R.id.sender_profile_image)
    }

    inner class ImageHolderME(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val meImage: ImageView = itemView.findViewById(R.id.me_image)
    }

    inner class ImageHolderSender(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderImage: ImageView = itemView.findViewById(R.id.sender_image)
    }
}
