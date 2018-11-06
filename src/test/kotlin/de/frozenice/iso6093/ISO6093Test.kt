package de.frozenice.iso6093

import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

const val tolerance = 0.0000000000001

class ISO6093Test : StringSpec({
  "parse NR1" {
    parse("0004902") shouldBe 4902.0.plusOrMinus(tolerance)
    parse("  04902") shouldBe 4902.0.plusOrMinus(tolerance)
    parse("   4902") shouldBe 4902.0.plusOrMinus(tolerance)

    parse("+004902") shouldBe 4902.0.plusOrMinus(tolerance)
    parse(" +04902") shouldBe 4902.0.plusOrMinus(tolerance)
    parse("  +4902") shouldBe 4902.0.plusOrMinus(tolerance)

    parse("0001234") shouldBe 1234.0.plusOrMinus(tolerance)
    parse("   1234") shouldBe 1234.0.plusOrMinus(tolerance)

    parse("+001234") shouldBe 1234.0.plusOrMinus(tolerance)
    parse("  +1234") shouldBe 1234.0.plusOrMinus(tolerance)

    parse("-56780") shouldBe (-56780.0).plusOrMinus(tolerance)
    parse(" -56780") shouldBe (-56780.0).plusOrMinus(tolerance)

    parse("0000000") shouldBe 0.0.plusOrMinus(tolerance)
    parse("      0") shouldBe 0.0.plusOrMinus(tolerance)

    parse("+000000") shouldBe 0.0.plusOrMinus(tolerance)
    parse("     +0") shouldBe 0.0.plusOrMinus(tolerance)

    parse("1234567") shouldBe 1234567.0.plusOrMinus(tolerance)
  }

  "parse NR2" {
    parse("1327.000") shouldBe 1327.0.plusOrMinus(tolerance)
    parse("0001327.") shouldBe 1327.0.plusOrMinus(tolerance)
    parse("   1327.") shouldBe 1327.0.plusOrMinus(tolerance)

    parse("+1327.00") shouldBe 1327.0.plusOrMinus(tolerance)
    parse("  +1327.") shouldBe 1327.0.plusOrMinus(tolerance)

    parse("00123,45") shouldBe 123.45.plusOrMinus(tolerance)
    parse("  123,45") shouldBe 123.45.plusOrMinus(tolerance)

    parse(" +123,45") shouldBe 123.45.plusOrMinus(tolerance)

    parse("  1237,0") shouldBe 1237.0.plusOrMinus(tolerance)

    parse(" +1237,0") shouldBe 1237.0.plusOrMinus(tolerance)

    parse(" 00.00001") shouldBe 0.00001.plusOrMinus(tolerance)

    parse(" +0.00001") shouldBe 0.00001.plusOrMinus(tolerance)

    parse("-5,67800") shouldBe (-5.678).plusOrMinus(tolerance)
    parse("-05,6780") shouldBe (-5.678).plusOrMinus(tolerance)

    parse("1234,567") shouldBe 1234.567.plusOrMinus(tolerance)

    parse("000,0000") shouldBe 0.0.plusOrMinus(tolerance)
    parse("     0,0") shouldBe 0.0.plusOrMinus(tolerance)

    parse("+0,00000") shouldBe 0.0.plusOrMinus(tolerance)
    parse("    +0,0") shouldBe 0.0.plusOrMinus(tolerance)
    parse("      0,") shouldBe 0.0.plusOrMinus(tolerance)
  }

  "parse NR3" {
    parse("+0,56E+4") shouldBe 5600.0.plusOrMinus(tolerance)
    parse("+5.6e+03") shouldBe 5600.0.plusOrMinus(tolerance)

    parse("+0,3E-04") shouldBe 0.00003.plusOrMinus(tolerance)
    parse(" 0,3e-04") shouldBe 0.00003.plusOrMinus(tolerance)

    parse("-2,8E+00") shouldBe (-2.8).plusOrMinus(tolerance)

    parse("+0,0E+00") shouldBe 0.0.plusOrMinus(tolerance)
    parse("   0.e+0") shouldBe 0.0.plusOrMinus(tolerance)
  }

  "self valid" {
    isValid(formatNR1(1245)) shouldBe true

    isValid(formatNR2(84561230.54679)) shouldBe true
    isValid(formatNR2(84561230.54679, DecimalMark.Comma)) shouldBe true

    isValid(formatNR3(84561230.54679)) shouldBe true
    isValid(formatNR3(84561230.54679, DecimalMark.Comma)) shouldBe true
  }
})
