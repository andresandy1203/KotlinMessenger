package com.example.kotlinmessenger.views

import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

//ViewHolder for LatestMessages Row
class LatestMessageRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.latest_message_textView_latest_messages.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }
        //Fetch Partner Id data from Firebase
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_textView_latest_messages.text =
                    chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.imageView_latest_message
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}