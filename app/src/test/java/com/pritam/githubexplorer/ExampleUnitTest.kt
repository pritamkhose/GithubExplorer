package com.pritam.githubexplorer

import com.pritam.githubexplorer.ui.fragment.UsersDetailsFragment
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    var usersDetailsFragment = UsersDetailsFragment()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun stringtoDateFormat_isCorrect(){
        assertEquals("14 Jul 2019", usersDetailsFragment.stringtoDateFormat("2019-07-14T06:56:42Z"))
        assertEquals("23 Jan 2018", usersDetailsFragment.stringtoDateFormat("2018-01-23T16:14:34Z"))

    }

    @Test
    fun stringtoDateFormat_isNotCorrect(){
        assertNotEquals("2019/07/14T06:56:42Z", usersDetailsFragment.stringtoDateFormat("2019-07-14T06:56:42Z"))
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
