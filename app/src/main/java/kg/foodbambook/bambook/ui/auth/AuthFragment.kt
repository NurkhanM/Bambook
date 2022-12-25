package kg.foodbambook.bambook.ui.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kg.foodbambook.kg.R


class AuthFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: MyPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_auth, container, false)
        initViewPager(v)
        return v
    }

    private fun initViewPager(v: View){
        viewPager = v.findViewById(R.id.viewPager)
        pagerAdapter = MyPagerAdapter(childFragmentManager)
        viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = v.findViewById(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)
    }
}
