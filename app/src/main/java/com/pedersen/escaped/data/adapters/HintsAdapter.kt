package com.pedersen.escaped.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.R
import com.pedersen.escaped.data.models.Hint
import timber.log.Timber

/**
 * Created by anderspedersen on 14/03/2018.
 */
class HintsAdapter(private var context: Context, private var list: ArrayList<Hint>) : BaseAdapter() {

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

        when (BuildConfig.isMaster) {
            true -> {
                vh.tvHeader.text = context.getString(R.string.hint_item_header_label, list[position].title)
                vh.tvBody.text = context.getString(R.string.hint_item_body_label, list[position].body)
            }
            false -> {
                vh.tvHeader.text = list[position].title
                vh.tvBody.visibility = View.GONE
            }
        }

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
    val tvHeader: TextView = view?.findViewById(R.id.header) as TextView
    val tvBody: TextView = view?.findViewById(R.id.body) as TextView
}
