package dev.christianbroomfield.d100web

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default
import java.io.File

class CommandLineArgs(parser: ArgParser) {
    val debug by parser.flagging(
        "-D", "--debug",
        help = "Enable Debug Mode")

    val data by parser.storing(
        "-d", "--data-directory",
        help = "Directory from which to populate the MongoDB on startup. Must be in JSON format."
    ).default<String?>(null)
        .addValidator {
            if (value != null && !File(value).isDirectory) {
                throw InvalidArgumentException("Must specify a directory from which to populate the MongoDB.")
            }
        }
}
