package kg.foodbambook.bambook.ui.auth

import androidx.viewpager.widget.PagerAdapter


class MyPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {


    private val listOfFragment: List<BaseFragment>? = arrayListOf(SignInFragment.newInstance(), SignUpFragment.newInstance())
    override fun getItem(i: Int): BaseFragment {
        return listOfFragment!![i]
    }

    override fun getCount(): Int {
        listOfFragment?.let { return  listOfFragment.size}
        return 0
    }

    override fun getPageTitle(position: Int): CharSequence? = listOfFragment?.let {  return listOfFragment[position].title}



    public interface PageChangeFragmentListener{
        fun onSwitchFragment(fragment: BaseFragment)
    }
}
