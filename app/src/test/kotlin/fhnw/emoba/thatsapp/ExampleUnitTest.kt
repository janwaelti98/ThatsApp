package fhnw.emoba.thatsapp

import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 *
 *
 */
class ExampleUnitTest {

    @Test
    fun testJunitSetup(){
        //given
        val s1 = 1
        val s2 = 2

        //when
        val sum = s1 + s2

        //then
        assertEquals(3, sum)
    }
}