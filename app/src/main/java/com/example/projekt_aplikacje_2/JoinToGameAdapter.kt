package com.example.projekt_aplikacje_2
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt_aplikacje_2.lobby.GameType
import com.example.projekt_aplikacje_2.lobby.LobbyInfo


class JoinToGameAdapter(private val List : ArrayList<LobbyInfo>, private var locationsArray: Array<String>): RecyclerView.Adapter<JoinToGameAdapter.MyViewHolder>() {
    private var images = createImages()
    private lateinit var mListener : OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replace(new: List<LobbyInfo>) {
        List.clear()
        for (i in new.indices) {
            List.add(i,new[i])
        }
        images = createImages()
        notifyDataSetChanged()
    }

    fun sortSolo(){
        val new = arrayListOf<LobbyInfo>()
        for( i in 0 until itemCount) if(List[i].gametype == GameType.SOLO){
            val lobby = LobbyInfo(
            lobby_id =  List[i].lobby_id,
            lobby_name= List[i].lobby_name,
            map= List[i].map,
            gametype=List[i].gametype,
            curr_players = List[i].curr_players,
            players_amount = List[i].players_amount
            )
            new.add(lobby)
        }
        replace(new)
    }

    fun sortTeam(){
        val new = arrayListOf<LobbyInfo>()
        for( i in 0 until itemCount) if(List[i].gametype == GameType.TEAM){
            val lobby = LobbyInfo(
                lobby_id =  List[i].lobby_id,
                lobby_name= List[i].lobby_name,
                map= List[i].map,
                gametype=List[i].gametype,
                curr_players = List[i].curr_players,
                players_amount = List[i].players_amount)
            new.add(lobby)
        }
        replace(new)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun sortPlace(){
        List.sortBy {
            it.lobby_name
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_layout,
            parent,false)
        return MyViewHolder(itemView,mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = List[position]
        holder.itemTitle.text = currentItem.lobby_name
        holder.itemDetail.text = currentItem.curr_players.toString() + "/" + currentItem.players_amount.toString()
        holder.itemPlace.text = locationsArray[currentItem.map]
        images[position]?.let { holder.itemImage.setImageResource(it) }
    }

    override fun getItemCount(): Int {
        return List.size
    }

    class MyViewHolder(itemView : View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView){
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        val itemTitle: TextView = itemView.findViewById(R.id.item_title)
        val itemDetail: TextView = itemView.findViewById(R.id.item_detail)
        val itemPlace: TextView = itemView.findViewById(R.id.item_place)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createImages(): Array<Int?> {
        val list = arrayOfNulls<Int>(itemCount)
        val currentItem = List

        for( i in 0 until itemCount) if(currentItem[i].gametype == GameType.TEAM){
            list[i]  = R.drawable.team
        }
        else {list[i] = R.drawable.solo}
        this.notifyDataSetChanged()
        return list
    }
}

