package com.mprusina.evooq.utils

import android.os.Bundle
import androidx.fragment.app.Fragment

// Fragment extension function to pass patient id as argument for PatientDetailsFragment
inline fun <T : Fragment> T.build(args: Bundle.() -> Unit): T =
    apply { arguments = Bundle().apply(args) }