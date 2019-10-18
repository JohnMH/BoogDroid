package ws.lamm.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class UtilTest {

    @ParameterizedTest(name = "\"{0}\" should be \"{1}\"")
    @MethodSource("provideStringsForMD5Sum")
    @Throws(Exception::class)
    fun md5Sum(input: String, expected: String) {
        val md5 = input.md5()

        Assertions.assertThat(md5).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun provideStringsForMD5Sum() = listOf(
                Arguments.of("", "d41d8cd98f00b204e9800998ecf8427e"),
                Arguments.of("a", "0cc175b9c0f1b6a831c399e269772661"),
                Arguments.of("abc", "900150983cd24fb0d6963f7d28e17f72"),
                Arguments.of("message digest", "f96b697d7cb7938d525a2f31aaf161d0"),
                Arguments.of("abcdefghijklmnopqrstuvwxyz", "c3fcd3d76192e4007dfb496cca67e13b"),
                Arguments.of("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", "d174ab98d277d9f5a5611c2c9f419d9f"),
                Arguments.of("12345678901234567890123456789012345678901234567890123456789012345678901234567890", "57edf4a22be3c955ac49da2e2107b67a")
        )
    }
}
