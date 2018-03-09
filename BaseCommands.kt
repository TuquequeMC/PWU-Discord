import co.aikar.commands.BaseCommand
import co.aikar.commands.JDACommandEvent
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Dependency
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild


class BaseCommands : BaseCommand() {
    @Dependency
    lateinit var bot: Bot;
    @Dependency
    lateinit var jda: JDA;

    @CommandAlias("ping")
    fun ping(event : JDACommandEvent, guild: Guild) {
        event.sendMessage("pong!")
    }
}
