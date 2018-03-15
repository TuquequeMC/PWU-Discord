import co.aikar.commands.JDACommandManager
import co.aikar.commands.JDAOptions
import co.aikar.idb.DB
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Role
import java.io.FileInputStream
import java.util.*
import java.util.logging.Logger
import kotlin.concurrent.fixedRateTimer

fun main(args: Array<String>) {
    val options = JDAOptions();
    val properties = Properties();
    val file = FileInputStream("bot.properties");
    properties.load(file);
    file.close()
    val logger = Logger.getLogger("EMCBot")

    val jda = JDABuilder(AccountType.BOT).setToken(properties.getProperty("discord.token")).buildAsync();
    val db = PooledDatabaseOptions
        .builder()
        .options(DatabaseOptions.builder()
            .poolName("EMCBot DB")
            .onFatalError({
                logger.info("DB Fatal Error!")
                DB.logException(it)
                Runtime.getRuntime().exit(1)
            })
            .logger(logger)
            .mysql(
                    properties.getProperty("db.user"),
                    properties.getProperty("db.pass"),
                    properties.getProperty("db.name"),
                    properties.getProperty("db.host")
            ).build()
        ).createHikariDatabase()


    DB.setGlobalDatabase(db)
    val bot = Bot(jda, options, logger);
    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("Shutting Down")
        bot.close();
        db.close();
    })
}

class Bot(jda: JDA, options: JDAOptions, private val botLogger: Logger) : JDACommandManager(jda, options) {

    private var timer: Timer

    init {
        this.registerDependency(javaClass, this)
        this.registerDependency(botLogger.javaClass, botLogger)
        this.registerCommand(BaseCommands())
        //this.setConfigProvider {}
        //this.setPermissionResolver { event, permission -> false };
        val listener = Listener(jda, this)
        jda.addEventListener(listener)
        this.timer = fixedRateTimer(name = "RankCheckTimer", initialDelay = 0, period = 60*1000) {
            listener.checkAllGuilds();
        }
    }

    override fun getLogger(): Logger {
        return botLogger
    }

    fun addRole(member: Member, id: Long) {
        val role = getRole(id)
        if (member.roles.contains(role)) {
            return;
        }
        logger.info("Adding role ${role.name} to ${member.effectiveName}");
        member.guild.controller.addRolesToMember(member, role).queue();
    }
    fun rmRole(member: Member, id: Long) {
        val role = getRole(id)
        if (!member.roles.contains(role)) {
            return;
        }
        logger.info("Removing role ${role.name} from ${member.effectiveName}");
        member.guild.controller.removeRolesFromMember(member, role).queue();
    }

    fun getRole(id: Long): Role {
        return jda.getRoleById(id);
    }

    fun close() {
        this.timer.cancel()
    }
}
