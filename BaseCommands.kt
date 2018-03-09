import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import net.dv8tion.jda.core.events.message.MessageReceivedEvent


class BaseCommands : BaseCommand() {

    @CommandAlias("ping")
    fun ping(event : MessageReceivedEvent) {
        event.channel.sendMessage("pong!").queue()
    }
}
