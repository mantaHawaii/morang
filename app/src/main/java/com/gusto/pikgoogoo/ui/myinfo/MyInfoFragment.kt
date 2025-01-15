package com.gusto.pikgoogoo.ui.myinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.User
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.FragmentMyInfoBinding
import com.gusto.pikgoogoo.ui.comment.my.MyCommentListFragment
import com.gusto.pikgoogoo.ui.grade.GradeListDialog
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.ui.profile.edit.EditProfileFragment
import com.gusto.pikgoogoo.ui.subject.bookmarked.BookmarkedSubjectListFragment
import com.gusto.pikgoogoo.ui.subject.my.MySubjectListFragment
import com.gusto.pikgoogoo.ui.withdrawal.WithdrawalDialog
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.util.GradeIconSelector
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyInfoFragment : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentMyInfoBinding
    private val iconSelector = GradeIconSelector()
    private lateinit var user: User

    private val viewModel: MyInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMyInfoBinding.inflate(inflater, container, false)

        val v = binding.root

        binding.fcvFmi.visibility = View.VISIBLE

        binding.cvLogout.setOnClickListener { (requireActivity() as MainActivity).logOut() }
        binding.ibClose.setOnClickListener { backPressed() }
        binding.cvEditProfile.setOnClickListener {
            showChildFragment(EditProfileFragment(binding.tvNickname.text.toString()), FragmentTags.EDIT_PROFILE_TAG)
        }
        binding.cvMarkedSubjects.setOnClickListener {
            showChildFragment(BookmarkedSubjectListFragment(), FragmentTags.BOOKMARKED_SUBJECT_LIST_TAG)
        }
        binding.cvMySubjects.setOnClickListener {
            showChildFragment(MySubjectListFragment(), FragmentTags.MY_SUBJECT_LIST_TAG)
        }

        binding.cvMyComments.setOnClickListener {
            showChildFragment(MyCommentListFragment(), FragmentTags.MY_COMMENT_LIST_TAG)
        }

        binding.cvRequestWithdrawal.setOnClickListener {
            val dialog = WithdrawalDialog()
            dialog.show(childFragmentManager, FragmentTags.WITHDRAWAL_TAG)
        }

        viewModel.fetchCurrentUserInfo(requireActivity())

        binding.ibGrade.setOnClickListener {
            GradeListDialog(user.id, user.grade, user.gradeIcon).show(childFragmentManager, FragmentTags.GRADE_LIST_TAG)
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
    }

    fun showChildFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fcv_fmi, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun subscribeObservers() {
        viewModel.userData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    user = dataState.result
                    binding.tvNickname.setText(user.nickname)
                    if (user.gradeIcon > 0) {
                        iconSelector.setImageViewGradeIcon(binding.ibGrade, user.gradeIcon)
                    } else {
                        viewModel.fetchGradeList()
                    }
                }
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
        viewModel.gradeData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    iconSelector.setImageViewGradeIcon(binding.ibGrade, user.grade, dataState.result)
                }
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
        viewModel.deleteState.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    parentFragmentManager.popBackStackImmediate()
                }
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
    }

    fun refresh() {
        viewModel.fetchCurrentUserInfo(requireActivity())
    }

}
