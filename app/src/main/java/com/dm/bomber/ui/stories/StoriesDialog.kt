package com.dm.bomber.ui.stories

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.dm.bomber.databinding.StoriesDialogBinding
import com.dm.bomber.ui.MainViewModel


class StoriesDialog : DialogFragment() {

    private var _binding: StoriesDialogBinding? = null

    private val binding get() = _binding!!

    private val model by activityViewModels<MainViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = StoriesDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())

        model.stories.observe(requireActivity()) { stories ->
            binding.stories.run {
                adapter = StoriesAdapter(stories, {
                    if (currentItem < stories.size - 1) currentItem += 1
                    else dismiss()
                    true
                }, {
                    if (currentItem > 0) currentItem -= 1
                    else dismiss()
                    true
                }, {
                    dismiss()
                })
            }

            val index = arguments?.getInt(POSITION) ?: 0

            binding.stories.setCurrentItem(index, false)
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val POSITION = "position"
    }
}