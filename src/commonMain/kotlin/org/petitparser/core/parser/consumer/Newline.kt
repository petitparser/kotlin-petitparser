package org.petitparser.core.parser.consumer

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that detects newlines platform independently. */
fun newline(message: String = "newline expected") = object : Parser<String> {
  override fun parseOn(input: Input): Output<String> {
    val buffer = input.buffer
    val position = input.position
    if (position < buffer.length) {
      when (buffer[position]) {
        // Unix and Unix-like systems (Linux, macOS, FreeBSD, AIX, Xenix, etc.),
        // Multics, BeOS, Amiga, RISC OS.
        '\n' -> return input.success("\n", position + 1)
        '\r' -> return if (position + 1 < buffer.length && buffer[position + 1] == '\n') {
          // Microsoft Windows, DOS (MS-DOS, PC DOS, etc.), Atari TOS, DEC
          // TOPS-10, RT-11, CP/M, MP/M, OS/2, Symbian OS, Palm OS, Amstrad
          // CPC, and most other early non-Unix and non-IBM operating systems.
          input.success("\r\n", position + 2)
        } else {
          // Commodore 8-bit machines (C64, C128), Acorn BBC, ZX Spectrum,
          // TRS-80, Apple II series, Oberon, the classic Mac OS, MIT Lisp
          // Machine and OS-9
          input.success("\r", position + 1)
        }
      }
    }
    return input.failure(message)
  }
}
