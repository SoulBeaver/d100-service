package dev.christianbroomfield.d100web

import com.xenomachina.argparser.ArgParser

class CommandLineArgs(parser: ArgParser) {
    val debug by parser.flagging(
        "-d", "--debug",
        help = "Enable Debug Mode")
}