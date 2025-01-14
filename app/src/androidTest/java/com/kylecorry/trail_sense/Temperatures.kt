package com.kylecorry.trail_sense

object Temperatures {
    val canadaLow = arrayOf(-31.0f, -27.6f, -21.7f, -10.2f, -1.5f, 4.6f, 8.4f, 6.6f, 1.6f, -4.2f, -15.8f, -26.8f)
    val canadaHigh = arrayOf(-20.8f, -15.2f, -7.0f, 2.9f, 11.6f, 18.1f, 21.0f, 19.9f, 12.4f, 4.3f, -7.5f, -17.1f)

    val alaskaLow = arrayOf(-31.4f, -27.9f, -24.5f, -14.7f, -3.8f, 2.9f, 4.8f, 2.2f, -3.7f, -12.7f, -23.4f, -28.9f)
    val alaskaHigh = arrayOf(-22.8f, -18.4f, -12.3f, -1.4f, 8.8f, 17.0f, 18.2f, 14.7f, 7.0f, -5.2f, -15.8f, -20.9f)

    val californiaLow = arrayOf(2.6f, 4.1f, 5.4f, 6.2f, 8.4f, 10.6f, 12.4f, 12.9f, 11.7f, 9.1f, 5.3f, 2.5f)
    val californiaHigh = arrayOf(13.8f, 15.9f, 17.3f, 20.3f, 23.1f, 26.1f, 28.8f, 29.4f, 28.3f, 25.3f, 19.1f, 14.6f)

    val mexicoLow = arrayOf(-5.8f, -4.2f, -1.4f, 2.5f, 6.2f, 8.3f, 8.5f, 8.4f, 6.8f, 3.1f, -1.2f, -4.9f)
    val mexicoHigh = arrayOf(10.6f, 13.1f, 17.0f, 20.8f, 23.9f, 25.0f, 24.3f, 23.8f, 22.0f, 18.8f, 15.3f, 10.7f)

    val costaRicaLow = arrayOf(17.1f, 17.3f, 18.2f, 19.0f, 18.8f, 18.3f, 18.6f, 18.2f, 17.7f, 17.5f, 17.4f, 17.0f)
    val costaRicaHigh = arrayOf(25.9f, 26.4f, 27.5f, 27.7f, 27.3f, 26.4f, 26.4f, 26.4f, 26.2f, 26.0f, 25.3f, 25.3f)

    val puertoRicoLow = arrayOf(13.6f, 13.5f, 13.8f, 14.7f, 15.9f, 16.5f, 16.6f, 16.8f, 16.7f, 16.3f, 15.7f, 14.5f)
    val puertoRicoHigh = arrayOf(26.0f, 26.1f, 26.7f, 27.3f, 27.8f, 28.5f, 28.8f, 28.9f, 28.8f, 28.4f, 27.3f, 26.2f)

    val newYorkLow = arrayOf(-9.5f, -9.8f, -6.8f, -1.6f, 4.0f, 10.0f, 13.5f, 13.3f, 9.8f, 4.7f, 0.0f, -5.3f)
    val newYorkHigh = arrayOf(-2.2f, -1.9f, 1.7f, 7.1f, 14.0f, 19.7f, 22.8f, 21.9f, 18.0f, 12.6f, 6.6f, 0.9f)

    val greenlandLow = arrayOf(-12.5f, -13.6f, -13.2f, -7.4f, -1.9f, 2.9f, 5.4f, 4.7f, 1.6f, -2.5f, -6.7f, -10.1f)
    val greenlandHigh = arrayOf(-7.9f, -8.6f, -7.8f, -2.5f, 2.9f, 9.6f, 12.9f, 11.5f, 7.0f, 1.5f, -2.8f, -5.8f)

    val hawaiiLow = arrayOf(16.6f, 16.5f, 17.4f, 18.2f, 18.9f, 20.1f, 20.7f, 20.9f, 20.6f, 20.1f, 19.1f, 17.8f)
    val hawaiiHigh = arrayOf(25.3f, 25.6f, 25.8f, 26.3f, 27.4f, 28.4f, 29.0f, 29.5f, 29.5f, 28.7f, 27.2f, 26.0f)

    val equadorLow = arrayOf(5.9f, 6.1f, 6.2f, 6.4f, 6.3f, 5.7f, 5.5f, 5.4f, 5.6f, 5.9f, 5.9f, 5.9f)
    val equadorHigh = arrayOf(15.5f, 15.4f, 15.5f, 15.4f, 15.4f, 14.7f, 14.4f, 14.9f, 15.8f, 16.1f, 16.3f, 15.9f)

    val brazilLow = arrayOf(21.2f, 21.0f, 21.2f, 21.3f, 20.9f, 20.0f, 19.1f, 19.7f, 20.5f, 21.3f, 21.2f, 21.3f)
    val brazilHigh = arrayOf(28.6f, 28.6f, 28.7f, 29.3f, 29.8f, 30.5f, 31.1f, 31.8f, 31.4f, 31.1f, 30.6f, 29.5f)

    val argentinaLow = arrayOf(15.6f, 14.6f, 12.8f, 9.1f, 6.1f, 3.0f, 2.0f, 3.1f, 5.6f, 9.3f, 12.1f, 14.7f)
    val argentinaHigh = arrayOf(28.4f, 27.2f, 24.7f, 21.8f, 18.9f, 15.3f, 15.2f, 17.7f, 20.2f, 23.3f, 25.6f, 28.0f)

    val boliviaLow = arrayOf(19.1f, 19.0f, 18.9f, 16.4f, 14.6f, 13.3f, 12.4f, 12.9f, 15.4f, 17.2f, 18.1f, 18.6f)
    val boliviaHigh = arrayOf(28.7f, 28.7f, 27.8f, 27.4f, 25.0f, 23.2f, 23.8f, 25.8f, 28.0f, 28.6f, 29.1f, 28.9f)

    val chileLow = arrayOf(0.3f, 0.0f, -1.2f, -2.9f, -4.9f, -6.2f, -6.7f, -6.2f, -4.7f, -3.0f, -1.9f, -0.4f)
    val chileHigh = arrayOf(8.3f, 7.9f, 6.5f, 4.2f, 1.3f, -0.5f, -0.7f, 0.0f, 1.9f, 4.5f, 5.9f, 7.5f)

    val moroccoLow = arrayOf(-19.6f, -18.4f, -16.1f, -12.9f, -10.1f, -5.9f, -2.4f, -2.2f, -5.6f, -10.1f, -15.5f, -18.4f)
    val moroccoHigh = arrayOf(-8.7f, -6.8f, -3.4f, 0.0f, 3.6f, 9.0f, 14.0f, 13.4f, 8.7f, 2.2f, -4.5f, -7.6f)

    val egyptLow = arrayOf(1.8f, 3.5f, 7.2f, 12.0f, 16.4f, 18.9f, 20.1f, 19.7f, 17.3f, 13.1f, 7.5f, 3.3f)
    val egyptHigh = arrayOf(17.0f, 19.2f, 23.4f, 28.5f, 32.5f, 35.1f, 35.4f, 35.1f, 32.4f, 28.2f, 22.5f, 18.4f)

    val chadLow = arrayOf(12.2f, 14.7f, 18.9f, 22.4f, 23.3f, 22.3f, 21.1f, 19.9f, 20.1f, 19.4f, 16.2f, 13.6f)
    val chadHigh = arrayOf(30.4f, 32.9f, 36.5f, 38.7f, 38.1f, 36.0f, 32.4f, 30.2f, 32.8f, 35.5f, 34.1f, 31.9f)

    val southAfricaLow = arrayOf(4.5f, 4.6f, 2.6f, -1.5f, -5.7f, -8.6f, -9.4f, -8.1f, -5.5f, -2.2f, 0.7f, 3.2f)
    val southAfricaHigh = arrayOf(21.7f, 21.0f, 18.4f, 14.1f, 9.6f, 6.3f, 6.1f, 8.4f, 11.7f, 15.0f, 17.6f, 20.1f)

    val madagascarLow = arrayOf(14.5f, 14.4f, 13.9f, 12.8f, 9.2f, 7.1f, 7.0f, 9.0f, 10.3f, 11.8f, 13.1f, 14.1f)
    val madagascarHigh = arrayOf(24.5f, 24.6f, 24.8f, 24.8f, 24.3f, 22.8f, 21.9f, 23.6f, 25.3f, 26.7f, 26.4f, 25.7f)

    val franceLow = arrayOf(1.4f, 1.7f, 3.1f, 5.0f, 8.8f, 11.6f, 13.6f, 13.5f, 10.9f, 7.9f, 3.6f, 2.1f)
    val franceHigh = arrayOf(8.1f, 9.7f, 12.8f, 15.0f, 19.3f, 22.4f, 25.2f, 25.2f, 22.1f, 17.1f, 11.7f, 8.8f)

    val unitedKingdomLow = arrayOf(-4.9f, -5.1f, -4.5f, -2.9f, -0.3f, 2.2f, 4.3f, 4.3f, 2.7f, 0.0f, -2.9f, -4.6f)
    val unitedKingdomHigh = arrayOf(-0.5f, -0.5f, 0.6f, 3.8f, 7.1f, 9.3f, 10.9f, 10.7f, 8.4f, 4.6f, 1.5f, -0.1f)

    val ukraineLow = arrayOf(-8.2f, -7.4f, -3.0f, 3.6f, 9.1f, 13.0f, 14.5f, 13.2f, 8.3f, 2.7f, -1.9f, -5.8f)
    val ukraineHigh = arrayOf(-2.5f, -1.6f, 3.8f, 13.7f, 20.9f, 23.6f, 25.0f, 24.7f, 19.2f, 11.8f, 3.8f, -0.9f)

    val swedenLow = arrayOf(-20.5f, -19.6f, -15.1f, -9.8f, -3.0f, 2.8f, 5.3f, 3.4f, -0.9f, -5.9f, -13.7f, -18.5f)
    val swedenHigh = arrayOf(-11.2f, -9.7f, -4.7f, 0.0f, 6.5f, 12.7f, 14.8f, 12.1f, 6.5f, 0.0f, -6.6f, -9.5f)

    val siberiaLow = arrayOf(-37.5f, -38.0f, -35.4f, -26.4f, -10.2f, 1.4f, 4.6f, 1.6f, -3.2f, -17.2f, -31.5f, -36.9f)
    val siberiaHigh = arrayOf(-30.3f, -30.4f, -25.6f, -14.5f, -1.4f, 10.2f, 13.9f, 9.6f, 2.7f, -10.9f, -24.4f, -29.8f)

    val mongoliaLow = arrayOf(-39.9f, -35.4f, -22.2f, -9.5f, -1.8f, 5.0f, 6.7f, 4.3f, -2.7f, -11.8f, -23.4f, -34.5f)
    val mongoliaHigh = arrayOf(-28.1f, -21.9f, -7.7f, 4.3f, 12.5f, 18.6f, 19.4f, 17.6f, 11.1f, 1.8f, -11.2f, -23.8f)

    val moscowLow = arrayOf(-12.8f, -12.6f, -7.3f, -0.3f, 5.2f, 9.5f, 11.2f, 9.5f, 4.6f, -0.1f, -5.8f, -10.2f)
    val moscowHigh = arrayOf(-6.9f, -5.5f, 0.0f, 8.2f, 15.7f, 19.7f, 20.8f, 18.8f, 13.1f, 6.2f, -1.3f, -5.0f)

    val saudiArabiaLow = arrayOf(1.8f, 3.8f, 7.9f, 12.8f, 17.9f, 20.3f, 21.6f, 21.6f, 19.1f, 13.9f, 8.0f, 3.5f)
    val saudiArabiaHigh = arrayOf(15.5f, 18.4f, 22.7f, 28.0f, 33.7f, 37.3f, 38.5f, 38.8f, 36.5f, 30.7f, 22.4f, 17.4f)

    val indiaLow = arrayOf(7.7f, 9.5f, 13.6f, 18.0f, 21.7f, 20.4f, 18.2f, 17.7f, 17.1f, 13.7f, 8.9f, 6.8f)
    val indiaHigh = arrayOf(23.5f, 26.7f, 30.9f, 34.2f, 36.4f, 31.4f, 24.6f, 23.6f, 24.9f, 26.4f, 24.7f, 23.7f)

    val chinaLow = arrayOf(-5.4f, -3.0f, 2.0f, 8.8f, 14.3f, 19.6f, 22.5f, 21.6f, 16.1f, 9.8f, 2.5f, -3.4f)
    val chinaHigh = arrayOf(4.1f, 7.4f, 13.2f, 20.7f, 26.7f, 31.1f, 30.9f, 30.0f, 26.2f, 20.8f, 13.0f, 6.3f)

    val indonesiaLow = arrayOf(22.2f, 22.3f, 22.5f, 22.6f, 22.8f, 22.5f, 21.8f, 21.6f, 21.8f, 22.2f, 22.3f, 22.2f)
    val indonesiaHigh = arrayOf(31.0f, 31.1f, 31.5f, 31.6f, 32.0f, 31.7f, 31.4f, 32.0f, 32.0f, 32.0f, 31.5f, 31.1f)

    val westernAustraliaLow = arrayOf(22.0f, 23.0f, 21.5f, 17.9f, 13.1f, 9.9f, 8.4f, 9.4f, 11.5f, 14.4f, 17.2f, 20.0f)
    val westernAustraliaHigh = arrayOf(38.0f, 37.7f, 35.9f, 32.0f, 27.5f, 23.3f, 22.6f, 24.2f, 27.4f, 30.6f, 33.3f, 36.1f)

    val queenslandLow = arrayOf(21.4f, 21.2f, 19.9f, 16.8f, 13.9f, 10.1f, 8.9f, 10.2f, 13.1f, 16.3f, 19.1f, 20.5f)
    val queenslandHigh = arrayOf(32.7f, 31.9f, 31.2f, 29.7f, 27.2f, 24.8f, 24.4f, 26.2f, 28.7f, 31.1f, 32.4f, 32.9f)

    val southAustraliaLow = arrayOf(17.4f, 17.0f, 13.9f, 9.5f, 5.1f, 1.7f, 0.9f, 2.2f, 6.1f, 10.0f, 13.3f, 15.6f)
    val southAustraliaHigh = arrayOf(33.5f, 32.7f, 29.7f, 25.3f, 20.0f, 16.5f, 16.4f, 18.5f, 23.2f, 27.0f, 29.9f, 31.8f)

    val newSouthWalesLow = arrayOf(3.1f, 3.3f, 1.1f, -2.4f, -5.2f, -7.2f, -8.4f, -7.3f, -5.5f, -2.9f, -0.8f, 1.4f)
    val newSouthWalesHigh = arrayOf(16.9f, 16.5f, 14.1f, 10.2f, 6.0f, 2.3f, 1.3f, 2.5f, 5.7f, 9.1f, 11.8f, 15.4f)

}