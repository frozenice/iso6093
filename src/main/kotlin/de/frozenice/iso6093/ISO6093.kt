@file:JvmName("ISO6093Format")
package de.frozenice.iso6093

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * every representation can contain leading spaces, but no embedded spaces
 *
 * either signed or unsigned (positive and zero only)
 *
 * in signed, the plus can be replace by a space
 *
 * signed zero is written with a plus or a space, no minus (negative zero) allowed
 */
enum class Representation {
  // ISO 6093, 6
  /**
   * integer only, also called implicit-point representation
   *
   * examples:
   * - 2094
   * - 002094
   * - +4321
   * - -8765
   * - 000
   * - +0000
   * - 0
   */
  NR1,

  // ISO 6093, 7
  /**
   * also called explicit-point unscaled representation
   *
   * examples:
   * - 7231.000
   * - +7321
   * - +7321.
   * - 00321,54
   * - 0.00012
   * - +00.00012
   */
  NR2,

  // ISO 6093, 8
  /**
   * also called explicit-point scaled representation
   *
   * made up of significand and exponent (an integer with a sign), divided by E or e
   *
   * exponent zero hast a plus, sign can be omitted if positive
   *
   * zero has only zeros in the significand and an exponent with a plus and only zeros
   *
   * it is called normalized, if the absolute value of the significand is >= 0,1 and < 1
   *
   * examples:
   * - 8,2E+00
   * - +0,65E+4
   * - +6.5e+30
   * - 3,0e-5
   * - +0,0E+00
   */
  NR3
}

/**
 * The character which is inserted between the integer and the fractional part of the number.
 *
 * see ISO 6093, 4.1
 */
enum class DecimalMark(internal val character: Char) {
  /**
   * the comma character: `,`
   */
  Comma(','),

  /**
   * a period: `.`
   */
  FullStop('.')
}

internal object Grammar {
  val digit = """[0-9]""" // 0/1/2/3/4/5/6/7/8/9
  val sign = """[\u002b\u002d]""" // + / -
  val decimalMark = """[\u002c\u002e]""" // , / .
  val space = """ """ // SPACE
  val exponentMark = """[Ee]""" /// E / e

  // ISO 6093, 6.2
  val unsignedNR1 = """($space*$digit$digit*)"""
  val signedNR1 = """($space*(?<sign>$sign|$space)(?<number>$digit$digit*))"""

  // ISO 6093, 7.2
  val unsignedNR2 = """($space*(?<number>$digit$digit*$decimalMark$digit*)|""" +
    """$space*(?<number2>$digit*$decimalMark$digit$digit*))"""
  val signedNR2 = """($space*(?<sign>$sign|$space)$digit$digit*$decimalMark$digit*|""" +
    """$space*(?<sign2>$sign|$space)$digit*$decimalMark$digit$digit*)"""

  // ISO 6093,7.3
  val significand = """(?<significand>$digit$digit*$decimalMark$digit*|$digit*$decimalMark$digit$digit*)"""
  val exponent = """(?<exponent>$sign?$digit$digit*)"""
  val unsignedNR3 = """($space*$significand$exponentMark$exponent)"""
  val signedNR3 = """($space*(?<sign>$sign|$space)$significand$exponentMark$exponent)"""
}

internal object Regexes {
  val unsignedNR1 = Grammar.unsignedNR1.toRegex()
  val signedNR1 = Grammar.signedNR1.toRegex()
  val unsignedNR2 = Grammar.unsignedNR2.toRegex()
  val signedNR2 = Grammar.signedNR2.toRegex()
  val unsignedNR3 = Grammar.unsignedNR3.toRegex()
  val signedNR3 = Grammar.signedNR3.toRegex()
}

/**
 * Checks if a string matches one of the three representations.
 *
 * @param str the string to check
 */
fun isValid(str: String) = isValidNR1(str) || isValidNR2(str) || isValidNR3(str)

/**
 * Checks if a string matches the NR1 representation.
 *
 * @param str the string to check
 */
fun isValidNR1(str: String) = isValidNR1Unsigned(str) || isValidNR1Signed(str)

/**
 * Checks if a string matches the NR1 representation in its unsigned form.
 *
 * @param str the string to check
 */
fun isValidNR1Unsigned(str: String) = str.matches(Regexes.unsignedNR1)

/**
 * Checks if a string matches the NR1 representation in its signed form.
 *
 * @param str the string to check
 */
fun isValidNR1Signed(str: String) = str.matches(Regexes.signedNR1)

/**
 * Checks if a string matches the NR2 representation.
 *
 * @param str the string to check
 */
fun isValidNR2(str: String) = isValidNR2Unsigned(str) || isValidNR2Signed(str)

/**
 * Checks if a string matches the NR2 representation in its unsigned form.
 *
 * @param str the string to check
 */
fun isValidNR2Unsigned(str: String) = str.matches(Regexes.unsignedNR2)

/**
 * Checks if a string matches the NR2 representation in its signed form.
 *
 * @param str the string to check
 */
fun isValidNR2Signed(str: String) = str.matches(Regexes.signedNR2)

/**
 * Checks if a string matches the NR3 representation.
 *
 * @param str the string to check
 */
fun isValidNR3(str: String) = isValidNR3Unsigned(str) || isValidNR3Signed(str)

/**
 * Checks if a string matches the NR3 representation in its unsigned form.
 *
 * @param str the string to check
 */
fun isValidNR3Unsigned(str: String) = str.matches(Regexes.unsignedNR3)

/**
 * Checks if a string matches the NR3 representation in its signed form.
 *
 * @param str the string to check
 */
fun isValidNR3Signed(str: String) = str.matches(Regexes.signedNR3)

/**
 * Parses a string in either of the three representations.
 *
 * Java's Double.valueOf is used, but keep in mind this matches a superset of ISO6093.
 * use one of the `isValid*` methods, when you want to check if the input matches a representation.
 */
fun parse(str: String): Double {
  if (!isValid(str)) throw NumberFormatException("string not formatted according to NR1, NR2 or NR3")

  return java.lang.Double.valueOf(str.replace(',', '.'))
}

/**
 * Formats a [Double] according the the NR1 representation.
 *
 * @param d the number to format
 */
fun formatNR1(d: Double): String = DecimalFormat("0").format(d)

/**
 * Formats a [Double] according the the NR2 representation.
 *
 * @param d the number to format
 * @param decimalMark which character to use for the decimal mark (default is FullStop)
 */
fun formatNR2(d: Double, decimalMark: DecimalMark = DecimalMark.FullStop): String {
  val dfs = DecimalFormatSymbols()
  dfs.decimalSeparator = decimalMark.character

  val pattern = "0.${"#".repeat(20)}"

  return DecimalFormat(pattern, dfs).format(d)
}

/**
 * Formats a [Double] according the the NR3 representation.
 *
 * @param d the number to format
 * @param decimalMark which character to use for the decimal mark (default is FullStop)
 */
fun formatNR3(d: Double, decimalMark: DecimalMark = DecimalMark.FullStop): String {
  val dfs = DecimalFormatSymbols()
  dfs.decimalSeparator = decimalMark.character

  val pattern = "0.${"#".repeat(20)}E0"

  return DecimalFormat(pattern, dfs).format(d)
}
