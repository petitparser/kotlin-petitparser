package org.petitparser.core.parser.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class TupleTest {
  @Test
  fun test_tuples() {
    val t2 = Tuple2(1, 2)
    assertEquals(1, t2.first)
    assertEquals(2, t2.second)
    val (t2_1, t2_2) = t2
    assertEquals(1, t2_1)
    assertEquals(2, t2_2)

    val t3 = Tuple3(1, 2, 3)
    assertEquals(3, t3.third)
    val (t3_1, t3_2, t3_3) = t3
    assertEquals(3, t3_3)

    val t4 = Tuple4(1, 2, 3, 4)
    assertEquals(4, t4.fourth)
    val (t4_1, t4_2, t4_3, t4_4) = t4
    assertEquals(4, t4_4)

    val t5 = Tuple5(1, 2, 3, 4, 5)
    assertEquals(5, t5.fifth)
    val (t5_1, t5_2, t5_3, t5_4, t5_5) = t5
    assertEquals(5, t5_5)

    val t6 = Tuple6(1, 2, 3, 4, 5, 6)
    assertEquals(6, t6.sixth)
    val (t6_1, t6_2, t6_3, t6_4, t6_5, t6_6) = t6
    assertEquals(6, t6_6)

    val t7 = Tuple7(1, 2, 3, 4, 5, 6, 7)
    assertEquals(7, t7.seventh)
    val (t7_1, t7_2, t7_3, t7_4, t7_5, t7_6, t7_7) = t7
    assertEquals(7, t7_7)

    val t8 = Tuple8(1, 2, 3, 4, 5, 6, 7, 8)
    assertEquals(8, t8.eighth)
    val (t8_1, t8_2, t8_3, t8_4, t8_5, t8_6, t8_7, t8_8) = t8
    assertEquals(8, t8_8)

    val t9 = Tuple9(1, 2, 3, 4, 5, 6, 7, 8, 9)
    assertEquals(8, t9.eighth)
    assertEquals(9, t9.ninth)
    val (t9_1, t9_2, t9_3, t9_4, t9_5, t9_6, t9_7, t9_8, t9_9) = t9
    assertEquals(9, t9_9)
  }
}
