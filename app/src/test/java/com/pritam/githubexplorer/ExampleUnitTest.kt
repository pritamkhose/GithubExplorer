package com.pritam.githubexplorer

import com.pritam.githubexplorer.ui.fragment.UsersDetailsFragment
import com.pritam.githubexplorer.utils.databinding.StringUtil
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private var usersDetailsFragment = UsersDetailsFragment()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun stringtoDateFormat_isCorrect(){
        assertEquals("Joined at :  14 Jul 2019", StringUtil.stringDateFormat("Joined at :\u0020","2019-07-14T06:56:42Z"))
        assertEquals("Last updated at :  23 Jan 2018", StringUtil.stringDateFormat("Last updated at :\u0020","2018-01-23T16:14:34Z"))
        assertEquals("Joined at :  14 Jul 2019", StringUtil.stringDateFormat("Joined at :\u0020","2019-07-14T06:56:42Z"))
        assertEquals("Last updated at :  23 Jan 2018", StringUtil.stringDateFormat("Last updated at :\u0020","2018-01-23T16:14:34Z"))
    }

    @Test
    fun stringtoDateFormat_isNotCorrect(){
        assertNotEquals("2019/07/14T06:56:42Z", StringUtil.stringDateFormat("Joined at :\u0020","2019-07-14T06:56:42Z"))
    }

    @Test
    fun isEmailValid_isCorrect(){
        assertEquals(true , usersDetailsFragment.isEmailValid("p.khose04@gmail.com"))
        assertEquals(true , usersDetailsFragment.isEmailValid("pritamkhose@gmail.co.in"))
        assertEquals(true , usersDetailsFragment.isEmailValid("p112@gmail.com"))

        assertEquals(true , usersDetailsFragment.isEmailValid("user@domain.com"))
        assertEquals(true , usersDetailsFragment.isEmailValid("user@domain.co.in"))
        assertEquals(true , usersDetailsFragment.isEmailValid("user.name@domain.com"))
        assertEquals(true , usersDetailsFragment.isEmailValid("user?name@domain.co.in"))
    }

    @Test
    fun isEmailValid_isNotCorrect(){
        assertEquals(false , usersDetailsFragment.isEmailValid("gmail.com"))
        assertEquals(false , usersDetailsFragment.isEmailValid("p.khosegmail.com"))

        assertEquals(false , usersDetailsFragment.isEmailValid("@yahoo.com"))
        assertEquals(false , usersDetailsFragment.isEmailValid(""))

    }


}
