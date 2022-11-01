package com.brandsin.user.ui.dialogs.filter


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.DialogSearchFilterBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.homepage.TagsItem
import com.brandsin.user.utils.map.observe
import java.util.ArrayList

class DialogFilterFragment : DialogFragment(), Observer<Any?>
{
    lateinit  var  binding: DialogSearchFilterBinding
    lateinit var viewModel: DialogFilterViewModel
    var newTagsList  = mutableListOf<TagsItem>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogSearchFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DialogFilterViewModel::class.java)
        binding.viewModel = viewModel

        when {
            arguments != null -> {
                when {
                    requireArguments().containsKey(Params.DIALOG_SEARCH_FILTER) -> {
                        newTagsList = (requireArguments().getSerializable(Params.DIALOG_SEARCH_FILTER)as ArrayList<TagsItem>)
                        newTagsList.removeAt(0)
                        viewModel.tagsList = newTagsList
                        if (requireArguments().getIntegerArrayList(Params.DIALOG_FILTER_TAGS) !=null){
                            viewModel.tagsID = requireArguments().getIntegerArrayList(Params.DIALOG_FILTER_TAGS)as ArrayList<Int>
                        }
                        if (requireArguments().getString(Params.DIALOG_FILTER_SORT) !=null){
                            viewModel.sort = requireArguments().getString(Params.DIALOG_FILTER_SORT).toString()
                        }
                    }
                }
            }
        }

        addListenerOnButton()
        viewModel.getFilter()

        viewModel.mutableLiveData.observe(this, this)

        observe(viewModel.filterAdapter.FilterLiveData) {
            if (it!!.id !=null) {
                if (viewModel.tagsID.size > 0) {
                    if (viewModel.tagsID.contains(it.id!!.toInt())) {
                        viewModel.tagsID.remove(it.id.toInt())
                    } else {
                        viewModel.tagsID.add(it.id.toInt())
                    }
                } else {
                    viewModel.tagsID.add(it.id!!.toInt())
                }
            }
        }

        binding.ibClose.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnDelete.setOnClickListener {
            viewModel.tagsID  = ArrayList<Int>()
            viewModel.sort = ""
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
            intent.putIntegerArrayListExtra(Params.DIALOG_FILTER_TAGS,viewModel.tagsID)
            intent.putExtra(Params.DIALOG_FILTER_SORT,viewModel.sort)
            requireActivity().setResult(Codes.DIALOG_CONFIRM_REQUEST, intent)
            requireActivity().finish()
        }

        binding.btnConfirm.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
            intent.putIntegerArrayListExtra(Params.DIALOG_FILTER_TAGS,viewModel.tagsID)
            intent.putExtra(Params.DIALOG_FILTER_SORT,viewModel.sort)
            requireActivity().setResult(Codes.DIALOG_CONFIRM_REQUEST, intent)
            requireActivity().finish()
        }
    }
    fun addListenerOnButton(){
        if (viewModel.sort.isNotEmpty()) {
            if (viewModel.sort == "min_price_product") {
                binding.rgSort.check(R.id.minimum_order)
            } else if (viewModel.sort == "delivery_time") {
                binding.rgSort.check(R.id.fastest_delivery)
            } else if (viewModel.sort == "A-Z") {
                binding.rgSort.check(R.id.a_to_z)
            } else if (viewModel.sort == "avg_rating") {
                binding.rgSort.check(R.id.rating)
            }
        }else{
            binding.rgSort.check(R.id.the_nearest)
        }

        binding.rgSort.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = requireActivity().findViewById(checkedId)
                if (radio.text.toString() == resources.getString(R.string.the_nearest)) {
                    viewModel.sort = ""
                } else if (radio.text.toString() == resources.getString(R.string.minimum_order)) {
                    viewModel.sort = "min_price_product"
                } else if (radio.text.toString() == resources.getString(R.string.fastest_delivery)) {
                    viewModel.sort = "delivery_time"
                } else if (radio.text.toString() == resources.getString(R.string.a_to_z)) {
                    viewModel.sort = "A-Z"
                } else if (radio.text.toString() == resources.getString(R.string.rating)) {
                    viewModel.sort = "avg_rating"
                }
            })

    }
    override fun onChanged(it: Any?)
    {
        when (it) {

        }
    }
}