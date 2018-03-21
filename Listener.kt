import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.guild.GuildAvailableEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class Listener(var jda: JDA, private var bot: Bot) : ListenerAdapter() {

    private val emc = 390610235250835467;

    private val comm1 = 425800238272479234;
    private val comm2 = 425801086700486656;
    private val comm3 = 425801334118154240;

    private val comm1Role =  425800300708888587;
    private val comm2Role =  425800319390449665;
    private val comm3Role =  425800424495513601;

    override fun onReady(event: ReadyEvent?) {
        checkAllGuilds()
    }

    fun checkAllGuilds() {
        jda.guilds.forEach {
            checkGuild(it);
        }
    }

    override fun onGuildAvailable(event: GuildAvailableEvent) {
        checkGuild(event.guild)
    }

    private fun checkGuild(guild: Guild) {
        if (guild.idLong != emc) {
            return;
        }
        bot.logger.info("Checking")
        checkChannel(guild, comm1, comm1Role);
        checkChannel(guild, comm2, comm2Role);
        checkChannel(guild, comm3, comm3Role);
    }

    private fun checkChannel(guild: Guild, channel: Long, roleId: Long) {
        val role = jda.getRoleById(roleId)
        val members = guild.getMembersWithRoles(role)
        members.forEach {
            val voiceState = it.voiceState
            if (!voiceState.inVoiceChannel() || voiceState.channel.idLong != channel) {
                bot.rmRole(it, roleId);
            }
        }
        for (member in guild.getVoiceChannelById(channel).members) {
            bot.addRole(member, roleId)
        }
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        if (event.guild.idLong != emc) {
            return;
        }
        val member = event.member
        val channel = event.channelJoined.idLong;
        processChannelJoin(channel, member)
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        if (event.guild.idLong != emc) {
            return;
        }
        val member = event.member
        val channel = event.channelLeft.idLong;
        processChannelLeave(channel, member)
    }

    override fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
        if (event.guild.idLong != emc) {
            return;
        }
        val member = event.member
        processChannelLeave(event.channelLeft.idLong, member);
        processChannelJoin(event.channelJoined.idLong, member);
    }

    private fun processChannelJoin(channel: Long, member: Member) {
        if (channel == comm1) {
            bot.addRole(member, comm1Role);
        } else if (channel == comm2) {
            bot.addRole(member, comm2Role);
        } else if (channel == comm3) {
            bot.addRole(member, comm3Role);
        }
    }

    private fun processChannelLeave(channel: Long, member: Member) {
        if (channel == comm1) {
            bot.rmRole(member, comm1Role);
        } else if (channel == comm2) {
            bot.rmRole(member, comm2Role);
        } else if (channel == comm3) {
            bot.rmRole(member, comm3Role);
        }
    }


}
