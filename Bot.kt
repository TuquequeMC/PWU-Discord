import co.aikar.commands.JDACommandManager
import co.aikar.commands.JDAOptions
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Role
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Logger

fun main(args: Array<String>) {
    val options = JDAOptions();
    val token = String(Files.readAllBytes(Paths.get("token"))).trim();
    val jda = JDABuilder(AccountType.BOT).setToken(token).buildAsync();
    Bot(jda, options);
}

class Bot(jda: JDA, options: JDAOptions) : JDACommandManager(jda, options) {
    private val botLogger = Logger.getLogger("EMCBot")
    init {
        this.registerDependency(javaClass, this)
        this.registerDependency(botLogger.javaClass, botLogger)
        this.registerCommand(BaseCommands())
        jda.addEventListener(Listener(jda, this))
    }

    override fun getLogger(): Logger {
        return botLogger
    }

    fun addRole(member: Member, id: Long) {
        val role = getRole(id)
        logger.info("Adding role ${role.name} to ${member.effectiveName}");
        member.guild.controller.addRolesToMember(member, role).queue();
    }
    fun rmRole(member: Member, id: Long) {
        val role = getRole(id)
        logger.info("Removing role ${role.name} from ${member.effectiveName}");
        member.guild.controller.removeRolesFromMember(member, role).queue();
    }

    fun getRole(id: Long): Role {
        return jda.getRoleById(id);
    }
}
