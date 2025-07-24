package site.remlit.blueb.residential.service

import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.inline.scheduled
import java.util.UUID

class ConfirmationService {
    companion object {
        // pair: confirmation code and task
        val pending = mutableListOf<Pair<UUID, () -> Unit>>()

        fun create(task: () -> Unit): UUID {
            val uuid = UUID.randomUUID()

            pending.add(Pair(uuid, task))
            scheduled(60 * 20) {
                cancel(uuid)
            }

            return uuid
        }

        fun sendConfirmationMessage(sender: CommandSender, uuid: UUID, message: String? = null) {
            if (message != null) MessageUtil.send(sender, message)
            MessageUtil.send(sender, "<yellow>Are you sure you want to do this?")
            MessageUtil.send(sender, "<green><click:run_command:/rd confirm $uuid>[Confirm]</click> <red><click:run_command:/rd cancel $uuid>[Cancel]</click>")
        }

        fun confirm(uuid: UUID) {
            val pair = pending.find { it.first == uuid }

            if (pair == null)
                throw GracefulCommandException("This confirmation has expired.")

            pair.second.invoke()

            pending.remove(pair)
        }

        fun cancel(uuid: UUID) {
            pending.find { it.first == uuid }
                ?.let { pending.remove(it) }
        }
    }
}