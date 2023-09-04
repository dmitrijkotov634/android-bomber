package com.dm.bomber.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.dm.bomber.databinding.CountryCodeRowBinding

class CountryCodeAdapter(
    context: Activity?,
    private val flags: IntArray,
    private val countryCodes: Array<String>
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = flags.size

    override fun getItem(i: Int): Long? = null

    override fun getItemId(i: Int): Long = 0

    override fun getView(index: Int, view: View?, parent: ViewGroup): View {
        var resultView: View? = view

        var holder: ViewHolder

        if (resultView == null) {
            val binding = CountryCodeRowBinding.inflate(inflater)
            resultView = binding.getRoot()
            resultView.setTag(ViewHolder(binding).also { holder = it })
        } else {
            holder = resultView.tag as ViewHolder
        }

        holder.binding.countryFlag.setImageResource(flags[index])
        holder.binding.countryCode.text = String.format("+%s", countryCodes[index])

        return resultView
    }

    private data class ViewHolder(val binding: CountryCodeRowBinding)
}