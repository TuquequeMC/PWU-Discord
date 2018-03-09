import co.aikar.commands.JDACommandManager
import co.aikar.commands.JDAOptions
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val options = JDAOptions();
    val token = String(Files.readAllBytes(Paths.get("token"))).trim();
    val jda = JDABuilder(AccountType.BOT).setToken(token).buildAsync();

    Bot(jda, options);
}

class Bot(jda: JDA, options: JDAOptions) : JDACommandManager(jda, options) {
    init {
        this.registerDependency(javaClass, this)
        this.registerCommand(BaseCommands())
    }
}
