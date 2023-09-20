package org.petitparser.core.parser.utils

data class Tuple2<out T1, out T2>(
  val first: T1,
  val second: T2,
)

data class Tuple3<out T1, out T2, out T3>(
  val first: T1,
  val second: T2,
  val third: T3,
)

data class Tuple4<out T1, out T2, out T3, out T4>(
  val first: T1,
  val second: T2,
  val third: T3,
  val fourth: T4,
)

data class Tuple5<out T1, out T2, out T3, out T4, out T5>(
  val first: T1,
  val second: T2,
  val third: T3,
  val fourth: T4,
  val fifth: T5,
)

data class Tuple6<out T1, out T2, out T3, out T4, out T5, out T6>(
  val first: T1,
  val second: T2,
  val third: T3,
  val fourth: T4,
  val fifth: T5,
  val sixth: T6,
)

data class Tuple7<out T1, out T2, out T3, out T4, out T5, out T6, out T7>(
  val first: T1,
  val second: T2,
  val third: T3,
  val fourth: T4,
  val fifth: T5,
  val sixth: T6,
  val seventh: T7,
)

data class Tuple8<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8>(
  val first: T1,
  val second: T2,
  val third: T3,
  val fourth: T4,
  val fifth: T5,
  val sixth: T6,
  val seventh: T7,
  val eight: T8,
)

data class Tuple9<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9>(
  val first: T1,
  val second: T2,
  val third: T3,
  val fourth: T4,
  val fifth: T5,
  val sixth: T6,
  val seventh: T7,
  val eight: T8,
  val ninth: T9,
)