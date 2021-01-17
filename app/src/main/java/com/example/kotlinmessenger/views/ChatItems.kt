package com.example.kotlinmessenger.views

import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

//ViewHolder Class for outbound messages
class ChatFromItem(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_from_row
        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

//ViewHolder Class for inbound messages
class ChatToItem(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_to_row
        Picasso.get().load(uri).into(targetImageView)


    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}