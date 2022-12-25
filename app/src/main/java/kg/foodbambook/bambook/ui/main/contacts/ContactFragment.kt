package kg.foodbambook.bambook.ui.main.contacts


import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kg.foodbambook.kg.App

import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.Contacts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_contact.*


private val TAG = ContactFragment::class.java.simpleName

class ContactFragment : Fragment(), View.OnClickListener {

    private lateinit var phoneNumber: TextView
    private lateinit var mapDescription: TextView
    private lateinit var workTime: TextView
    private lateinit var facebookLink: TextView
    private lateinit var instagramLink: TextView

    private lateinit var facebookCard: View
    private lateinit var instagramCard: View
    private var contacts: Contacts? = null
    private lateinit var mapImage:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_contact, container, false)
        initViews(v)
        if (contacts == null)
            getContactInfo()
        else
            updateUI(contacts!!)

        return v
    }

    private fun initViews(v: View){
        phoneNumber = v.findViewById(R.id.phone_number)
        mapDescription = v.findViewById(R.id.map_description)
        workTime = v.findViewById(R.id.work_time)
        facebookLink = v.findViewById(R.id.facebook_link)
        instagramLink = v.findViewById(R.id.instagram_link)
        instagramLink.text="instagram"
        mapImage = v.findViewById(R.id.map)

        facebookCard = v.findViewById(R.id.facebook_card)
        facebookCard.setOnClickListener(this)
        instagramCard = v.findViewById(R.id.insta_card)
        instagramCard.setOnClickListener(this)

    }

    private fun getContactInfo(){
        App.getClient(context!!)
            .getContacts()
            .enqueue(object: Callback<Contacts?> {
                override fun onFailure(call: Call<Contacts?>, t: Throwable) {

                }

                override fun onResponse(call: Call<Contacts?>, response: Response<Contacts?>) {
                    if (response.isSuccessful) {
                        contacts = response.body()!!
                        contacts!!.phone = contacts!!.phone.replace(",", "\n")
                        updateUI(contacts!!)
                    }else{

                    }
                }
            })
    }

    override fun onClick(v: View?) {
        Log.e(TAG, "onClick")
        if (contacts== null) return
        when(v!!.id){
            facebookCard.id -> {
                launchFacebook(requireContext(), contacts!!.facebook_link)
            }

            instagramCard.id -> {
                launchInstagram(requireContext(), contacts!!.instagram_link)
            }
        }
    }

    private fun updateUI(contacts: Contacts) {
        mapDescription.text = contacts.map_description
        workTime.text = requireContext().getString(R.string.work_time, contacts.work_start, contacts.work_end)
        phoneNumber.text = contacts.phone
//        facebookLink.text = contacts.facebook_link
//        instagramLink.text = contacts.instagram_link
        Glide.with(requireContext()).load(contacts.map_image).into(mapImage)
    }

    private fun launchFacebook(context: Context, facebookId: String){
        context.startActivity(getOpenFacebookIntent(context, facebookId))
    }
    private fun getOpenFacebookIntent(context: Context, facebookId: String): Intent {

           return Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/$facebookId"))

    }

    private fun launchInstagram(context: Context, instagramId: String){
        val uri = Uri.parse("http://instagram.com/_u/$instagramId")
        val intent = Intent(Intent.ACTION_VIEW,uri)

        intent.setPackage("com.instagram.android")

        try {
            context.startActivity(intent)
        }catch (e: ActivityNotFoundException){
            context.startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("http://instagram.com/$instagramId")))
        }
    }

}
