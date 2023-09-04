package com.dm.bomber.ui.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.dm.bomber.R
import com.dm.bomber.databinding.DialogAdvertisingBinding
import com.dm.bomber.ui.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class AdvertisingDialog : DialogFragment() {
    private var _binding: DialogAdvertisingBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAdvertisingBinding.inflate(getLayoutInflater())
        _binding!!.advertisingDescription.movementMethod = LinkMovementMethod.getInstance()

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.getRoot())
            .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.setCancelable(false)

        val model by activityViewModels<MainViewModel>()

        model.getAdvertisingCounter().observe(viewLifecycleOwner) { counter: Int ->
            binding.skipButton.text = getString(R.string.skip, counter)
            binding.skipButton.setEnabled(counter == 0)
        }

        model.advertising.observe(viewLifecycleOwner) { dataSnapshot: DocumentSnapshot? ->
            if (dataSnapshot == null)
                return@observe

            var descriptionKey = "description-" + Locale.getDefault().language
            if (!dataSnapshot.contains(descriptionKey))
                descriptionKey = "description"

            val description = dataSnapshot.getString(descriptionKey)
            val html = dataSnapshot.getBoolean(HTML_KEY)

            var buttonKey = "button-" + Locale.getDefault().language
            if (!dataSnapshot.contains(buttonKey))
                buttonKey = "button"

            val descriptionPrepared = if (html != null && html)
                HtmlCompat.fromHtml(
                    description!!,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ) else description!!

            binding.advertisingDescription.text = descriptionPrepared
            binding.advertisingButton.text = dataSnapshot.getString(buttonKey)
            binding.advertisingButton.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(dataSnapshot.getString(URL_KEY)))
                )
            }

            val image = dataSnapshot.getString(IMAGE_KEY)
            if (!image.isNullOrEmpty()) {
                binding.advertisingImage.setVisibility(View.VISIBLE)
                Glide
                    .with(this)
                    .load(image)
                    .fitCenter()
                    .into(binding.advertisingImage)
            } else
                binding.advertisingImage.setVisibility(View.GONE)

            model.startCounting()
        }

        binding.skipButton.setOnClickListener {
            dialog!!.cancel()
            model.advertisingTrigger.setValue(true)
        }

        return binding.getRoot()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val HTML_KEY = "html"
        private const val IMAGE_KEY = "image"
        private const val URL_KEY = "url"
    }
}