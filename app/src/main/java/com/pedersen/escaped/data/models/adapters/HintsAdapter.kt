package com.pedersen.escaped.data.models.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pedersen.escaped.R
import com.pedersen.escaped.data.models.Hint
import timber.log.Timber

/**
 * Created by anderspedersen on 14/03/2018.
 */
class HintsAdapter(private var context: Context, list: ArrayList<Hint>) : BaseAdapter() {

    private var list = list

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view: View?
        val vh: ViewHolder

        val inflater = LayoutInflater.from(context)


        if (convertView == null) {
            view = inflater.inflate(R.layout.hint_list_item, parent, false)
            vh = ViewHolder(view)
            view.tag = vh
            Timber.i("set Tag for ViewHolder, position: $position")
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }

        vh.tvContent.text = list[position].title

        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}

private class ViewHolder(view: View?) {
    val tvContent: TextView = view?.findViewById(R.id.header) as TextView
}
